// File: src/main/java/com/example/breastcancer/ChatPanel.java
package com.example.breastcancer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
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
 * Simple Swing chat UI that posts to your /api/chat endpoint.
 *
 * Added (23 May 2025):
 *  • Consistent #eeeeee background.
 *  • “Redo Assessment” button (goes back to Risk Form).
 *  • “Logout” button (returns to Login screen and clears session).
 */
public class ChatPanel extends JPanel {

    private final HttpApiClient        api;
    private final String               sessionId;
    private final List<JsonObject>     riskCache;
    private final MainApp              mainApp;      // ← NEW

    private final JTextArea  chatArea   = new JTextArea();
    private final JTextField inputFld   = new JTextField();
    private final JButton    sendBtn    = new JButton("Send");
    private final JButton    redoBtn    = new JButton("Redo Assessment"); // ← renamed
    private final JButton    logoutBtn  = new JButton("Logout");          // ← NEW

    private final Gson gson = new Gson();

    public ChatPanel(HttpApiClient api,
                     String sessionId,
                     List<JsonObject> riskCache,
                     String greeting,
                     MainApp mainApp) {         // ← NEW param
        this.api       = api;
        this.sessionId = sessionId;
        this.riskCache = riskCache;
        this.mainApp   = mainApp;
        build(greeting);
    }

    private void build(String greeting) {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setBackground(new Color(0xeeeeee));              // ← uniform style

        /* ---------------- navigation bar (top) ---------------- */
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        nav.setOpaque(false);
        nav.add(redoBtn);
        nav.add(logoutBtn);
        add(nav, BorderLayout.NORTH);

        /* ---------------- chat area (centre) ---------------- */
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        /* ---------------- bottom input area ---------------- */
        JPanel bottom = new JPanel(new BorderLayout(5,5));
        bottom.setOpaque(false);
        bottom.add(inputFld, BorderLayout.CENTER);
        bottom.add(sendBtn,  BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        appendAssistant(greeting);

        /* ---- listeners ---- */
        sendBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                submit();
            }
        });
        inputFld.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                submit();
            }
        });
        redoBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                /* Jump back to the risk-assessment form */
                mainApp.showRiskPanel();
            }
        });
        logoutBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                /* Clear session and return to sign-in */
                mainApp.logout();
            }
        });
    }

    /* ---------------- network helper ---------------- */

    private void submit() {
        final String msg = inputFld.getText().trim();
        if (msg.isEmpty()) return;

        appendUser(msg);
        inputFld.setText("");
        sendBtn.setEnabled(false);

        new SwingWorker<Void,Void>() {
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
