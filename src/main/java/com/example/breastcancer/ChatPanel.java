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

public class ChatPanel extends JPanel {

    private final HttpApiClient api;
    private final String sessionId;
    private final List<JsonObject> riskCache;
    private final MainApp mainApp;

    private final JTextArea chatArea = new JTextArea();
    private final JTextField inputFld = new JTextField();
    private final JButton sendBtn = new JButton("Send");
    private final JButton redoBtn = new JButton("Redo Assessment");
    private final JButton logoutBtn = new JButton("Logout");

    private final Gson gson = new Gson();

    public ChatPanel(HttpApiClient api,
            String sessionId,
            List<JsonObject> riskCache,
            String greeting,
            MainApp mainApp) {
        this.api = api;
        this.sessionId = sessionId;
        this.riskCache = riskCache;
        this.mainApp = mainApp;
        build(greeting);
    }

    private void build(String greeting) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(0xeeeeee));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        nav.setOpaque(false);
        nav.add(redoBtn);
        nav.add(logoutBtn);
        add(nav, BorderLayout.NORTH);

        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setOpaque(false);
        bottom.add(inputFld, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        appendAssistant(greeting);

        /**
         * External - RegisterPanel.java, LoginPanel.java: JavaFX uses...
         * ...EventHandler<T extends Event> for all events.
         * 
         * HERE HOWEVER, Swing uses specific...
         * ...listener interfaces (ActionListener) for events.
         * 
         * Both handle similar events, but with different APIs.
         */

        sendBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                submit();
            }
        });

        inputFld.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                submit();
            }
        });

        redoBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {

                mainApp.showRiskPanel();
            }
        });

        logoutBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {

                mainApp.logout();
            }
        });
    }

    private void submit() {
        final String msg = inputFld.getText().trim();
        if (msg.isEmpty())
            return;

        appendUser(msg);
        inputFld.setText("");
        sendBtn.setEnabled(false);

        new SwingWorker<Void, Void>() {
            private String answer;

            @Override
            protected Void doInBackground() throws Exception {
                JsonObject body = new JsonObject();
                body.addProperty("session_id", sessionId);
                body.add("riskData", gson.toJsonTree(riskCache));
                body.addProperty("input", msg);
                JsonObject res = api.postChat(body);
                answer = res.get("answer").getAsString();
                return null;
                /**
                 * This is where we send the request to the Flask back-end.
                 * We see another .postChat(body) method call,
                 * which is similar to the one in RiskCalculationService.java.
                 * 
                 * However, here we are sending a chat message
                 * to the Flask back-end, which will process it
                 * and return a response.
                 * 
                 * The body (JsonObject) contains:
                 * - session_id: the current session ID
                 * - riskData: the cached risk data from the risk assessment
                 * - input: the user's message
                 * 
                 * The Flask back-end will process this data,
                 * generate a response, and return it as a JsonObject.
                 * 
                 * You can see we saved it into a variable called <res>
                 * 
                 * This variable contains the response from the Flask back-end,
                 * which includes the assistant's reply to the user's message.
                 * 
                 * This is all possible because we have a
                 * variable called <api> of type HttpApiClient
                 * injected into this class.
                 * 
                 * api takes in JsonObject and res is returned with a JsonObject as well,
                 * but this time it contains the assistant's response.
                 * 
                 * at the end it is turned into a String and later we can see
                 * below in the done() method
                 * that we append the assistant's response to the chat area.
                 * 
                 * upon completion, we enable the send button again
                 *
                 * appendUser and appendAssistant methods
                 * are tiny helper methods
                 * but the modularization is very useful...
                 * 
                 * Here is where we call them:
                 * 
                 * When submit() is called (this is where doInBackground() is called...
                 * ... as well as done() via SwingWorker) - ASYNC!!!):
                 *      1. We call appendUser(msg) to add the user's message
                 * When done() is automatically called...
                 * ...(i.e, once doInBackground() is finished),
                 *      2. We call appendAssistant(answer) to add the assistant's response.
                 */
            }

            @Override
            protected void done() {
                appendAssistant(answer);
                sendBtn.setEnabled(true);
            }
        }.execute();
    }

    private void appendUser(String txt) {
        chatArea.append("You: " + txt + "\n\n");
    }

    private void appendAssistant(String txt) {
        chatArea.append("Bot: " + txt + "\n\n");
    }
}


