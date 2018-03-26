package com.cse442.friend_builder.model;

import java.util.ArrayList;

public class Current extends User {

    public Current(String _email, String _name, String _description, double longitude, double latitude) {
        email = _email;
        name = _name;
        description = _description;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Current() {}
}
