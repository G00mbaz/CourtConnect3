package com.example.models;

import com.google.firebase.firestore.GeoPoint;

public class CourtRequests {
    private String name;
    private String address;
    private String lights;
    private GeoPoint coordinates;
    private String status; // שדה חדש

    // דרוש לפיירסטור
    public CourtRequests() {}

    public CourtRequests(String name, String address, String lights, GeoPoint coordinates) {
        this.name = name;
        this.address = address;
        this.lights = lights;
        this.coordinates = coordinates;
        this.status = "pending"; // הגדרה כברירת מחדל
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLights() {
        return lights;
    }

    public void setLights(String lights) {
        this.lights = lights;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
