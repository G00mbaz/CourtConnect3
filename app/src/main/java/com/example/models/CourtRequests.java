package com.example.models;

import com.google.firebase.firestore.GeoPoint;

public class CourtRequests {
    private String name;
    private String address;
    private String lights;
    private GeoPoint location;

    public CourtRequests() {}

    public CourtRequests(String name, String address, String lights, GeoPoint loction){
        this.name=name;
        this.address=address;
        this.lights=lights;
        this.location=loction;
    }

    public String getName() { return name;}
    public void setName(String name) {this.name=name; }

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address=address;}

    public String getLights() {return lights;}
    public void setLights(String lights) {this.lights=lights;}

    public GeoPoint getLoction() {return location;}
    public void setLoction(GeoPoint loction) {this.location = loction;}

    }