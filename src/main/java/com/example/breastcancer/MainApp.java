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
    private final JPanel root = new JPanel(cards);

    private final Session session = new Session();
    private final HttpApiClient api = new HttpApiClient();
    private final RiskCalculationService service = new RiskCalculationService(api);
    private final String sessionId = UUID.randomUUID().toString().substring(0, 8);

    private final JFXPanel registerFx = new JFXPanel();
    private final JFXPanel loginFx = new JFXPanel();
    private RiskFormPanel riskPanel;

    public MainApp() {
        super("Breast-Cancer ChatBot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 640);
        setLocationRelativeTo(null);

        new JFXPanel();

        buildScreens();
        cards.show(root, "register");
        add(root);
    }

    private void buildScreens() {

        root.add(registerFx, "register");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                RegisterController rc = new RegisterController(session, MainApp.this);
                RegisterPanel p = new RegisterPanel(rc);
                registerFx.setScene(new Scene(p.getPane()));
            }
        });

        root.add(loginFx, "login");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                LoginController lc = new LoginController(session, MainApp.this);
                LoginPanel p = new LoginPanel(lc);
                loginFx.setScene(new Scene(p.getPane()));
            }
        });

        RiskFormController rfc = new RiskFormController(service, session, this);
        riskPanel = new RiskFormPanel(rfc, session);
        root.add(riskPanel, "risk");

    }

    public void onLoginSuccess(String greeting) {

        if (session.getRiskCache().isEmpty()) {
            cards.show(root, "risk");
        } else {
            ChatPanel chatP = new ChatPanel(api, sessionId, session.getRiskCache(), greeting, this);
            root.add(chatP, "chat");
            cards.show(root, "chat");
        }
    }

    public void onRiskSuccess(JsonObject merged, String greeting) {
        session.addRisk(merged);
        ChatPanel chatP = new ChatPanel(api, sessionId, session.getRiskCache(), greeting, this);
        root.add(chatP, "chat");
        cards.show(root, "chat");
    }

    public void showLoginPanel() {
        cards.show(root, "login");
    }

    public void showRegisterPanel() {
        cards.show(root, "register");
    }

    public void showRiskPanel() {
        riskPanel.resetForm();
        cards.show(root, "risk");
    }

    public void logout() {
        session.logout();
        cards.show(root, "login");
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainApp().setVisible(true);
            }
        });
    }
}