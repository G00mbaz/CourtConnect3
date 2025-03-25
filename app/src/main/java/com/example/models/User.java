package com.example.models;

import java.util.List;

public class User {
    private String userId;
    private String name;
    private String email;
    private List<Court> favoriteCourts;

    public User() {}

    public User(String userId, String name, String email, String phone, List<Court> favoriteCourts) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.favoriteCourts = favoriteCourts;
    }

    // גטרים וסטרים (Firebase דורש אותם)
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public List<Court> getFavoriteCourts() { return favoriteCourts; }
    public void setFavoriteCourts(List<Court> favoriteCourts) { this.favoriteCourts = favoriteCourts; }
}
