package com.cse442.friend_builder.model;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by zheng on 3/11/2018.
 */

public class HostedEvent extends Event {

    public HostedEvent(String _hostName, String _name, String _theme, String _date, String _begin, String _end, boolean _active) {
        hostName = _hostName;
        name = _name;
        theme = _theme;
        date = _date;
        begin = _begin;
        end = _end;
        active = _active;
    }

    public HostedEvent() {}
}
