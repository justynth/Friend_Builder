package com.cse442.friend_builder.model;

import java.util.ArrayList;

public class Current extends User {

    public Current(String _email, String _name, String _description, double _lat, double _lon) {
        email = _email;
        name = _name;
        description = _description;
        latitude = _lat;
        longitude = _lon;
    }
    public Current() {}
}
