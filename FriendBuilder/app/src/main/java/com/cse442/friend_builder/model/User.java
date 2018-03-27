package com.cse442.friend_builder.model;

import java.util.ArrayList;

public abstract class User {
    protected String email;
    protected String name;
    protected String description;
    protected double latitude;
    protected double longitude;
    protected String interest0;
    protected String interest1;
    protected String interest2;

    public double getLat() {return latitude;}
    public double getLon() {return longitude;}
    public void setLatitude(double l){
        this.latitude = l;
    }
    public void setLongitude(double l){
        this.longitude = l;
    }

    public String getInterest0() {return interest0;}
    public String getInterest1() {return interest1;}
    public String getInterest2() {return interest2;}
    public void setInterest0(String a) {this.interest0 = a;}
    public void setInterest1(String a) {this.interest1 = a;}
    public void setInterest2(String a) {this.interest2 = a;}


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

}
