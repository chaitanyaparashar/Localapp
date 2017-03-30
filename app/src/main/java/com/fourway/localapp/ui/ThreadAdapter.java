package com.fourway.localapp.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.fourway.localapp.R;
import com.fourway.localapp.data.Message;
import com.fourway.localapp.request.helper.VolleySingleton;
import com.squareup.picasso.Picasso;

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
    private static final int  SELF_TEXT = 555;
    private static final int OTHER_TEXT = 556;
    private static final int SELF_IMAGE = 557;
    private static final int OTHER_IMAGE = 558;

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
        String msgToken = message.getmUserID();
        if (msgToken == null) {
            msgToken = "";
        }
        //If its owner  id is  equals to the logged in user id
        if (msgToken.equals(token)) {
            //Returning self
            try {
                if (message.getMediaType().equals(FeedFragment.MediaType.MEDIA_TEXT))
                {
                    return SELF_TEXT;
                }else {
                    return SELF_IMAGE;
                }

            }catch (NullPointerException e){
                return SELF_TEXT;
            }


        }else {
            try {
                if (message.getMediaType().equals(FeedFragment.MediaType.MEDIA_TEXT))
                {
                    return OTHER_TEXT;
                }else {
                    return OTHER_IMAGE;
                }
            }catch (NullPointerException e) {
                return position;
            }

        }
        //else returning position
//        return position;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Creating view
        View itemView = null;
        //if view type is self
       /* if (viewType == SELF_TEXT) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread, parent, false);
        } else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread_others, parent, false);
        }*/
        switch (viewType) {
            case SELF_TEXT:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread, parent, false);
                break;
            case SELF_IMAGE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_image, parent, false);
                itemView.setTag("img");
                break;
            case OTHER_TEXT:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_others, parent, false);
                break;
            case OTHER_IMAGE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_image_others, parent, false);
                itemView.setTag("img");
                break;
            default: itemView = LayoutInflater.from(parent.getContext())
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
        String userPicUrl = message.getPicUrl();//"https://s3-us-west-1.amazonaws.com/com.fourway.localapp.profileimage/vijay@gmail.com";
        if (holder.textViewMessage != null) {
            holder.textViewMessage.setText(text);
        }
        if (message.getMessageType() != null) {
            holder.messageTypeImageView.setImageResource(getEmojiResourceIdByMsgType(message.getMessageType()));
        }
        if (userPicUrl!=null) {
            holder.proPic.setImageUrl(userPicUrl, VolleySingleton.getInstance(context).getImageLoader());
//            holder.proPic.setImageBitmap(message.getImgBitmap());
        }

        if (message.getMediaType()!= null && message.getMediaType() == FeedFragment.MediaType.MEDIA_IMAGE) {
//            holder.imageMedia.setImageDrawable(new BitmapDrawable(context.getResources(),BitmapFactory.decodeFile(message.getMediaURL())));
            Picasso.with(context).load(message.getMediaURL()).into(holder.imageMedia);
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
        public ImageView imageMedia;
//        public TextView textViewTimeime;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (EmojiconTextView) itemView.findViewById(R.id.textViewMsg);
            proPic = (CircularNetworkImageView) itemView.findViewById(R.id.msg_pic);
            messageTypeImageView = (ImageView) itemView.findViewById(R.id.msg_emoji);
//            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            if (itemView.getTag()!= null && itemView.getTag().equals("img")) {
                imageMedia = (ImageView) itemView.findViewById(R.id.msg_img);
            }
        }
    }

    public static int getEmojiResourceIdByMsgType(FeedFragment.MessageType messageType){
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
