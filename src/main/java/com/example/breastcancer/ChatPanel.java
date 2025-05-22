package com.example.breastcancer;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Restores the simple Chat UI from the original code so MainApp compiles.
 */
public class ChatPanel extends JPanel {

    private final ApiClient api;
    private final String    sessionId;
    private final List<JsonObject> riskCache;

    private final JTextArea  chatArea  = new JTextArea();
    private final JTextField inputFld  = new JTextField();
    private final JButton    sendBtn   = new JButton("Send");

    private final Gson gson = new Gson();

    public ChatPanel(ApiClient api, String sessionId, List<JsonObject> riskCache, String greeting) {
        this.api       = api;
        this.sessionId = sessionId;
        this.riskCache = riskCache;
        build(greeting);
    }

    /* ---------------- build UI ---------------- */
    private void build(String greeting) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.add(inputFld, BorderLayout.CENTER);
        bottom.add(sendBtn,  BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        appendAssistant(greeting);

        /* ---- listeners (anonymous inner classes, no lambdas) ---- */
        sendBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { submit(); }
        });
        inputFld.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { submit(); }
        });
    }

    /* ---------------- behaviour ---------------- */
    private void submit() {
        String msg = inputFld.getText().trim();
        if (msg.isEmpty()) return;
        appendUser(msg);
        inputFld.setText("");
        sendBtn.setEnabled(false);

        new SwingWorker<Void, Void>() {
            private String answer;
            @Override protected Void doInBackground() throws Exception {
                JsonObject body = new JsonObject();
                body.addProperty("session_id", sessionId);
                body.add("riskData", gson.toJsonTree(riskCache));
                body.addProperty("input", msg);
                JsonObject res = api.postChat(body);
                answer = res.get("answer").getAsString();
                return null;
            }
            @Override protected void done() {
                appendAssistant(answer);
                sendBtn.setEnabled(true);
            }
        }.execute();
    }

    /* ---------------- helpers ---------------- */
    private void appendUser(String txt)      { chatArea.append("You: " + txt + "\n\n"); }
    private void appendAssistant(String txt) { chatArea.append("Bot: " + txt + "\n\n"); }
}