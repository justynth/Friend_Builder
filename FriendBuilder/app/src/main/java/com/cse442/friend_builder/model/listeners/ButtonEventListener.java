package com.cse442.friend_builder.model.listeners;

import android.view.View;

import com.cse442.friend_builder.model.HostedEvent;

/**
 * Created by zheng on 3/25/2018.
 */

public class ButtonEventListener implements View.OnClickListener {

    public long timeStamp;
    public HostedEvent hostedEvent;
    public String key;

    public ButtonEventListener(long timeStamp, HostedEvent hostedEvent, String key) {
        this.timeStamp = timeStamp;
        this.hostedEvent = hostedEvent;
        this.key = key;
    }

    @Override
    public void onClick(View v) {

    }
}
