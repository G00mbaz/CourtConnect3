package com.example.models;

public class ChatMessage {
    private String userName;
    private String message;
    private long timestamp;
    private String profileImageUrl;

    public ChatMessage() {}

    public ChatMessage(String userName, String message, long timestamp, String profileImageUrl) {
        this.userName = userName;
        this.message = message;
        this.timestamp = timestamp;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
