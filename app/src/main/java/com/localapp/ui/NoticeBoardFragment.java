package com.localapp.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.localapp.R;
import com.localapp.appcontroller.AppController;
import com.localapp.data.NoticeBoard;
import com.localapp.data.NoticeBoardMessage;
import com.localapp.feedback.AppPreferences;
import com.localapp.request.CommonRequest;
import com.localapp.request.DeleteNoticeBoardMessageRequest;
import com.localapp.request.DeleteNoticeBoardRequest;
import com.localapp.request.GetNearestNoticeBoardRequest;
import com.localapp.request.GetNoticeBoardMessageRequest;
import com.localapp.request.MyNoticeBoardRequest;
import com.localapp.request.PostNoticeBoardMessageRequest;
import com.localapp.request.SubscribeUnsubscribeNoticeBoardRequest;
import com.localapp.util.utility;

import java.util.ArrayList;
import java.util.List;

import static com.localapp.util.utility.isLocationAvailable;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeBoardFragment extends Fragment implements MyNoticeBoardRequest.MyNoticeBoardRequestCallback,
        GetNearestNoticeBoardRequest.GetNearestNoticeBoardRequestCallback, GetNoticeBoardMessageRequest.GetNoticeBoardMessageRequestCallback,
        PostNoticeBoardMessageRequest.PostNoticeBoardMessageResponseCallback,SubscribeUnsubscribeNoticeBoardRequest.SubscribeUnsubscribeNoticeBoardCallback,
        DeleteNoticeBoardRequest.DeleteNoticeBoardResponseCallback, DeleteNoticeBoardMessageRequest.DeleteNoticeBoardMessageResponseCallback{

    private static final int CREATE_NOTICE_BOARD_REQUEST_CODE = 101;
    private RecyclerView recyclerView, recyclerViewNearYou;
    private List<NoticeBoard> noticeBoardList;
    private List<NoticeBoard> nearestNoticeBoardList;
    private NoticeAdapter noticeAdapter;
    private NoticeAdapterNearYou noticeAdapterNearYou;
    DialogNoticeBoardMessageAdapter messageAdapter;
    private FloatingActionButton noticeCreateFab;
    RecyclerView noticeMessageRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    //******************************** tool tips *******************//

    private RelativeLayout overlayRL;
    private LinearLayout overlayFloatingLL;

    public NoticeBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fView = inflater.inflate(R.layout.fragment_notice_board, container, false);

        setupView(fView);
        if (!AppPreferences.getInstance(AppController.getAppContext()).isLaunchedNoticeboardToolTip()) {
            toolTips(fView);
        }
        return fView;
    }


    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        if (view != null) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(onKeyListener);
        }
    }

    /**
     * initialization of view objects
     * @param fView
     */
    private void setupView(View fView) {
        recyclerView = (RecyclerView) fView.findViewById(R.id.notice_board_recyclerView);
        recyclerViewNearYou = (RecyclerView) fView.findViewById(R.id.notice_board_near_you_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerViewNearYou.setHasFixedSize(true);

        noticeBoardList = new ArrayList<>();
        nearestNoticeBoardList = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerViewNearYou.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewNearYou.setItemAnimator(new DefaultItemAnimator());

//        dummyData();
        noticeAdapter = new NoticeAdapter(getContext(), noticeBoardList);
        noticeAdapterNearYou = new NoticeAdapterNearYou(getContext(), nearestNoticeBoardList);

        recyclerView.setAdapter(noticeAdapter);
        recyclerViewNearYou.setAdapter(noticeAdapterNearYou);


        noticeCreateFab = (FloatingActionButton) fView.findViewById(R.id.fab);
        noticeCreateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLocationAvailable(getContext())) {
                    startActivityForResult(new Intent(getContext(), CreateNoticeActivity.class),CREATE_NOTICE_BOARD_REQUEST_CODE);
                }


                if (overlayRL != null) {
                    overlayRL.setVisibility(View.GONE);
                }




            }
        });


        swipeRefreshLayout = (SwipeRefreshLayout) fView.findViewById(R.id.swipe_refresh_layout_notice_board);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#2196f3"));

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                requestForMyNoticeBoard();
            }
        });



    }


    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            requestForMyNoticeBoard();
        }
    };



    View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction()!= KeyEvent.ACTION_DOWN) {
                HomeActivity.mViewPager.setCurrentItem(0);
                return true;
            }
            return false;
        }
    };



    AlertDialog dialog;
    public void showNoticeBoardDialog(final NoticeBoard noticeBoard, final boolean hasSubscribed) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.notice_board_dialog,null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_notice_dialog);
        Button subButton = (Button) view.findViewById(R.id.subscribe_btn);

        if (HomeActivity.mUserId != null && HomeActivity.mUserId.equals(noticeBoard.getAdminId())) {
            subButton.setVisibility(View.GONE);
        }else {
            linearLayout.setVisibility(View.GONE);
        }




        TextView noticeName = (TextView)view.findViewById(R.id.notice_board_name_textView);
        noticeMessageRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewDialog);

        final EditText messageEditText = (EditText) view.findViewById(R.id._input_notice_message);
        final ImageButton postBtn = (ImageButton)  view.findViewById(R.id._notice_post_btn);

        noticeName.setText(noticeBoard.getName());


        if (hasSubscribed) {
            subButton.setText("Unsubscribe");
        }else {
            subButton.setText("Subscribe");
        }

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeActivity.mUserId !=null && !HomeActivity.mUserId.equals("")) {
                    CommonRequest.RequestType type;
                    if (hasSubscribed) {
                        type = CommonRequest.RequestType.COMMON_REQUEST_UNSUBSCRIBE_NOTICE_BOARD;
                    } else {
                        type = CommonRequest.RequestType.COMMON_REQUEST_SUBSCRIBE_NOTICE_BOARD;

                    }

                    requestSubscribeAndUnsub(noticeBoard, type);
                }else {
                    Toast.makeText(getContext(), "Please login first...", Toast.LENGTH_SHORT).show();
                }

                if (dialog!=null)
                    dialog.dismiss();
            }
        });



        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageEditText.getText().toString().trim();
                if (!msg.isEmpty()) {
                    NoticeBoardMessage message = new NoticeBoardMessage(msg);
                    message.setAdminId(noticeBoard.getId());
                    message.setTimestamp(""+System.currentTimeMillis());

                    PostNoticeBoardMessageRequest postNoticeBoardMessageRequest = new PostNoticeBoardMessageRequest(getContext(),message,NoticeBoardFragment.this);
                    postNoticeBoardMessageRequest.executeRequest();
                    messageEditText.setText("");
                }
            }
        });



        messageAdapter = new DialogNoticeBoardMessageAdapter(getContext(),noticeBoard);

        noticeMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noticeMessageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        noticeMessageRecyclerView.setAdapter(messageAdapter);



//        dialog.setCancelable(false);
        builder.setView(view);
        dialog = builder.create();

        dialog.show();
    }

    //for testing
    void dummyData() {
        NoticeBoard noticeBoard = new NoticeBoard();
        NoticeBoardMessage message = new NoticeBoardMessage();
        noticeBoard.setName("My Notice");
        message.setMsg("this is a dummy message");
        message.setTimestamp("12th feb 2017 Sunday, 12:30 pm");
        List<NoticeBoardMessage> list = new ArrayList();
        list.add(message);
        noticeBoard.setMessagesList(list);

        for (int i=0;i<20;i++) {

            noticeBoardList.add(noticeBoard);
            message.setMsg("this is a dummy message "+(i));
            list.add(message);
            noticeBoard.setMessagesList(list);
        }
    }

    private void requestForMyNoticeBoard() {
        if (HomeActivity.mUserId != null && !HomeActivity.mUserId.equals("")) {
            MyNoticeBoardRequest noticeBoardRequest = new MyNoticeBoardRequest(getContext(), HomeActivity.mUserId, this);
            noticeBoardRequest.executeRequest();
            swipeRefreshLayout.setRefreshing(true);
        }else {
            requestForNearbyNoticeBoard();
        }
    }

    private void requestForNearbyNoticeBoard() {
        if (HomeActivity.mLastKnownLocation != null) {
            GetNearestNoticeBoardRequest nearestNoticeBoardRequest = new GetNearestNoticeBoardRequest(getContext(), this, HomeActivity.mLastKnownLocation);
            nearestNoticeBoardRequest.executeRequest();
        }else {
            swipeRefreshLayout.setRefreshing(false);
            new CountDownTimerTask(5000,5000).start();
        }
    }

    private void requestForNoticeBoardMsg(NoticeBoard mNoticeBoard,boolean hasSubscribed) {
        GetNoticeBoardMessageRequest getNoticeBoardMessageRequest = new GetNoticeBoardMessageRequest(getContext(),mNoticeBoard, hasSubscribed,this);
        getNoticeBoardMessageRequest.executeRequest();
    }

    private void requestSubscribeAndUnsub(NoticeBoard mNoticeBoard, CommonRequest.RequestType requestType) {
        SubscribeUnsubscribeNoticeBoardRequest request = new SubscribeUnsubscribeNoticeBoardRequest(getContext(),mNoticeBoard.getId(),HomeActivity.mUserId,requestType,NoticeBoardFragment.this);
        request.executeRequest();
    }

    private void requestDeleteNoticeBoard (NoticeBoard mNoticeBoard) {
        DeleteNoticeBoardRequest request = new DeleteNoticeBoardRequest(getContext(),mNoticeBoard,this);
        request.executeRequest();
    }

    private void requestDeleteNoticeBoardMessage(NoticeBoardMessage mNoticeBoardMessage, String uToken) {
        DeleteNoticeBoardMessageRequest request = new DeleteNoticeBoardMessageRequest(getContext(),mNoticeBoardMessage, uToken,this);
        request.executeRequest();
    }



    @Override
    public void MyNoticeBoardResponse(CommonRequest.ResponseCode responseCode, List<NoticeBoard> myNoticeBoards, List<NoticeBoard> subscribedNoticeBoardList) {
        swipeRefreshLayout.setRefreshing(false);
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            if (noticeBoardList.size()>0) {
                noticeBoardList.clear();
                noticeAdapter.notifyDataSetChanged();
            }

            if (myNoticeBoards.size() != 0){
                noticeBoardList.addAll(myNoticeBoards);
            }

            if (subscribedNoticeBoardList.size() != 0) {
                noticeBoardList.addAll(subscribedNoticeBoardList);
            }

            noticeAdapter.notifyDataSetChanged();

            requestForNearbyNoticeBoard();
        }
    }

    @Override
    public void GetNearestNoticeBoardResponse(CommonRequest.ResponseCode responseCode, List<NoticeBoard> mNoticeBoards) {
        swipeRefreshLayout.setRefreshing(false);
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            if (nearestNoticeBoardList.size()>0) {
                nearestNoticeBoardList.clear();
            }

            if (mNoticeBoards.size() != 0){
                nearestNoticeBoardList.addAll(mNoticeBoards);
                noticeAdapterNearYou.notifyDataSetChanged();
            }

            noticeAdapterNearYou.notifyDataSetChanged();
        }
    }

    @Override
    public void GetNoticeBoardMessageResponse(CommonRequest.ResponseCode responseCode, NoticeBoard mNoticeBoard,boolean hasSubscribed) {
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {

            if (dialog != null && dialog.isShowing()) {
                messageAdapter.setNoticeBoard(mNoticeBoard);
                messageAdapter.notifyDataSetChanged();
                noticeMessageRecyclerView.scrollToPosition(mNoticeBoard.getMessagesList().size() -1);
            }else {
                showNoticeBoardDialog(mNoticeBoard, hasSubscribed);
            }
        }
    }

    @Override
    public void PostNoticeBoardResponse(CommonRequest.ResponseCode res, NoticeBoardMessage mNoticeBoardMessage) {
        if (res == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            Toast.makeText(getContext(), "msg post", Toast.LENGTH_SHORT).show();
            requestForMyNoticeBoard();
            NoticeBoard tempNoticeBoard = new NoticeBoard(mNoticeBoardMessage.getAdminId());
            tempNoticeBoard.setAdminId(HomeActivity.mUserId);
            requestForNoticeBoardMsg(tempNoticeBoard,false);

        }

    }

    @Override
    public void SubscribeUnsubscribeNoticeBoardResponse(CommonRequest.ResponseCode responseCode, CommonRequest.RequestType mRequestType, String errorMsg) {
        String req = "subscribed";
        if (mRequestType == CommonRequest.RequestType.COMMON_REQUEST_SUBSCRIBE_NOTICE_BOARD) {
            req = "subscribed";
        }else {
            req = "unsubscribed";
        }
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            Toast.makeText(getContext(), "Noticeboard " + req , Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(true);
            requestForMyNoticeBoard();
        }else if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE) {
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteNoticeBoardResponse(CommonRequest.ResponseCode responseCode) {
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            toast("Notice Board deleted");
            swipeRefreshLayout.setRefreshing(true);
            requestForMyNoticeBoard();
        }

    }

    @Override
    public void deleteNoticeBoardMessageResponse(CommonRequest.ResponseCode responseCode) {
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            toast("Message deleted");
            requestForMyNoticeBoard();
        }
    }


    void toast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_NOTICE_BOARD_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            swipeRefreshLayout.setRefreshing(true);
            requestForMyNoticeBoard();
        }
    }

    /**
     * Adapters
     */
    class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
        private Context mContext;
        private List<NoticeBoard> noticeBoardList;

        public NoticeAdapter(Context mContext, List<NoticeBoard> noticeBoardList) {
            this.mContext = mContext;
            this.noticeBoardList = noticeBoardList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notic_card, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            NoticeBoard noticeBoard = noticeBoardList.get(position);
            int size = noticeBoard.getMessagesList().size();
            if (size>0) {
                NoticeBoardMessage message = noticeBoard.getMessagesList().get(size-1);
                if (message.getId() != null) {
                    holder.noticeLastMsg.setText(message.getMsg());
                    holder.noticeTime.setText(utility.getTimeAndDate(message.getTimestamp()));
                }else {
                    holder.noticeLastMsg.setText("");
                    holder.noticeTime.setText("");
                }
            }else {
                holder.noticeLastMsg.setText("");
                holder.noticeTime.setText("");
            }


            holder.noticeName.setText(noticeBoard.getName());

        }

        @Override
        public int getItemCount() {
            return noticeBoardList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView noticeName,noticeLastMsg,noticeTime;
            public ImageView dotsButton;

            public ViewHolder(View itemView) {
                super(itemView);
                noticeName = (TextView) itemView.findViewById(R.id.notice_name_TextView);
                noticeLastMsg = (TextView) itemView.findViewById(R.id.notice_lastMsg_TextView);
                noticeTime = (TextView) itemView.findViewById(R.id.notice_Msg_time_TextView);
                dotsButton = (ImageView) itemView.findViewById(R.id.notice_menu);
                dotsButton.setOnClickListener(this);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();

                if (v.getId() == R.id.notice_menu) {
                    showPopupMenu(v,position);
                }else {
                    NoticeBoard noticeBoard = noticeBoardList.get(position);
                    requestForNoticeBoardMsg(noticeBoard, true);
                }
            }
        }



        /**
         * Showing popup menu when tapping on 3 dots
         */
        private void showPopupMenu(View view,int position) {

            int menuId;

            NoticeBoard noticeBoard = noticeBoardList.get(position);

            if (HomeActivity.mUserId !=null && HomeActivity.mUserId.equals(noticeBoard.getAdminId())){
                menuId = R.menu.menu_my_notice;
            }else {
                menuId = R.menu.menu_subscribe_notice;
            }

            // inflate menu
            PopupMenu popup = new PopupMenu(mContext, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(menuId, popup.getMenu());
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
            popup.show();
        }

        /**
         * Click listener for popup menu items
         */
        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
            int adapterPosition;

            public MyMenuItemClickListener(int position) {
                adapterPosition = position;
            }

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.action_delete:
                        requestDeleteNoticeBoard(noticeBoardList.get(adapterPosition));
                        return true;
                    case R.id.action_unsubscribe:
                        requestSubscribeAndUnsub(noticeBoardList.get(adapterPosition), CommonRequest.RequestType.COMMON_REQUEST_UNSUBSCRIBE_NOTICE_BOARD);
                        return true;
                    default:
                }
                return false;
            }
        }



    }

    class NoticeAdapterNearYou extends RecyclerView.Adapter<NoticeAdapterNearYou.ViewHolder>{
        private Context mContext;
        private List<NoticeBoard> noticeBoardList;

        public NoticeAdapterNearYou(Context mContext, List<NoticeBoard> noticeBoardList) {
            this.mContext = mContext;
            this.noticeBoardList = noticeBoardList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notice_near_you_card, parent, false);
            return new NoticeAdapterNearYou.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            NoticeBoard noticeBoard = noticeBoardList.get(position);

            holder.noticeName.setText(noticeBoard.getName());

        }

        @Override
        public int getItemCount() {
            return noticeBoardList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView noticeName;
            public ViewHolder(View itemView) {
                super(itemView);
                noticeName = (TextView) itemView.findViewById(R.id.notice_name_nearyou_TextView);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                try {
                    NoticeBoard noticeBoard = noticeBoardList.get(getAdapterPosition());
                    requestForNoticeBoardMsg(noticeBoard, false);
                }catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class DialogNoticeBoardMessageAdapter extends RecyclerView.Adapter<DialogNoticeBoardMessageAdapter.ViewHolder>{
        private Context mContext;
        private NoticeBoard mNoticeBoard;

        public DialogNoticeBoardMessageAdapter(Context mContext, NoticeBoard mNoticeBoard) {
            this.mContext = mContext;
            this.mNoticeBoard = mNoticeBoard;
        }

        public void setNoticeBoard(NoticeBoard mNoticeBoard){
            this.mNoticeBoard = mNoticeBoard;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.noticeboard_message_card, parent, false);
            return new DialogNoticeBoardMessageAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final NoticeBoardMessage noticeBoardMessage = mNoticeBoard.getMessagesList().get(position);
            holder.noticeMessage.setText(noticeBoardMessage.getMsg());
            holder.timestamp.setText(utility.getTimeAndDate(noticeBoardMessage.getTimestamp()));

            if (HomeActivity.mUserId!=null && HomeActivity.mUserId.equals(mNoticeBoard.getAdminId())) {
                holder.deleteImageView.setVisibility(View.VISIBLE);
            }

            holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HomeActivity.mLoginToken != null && !HomeActivity.mLoginToken.equals("")) {
                        requestDeleteNoticeBoardMessage(noticeBoardMessage, HomeActivity.mLoginToken);
                        mNoticeBoard.getMessagesList().remove(position);
                        messageAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mNoticeBoard.getMessagesList().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView noticeMessage;
            public TextView timestamp;
            public ImageView deleteImageView;
            public ViewHolder(View itemView) {
                super(itemView);
                noticeMessage = (TextView) itemView.findViewById(R.id.notice_Msg_TextView);
                timestamp = (TextView) itemView.findViewById(R.id.notice_Msg_time_TextView);
                deleteImageView = (ImageView) itemView.findViewById(R.id.msg_delete);
                deleteImageView.setVisibility(View.GONE);
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
            if(AppController.isActivityVisible()) {
                requestForMyNoticeBoard();
            }else {
                new CountDownTimerTask(5000, 5000).start();
            }
        }
    }

    private void toolTips (View view) {
        overlayRL = (RelativeLayout) view.findViewById(R.id.rlOverlay);
        overlayFloatingLL = (LinearLayout) view.findViewById(R.id.rlFloating);

        overlayRL.setVisibility(View.VISIBLE);

        overlayRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayRL.setVisibility(View.GONE);
                AppPreferences.getInstance(AppController.getAppContext()).noticeboardToolTipLaunched();
            }
        });
    }

}
