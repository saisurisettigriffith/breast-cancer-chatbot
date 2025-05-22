// ChatMessage.java
package com.example.breastcancer;

public final class ChatMessage {
    private final String sender;
    private final String text;

    public ChatMessage(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }
    public String getSender() { return sender; }
    public String getText() { return text; }
}