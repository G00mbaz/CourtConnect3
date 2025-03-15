package com.example.models;

import java.util.List;

public class Court {
    private String courtId;
    private String name;
    private double latitude;
    private double longitude;
    private boolean available;
    private boolean lights;
    private String openingHours;
    private List<String> players;

    public Court() {}

    public Court(String courtId, String name, double latitude, double longitude, boolean available, boolean lights, String openingHours, List<String> players) {
        this.courtId = courtId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.available = available;
        this.lights = lights;
        this.openingHours = openingHours;
        this.players = players;
    }

    public String getCourtId() { return courtId; }
    public void setCourtId(String courtId) { this.courtId = courtId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public boolean isLights() { return lights; }
    public void setLights(boolean lights) { this.lights = lights; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public List<String> getPlayers() { return players; }
    public void setPlayers(List<String> players) { this.players = players; }
}
