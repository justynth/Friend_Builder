package com.cse442.friend_builder.model;

import java.util.ArrayList;

public class Current extends User {

    public Current(int _id, String _name, ArrayList<Event> _events) {
        id = _id;
        name = _name;
        events = _events;
    }

    @Override
    public boolean isCurrent() {
        return true;
    }
}
