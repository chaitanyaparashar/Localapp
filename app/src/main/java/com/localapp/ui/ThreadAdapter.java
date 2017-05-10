package com.localapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.localapp.R;
import com.localapp.appcontroller.AppController;
import com.localapp.data.Message;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by 4 way on 21-02-2017.
 */

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

    //user id
    private String uID;
    private Context context;
    private LruCache<String,Bitmap> videoThumbnailCache;

    //Tag for tracking self message
    private static final int  SELF_TEXT = 555;
    private static final int OTHER_TEXT = 556;
    private static final int SELF_IMAGE = 557;
    private static final int OTHER_IMAGE = 558;
    private static final int SELF_VIDEO = 559;
    private static final int OTHER_VIDEO = 560;
    private static final int SELF_AUDIO = 561;
    private static final int OTHER_AUDIO = 562;


    //ArrayList of messages object containing all the messages in the thread
    private ArrayList<Message> messages;

    //Constructor
    public ThreadAdapter(Context context, ArrayList<Message> messages, String uID){
        this.uID = uID;
        this.messages = messages;
        this.context = context;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        videoThumbnailCache = new LruCache<>(cacheSize);
    }

    //IN this method we are tracking the self message
    @Override
    public int getItemViewType(int position) {
        //getting message object of current position
        Message message = messages.get(position);
        String msgUserID = message.getmUserID();
        FeedFragment.MediaType mediaType = message.getMediaType();
        if (msgUserID == null) {
            msgUserID = "";
        }
        //If its owner  id is  equals to the logged in user id

        if (mediaType != null) {
            if (msgUserID.equals(uID)) {
                switch (mediaType) {
                    case MEDIA_TEXT:
                        return SELF_TEXT;

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
                        return OTHER_TEXT;

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
                itemView.setTag("img");
                break;
            case SELF_VIDEO:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_video, parent, false);
                itemView.setTag("vdo");
                break;
            case SELF_AUDIO:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_audio, parent, false);
                itemView.setTag("ado");
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
            case OTHER_VIDEO:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_video_others, parent, false);
                itemView.setTag("vdo");
                break;
            case OTHER_AUDIO:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_thread_audio_others, parent, false);
                itemView.setTag("ado");
                break;

            default: itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread_others, parent, false);
        }
        //returing the view
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //Adding messages to the views
        Message message = messages.get(position);
        FeedFragment.MediaType mediaType = message.getMediaType();
        String text = message.getmText();
        String mURL = message.getMediaURL();
        String userPicUrl = message.getPicUrl();//"https://s3-us-west-1.amazonaws.com/com.fourway.localapp.profileimage/vijay@gmail.com";

        if (message.getMessageType() != null) {
            holder.messageTypeImageView.setImageResource(getEmojiResourceIdByMsgType(message.getMessageType()));
        }
        if (userPicUrl!=null) {
            Picasso.with(AppController.getAppContext()).load(userPicUrl).placeholder(R.drawable.ic_user).into(holder.proPic);
//            holder.proPic.setImageUrl(userPicUrl, VolleySingleton.getInstance(context).getImageLoader());
//            holder.proPic.setImageBitmap(message.getImgBitmap());
        }

        /*if (message.getMediaType()!= null && message.getMediaType() == FeedFragment.MediaType.MEDIA_IMAGE) {



        } else if (message.getMediaType()!= null && message.getMediaType() == FeedFragment.MediaType.MEDIA_VIDEO) {
//            holder.imageMedia.setImageBitmap(ThumbnailUtils.createVideoThumbnail(Uri.parse(message.getMediaURL()).getPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));


        }*/
        if (mediaType!=null)
        switch (mediaType) {
            case MEDIA_TEXT:
                if (holder.textViewMessage != null) {
                    holder.textViewMessage.setText(text);
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
        public EmojiconTextView textViewMessage;
        public CircularImageView proPic;
        public ImageView messageTypeImageView;

        public ImageView imageMedia;

        public ImageView mVideoThumbnail;
//        public TextView textViewTimeime;

        public ImageButton audioPlayButton;
        public SeekBar seekBar;
        public MediaPlayer mPlayer = null;
        private double startTime = 0;
        private double finalTime = 0;
        public Handler mHandler = new Handler();



        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (EmojiconTextView) itemView.findViewById(R.id.textViewMsg);
            proPic = (CircularImageView) itemView.findViewById(R.id.msg_pic);
            messageTypeImageView = (ImageView) itemView.findViewById(R.id.msg_emoji);
//            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            if (itemView.getTag()!= null && itemView.getTag().equals("img")) {
                imageMedia = (ImageView) itemView.findViewById(R.id.msg_img);

            }

            if (itemView.getTag()!= null && itemView.getTag().equals("vdo")) {
                mVideoThumbnail = (ImageView) itemView.findViewById(R.id.thumbli);
            }

            if (itemView.getTag()!= null && itemView.getTag().equals("ado")) {

                audioPlayButton =(ImageButton) itemView.findViewById(R.id._audio_play);
                audioPlayButton.setOnClickListener(this);
                seekBar = (SeekBar) itemView.findViewById(R.id._seekBar);
                seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
                seekBar.setProgress(0);
                mPlayer = new MediaPlayer();
                mPlayer.setOnPreparedListener(onPreparedListener);
            }



        }



        @Override
        public void onClick(View v) {

            if (v.getId() == R.id._audio_play) {
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


}
