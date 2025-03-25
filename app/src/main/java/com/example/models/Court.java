package com.example.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class Court {
    private String courtId;
    private String name;
    private String lights;
    private String address;
    private GeoPoint location;

    public Court() {}

    public Court(String courtId, String name, String lights, GeoPoint location) {
        this.courtId = courtId;
        this.name = name;
        this.address=address;
        this.lights = lights;
        this.location=location;
    }

    public String getCourtId() { return courtId; }
    public void setCourtId(String courtId) { this.courtId = courtId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String isLights() { return lights; }
    public void setLights(String lights) { this.lights = lights; }

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address=address;}

    public GeoPoint isLoction() {return location;}
    public void setLoction(GeoPoint loction) {this.location=loction; }

}
