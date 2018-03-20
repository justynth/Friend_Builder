package com.cse442.friend_builder.model;

import java.util.ArrayList;

public abstract class User {
    protected int id;
    protected String name;
    protected ArrayList<Event> events;

    public boolean isCurrent() {
        return false;
    }
}
