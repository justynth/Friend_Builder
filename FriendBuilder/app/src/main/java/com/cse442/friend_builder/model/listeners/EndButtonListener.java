package com.cse442.friend_builder.model.listeners;

import android.view.View;

import com.cse442.friend_builder.model.HostedEvent;

/**
 * Created by zheng on 3/25/2018.
 */

public class EndButtonListener implements View.OnClickListener {

    public HostedEvent hostedEvent;
    public long startTime;
    public String endTime;


    public EndButtonListener(HostedEvent hostedEvent, long startTime) {
        this.hostedEvent = hostedEvent;
        this.startTime = startTime;
    }

    @Override
    public void onClick(View v) {

    }
}
