package com.example.breastcancer;

import java.awt.CardLayout;
import java.util.List;
import java.util.UUID;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.google.gson.JsonObject;

/**
 * Application entry-point and simple screen-flow manager.
 * All screens live inside one CardLayout so we can jump
 * between them with cards.show(root, "name").
 */
public class MainApp extends JFrame {

    /* ---------- navigation ---------- */
    private final CardLayout cards = new CardLayout();
    private final JPanel     root  = new JPanel(cards);

    /* ---------- back-end / state ---------- */
    private final Session                 session   = new Session();
    private final HttpApiClient           api       = new HttpApiClient();
    private final RiskCalculationService  service   = new RiskCalculationService(api);

    /* ---------- UI screens ---------- */
    private RegisterPanel registerPanel;
    private LoginPanel    loginPanel;
    private RiskFormPanel riskFormPanel;
    private ChatPanel     chatPanel;

    /* ---------- controllers ---------- */
    private RegisterController registerController;
    private LoginController    loginController;
    private RiskFormController riskFormController;
    private ChatController     chatController;

    /* ---------- misc ---------- */
    private final String sessionId =
        UUID.randomUUID().toString().substring(0, 8);

    public MainApp() {
        super("Breast-Cancer ChatBot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 640);
        setLocationRelativeTo(null);

        buildScreens();
        add(root);
        cards.show(root, "register");          // first screen
    }

    /* ======================================================================
     *  Build screens + wire controllers
     * ====================================================================== */
    private void buildScreens() {
        /* ---- register / login ---- */
        registerController = new RegisterController(session, this);
        registerPanel      = new RegisterPanel(registerController);
        root.add(registerPanel, "register");

        loginController    = new LoginController(session, this);
        loginPanel         = new LoginPanel(loginController);
        root.add(loginPanel, "login");

        /* ---- risk form ---- */
        riskFormController = new RiskFormController(service, session, this);
        riskFormPanel      = new RiskFormPanel(riskFormController, session);
        root.add(riskFormPanel, "form");
    }

    /* ======================================================================
     *  Navigation helpers – invoked by controllers
     * ====================================================================== */
    public void showLoginPanel()     { cards.show(root, "login");     }
    public void showRegisterPanel()  { cards.show(root, "register");  }

    /** After successful sign-in. */
    public void onLoginSuccess(String greeting) {
        if (!session.getRiskCache().isEmpty()) {
            showChat(session.getRiskCache(), greeting);   // user already has predictions
        } else {
            cards.show(root, "form");                     // need parameters first
        }
    }

    /** After /api/risk completes. */
    public void onRiskSuccess(JsonObject merged, String greeting) {
        session.addRisk(merged);
        showChat(session.getRiskCache(), greeting);
    }

    /* ------------------------------------------------------------------ */
    private void showChat(List<JsonObject> riskCache, String greeting) {
        chatController = new ChatController(session, this);
        chatPanel      = new ChatPanel(api, sessionId, riskCache, greeting);
        root.add(chatPanel, "chat");
        cards.show(root, "chat");
    }

    /* ====================================================================== */
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()
        );
        SwingUtilities.invokeLater(new Runnable() {
            public void run() { new MainApp().setVisible(true); }
        });
    }
}
