package com.fourway.localapp.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.fourway.localapp.R;
import com.fourway.localapp.data.BroadcastRequestData;
import com.fourway.localapp.data.GetFeedRequestData;
import com.fourway.localapp.data.Message;
import com.fourway.localapp.request.BroadcastRequest;
import com.fourway.localapp.request.CommonRequest;
import com.fourway.localapp.request.GetFeedRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment implements BroadcastRequest.BroadcastResponseCallback, GetFeedRequest.GetFeedRequestCallback {

    //Recyclerview objects
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private GridView emojiGridView;

    EmojiconEditText chatText;
    ImageView sendImageViewBtn, camShoutImgBtn,emoticImgBtn;

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


        //Initializing recyclerview
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

        String[] en = {"k","k","k","k","k","k","k","k","k","k","k","k","k","k","k","k","k","k"};

        EmojiGridAdapter adapter1  =  new EmojiGridAdapter(getContext(),android.R.layout.simple_gallery_item,en);
        emojiGridView.setAdapter(adapter1);



        EmojIconActions  emojIcon=new EmojIconActions(getActivity(),view,chatText,emoticImgBtn);
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard,R.drawable.ic_smily);
        emojIcon.ShowEmojIcon();

        //test
        request();
        return view;
    }

    /**
     * TODO: For Testing
     */
    void request() {
        LatLng latLng = new LatLng(28.545544, 77.331020);
        GetFeedRequest feedRequest = new GetFeedRequest(getContext(), latLng, this);
        feedRequest.executeRequest();
    }


    View.OnClickListener sendTextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = chatText.getText().toString();
            if (!text.matches("")) {

                Message messageData = new Message();
                messageData.setToken("bieGrastGiOdkeoqusherCacmaw");
                messageData.setmText(text);

                messages.add(messageData);

                adapter.notifyDataSetChanged();
                chatText.setText("");
                scrollToBottom();
                BroadcastRequest broadcastRequest = new BroadcastRequest(getContext(), messageData, FeedFragment.this);
                broadcastRequest.executeRequest();

            }

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

    View.OnClickListener camaraClickListener = new View.OnClickListener() {
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
                camShoutImgBtn.setOnClickListener(camaraClickListener);
                emojiGridView.setVisibility(View.GONE);
            }else {
                sendImageViewBtn.setOnClickListener(sendTextClickListener);
                sendImageViewBtn.setImageResource(R.drawable.ic_send);
                camShoutImgBtn.setImageResource(R.drawable.ic_dropdown);
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

    }

    class EmojiGridAdapter extends ArrayAdapter {
        Context mContext;
        int[] emojiID = {R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher};
        public EmojiGridAdapter(Context context, int resource, String[] emoji_name) {
            super(context, resource, emoji_name);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.emoticon_grid_layout,null);
            ImageView imageView = (ImageView)view.findViewById(R.id.emoji_icon);
            imageView.setImageResource(R.drawable.ic_smily);

            return view;
        }
    }

}
