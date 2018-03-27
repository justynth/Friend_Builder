package com.cse442.friend_builder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cse442.friend_builder.model.Messages;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by PeterYang on 3/25/2018.
 */

public class MessageAdaptar extends RecyclerView.Adapter<MessageAdaptar.MessageViewHolder>{
    private List<Messages> userMessages;
    public MessageAdaptar(List<Messages> userMessages){
        this.userMessages = userMessages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout,parent,false);
        return new MessageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Messages messages = userMessages.get(position);
        holder.mess.setText(messages.getMessage());
        holder.namer.setText(messages.getName());
    }

    @Override
    public int getItemCount() {
        return userMessages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView mess;
        public TextView namer;
        public MessageViewHolder(View view) {
            super(view);
            mess = (TextView) view.findViewById(R.id.messageText);
            namer = (TextView) view.findViewById(R.id.nameUser);
        }
    }
}


