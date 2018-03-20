package com.cse442.friend_builder.model;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by zheng on 3/11/2018.
 */

public class HostedEvent extends Event {

    public HostedEvent(String _hostName, String _name, String _theme, Date _date, Time _begin, Time _end, boolean _active) {
        hostName = _hostName;
        name = _name;
        theme = _theme;
        date = _date;
        begin = _begin;
        end = _end;
        active = _active;
    }

    @Override
    public boolean isHostedEvent() {
        return true;
    }
}
