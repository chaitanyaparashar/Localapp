package com.fourway.localapp.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fourway.localapp.R;
import com.fourway.localapp.data.Message;

import java.util.ArrayList;

/**
 * Created by 4 way on 21-02-2017.
 */

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

    //user id
    private String token;
    private Context context;

    //Tag for tracking self message
    private int SELF = 555;

    //ArrayList of messages object containing all the messages in the thread
    private ArrayList<Message> messages;

    //Constructor
    public ThreadAdapter(Context context, ArrayList<Message> messages, String token){
        this.token = token;
        this.messages = messages;
        this.context = context;
    }

    //IN this method we are tracking the self message
    @Override
    public int getItemViewType(int position) {
        //getting message object of current position
        Message message = messages.get(position);
        String msgToken = message.getToken();
        if (msgToken == null) {
            msgToken = "";
        }
        //If its owner  id is  equals to the logged in user id
        if (msgToken.equals(token)) {
            //Returning self
            String s= message.getToken();
            s=s+"k";
            return SELF;
        }
        //else returning position
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Creating view
        View itemView;
        //if view type is self
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread, parent, false);
        } else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread_others, parent, false);
        }
        //returing the view
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Adding messages to the views
        Message message = messages.get(position);
        String text = message.getmText();
        String mURL = message.getMediaURL();
        holder.textViewMessage.setText(text);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    //Initializing views
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage;
//        public TextView textViewTimeime;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMsg);
//            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
        }
    }
}
