package com.cse442.friend_builder.model;

/**
 * Created by PeterYang on 3/25/2018.
 */

public class Messages {
    private String message, name,type;

    public Messages(){

    }

    public Messages(String message, String name, String type) {
        this.message = message;
        this.name = name;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setName(String name){this.name = name;}
    public String getName(){
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
