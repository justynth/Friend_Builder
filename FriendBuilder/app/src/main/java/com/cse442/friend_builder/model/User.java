package com.cse442.friend_builder.model;

import java.util.ArrayList;

public abstract class User {
    protected String email;
    protected String name;
    protected String description;

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