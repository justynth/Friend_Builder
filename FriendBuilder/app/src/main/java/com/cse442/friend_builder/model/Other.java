package com.cse442.friend_builder.model;

import java.util.ArrayList;

public class Other extends User {

    public Other(int _id, String _name, ArrayList<Event> _events) {
        id = id;
        name = _name;
        events = _events;
    }

    @Override
    public boolean isCurrent() {
        return false;
    }
}
