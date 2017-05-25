package com.localapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.localapp.compressor.Compressor;
import com.localapp.VideoPlay;
import com.localapp.appcontroller.AppController;
import com.localapp.audio.ViewProxy;
import com.localapp.camera.Camera2Activity;
import com.localapp.data.GetFeedRequestData;
import com.localapp.data.Message;
import com.localapp.data.NotificationData;
import com.localapp.fcm.FcmNotificationRequest;
import com.localapp.feedback.AppPreferences;
import com.localapp.login_session.SessionManager;
import com.localapp.request.CommonRequest;
import com.localapp.request.EmergencyMsgAcceptRequest;
import com.localapp.request.GetFeedRequest;
import com.localapp.request.PicUrlRequest;
import com.squareup.picasso.Picasso;
import com.localapp.util.RecyclerTouchListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.localapp.R;

import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SUCCESS;
import static com.localapp.ui.FeedFragment.MediaType.MEDIA_AUDIO;
import static com.localapp.ui.FeedFragment.MediaType.MEDIA_IMAGE;
import static com.localapp.ui.FeedFragment.MediaType.MEDIA_VIDEO;
import static com.localapp.ui.ThreadAdapter.getEmojiResourceIdByMsgType;
import static com.localapp.util.utility.isLocationAvailable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment implements GetFeedRequest.GetFeedRequestCallback,PicUrlRequest.PicUrlResponseCallback,EmergencyMsgAcceptRequest.EmergencyMsgAcceptResponseCallback {

    private final String TAG = "FeedFragment";
    private static final String sAddress = "tcp://13.56.50.98:1883";
//    private final String sAddress = "tcp://192.172.2.178:1883";//local
//    private final String sAddress = "tcp://192.172.3.23:2883";
    private static final String mTopic = "localapp";
    private static final String mTopicAcceptMsg = "accept";
//    private static final String mTopic = "vijay";

    private SessionManager sessionManager;
    final static String[] CAMERA_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};
    final static String[] AUDIO_PERMISSIONS = {Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_AUDIO_CODE = 300;
    private static final int REQUEST_CAMERA_CODE = 201;

    private MQTT mqtt = null;
    private ProgressDialog progressDialog = null;
    private FutureConnection connection = null;
    private Map<String, String> mParams;

    //Recyclerview objects
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private GridView emojiGridView;

    private ListView emergencyMessageListView;
    private EmergencyListAdapter emergencyListAdapter;
    private List<Message> emergencyMessageList;

    private View typeMessageAreaPreventClickView;


    //******************************** tool tips *******************//

    private RelativeLayout overlayRL;
    private LinearLayout overlayVoiceLL, overlayCamMediaLL;
    private TextView textHelp;


    EmojiconEditText chatText;
    ImageView sendImageViewBtn, camShoutImgBtn,emoticImgBtn;
    public static int selectedMessageTypeInt = 0;
    public static int selectedEmojiResourceID = R.drawable.emoji_staright;
    public final String[]  emoji_name = {"Straight","Shout","Whisper","Gossip","Murmur","Mumble","Emergency"};
    public static int[] emojiResourceID = {R.drawable.emoji_staright,R.drawable.emoji_shout,R.drawable.emoji_whisper,R.drawable.emoji_gossip,R.drawable.emoji_murmer,R.drawable.emoji_mumble,R.drawable.emoji_emergency};

    @Override
    public void EmergencyMsgAcceptResponse(CommonRequest.ResponseCode responseCode) {
        if (responseCode == COMMON_RES_SUCCESS) {
//            toast("accepted em");
        }
    }

    public enum MediaType {
        MEDIA_TEXT(0),
        MEDIA_IMAGE(1),
        MEDIA_VIDEO(2),
        MEDIA_AUDIO(3);

        private final int number;

        MediaType(int number) {
            this.number = number;
        }

         MediaType() {
            this.number = ordinal();
        }

        public int getNumber() {
            return number;
        }
    }

    //ArrayList of messages to store the thread messages
    private ArrayList<Message> messages;


    LinearLayout linearLayoutMsgArea;

    /************** Valuables for Audio ****************/
    private TextView recordTimeText;
    private View recordPanel;
    private View slideText;
    private float startedDraggingX = -1;
    private float distCanMove = dp(80);
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private Timer timer;


    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);


        sessionManager = new SessionManager(getContext());

        linearLayoutMsgArea = (LinearLayout) view.findViewById(R.id.linear_layout_msg_area);
        typeMessageAreaPreventClickView = (View) view.findViewById(R.id.type_message_area_prevent_click_View);
        typeMessageAreaPreventClickView.setOnClickListener(typeMessageSurfaceClickListener);
        initializationOfAudioObjects(view);
        //Initializing recyclerView
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#2196f3"));
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        emojiGridView = (GridView) view.findViewById(R.id.shout_emiji);
        chatText = (EmojiconEditText) view.findViewById(R.id.chat_text);
        camShoutImgBtn = (ImageView) view.findViewById(R.id.btn_cam_shout);
        camShoutImgBtn.setOnClickListener(cameraClickListener);
        sendImageViewBtn = (ImageView) view.findViewById(R.id.btn_send_speak);
        emoticImgBtn = (ImageView) view.findViewById(R.id.btn_emoticon);

        recyclerView.addOnItemTouchListener(recyclerTouchListener);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setItemViewCacheSize(50);
        layoutManager = new LinearLayoutManager(getContext());

        emergencyMessageListView = (ListView) view.findViewById(R.id.emergency_ListView);
        emergencyMessageList = new ArrayList<>();
        emergencyListAdapter = new EmergencyListAdapter(getActivity(),emergencyMessageList);
        emergencyMessageListView.setAdapter(emergencyListAdapter);

        emergencyMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getId() == R.id.accept_btn) {
                    Message message = emergencyMessageList.get(position);
                    emergencyMessageList.remove(position);

                    sendNotification(message.getFcmToken());
                    EmergencyMsgAcceptRequest msgAcceptRequest = new EmergencyMsgAcceptRequest(getContext(),message.getMsgIdOnlyForFrontEnd(), "1",FeedFragment.this);
                    if (connection.isConnected()) {
                        connection.publish(mTopicAcceptMsg, message.getMsgIdOnlyForFrontEnd().getBytes(), QoS.AT_LEAST_ONCE, false);
                        msgAcceptRequest.executeRequest();
                    }else {
                        connectMqtt();
                        connection.publish(mTopicAcceptMsg, message.getMsgIdOnlyForFrontEnd().getBytes(), QoS.AT_LEAST_ONCE, false);
                        msgAcceptRequest.executeRequest();
                    }
                    if (emergencyMessageList.size() == 0) {
                        emergencyMessageListView.setVisibility(View.GONE);
                    }
                    emergencyListAdapter.notifyDataSetChanged();
                }
            }
        });

        emoticImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        recyclerView.setLayoutManager(layoutManager);
//        sendImageViewBtn.setOnClickListener(sendVoiceClickListener);
        sendImageViewBtn.setOnTouchListener(audioSendOnTouchListener);
        chatText.addTextChangedListener(textWatcher);
        // Inflate the layout for this fragment

        //Initializing message arraylist
                messages = new ArrayList<>();
        adapter = new ThreadAdapter(getContext(),messages,HomeActivity.mUserId);//hardcoded token
        recyclerView.setAdapter(adapter);


        EmojiGridAdapter adapter1  =  new EmojiGridAdapter(getContext(),android.R.layout.simple_gallery_item,emoji_name);
        emojiGridView.setAdapter(adapter1);
        emojiGridView.setOnItemClickListener(selectMsgTypeEmojiListener);



        EmojIconActions  emojIcon=new EmojIconActions(getActivity(),view,chatText,emoticImgBtn);
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard,R.drawable.ic_smily);
        emojIcon.ShowEmojIcon();



        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                request();
            }
        });

//        connectMqtt();

        //calcultae distance
//        String dis = calcDistance(new LatLng(28.550123, 77.326640),new LatLng(28.550189, 77.353430),"m",true);
//        toast(dis);

        if (!AppPreferences.getInstance(AppController.getAppContext()).isLaunchedBroadcastToolTip()) {
            toolTips(view);
        }
//
//
        return view;
    }


    // mqtt callback used for Future
    <T> Callback<T> onui(final Callback<T> original) {
        return new Callback<T>() {
            public void onSuccess(final T value) {
                getActivity().runOnUiThread(new Runnable(){
                    public void run() {
                        original.onSuccess(value);
                    }
                });
            }
            public void onFailure(final Throwable error) {

                try {
                    getActivity().runOnUiThread(new Runnable(){

                        public void run() {
                            original.onFailure(error);
                        }
                    });
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
        };
    }

    private void connectMqtt() {
        mqtt = new MQTT();


        mqtt.setClientId(HomeActivity.mLoginToken);
        try {
            mqtt.setHost(sAddress);
            Log.d(TAG, "Address set: " + sAddress);
        }
        catch(URISyntaxException urise) {
            Log.e(TAG, "URISyntaxException connecting to " + sAddress + " - " + urise);
        }


        connection = mqtt.futureConnection();
        connection.connect().then(onui(new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                Log.v(TAG,"Mqtt Connected");
                subscribe();//subscribed
            }

            @Override
            public void onFailure(Throwable value) {
                Log.e(TAG, "Exception connecting to " + sAddress + " - " + value);
            }
        }));



    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        connection.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (connection==null ||!connection.isConnected()) {
            connectMqtt();
        }
    }


    private void sendNotification(String fcmToken){
        if (ProfileFragment.myProfile != null) {
            NotificationData data = new NotificationData(fcmToken);
            data.setName(ProfileFragment.myProfile.getuName());
            data.setEmail(ProfileFragment.myProfile.getuEmail());
            data.setMobile(ProfileFragment.myProfile.getuMobile());
            data.setImg_url(ProfileFragment.myProfile.getuPictureURL());
            data.setLatLng(HomeActivity.mLastKnownLocation);
            data.setProfession(ProfileFragment.myProfile.getProfession());
            data.setProfile(ProfileFragment.myProfile);
            FcmNotificationRequest request = new FcmNotificationRequest(getContext(), data);
            request.executeRequest();
        }
    }


    private void subscribe() {
        Topic[] topics = {new Topic(mTopic, QoS.AT_LEAST_ONCE),new Topic(mTopicAcceptMsg,QoS.AT_LEAST_ONCE)};
        connection.subscribe(topics).then(onui(new Callback<byte[]>() {
            @Override
            public void onSuccess(byte[] value) {
                connection.receive().then(onui(new Callback<org.fusesource.mqtt.client.Message>() {
                    @Override
                    public void onSuccess(org.fusesource.mqtt.client.Message message) {
                        String receivedMessageTopic = message.getTopic();
                        byte[] payload = message.getPayload();
                        String messagePayLoad = new String(payload);
                        message.ack();
                        JSONObject jsonObject = null;
                        Message messageData = new Message();
                        try {
                            jsonObject = new JSONObject(messagePayLoad);
                            messageData.setmUserID(jsonObject.getString("userId"));
                            messageData.setMsgIdOnlyForFrontEnd(jsonObject.getString("emergencyId"));
                            messageData.setPicUrl(jsonObject.getString("picUrl"));
                            messageData.setMediaURL(jsonObject.getString("mediaUrl"));
                            messageData.setMediaType(MediaType.values()[Integer.parseInt(jsonObject.getString("mediaType"))]);
                            messageData.setToken(jsonObject.getString("token"));
                            messageData.setFcmToken(jsonObject.getString("fcmToken"));
                            messageData.setmText(jsonObject.getString("msg"));
                            messageData.setTimeStamp(jsonObject.getString("timestamp"));
                            messageData.setMessageType(getMessageType(jsonObject.getInt("messageType")));
                            JSONArray latlngJsonArray = new JSONArray(jsonObject.getString("longlat"));
                            messageData.setmLatLng(new LatLng(Double.valueOf(latlngJsonArray.getString(0)),Double.valueOf(latlngJsonArray.getString(1))));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (receivedMessageTopic.equals(mTopic)) {
                            if (HomeActivity.mUserId == null || !HomeActivity.mUserId.equals(messageData.getmUserID()) &&
                                    isMessageForMe(messageData.getMessageType(), messageData.getmLatLng())) {
                                messages.add(messageData);
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//                                scrollToBottom();
                                if (messageData.getMessageType() == MessageType.EMERGENCY) {
                                    emergencyMessageListView.setVisibility(View.VISIBLE);
                                    emergencyMessageList.add(messageData);
                                    emergencyListAdapter.notifyDataSetChanged();
                                    emergencyMessageListView.scrollTo(emergencyListAdapter.getCount()-2,emergencyListAdapter.getCount() -1);
                                }

                                if (messageData.getMessageType() == MessageType.WHISPER){
                                    whisperMsg(messageData);
                                }
                            }
                        }else {
                            for (Message message1:emergencyMessageList){
                                if (message1.getMsgIdOnlyForFrontEnd().equals(messagePayLoad)){
                                    int index = emergencyMessageList.indexOf(message1);
                                    emergencyMessageList.remove(index);
                                    emergencyListAdapter.notifyDataSetChanged();
                                    if (emergencyMessageList.size() == 0) {
                                        emergencyMessageListView.setVisibility(View.GONE);
                                    }
                                    break;
                                }
                            }
//                            toast("accept");
                        }
                        subscribe();//must subscribe on received every msg
                    }

                    @Override
                    public void onFailure(Throwable value) {
                        Log.e(TAG, "Exception receiving message: " + value);
                    }
                }));
            }

            @Override
            public void onFailure(Throwable value) {
                Log.e(TAG, "Exception subscribe: " + value);
            }
        }));
    }

    private void toast(String message)
    {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }


    void request() {
        if (HomeActivity.mLastKnownLocation !=null ) {
//            LatLng latLng = new LatLng(28.545544, 77.331020);
            GetFeedRequest feedRequest = new GetFeedRequest(getContext(), HomeActivity.mLastKnownLocation, this);
            feedRequest.executeRequest();
            swipeRefreshLayout.setRefreshing(true);
        }else {
            swipeRefreshLayout.setRefreshing(false);
            new CountDownTimerTask(5000,5000).start();
        }

        if (HomeActivity.mUserId != null && !HomeActivity.mUserId.equals("")){
            typeMessageAreaPreventClickView.setVisibility(View.GONE);
        }else {
            typeMessageAreaPreventClickView.setVisibility(View.VISIBLE);
        }

    }


    View.OnClickListener typeMessageSurfaceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (HomeActivity.mUserId == null || HomeActivity.mUserId.equals("")) {
                Toast.makeText(getContext(), "Please login first...", Toast.LENGTH_SHORT).show();
            }else {
                typeMessageAreaPreventClickView.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Please click again...", Toast.LENGTH_SHORT).show();
            }
        }
    };



    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            request();
        }
    };


    View.OnClickListener sendTextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = chatText.getText().toString().trim();
            if (!text.matches("") && isLocationAvailable(getContext())) {

                Message messageData = new Message();
//                messageData.setToken("58c93b21f81fde4c11fe02e1");
                messageData.setToken(HomeActivity.mLoginToken);
                if (HomeActivity.mUserId != null) {
                    messageData.setmUserID(HomeActivity.mUserId);
                } else {
                    messageData.setmUserID("");
                }

                if (sessionManager.getFcmToken() != null) {
                    messageData.setFcmToken(sessionManager.getFcmToken());
                }else {
                    messageData.setFcmToken(FirebaseInstanceId.getInstance().getToken());
                }

                if (HomeActivity.mPicUrl !=null) {
                    messageData.setPicUrl(HomeActivity.mPicUrl);
                }
                messageData.setMessageType(getMessageType(selectedMessageTypeInt));

                if (messageData.getMessageType() == MessageType.MUMBLE) {
                    text = mumbleMessage(text);
                }

                if (messageData.getMessageType() == MessageType.EMERGENCY) {
                    messageData.setMsgIdOnlyForFrontEnd(nextSessionId());
                } else {
                    messageData.setMsgIdOnlyForFrontEnd("");
                }

                messageData.setTimeStamp(String.valueOf(System.currentTimeMillis()/1000));
                messageData.setmText(text);
                messageData.setMediaType(MediaType.MEDIA_TEXT);
                messageData.setmLatLng(HomeActivity.mLastKnownLocation);

                messages.add(messageData);
                adapter.notifyDataSetChanged();
                chatText.setText("");
                scrollToBottom();

                if (messageData.getMessageType() == MessageType.WHISPER){//remove after 2 min if WHISPER Message
                    whisperMsg(messageData);
                }


                /*****/
                /*Drawable d = getResources().getDrawable(R.drawable.aaa); // the drawable (Captain Obvious, to the rescue!!!)
                Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();*/
                /******/


                mParams =  new HashMap<>();
                mParams.put("token",messageData.getToken());
                mParams.put("fcmToken",messageData.getFcmToken());
                mParams.put("userId",messageData.getmUserID());
                mParams.put("picUrl",messageData.getPicUrl());
                mParams.put("mediaUrl","");
                mParams.put("mediaType",""+messageData.getMediaType().getNumber());
                mParams.put("emergencyId",messageData.getMsgIdOnlyForFrontEnd());
                mParams.put("msg",messageData.getmText());
                mParams.put("timestamp",messageData.getTimeStamp());
//                mParams.put("img",bitmap);
                mParams.put("messageType",""+selectedMessageTypeInt);
                String[] latlng = {""+messageData.getmLatLng().latitude,""+messageData.getmLatLng().longitude};
                mParams.put("longlat",Arrays.toString(latlng));
                JSONObject jsonObject = new JSONObject(mParams);
                if (connection.isConnected()) {
                    connection.publish(mTopic, jsonObject.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
                }else {
                    connectMqtt();
                    connection.publish(mTopic, jsonObject.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
                }

                initMsgTypeEmoji(); //set default message type straight

            }

        }
    };


    private void sendMedia(MediaType mediaType,String mediaUrl, int to ) {
        Message messageData = new Message();
//                messageData.setToken("58c93b21f81fde4c11fe02e1");
        messageData.setToken(HomeActivity.mLoginToken);
        if (HomeActivity.mUserId != null) {
            messageData.setmUserID(HomeActivity.mUserId);
        } else {
            messageData.setmUserID("");
        }

        if (HomeActivity.mPicUrl !=null) {
            messageData.setPicUrl(HomeActivity.mPicUrl);
        }

        messageData.setMediaURL(mediaUrl);

        messageData.setMessageType(getMessageType(selectedMessageTypeInt));


        if (messageData.getMessageType() == MessageType.EMERGENCY) {
            messageData.setMsgIdOnlyForFrontEnd(nextSessionId());
        } else {
            messageData.setMsgIdOnlyForFrontEnd("");
        }

        messageData.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        messageData.setMediaType(mediaType);
        messageData.setmLatLng(HomeActivity.mLastKnownLocation);



        if (to == 1) {
            mParams =  new HashMap<>();
            mParams.put("token",messageData.getToken());
            mParams.put("userId",messageData.getmUserID());
            mParams.put("picUrl",messageData.getPicUrl());
            mParams.put("emergencyId","");
            mParams.put("mediaUrl",messageData.getMsgIdOnlyForFrontEnd());
            mParams.put("msg","");
            mParams.put("mediaUrl",messageData.getMediaURL());
            mParams.put("mediaType",""+messageData.getMediaType().getNumber());
            mParams.put("timestamp",messageData.getTimeStamp());
//                mParams.put("img",bitmap);
            mParams.put("messageType",""+selectedMessageTypeInt);
            String[] latlng = {""+messageData.getmLatLng().latitude,""+messageData.getmLatLng().longitude};
            mParams.put("longlat",Arrays.toString(latlng));
            JSONObject jsonObject = new JSONObject(mParams);
            if (connection.isConnected()) {
                connection.publish(mTopic, jsonObject.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
            }else {
                connectMqtt();
                connection.publish(mTopic, jsonObject.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
            }
        }else {
            messages.add(messageData);
            adapter.notifyDataSetChanged();
            scrollToBottom();

        }
    }

    AdapterView.OnItemClickListener selectMsgTypeEmojiListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedMessageTypeInt = position;
            selectedEmojiResourceID = emojiResourceID[position];
            camShoutImgBtn.setImageResource(selectedEmojiResourceID);
            emojiGridView.setVisibility(View.GONE);
        }
    };

    void initMsgTypeEmoji() {
        selectedMessageTypeInt = 0;
        selectedEmojiResourceID = emojiResourceID[0];
        camShoutImgBtn.setImageResource(R.drawable.ic_camera);
    }



    View.OnClickListener dropDownClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (emojiGridView.getVisibility() == View.VISIBLE) {
                emojiGridView.setVisibility(View.GONE);
            }else {
                emojiGridView.setVisibility(View.VISIBLE);
            }
        }
    };

    View.OnClickListener cameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isCameraPermissionGrated()){
                openCamera();
            }else {
                requestPermissions(CAMERA_PERMISSIONS,REQUEST_CAMERA_CODE);
            }

        }
    };


    void openCamera(){
        Intent intent = new Intent(getContext(),Camera2Activity.class);
        intent.putExtra("requestCode", CAMERA_REQUEST);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    boolean isCameraPermissionGrated(){
        return (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    boolean isAudioPermissionGranted() {
        return (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(chatText.getText())) {
//                sendImageViewBtn.setOnClickListener(sendVoiceClickListener);
                sendImageViewBtn.setOnTouchListener(audioSendOnTouchListener);
                sendImageViewBtn.setImageResource(R.drawable.ic_speak);
                camShoutImgBtn.setImageResource(R.drawable.ic_camera);
                camShoutImgBtn.setOnClickListener(cameraClickListener);
                emojiGridView.setVisibility(View.GONE);
            }else {
                sendImageViewBtn.setOnClickListener(sendTextClickListener);
                sendImageViewBtn.setOnTouchListener(null);
                sendImageViewBtn.setImageResource(R.drawable.ic_send);
                camShoutImgBtn.setImageResource(selectedEmojiResourceID);
                camShoutImgBtn.setOnClickListener(dropDownClickListener);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void scrollToBottom() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1)
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
    }

    private void setBroadcastHistory(GetFeedRequestData simpleMsgData, GetFeedRequestData emergencyMsgData) {

        for(Message msg : simpleMsgData.getMessageList()) {
            if (isMessageForMe(msg.getMessageType(),msg.getmLatLng())) {
                messages.add(msg);

                if (msg.getMessageType() == MessageType.WHISPER){
                    whisperMsg(msg);
                }
            }
        }

        for (Message msg : emergencyMsgData.getMessageList()) {
            if (isMessageForMe(msg.getMessageType(), msg.getmLatLng())) {
                emergencyMessageListView.setVisibility(View.VISIBLE);
                emergencyMessageList.add(msg);
            }
        }

        adapter.notifyDataSetChanged();
        emergencyListAdapter.notifyDataSetChanged();


    }


    @Override
    public void GetFeedResponse(CommonRequest.ResponseCode responseCode, GetFeedRequestData data, GetFeedRequestData emergencyData) {
        switch (responseCode) {
            case COMMON_RES_SUCCESS:
                if (messages.size()>0) {
                    messages.clear();
                }

                if (emergencyMessageList.size() > 0){
                    emergencyMessageList.clear();
                }


                setBroadcastHistory(data, emergencyData);

                /*messages.addAll(data.getMessageList());
                emergencyMessageList.addAll(emergencyData.getMessageList());
                adapter.notifyDataSetChanged();
                emergencyListAdapter.notifyDataSetChanged();
                if (emergencyMessageList.size() > 0) {
                    emergencyMessageListView.setVisibility(View.VISIBLE);
                }*/
                break;
            case COMMON_RES_CONNECTION_TIMEOUT:
                break;
            case COMMON_RES_FAILED_TO_CONNECT:
                Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                break;
            case COMMON_RES_INTERNAL_ERROR:
                break;
            case COMMON_RES_SERVER_ERROR_WITH_MESSAGE:
                break;
        }

        swipeRefreshLayout.setRefreshing(false);

    }


    class EmojiGridAdapter extends ArrayAdapter {
        Context mContext;
        String[] emojiName;

        public EmojiGridAdapter(Context context, int resource, String[] emoji_name) {
            super(context, resource, emoji_name);
            mContext = context;
            this.emojiName = emoji_name;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.emoticon_grid_layout,null);
            ImageView imageView = (ImageView)view.findViewById(R.id.emoji_icon);
            TextView textView = (TextView) view.findViewById(R.id.emoji_text);
            textView.setText(emojiName[position]);
            imageView.setImageResource(emojiResourceID[position]);

            return view;
        }
    }


    /*********** calculate distance by google**********/
    /**
     *
     * @param from
     * @param to
     * @param unit
     * @param showUnit
     * @return
     */
    private String calcDistance(LatLng from, LatLng to,String unit,boolean showUnit) {
        double distance = SphericalUtil.computeDistanceBetween(from, to);
        return formatNumber(distance,unit,showUnit);
    }

    private String formatNumber(double distance,String unit,boolean showUnit) {
        /*String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }*/

        switch (unit) {
            case "km":
                distance /= 1000;
                unit = "km";
                break;
            case "mm":
                distance *= 1000;
                unit = "mm";
                break;
            default:
                unit = "m";
        }

        if (!showUnit) {
            unit = "";
        }

        return String.format("%4.3f%s", distance, unit);
    }

    public enum MessageType {
        STRAIGHT,
        SHOUT,
        WHISPER,
        GOSSIP,
        MURMUR,
        MUMBLE,
        EMERGENCY
    }
    public static MessageType getMessageType(int type) {
        switch (type) {
            case 0: return MessageType.STRAIGHT;
            case 1: return MessageType.SHOUT;
            case 2: return MessageType.WHISPER;
            case 3: return MessageType.GOSSIP;
            case 4: return MessageType.MURMUR;
            case 5: return MessageType.MUMBLE;
            case 6: return MessageType.EMERGENCY;
        }
        return MessageType.STRAIGHT;
    }

    private boolean isMessageForMe(MessageType messageType, LatLng latLng) {
        double distance = Double.valueOf(calcDistance(HomeActivity.mLastKnownLocation,latLng,"km",false));
        switch (messageType) {
            case STRAIGHT:
                if (distance <= 2)
                return true;
                break;
            case SHOUT:
                if (distance <= 5)
                    return true;
                break;
            case WHISPER:
                //TODO: will remove in 2 min
                if (distance <= 2)
                    return true;
                break;
            case GOSSIP:
                if (distance <= 4)
                    return true;
                break;
            case MURMUR:
                if (distance <= 1)
                    return true;
                break;
            case MUMBLE:
                //TODO: rearrange string
                if (distance <= 2)
                    return true;
                break;
            case EMERGENCY:
                if (distance <= 10)
                    return true;
                break;
        }
        return false;
    }

    /**
     *
     * @param msgText
     * @return
     */
    public String mumbleMessage(String msgText) {

        String mumbledStr = "";
        StringTokenizer st = new StringTokenizer(msgText," ");
        List<String> stringList = new ArrayList<>();
        while (st.hasMoreTokens()) {
            stringList.add(st.nextToken());
        }
        Collections.shuffle(stringList);


        for (int i=0;i<stringList.size();i++) {
            mumbledStr += stringList.get(i)+ " ";
        }

        if (stringList.size()>1 && mumbledStr.trim().equals(msgText)) {
            Collections.shuffle(stringList);
            mumbledStr = "";
            for (int i=0;i<stringList.size();i++) {
                mumbledStr += stringList.get(i)+ " ";
            }
        }

        return mumbledStr.trim();
    }


    public void whisperMsg(Message message){
        new CountDownWhisper(message,60000*2,1000).start();

    }

    /**
     * Random generate string for emergency msg id only for front end
     * @return
     */
    public String nextSessionId() {
       SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }




    /*********** calculate distance by GeoDataSource.com**********/

    /*private double distance(double lat1, double lon1, double lat2, double lon2, String unit){

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);

    }

    *//*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*//*
	*//*::	This function converts decimal degrees to radians						 :*//*
	*//*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*//*
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    *//*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*//*
	*//*::	This function converts radians to decimal degrees						 :*//*
	*//*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*//*
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }*/


    class EmergencyListAdapter extends BaseAdapter{
        private Activity context;
        private List<Message> emergencyMessageList;
        private LayoutInflater inflater = null;

        public EmergencyListAdapter(Activity context, List<Message> emergencyMessageList) {
            this.context = context;
            this.emergencyMessageList = emergencyMessageList;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return emergencyMessageList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View vi=convertView;
            if(convertView==null)
                vi = inflater.inflate(R.layout.emergency_message_list, null);
            CircularImageView proPicImageView = (CircularImageView)vi.findViewById(R.id.msg_pic);
            EmojiconTextView msgTextView = (EmojiconTextView)vi.findViewById(R.id.textViewMsg);
            Button acceptButton = (Button) vi.findViewById(R.id.accept_btn);
            ImageView messageTypeImageView = (ImageView) vi.findViewById(R.id.msg_emoji) ;

            Message message = emergencyMessageList.get(position);

//            proPicImageView.setImageUrl(message.getMediaURL(), VolleySingleton.getInstance(context).getImageLoader());
            Picasso.with(context).load(message.getPicUrl()).into(proPicImageView);
            msgTextView.setText(message.getmText());
            if (message.getMessageType() != null) {
                messageTypeImageView.setImageResource(getEmojiResourceIdByMsgType(message.getMessageType()));
            }

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListView) parent).performItemClick(v, position, 0);
                }
            });


            return vi;
        }
    }

    public static final int CAMERA_REQUEST = 55;
    public static final int VIDEO_REQUEST = 56;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == CAMERA_REQUEST) {
            Uri resultData = Uri.parse(data.getStringExtra("result"));
            sendMedia(MEDIA_IMAGE,resultData.toString(),0);

            File compressFile = new File(resultData.getPath());
            int file_size = Integer.parseInt(String.valueOf(compressFile.length()/1024));

            if (file_size > 80) {
                compressFile = Compressor.getDefault(getActivity()).compressToFile(compressFile);
            }


            PicUrlRequest picUrlRequest = new PicUrlRequest(getContext(),compressFile,MEDIA_IMAGE,this);
            picUrlRequest.executeRequest();
        }else if (resultCode == VIDEO_REQUEST) {
            Uri resultData = Uri.parse(data.getStringExtra("result"));
            sendMedia(MEDIA_VIDEO,resultData.toString(),0);

            PicUrlRequest picUrlRequest = new PicUrlRequest(getContext(),new File(resultData.getPath()),MEDIA_VIDEO,this);
            picUrlRequest.executeRequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CAMERA_CODE:
                if (grantResults.length > 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            }else {
                Toast.makeText(getContext(), R.string.permission_denied, Toast.LENGTH_LONG).show();
            }

            break;
            case REQUEST_AUDIO_CODE:
                if (!(grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(getContext(), R.string.permission_denied, Toast.LENGTH_LONG).show();
            }
            break;

        }
    }

    @Override
    public void PicUrlResponse(CommonRequest.ResponseCode responseCode, String picUrl,MediaType mediaType) {

        if (responseCode == COMMON_RES_SUCCESS) {
            switch (mediaType) {
                case MEDIA_IMAGE:sendMedia(MEDIA_IMAGE,picUrl,1);return;
                case MEDIA_VIDEO:sendMedia(MediaType.MEDIA_VIDEO,picUrl,1);return;
                case MEDIA_AUDIO:sendMedia(MediaType.MEDIA_AUDIO,picUrl,1);
            }

        }

    }


    RecyclerTouchListener recyclerTouchListener = new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
        @Override
        public void onClick(View view, int position) {
            if (view.getTag() != null && view.getTag().equals("vdo")){
                Message message = messages.get(position);

                Intent intent=new Intent(getActivity(), VideoPlay.class);
                intent.putExtra("url",message.getMediaURL());
                startActivity(intent);

                /*mProgressBar.setVisibility(View.VISIBLE);

//                if (videoView.uri
                videoView.setVideoURI(Uri.parse(message.getMediaURL()));
//                holder.videoView.setMediaController(new MediaController(context));
               videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mProgressBar.setVisibility(View.GONE);
                        videoView.start();
                    }
                });*/

            }
        }

        @Override
        public void onLongClick(View view, int position) {

        }
    });



    /*****************============= FOR Audio =======================***************/

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mAudioFileName = null;
    private static String mAudioPath = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
    private boolean isCanceled = false;
    ImageView recImage;


    boolean isStartRecording = false;
    public void initializationOfAudioObjects(View view) {

        // Record to the external cache directory for visibility
        mAudioPath = getActivity().getExternalCacheDir().getAbsolutePath();
        mAudioPath = Environment
                .getExternalStorageDirectory() + "/Localapp";
        File folder = null;
        String state = Environment.getExternalStorageState();
        if (state.contains(Environment.MEDIA_MOUNTED)) {
            folder = new File(Environment
                    .getExternalStorageDirectory() + "/Localapp");
        } else {
            folder = new File(Environment
                    .getExternalStorageDirectory() + "/Localapp");
        }

        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }


        recordPanel = view.findViewById(R.id.record_panel);
        recordTimeText = (TextView) view.findViewById(R.id.recording_time_text);
        recImage = (ImageView) view.findViewById(R.id.rec_img);
        slideText = view.findViewById(R.id.slideText);
        TextView textView = (TextView) view.findViewById(R.id.slideToCancelTextView);
        textView.setText("Slide to cancel");
    }

    View.OnTouchListener audioSendOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                sendImageViewBtn.setImageResource(R.drawable.ic_speak_pressed);
                recImage.setVisibility(View.VISIBLE);
                recordPanel.setVisibility(View.VISIBLE);
                linearLayoutMsgArea.setVisibility(View.INVISIBLE);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
                        .getLayoutParams();
                params.leftMargin = dp(30);
                slideText.setLayoutParams(params);
                ViewProxy.setAlpha(slideText, 1);
                startedDraggingX = -1;

                if (isAudioPermissionGranted()){
                    startRecording();
                    isStartRecording = true;
                    isCanceled = false;
                }else {
                    requestPermissions(AUDIO_PERMISSIONS, REQUEST_AUDIO_CODE);
                }


                sendImageViewBtn.getParent()
                        .requestDisallowInterceptTouchEvent(true);


            }else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                    || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                sendImageViewBtn.setImageResource(R.drawable.ic_speak);

                startedDraggingX = -1;

                if (isStartRecording) {
                     stopRecording();

                    isStartRecording = false;

                }

                recordPanel.setVisibility(View.INVISIBLE);
                linearLayoutMsgArea.setVisibility(View.VISIBLE);

            }else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                float x = motionEvent.getX();
                if (x < -distCanMove-180) {
                    if (isStartRecording) {
                        sendImageViewBtn.setImageResource(R.drawable.ic_speak);
                        isCanceled = true;
                         stopRecording();
                        isStartRecording = false;
                    }
                    recordPanel.setVisibility(View.INVISIBLE);
                    linearLayoutMsgArea.setVisibility(View.VISIBLE);
                }
                x = x + ViewProxy.getX(sendImageViewBtn);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
                        .getLayoutParams();

                if (startedDraggingX != -1) {
                    float dist = (x - startedDraggingX);
                    params.leftMargin = dp(30) + (int) dist;
                    slideText.setLayoutParams(params);
                    float alpha = 1.3f + dist / distCanMove;
                    if (alpha > 1) {
                        alpha = 1;
                    } else if (alpha < 0) {
                        alpha = 0;
                    }
                    ViewProxy.setAlpha(slideText, alpha);
                }

                if (x <= ViewProxy.getX(slideText) + slideText.getWidth()
                        + dp(30)) {
                    if (startedDraggingX == -1) {
                        startedDraggingX = x;
                        distCanMove = (recordPanel.getMeasuredWidth()
                                - slideText.getMeasuredWidth() - dp(20)) / 2.0f;
                        if (distCanMove <= 0) {
                            distCanMove = dp(200);
                        } else if (distCanMove > dp(200)) {
                            distCanMove = dp(80);
                        }
                    }
                }

                if (params.leftMargin > dp(30)) {
                    params.leftMargin = dp(30);
                    slideText.setLayoutParams(params);
                    ViewProxy.setAlpha(slideText, 1);
                    startedDraggingX = -1;
                }

                v.onTouchEvent(motionEvent);

            }
            return true;
        }
    };


    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mAudioFileName = mAudioPath + "/audio"+System.currentTimeMillis()+".3gp";
        mRecorder.setOutputFile(mAudioFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
        startRecord();
    }

    private void stopRecording() {
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            if (!isCanceled && !recordTimeText.getText().toString().equals("00:00")) {
                Uri resultData = Uri.parse(mAudioFileName);
                sendMedia(MEDIA_AUDIO, mAudioFileName, 0);
//                toast(resultData.toString());

                PicUrlRequest picUrlRequest = new PicUrlRequest(getContext(), new File(resultData.getPath()), MEDIA_AUDIO, this);
                picUrlRequest.executeRequest();
            }
        }catch (Exception e){
            e.printStackTrace();
            mRecorder.release();
            mRecorder = null;
        }
        stopRecord();


    }



    private void startRecord() {
        // TODO Auto-generated method stub
        startTime = SystemClock.uptimeMillis();
        timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 1000);
        vibrate();
    }

    private void stopRecord() {
        // TODO Auto-generated method stub
        if (timer != null) {
            timer.cancel();
        }
        if (recordTimeText.getText().toString().equals("00:00")) {
            vibrate();
            return;
        }
        recordTimeText.setText("00:00");
        vibrate();
    }

    public static int dp(float value) {
        return (int) Math.ceil(1 * value);
    }

    private void vibrate() {
        // TODO Auto-generated method stub
        try {
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            @SuppressLint("DefaultLocale") final String hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(updatedTime)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(updatedTime)),
                    TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(updatedTime)));
            final long lastsec = TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(updatedTime));
            System.out.println(lastsec + " hms " + hms);
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (recordTimeText != null) {
                            recordTimeText.setText(hms);

                            if (lastsec !=0 && lastsec%2==0) {
                                recImage.setVisibility(View.VISIBLE);
                            }else {
                                recImage.setVisibility(View.INVISIBLE);
                            }

                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(onKeyListener);
    }

    View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction()!= KeyEvent.ACTION_DOWN ) {
                HomeActivity.mViewPager.setCurrentItem(0);
                return true;
            }
            return false;
        }
    };

    public class CountDownWhisper extends CountDownTimer{
        private Message message;
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountDownWhisper(Message message,long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.message = message;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d("CountDownWhisper",message.getmText()+": "+millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            if (messages.contains(message)){
                messages.remove(message);
                adapter.notifyDataSetChanged();
            }
        }
    }


    private class CountDownTimerTask extends CountDownTimer {


        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountDownTimerTask(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d("CountDownTimerTask",": "+millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            if (AppController.isActivityVisible()) {
                request();
            }else {
                new CountDownTimerTask(5000,5000).start();
            }

        }
    }

    private int tipCount = 0;
    private void toolTips (View view) {
        overlayRL = (RelativeLayout) view.findViewById(R.id.rlOverlay);
        overlayVoiceLL = (LinearLayout) view.findViewById(R.id.rlVoice);
        overlayCamMediaLL = (LinearLayout) view.findViewById(R.id.rlCamMedia);
        textHelp = (TextView) view.findViewById(R.id.textHelp);

        overlayRL.setVisibility(View.VISIBLE);

        textHelp.setOnClickListener(toolTipClickListener);
        overlayRL.setOnClickListener(toolTipClickListener);
    }

    private View.OnClickListener toolTipClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (tipCount) {
                case 0:
                    overlayVoiceLL.setVisibility(View.GONE);
                    overlayCamMediaLL.setVisibility(View.VISIBLE);
                    textHelp.setText("Got It");
                    tipCount++;
                    break;
                default:
                    overlayRL.setVisibility(View.GONE);
                    AppPreferences.getInstance(AppController.getAppContext()).broadcastToolTipLaunched();
            }
        }
    };
}
