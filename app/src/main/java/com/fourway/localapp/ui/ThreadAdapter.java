package com.fourway.localapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.fourway.localapp.R;
import com.fourway.localapp.data.Message;
import com.fourway.localapp.request.helper.VolleySingleton;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

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
        String userPicUrl = "https://s3-us-west-1.amazonaws.com/com.fourway.localapp.profileimage/vijay@gmail.com";
        holder.textViewMessage.setText(text);
        if (message.getMessageType() != null) {
            holder.messageTypeImageView.setImageResource(getEmojiResourceIdByMsgType(message.getMessageType()));
        }
        if (userPicUrl!=null) {
            holder.proPic.setImageUrl(userPicUrl, VolleySingleton.getInstance(context).getImageLoader());
//            holder.proPic.setImageBitmap(message.getImgBitmap());
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    //Initializing views
    public class ViewHolder extends RecyclerView.ViewHolder {
        public EmojiconTextView textViewMessage;
        public CircularNetworkImageView proPic;
        public ImageView messageTypeImageView;
//        public TextView textViewTimeime;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (EmojiconTextView) itemView.findViewById(R.id.textViewMsg);
            proPic = (CircularNetworkImageView) itemView.findViewById(R.id.msg_pic);
            messageTypeImageView = (ImageView) itemView.findViewById(R.id.msg_emoji);
//            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
        }
    }

    public int getEmojiResourceIdByMsgType(FeedFragment.MessageType messageType){
        switch (messageType) {
            case STRAIGHT:
                    return FeedFragment.emojiResourceID[0];
            case SHOUT:
                return FeedFragment.emojiResourceID[1];
            case WHISPER:
                return FeedFragment.emojiResourceID[2];
            case GOSSIP:
                return FeedFragment.emojiResourceID[3];
            case MURMUR:
                return FeedFragment.emojiResourceID[4];
            case MUMBLE:
                return FeedFragment.emojiResourceID[5];
            case EMERGENCY:
                return FeedFragment.emojiResourceID[6];
        }
        return FeedFragment.emojiResourceID[0];
    }


}
