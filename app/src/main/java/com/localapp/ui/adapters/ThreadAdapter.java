package com.localapp.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.localapp.R;
import com.localapp.appcontroller.AppController;
import com.localapp.models.Message;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.localapp.models.ReplyMessage;
import com.localapp.ui.fragments.FeedFragment;
import com.localapp.ui.activities.HomeActivity;
import com.localapp.utils.Constants;
import com.localapp.utils.Utility;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * Created by Vijay Kumar on 21-02-2017.
 * @author Vijay Kumar
 */

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

    //user id
    private String uID;
    private Context context;
    private LruCache<String,Bitmap> videoThumbnailCache;

    //Tag for tracking self message
    private static final int SELF_TEXT = 555;
    private static final int OTHER_TEXT = 556;
    private static final int SELF_IMAGE = 557;
    private static final int OTHER_IMAGE = 558;
    private static final int SELF_VIDEO = 559;
    private static final int OTHER_VIDEO = 560;
    private static final int SELF_AUDIO = 561;
    private static final int OTHER_AUDIO = 562;

    private static final int SELF_TEXT_REPLY = 563;
    private static final int OTHER_TEXT_REPLY = 564;

    private static final String IMAGE = "img";
    private static final String VIDEO = "vdo";
    private static final String AUDIO = "ado";

    private static final String TEXT_REPLY_TAG = "text_reply";


    //ArrayList of messages object containing all the messages in the thread
    public ArrayList<Message> messages;
    public ArrayList<Message> selected_messageList=new ArrayList<>();
    private RecyclerViewListener recyclerViewListener;


    private  Drawable drawableSelected;
    private  Drawable unSelected ;
    //Constructor
    public ThreadAdapter(Context context, ArrayList<Message> messages,ArrayList<Message> selectedList, String uID,RecyclerViewListener listener){
        this.uID = uID;
        this.messages = messages;
        this.selected_messageList = selectedList;
        this.context = context;
        this.recyclerViewListener = listener;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        videoThumbnailCache = new LruCache<>(cacheSize);

        drawableSelected = new ColorDrawable(ContextCompat.getColor(context,R.color.list_item_selected_state));
        unSelected = new ColorDrawable(ContextCompat.getColor(context,android.R.color.transparent));
    }

    //IN this method we are tracking the self message
    @Override
    public int getItemViewType(int position) {
        //getting message object of current position
        Message message = messages.get(position);
        String msgUserID = message.getmUserID();
        String msgReplyId = message.getReplyMessageId();
        FeedFragment.MediaType mediaType = message.getMediaType();
        if (msgUserID == null) {
            msgUserID = "";
        }
        //If its owner  id is  equals to the logged in user id

        if (mediaType != null) {
            if (msgUserID.equals(uID)) {
                switch (mediaType) {
                    case MEDIA_TEXT:
                        if (msgReplyId == null || msgReplyId.equals("null")) {
                            return SELF_TEXT;
                        }else {
                            return SELF_TEXT_REPLY;
                        }

                    case MEDIA_IMAGE:
                        return SELF_IMAGE;

                    case MEDIA_VIDEO:
                        return SELF_VIDEO;
                    case MEDIA_AUDIO:
                        return SELF_AUDIO;

                }
            } else {
                switch (mediaType) {
                    case MEDIA_TEXT:
                        if (msgReplyId == null || msgReplyId.equals("null")) {
                            return OTHER_TEXT;
                        }else {
                            return OTHER_TEXT_REPLY;
                        }

                    case MEDIA_IMAGE:
                        return OTHER_IMAGE;

                    case MEDIA_VIDEO:
                        return OTHER_VIDEO;
                    case MEDIA_AUDIO:
                        return OTHER_AUDIO;

                }
            }
        }else {
            return 0;
        }
        return 0;
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
                itemView.setTag(IMAGE);
                break;
            case SELF_VIDEO:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_video, parent, false);
                itemView.setTag(VIDEO);
                break;
            case SELF_AUDIO:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_audio, parent, false);
                itemView.setTag(AUDIO);
                break;

            case OTHER_TEXT:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_others, parent, false);
                break;
            case OTHER_IMAGE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_image_others, parent, false);
                itemView.setTag(IMAGE);
                break;
            case OTHER_VIDEO:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_video_others, parent, false);
                itemView.setTag(VIDEO);
                break;
            case OTHER_AUDIO:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_audio_others, parent, false);
                itemView.setTag(AUDIO);
                break;

            case SELF_TEXT_REPLY:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_reply, parent, false);
                itemView.setTag(TEXT_REPLY_TAG);
                break;
            case OTHER_TEXT_REPLY:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_reply_others, parent, false);
                itemView.setTag(TEXT_REPLY_TAG);
                break;


            default: itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread_others, parent, false);
        }
        //returing the view
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //Adding messages to the views
        final Message message = messages.get(position);
        FeedFragment.MediaType mediaType = message.getMediaType();
        String text = message.getmText();
        String mURL = message.getMediaURL();
        String userName = (message.getName() != null) ? "~" + message.getName() : "";
        String timeStamp = message.getTimeStamp();
        String replyId = message.getReplyMessageId();
        ReplyMessage replyMessage = message.getReplyMessage();
        final String user_id = message.getmUserID();
        final String userPicUrl = message.getPicUrl();//"https://s3-us-west-1.amazonaws.com/com.fourway.localapp.profileimage/vijay@gmail.com";

        if (holder.nameTextView != null) {
            holder.nameTextView.setText(userName);
        }

        if (holder.timeTextView != null){
            holder.timeTextView.setText(Utility.getSmsTime(timeStamp));
        }
        if (message.getMessageType() != null) {
            holder.messageTypeImageView.setImageResource(getEmojiResourceIdByMsgType(message.getMessageType()));
        }
        if (userPicUrl!=null) {
            Picasso.with(AppController.getAppContext()).load(userPicUrl).placeholder(R.drawable.ic_user).into(holder.proPic);
//            holder.proPic.setImageUrl(userPicUrl, VolleySingleton.getInstance(context).getImageLoader());
//            holder.proPic.setImageBitmap(message.getImgBitmap());
        }

        holder.proPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_messageList.size() == 0 && user_id != null){
                    Utility.openPublicProfile(context, user_id, null);
                }else if (selected_messageList.size() > 0) {
                    holder.itemView.performClick();
                }
            }
        });

        holder.proPic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.itemView.performLongClick();
                return true;
            }
        });


        /*if (message.getMediaType()!= null && message.getMediaType() == FeedFragment.MediaType.MEDIA_IMAGE) {



        } else if (message.getMediaType()!= null && message.getMediaType() == FeedFragment.MediaType.MEDIA_VIDEO) {
//            holder.imageMedia.setImageBitmap(ThumbnailUtils.createVideoThumbnail(Uri.parse(message.getMediaURL()).getPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));


        }*/
        if (mediaType!=null)
        switch (mediaType) {
            case MEDIA_TEXT:
                if (holder.textViewMessage != null) {
                    holder.textViewMessage.setText(text);

                    if (replyId != null && !replyId.equals("null")) {  //reply message
                        holder.nameTextViewOld.setText("~"+replyMessage.getName());
                        holder.textViewMessageOld.setText(replyMessage.getTextMessage());
                    }
                }
                break;
            case MEDIA_IMAGE:
                //holder.imageMedia.setImageDrawable(new BitmapDrawable(context.getResources(),BitmapFactory.decodeFile(message.getMediaURL())));
                Picasso.with(AppController.getAppContext()).load(message.getMediaURL()).placeholder(R.drawable.ic_picture).into(holder.imageMedia);
                break;
            case MEDIA_VIDEO:


                final Bitmap bm = getBitmapFromMemCache(message.getMediaURL());
                if (bm == null){
                    BitmapWorkerTask task = new BitmapWorkerTask(holder.mVideoThumbnail);
                    task.execute(message.getMediaURL());
                }else {
                    holder.mVideoThumbnail.setImageBitmap(bm);
                }


                /*try {
                    holder.mVideoThumbnail.setImageBitmap(retrieveVideoFrameFromVideo(message.getMediaURL()));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }*/

                break;

            case MEDIA_AUDIO:

                break;
        }








        if(selected_messageList.contains(messages.get(position)))
            holder.ll_listitem.setForeground(drawableSelected);
        else
            holder.ll_listitem.setForeground(unSelected);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewListener.onClick(v, position);
                /*if (uID.equals(user_id)) {
                    recyclerViewListener.onClick(v, position);
                }else {
                    recyclerViewListener.onClick(v, position);
                    if (selected_messageList.size() > 0) {
                        Toast.makeText(context, "Please select your message", Toast.LENGTH_SHORT).show();
                    }
                }*/
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (uID.equals(user_id)) {
                    recyclerViewListener.onLongClick(v, position);
                    return true;
                }else {
                    //TODO: for testing
                    recyclerViewListener.onLongClick(v, position);
                    return true;
//                    Toast.makeText(context, "Please select your message", Toast.LENGTH_SHORT).show();
                }


//                return false;
            }
        });
    }




    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static String  currentMediaDataSource;
    public static ImageButton oldAudioPlayButton;
    public static SeekBar oldSeekBar;
    public static MediaPlayer oldMediaPlayer;
    //Initializing views
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public FrameLayout ll_listitem;
        public EmojiconTextView textViewMessage, textViewMessageOld;
        public CircularImageView proPic;
        public ImageView messageTypeImageView;
        public TextView nameTextView, timeTextView,nameTextViewOld;

        public RoundedImageView imageMedia;

        public ImageView mVideoThumbnail;
//        public TextView textViewTimeime;

        public ImageButton audioPlayButton;
        public SeekBar seekBar;
        public MediaPlayer mPlayer = null;
        private double startTime = 0;
        private double finalTime = 0;
        public Handler mHandler = new Handler();


        //for reply message




        public ViewHolder(View itemView) {
            super(itemView);
            ll_listitem = (FrameLayout) itemView.findViewById(R.id.ll_listitem);
            nameTextView = (TextView) itemView.findViewById(R.id.textViewName);
            timeTextView = (TextView) itemView.findViewById(R.id.sms_time);
            textViewMessage = (EmojiconTextView) itemView.findViewById(R.id.textViewMsg);
            proPic = (CircularImageView) itemView.findViewById(R.id.msg_pic);
            messageTypeImageView = (ImageView) itemView.findViewById(R.id.msg_emoji);
//            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            if (itemView.getTag()!= null && itemView.getTag().equals(IMAGE)) {
                imageMedia = (RoundedImageView) itemView.findViewById(R.id.msg_img);

            }

            if (itemView.getTag()!= null && itemView.getTag().equals(VIDEO)) {
                mVideoThumbnail = (ImageView) itemView.findViewById(R.id.thumbli);
            }

            if (itemView.getTag()!= null && itemView.getTag().equals(AUDIO)) {

                audioPlayButton =(ImageButton) itemView.findViewById(R.id._audio_play);
                audioPlayButton.setOnClickListener(this);
                seekBar = (SeekBar) itemView.findViewById(R.id._seekBar);
                seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
                seekBar.setProgress(0);
                mPlayer = new MediaPlayer();
                mPlayer.setOnPreparedListener(onPreparedListener);
            }


            if (itemView.getTag() != null && itemView.getTag().equals(TEXT_REPLY_TAG)) {
                nameTextViewOld = (TextView) itemView.findViewById(R.id.textViewNameOld);
                textViewMessageOld = (EmojiconTextView) itemView.findViewById(R.id.textViewMsgOld);
            }



        }



        @Override
        public void onClick(View v) {

            if (selected_messageList.size()== 0 && v.getId() == R.id._audio_play) {
                if (mPlayer != null && !mPlayer.isPlaying()) {
                    startPlaying();
                }else {
                    pause();
                }


            }

        }

        private void startPlaying() {
            if (isChangedDataSource(messages.get(getAdapterPosition()).getMediaURL())) {
                try {
                    audioPlayButton.setBackgroundResource(android.R.drawable.ic_media_pause);
                    mPlayer.reset();
                    mPlayer.setDataSource(messages.get(getAdapterPosition()).getMediaURL());
                    mPlayer.prepare();

                    mPlayer.start();
                    oldAudioPlayButton = audioPlayButton;
                    oldSeekBar = seekBar;
                    oldMediaPlayer = mPlayer;
                    currentMediaDataSource = messages.get(getAdapterPosition()).getMediaURL();

                } catch (IOException e) {
                    Log.e("ThreadAdapter", "prepare() failed");
                    audioPlayButton.setBackgroundResource(android.R.drawable.ic_media_play);
                }
            }else {
                mPlayer.start();
                audioPlayButton.setBackgroundResource(android.R.drawable.ic_media_pause);
            }

        }

        private void pause(){
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.pause();
                audioPlayButton.setBackgroundResource(android.R.drawable.ic_media_play);
            }

        }

        private void stopPlaying() {
            mPlayer.release();
            mPlayer = null;
        }

        private boolean isChangedDataSource(String path) {

            if (currentMediaDataSource != null && currentMediaDataSource.equals(path)) {
                return false;
            }else {
                if (currentMediaDataSource != null) {
                    oldAudioPlayButton.setBackgroundResource(android.R.drawable.ic_media_play);
                    oldSeekBar.setProgress(0);
                    oldMediaPlayer.stop();
                }

                return true;
            }
        }


        MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                ((HomeActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            finalTime = mp.getDuration();
                            startTime = mp.getCurrentPosition();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        seekBar.setMax((int)finalTime);
                        seekBar.setProgress((int)startTime);

                        if (finalTime == startTime) {
                            audioPlayButton.setBackgroundResource(android.R.drawable.ic_media_play);
                            seekBar.setProgress(0);
                        }

                        mHandler.postDelayed(this,100);
                    }
                });
            }
        };

        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mPlayer != null && fromUser){
                    mPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        };


    }

    public static int getEmojiResourceIdByMsgType(FeedFragment.MessageType messageType){
        switch (messageType) {
            case STRAIGHT:
                    return Constants.emojiResourceID[0];
            case SHOUT:
                return Constants.emojiResourceID[1];
            case WHISPER:
                return Constants.emojiResourceID[2];
            case GOSSIP:
                return Constants.emojiResourceID[3];
            case MURMUR:
                return Constants.emojiResourceID[4];
            case MUMBLE:
                return Constants.emojiResourceID[5];
            case EMERGENCY:
                return Constants.emojiResourceID[6];
        }
        return Constants.emojiResourceID[0];
    }


    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                bitmap = retrieveVideoFrameFromVideo(params[0]);
                if (bitmap != null) {
                    addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = (ImageView)imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
//                    imageView.setImageDrawable(new BitmapDrawable(context.getResources(),bitmap));
                }
            }
        }


        public Bitmap retrieveVideoFrameFromVideo(String videoPath) throws Throwable
        {
            Bitmap bitmap = null;
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try
            {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14)
                    mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                else
                    mediaMetadataRetriever.setDataSource(videoPath);
                //   mediaMetadataRetriever.setDataSource(videoPath);
                bitmap = mediaMetadataRetriever.getFrameAtTime();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Throwable("Exception in retrieveVideoFrameFromVideo(String videoPath)" + e.getMessage());

            } finally {
                if (mediaMetadataRetriever != null) {
                    mediaMetadataRetriever.release();
                }
            }
            return getResizedBitmap(bitmap,200);
        }



    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            videoThumbnailCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return (Bitmap) videoThumbnailCache.get(key);

    }

    public interface RecyclerViewListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }


}
