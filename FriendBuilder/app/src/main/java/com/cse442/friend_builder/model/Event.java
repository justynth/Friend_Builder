package com.cse442.friend_builder.model;

import java.sql.Time;
import java.sql.Date;

public class Event {
    protected String hostName;
    protected String name;
    protected String theme;
    protected Date date;
    protected Time begin;
    protected Time end;
    protected boolean active;

    public boolean isActive() {
        return active;
    }

    public boolean isHostedEvent() {
        return false;
    }

    public String getHostName() {
        return hostName;
    }
    public String getEventName() {
        return name;
    }
    public String getTheme() {
        return theme;
    }
    public String getDate() {
        return date.toString();
    }
    public String getBegin() {
        return begin.toString();
    }
    public String getEnd() {
        return end.toString();
    }
    public boolean getActive() {
        return active;
    }

}
