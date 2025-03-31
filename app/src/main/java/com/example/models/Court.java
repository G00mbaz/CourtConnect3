package com.example.models;

import com.google.firebase.firestore.GeoPoint;

public class Court {
    private String courtId;
    private String name;
    private String lights;
    private String address;
    private GeoPoint location;

    public Court() {}

    public Court(String courtId, String name, String lights, String address, GeoPoint location) {
        this.courtId = courtId;
        this.name = name;
        this.lights = lights;
        this.address = address;
        this.location = location;
    }

    public String getCourtId() {
        return courtId;
    }

    public void setCourtId(String courtId) {
        this.courtId = courtId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLights() {
        return lights;
    }

    public void setLights(String lights) {
        this.lights = lights;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
