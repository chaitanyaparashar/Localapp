package com.fourway.localapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fourway.localapp.R;
import com.fourway.localapp.data.GetFeedRequestData;
import com.fourway.localapp.data.Message;
import com.fourway.localapp.request.BroadcastRequest;
import com.fourway.localapp.request.CommonRequest;
import com.fourway.localapp.request.GetFeedRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment implements BroadcastRequest.BroadcastResponseCallback, GetFeedRequest.GetFeedRequestCallback {

    private final String TAG = "FeedFragment";
//    private static final String sAddress = "tcp://ec2-52-53-110-212.us-west-1.compute.amazonaws.com:1883";
    //private final String sAddress = "tcp://192.172.3.78:1883";
    private final String sAddress = "tcp://192.172.3.23:2883";
//    private static final String mTopic = "localapp";
    private static final String mTopic = "vijay";

    private MQTT mqtt = null;
    private ProgressDialog progressDialog = null;
    FutureConnection connection = null;
    private Map<String, String> mParams;

    //Recyclerview objects
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private GridView emojiGridView;

    EmojiconEditText chatText;
    ImageView sendImageViewBtn, camShoutImgBtn,emoticImgBtn;
    public static int selectedMessageTypeInt = 0;
    public static int selectedEmojiResourceID = R.mipmap.ic_launcher;
    public final String[]  emoji_name = {"Straight","Shout","Whisper","Gossip","Murmur","Mumble","Emergency"};
    public static int[] emojiResourceID = {R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher};


    //ArrayList of messages to store the thread messages
    private ArrayList<Message> messages;


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


        //Initializing recyclerView
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#2196f3"));
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        emojiGridView = (GridView) view.findViewById(R.id.shout_emiji);
        chatText = (EmojiconEditText) view.findViewById(R.id.chat_text);
        camShoutImgBtn = (ImageView) view.findViewById(R.id.btn_cam_shout);
        sendImageViewBtn = (ImageView) view.findViewById(R.id.btn_send_speak);
        emoticImgBtn = (ImageView) view.findViewById(R.id.btn_emoticon);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        emoticImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        recyclerView.setLayoutManager(layoutManager);
        sendImageViewBtn.setOnClickListener(sendVoiceClickListener);
        chatText.addTextChangedListener(textWatcher);
        // Inflate the layout for this fragment

        //Initializing message arraylist
                messages = new ArrayList<>();
        adapter = new ThreadAdapter(getContext(),messages,"bieGrastGiOdkeoqusherCacmaw");//hardcoded token
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
                swipeRefreshLayout.setRefreshing(true);
                request();
            }
        });

        connectMqtt();

        //calcultae distance
        String dis = calcDistance(new LatLng(28.550123, 77.326640),new LatLng(28.550189, 77.353430),"m",true);
        toast(dis);

//
//
        return view;
    }


    // callback used for Future
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
                getActivity().runOnUiThread(new Runnable(){
                    public void run() {
                        original.onFailure(error);
                    }
                });
            }
        };
    }

    private void connectMqtt() {
        mqtt = new MQTT();


        mqtt.setClientId(HomeActivity.mLoginToken);
        try
        {
            mqtt.setHost(sAddress);
            Log.d(TAG, "Address set: " + sAddress);
        }
        catch(URISyntaxException urise)
        {
            Log.e(TAG, "URISyntaxException connecting to " + sAddress + " - " + urise);
        }



        connection = mqtt.futureConnection();
        progressDialog = ProgressDialog.show(getContext(), "",
                "Connecting...", true);
        connection.connect().then(onui(new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                progressDialog.dismiss();
//                connectButton.setEnabled(false);
//                sendButton.setEnabled(true);
                toast("Connected");
                subscribe();//subscribed
            }

            @Override
            public void onFailure(Throwable value) {
                toast("Problem connecting to host");
                Log.e(TAG, "Exception connecting to " + sAddress + " - " + value);
                progressDialog.dismiss();
//                connectButton.setEnabled(true);
//                sendButton.setEnabled(false);
            }
        }));



    }

    @Override
    public void onStop() {
        super.onStop();
        connection.disconnect();
        toast("Disconnecting...");
    }

    private void subscribe() {
        Topic[] topics = {new Topic(mTopic, QoS.AT_LEAST_ONCE)};
        connection.subscribe(topics).then(onui(new Callback<byte[]>() {
            @Override
            public void onSuccess(byte[] value) {
                connection.receive().then(onui(new Callback<org.fusesource.mqtt.client.Message>() {
                    @Override
                    public void onSuccess(org.fusesource.mqtt.client.Message message) {
                        String receivedMesageTopic = message.getTopic();
                        byte[] payload = message.getPayload();
                        String messagePayLoad = new String(payload);
                        message.ack();
                        JSONObject jsonObject = null;
                        Message messageData = new Message();
                        try {
                            jsonObject = new JSONObject(messagePayLoad);
                            messageData.setToken(jsonObject.getString("userId"));
                            messageData.setmText(jsonObject.getString("msg"));
                            messageData.setTimeStamp(jsonObject.getString("timestamp"));
                            messageData.setMessageType(getMessageType(jsonObject.getInt("messageType")));
                            JSONArray latlngJsonArray = jsonObject.getJSONArray("longlat");
                            messageData.setmLatLng(new LatLng(Double.valueOf(latlngJsonArray.getString(0)),Double.valueOf(latlngJsonArray.getString(1))));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (!HomeActivity.mLoginToken.equals(messageData.getToken()) &&
                                isMessageForMe(messageData.getMessageType(),messageData.getmLatLng())) {
                            messages.add(messageData);
                            adapter.notifyDataSetChanged();
                            scrollToBottom();
                        }
                        subscribe();
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

    /**
     * TODO: For Testing
     */
    void request() {
        LatLng latLng = new LatLng(28.545544, 77.331020);
        GetFeedRequest feedRequest = new GetFeedRequest(getContext(), latLng, this);
        feedRequest.executeRequest();
    }



    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            request();
        }
    };


    View.OnClickListener sendTextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = chatText.getText().toString();
            if (!text.matches("")) {

                Message messageData = new Message();
                messageData.setToken("58c902bbf81fde0d49d7ff5e");
//                messageData.setToken(HomeActivity.mLoginToken);
                messageData.setMessageType(MessageType.STRAIGHT);
                messageData.setTimeStamp(String.valueOf(System.currentTimeMillis()/1000));
                messageData.setmText(text);
                messageData.setmLatLng(HomeActivity.mLastKnownLocation);

                messages.add(messageData);
                adapter.notifyDataSetChanged();
                chatText.setText("");
                scrollToBottom();
                /*BroadcastRequest broadcastRequest = new BroadcastRequest(getContext(), messageData, FeedFragment.this);
                broadcastRequest.executeRequest();*/

                /*****/
                /*Drawable d = getResources().getDrawable(R.drawable.aaa); // the drawable (Captain Obvious, to the rescue!!!)
                Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();*/
                /******/

                mParams =  new HashMap<>();
                mParams.put("userId",messageData.getToken());
                mParams.put("msg",messageData.getmText());
                mParams.put("timestamp",messageData.getTimeStamp());
//                mParams.put("img",bitmap);
                mParams.put("messageType",""+selectedMessageTypeInt);
                String[] latlng = {""+messageData.getmLatLng().latitude,""+messageData.getmLatLng().longitude};
                mParams.put("longlat",Arrays.toString(latlng));
                if (connection.isConnected()) {
                    connection.publish(mTopic, mParams.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
                }else {
                    connectMqtt();
                    connection.publish(mTopic, mParams.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
                }

            }

        }
    };

    AdapterView.OnItemClickListener selectMsgTypeEmojiListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedMessageTypeInt = position;
            selectedEmojiResourceID = emojiResourceID[position];
            emojiGridView.setVisibility(View.GONE);
        }
    };

    View.OnClickListener sendVoiceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "Voice", Toast.LENGTH_SHORT).show();
        }
    };

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

        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(chatText.getText())) {
                sendImageViewBtn.setOnClickListener(sendVoiceClickListener);
                sendImageViewBtn.setImageResource(R.drawable.ic_speak);
                camShoutImgBtn.setImageResource(R.drawable.ic_camera);
                camShoutImgBtn.setOnClickListener(cameraClickListener);
                emojiGridView.setVisibility(View.GONE);
            }else {
                sendImageViewBtn.setOnClickListener(sendTextClickListener);
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


    @Override
    public void onBroadcastResponce(CommonRequest.ResponseCode res) {

    }

    @Override
    public void GetFeedResponse(CommonRequest.ResponseCode responseCode, GetFeedRequestData data) {
        switch (responseCode) {
            case COMMON_RES_SUCCESS:
                if (messages.size()>0) {
                    messages.clear();
                }
                messages.addAll(data.getMessageList());
                adapter.notifyDataSetChanged();
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
            imageView.setImageResource(R.drawable.ic_smily);

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
    public MessageType getMessageType(int type) {
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
                if (distance <= 3)
                return true;
                break;
            case SHOUT:
                if (distance <= 5)
                    return true;
                break;
            case WHISPER:
                if (distance <= 2)
                    return true;
                break;
            case GOSSIP:
                if (distance <= 1)
                    return true;
                break;
            case MURMUR:
                if (distance <= 0.5)
                    return true;
                break;
            case MUMBLE:
                if (distance <= 0.3)
                    return true;
                break;
            case EMERGENCY:
                if (distance <= 5)
                    return true;
                break;
        }
        return false;
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



}
