/* ──────────────────────────────────────────────
 * File: src/main/java/com/example/breastcancer/MainApp.java
 * Shows the risk form only for first-time users;
 * returning users jump straight to the chat.
 *
 * Added (23 May 2025):
 *  • showRiskPanel() helper so ChatPanel can trigger “Redo Assessment”.
 *  • logout() helper clears current Session and returns to Login.
 *  • ChatPanel constructor updated to accept MainApp reference.
 * ──────────────────────────────────────────── */
package com.example.breastcancer;

import java.awt.CardLayout;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.google.gson.JsonObject;

import javafx.embed.swing.JFXPanel;
import javafx.application.Platform;
import javafx.scene.Scene;

public class MainApp extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel     root  = new JPanel(cards);

    /* ---------- back-end / state ---------- */
    private final Session                 session   = new Session();
    private final HttpApiClient           api       = new HttpApiClient();
    private final RiskCalculationService  service   = new RiskCalculationService(api);
    private final String                  sessionId = UUID.randomUUID().toString().substring(0, 8);

    /* ---------- JavaFX wrappers ---------- */
    private final JFXPanel registerFx = new JFXPanel();
    private final JFXPanel loginFx    = new JFXPanel();

    public MainApp() {
        super("Breast-Cancer ChatBot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 640);
        setLocationRelativeTo(null);

        /* Boot JavaFX runtime (needed once) */
        new JFXPanel();

        buildScreens();
        cards.show(root, "register");
        add(root);
    }

    private void buildScreens() {
        /* -------- REGISTER (JavaFX) -------- */
        root.add(registerFx, "register");
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RegisterController rc = new RegisterController(session, MainApp.this);
                RegisterPanel      p  = new RegisterPanel(rc);
                registerFx.setScene(new Scene(p.getPane()));
            }
        });

        /* -------- LOGIN (JavaFX) -------- */
        root.add(loginFx, "login");
        Platform.runLater(new Runnable() {
            @Override public void run() {
                LoginController lc = new LoginController(session, MainApp.this);
                LoginPanel      p  = new LoginPanel(lc);
                loginFx.setScene(new Scene(p.getPane()));
            }
        });

        /* -------- RISK FORM (Swing) -------- */
        RiskFormController rfc = new RiskFormController(service, session, this);
        RiskFormPanel      rfp = new RiskFormPanel(rfc, session);
        root.add(rfp, "risk");
        /* Chat panel created later when needed */
    }

    /* Called by LoginController on successful login */
    public void onLoginSuccess(String greeting) {
        /* If user has *no* prior assessments → show the form,
           otherwise jump straight to chat. */
        if (session.getRiskCache().isEmpty()) {
            cards.show(root, "risk");
        } else {
            ChatPanel chatP = new ChatPanel(api, sessionId, session.getRiskCache(), greeting, this); // ← pass MainApp
            root.add(chatP, "chat");
            cards.show(root, "chat");
        }
    }

    /* Called by RiskFormController after GAIL returns */
    public void onRiskSuccess(JsonObject merged, String greeting) {
        session.addRisk(merged);  /* also persists to Mongo */
        ChatPanel chatP = new ChatPanel(api, sessionId, session.getRiskCache(), greeting, this); // ← pass MainApp
        root.add(chatP, "chat");
        cards.show(root, "chat");
    }

    /* ---------- navigation helpers ---------- */
    public void showLoginPanel()    { cards.show(root, "login");    }
    public void showRegisterPanel() { cards.show(root, "register"); }
    public void showRiskPanel()     { cards.show(root, "risk"); }     // ← NEW

    /** Clears the current session and returns to the Login screen. */
    public void logout() {
        session.logout();
        cards.show(root, "login");
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()
        );
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                new MainApp().setVisible(true);
            }
        });
    }
}
