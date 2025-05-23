// File: src/main/java/com/example/breastcancer/MainApp.java
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

/**
 * Application entry-point and screen‐flow manager.
 * Embeds the JavaFX Register/Login forms via JFXPanel,
 * then swaps to Swing RiskFormPanel and ChatPanel.
 */
public class MainApp extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel     root  = new JPanel(cards);

    /* ---------- back-end / state ---------- */
    private final Session                 session   = new Session();
    private final HttpApiClient           api       = new HttpApiClient();
    private final RiskCalculationService  service   = new RiskCalculationService(api);
    private final String                  sessionId = UUID.randomUUID().toString().substring(0,8);

    /* ---------- JavaFX wrappers ---------- */
    private final JFXPanel registerFx = new JFXPanel();
    private final JFXPanel loginFx    = new JFXPanel();

    public MainApp() {
        super("Breast-Cancer ChatBot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 640);
        setLocationRelativeTo(null);

        // Kick-off JavaFX runtime
        new JFXPanel();

        buildScreens();
        add(root);
        cards.show(root, "register");
    }

    private void buildScreens() {
        // --- REGISTER (JavaFX) ---
        root.add(registerFx, "register");
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RegisterController rc = new RegisterController(session, MainApp.this);
                RegisterPanel      p  = new RegisterPanel(rc);
                registerFx.setScene(new Scene(p.getPane()));
            }
        });

        // --- LOGIN (JavaFX) ---
        root.add(loginFx, "login");
        Platform.runLater(new Runnable() {
            @Override public void run() {
                LoginController lc = new LoginController(session, MainApp.this);
                LoginPanel      p  = new LoginPanel(lc);
                loginFx.setScene(new Scene(p.getPane()));
            }
        });

        // --- RISK FORM (Swing) ---
        RiskFormController rfc = new RiskFormController(service, session, this);
        RiskFormPanel      rfp = new RiskFormPanel(rfc, session);
        root.add(rfp, "risk");

        // CHAT panel is created dynamically in onRiskSuccess()
    }

    // Called by LoginController on successful login
    public void onLoginSuccess(String greeting) {
        cards.show(root, "risk");
    }

    // Called by RiskFormController after GAIL returns
    public void onRiskSuccess(JsonObject merged, String greeting) {
        session.addRisk(merged);
        ChatPanel chatP = new ChatPanel(api, sessionId, session.getRiskCache(), greeting);
        root.add(chatP, "chat");
        cards.show(root, "chat");
    }

    // Navigation helpers
    public void showLoginPanel()    { cards.show(root, "login");    }
    public void showRegisterPanel() { cards.show(root, "register"); }

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
