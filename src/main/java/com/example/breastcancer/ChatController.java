package com.example.breastcancer;

public class ChatController {
    private final MainApp mainApp;
    private final Session session;

    public ChatController(Session session, MainApp mainApp) {
        this.mainApp = mainApp;
        this.session = session;
    }
}