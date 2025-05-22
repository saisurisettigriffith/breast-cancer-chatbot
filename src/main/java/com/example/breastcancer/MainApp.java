package com.example.breastcancer;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.google.gson.JsonObject;

/**
 * Application entry‑point (single‑package version).
 */
public class MainApp extends JFrame implements RiskSubmitListener {

    private final CardLayout cards = new CardLayout();
    private final JPanel     root  = new JPanel(cards);

    /* ───────── dependencies ───────── */
    private final ApiClient              api         = new HttpApiClient();
    private final RiskCalculationService calcService = new RiskCalculationService(api);

    /* ───────── session state ───────── */
    private final List<JsonObject> riskCache = new ArrayList<>();
    private final String           sessionId = UUID.randomUUID().toString().substring(0, 8);

    public MainApp() {
        super("Breast Cancer ChatBot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 640);
        setLocationRelativeTo(null);

        RiskFormController formController = new RiskFormController(calcService, this);
        root.add(new RiskFormPanel(formController), "form");
        add(root);
    }

    /** Callback invoked by RiskFormController after successful calculation. */
    @Override
    public void onRiskSuccess(JsonObject merged, String greeting) {
        riskCache.add(merged);
        ChatPanel chat = new ChatPanel(api, sessionId, riskCache, greeting);
        root.add(chat, "chat");
        cards.show(root, "chat");
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() { new MainApp().setVisible(true); }
        });
    }
}