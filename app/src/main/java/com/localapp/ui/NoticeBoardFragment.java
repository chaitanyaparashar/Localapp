package com.localapp.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.localapp.R;
import com.localapp.data.NoticeBoard;
import com.localapp.data.NoticeBoardMessage;
import com.localapp.request.CommonRequest;
import com.localapp.request.DeleteNoticeBoardMessageRequest;
import com.localapp.request.DeleteNoticeBoardRequest;
import com.localapp.request.GetNearestNoticeBoardRequest;
import com.localapp.request.GetNoticeBoardMessageRequest;
import com.localapp.request.MyNoticeBoardRequest;
import com.localapp.request.PostNoticeBoardMessageRequest;
import com.localapp.request.SubscribeUnsubscribeNoticeBoardRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeBoardFragment extends Fragment implements MyNoticeBoardRequest.MyNoticeBoardRequestCallback,
        GetNearestNoticeBoardRequest.GetNearestNoticeBoardRequestCallback, GetNoticeBoardMessageRequest.GetNoticeBoardMessageRequestCallback,
        PostNoticeBoardMessageRequest.PostNoticeBoardMessageResponseCallback,SubscribeUnsubscribeNoticeBoardRequest.SubscribeUnsubscribeNoticeBoardCallback,
        DeleteNoticeBoardRequest.DeleteNoticeBoardResponseCallback, DeleteNoticeBoardMessageRequest.DeleteNoticeBoardMessageResponseCallback{

    private RecyclerView recyclerView, recyclerViewNearYou;
    private List<NoticeBoard> noticeBoardList;
    private List<NoticeBoard> nearestNoticeBoardList;
    private NoticeAdapter noticeAdapter;
    private NoticeAdapterNearYou noticeAdapterNearYou;
    DialogNoticeBoardMessageAdapter messageAdapter;
    private FloatingActionButton noticeCreateFab;

    private SwipeRefreshLayout swipeRefreshLayout;

    public NoticeBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fView = inflater.inflate(R.layout.fragment_notice_board, container, false);

        setupView(fView);
        return fView;
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


        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, recyclerItemClickListener));
        recyclerViewNearYou.addOnItemTouchListener(new RecyclerTouchListener(getContext(),recyclerViewNearYou,recyclerItemClickListener));*/

        noticeCreateFab = (FloatingActionButton) fView.findViewById(R.id.fab);
        noticeCreateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeActivity.mUserId !=null && !HomeActivity.mUserId.equals("")) {
                    startActivity(new Intent(getContext(), CreateNoticeActivity.class));
                }else {
                    Toast.makeText(getContext(), "Please login first...", Toast.LENGTH_SHORT).show();
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



    public void showNoticeBoardDialog(final NoticeBoard noticeBoard, final boolean hasSubscribed) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.notice_board_dialog,null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_notice_dialog);
        Button subButton = (Button) view.findViewById(R.id.subscribe_btn);

        if (HomeActivity.mUserId != null && HomeActivity.mUserId.equals(noticeBoard.getAdminId())) {
            subButton.setVisibility(View.GONE);
        }else {
            linearLayout.setVisibility(View.GONE);
        }




        TextView noticeName = (TextView)view.findViewById(R.id.notice_board_name_textView);
        RecyclerView messageRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewDialog);

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
                        noticeBoardList.remove(noticeBoardList.indexOf(noticeBoard));
                        noticeAdapter.notifyDataSetChanged();
                    } else {
                        type = CommonRequest.RequestType.COMMON_REQUEST_SUBSCRIBE_NOTICE_BOARD;
                        if (!noticeBoardList.contains(noticeBoard)) {
                            noticeBoardList.add(noticeBoard);
                            noticeAdapter.notifyDataSetChanged();
                        }
                    }

                    requestSubscribeAndUnsub(noticeBoard, type);
                }else {
                    Toast.makeText(getContext(), "Please login first...", Toast.LENGTH_SHORT).show();
                }

            }
        });



        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageEditText.getText().toString().trim();
                if (!msg.isEmpty()) {
                    NoticeBoardMessage message = new NoticeBoardMessage(msg);
                    message.setAdminId(noticeBoard.getId());

                    PostNoticeBoardMessageRequest postNoticeBoardMessageRequest = new PostNoticeBoardMessageRequest(getContext(),message,NoticeBoardFragment.this);
                    postNoticeBoardMessageRequest.executeRequest();
                    noticeBoard.getMessagesList().add(message);
                    messageAdapter.notifyDataSetChanged();
                    messageEditText.setText("");
                }
            }
        });



        messageAdapter = new DialogNoticeBoardMessageAdapter(getContext(),noticeBoard);

        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        messageRecyclerView.setAdapter(messageAdapter);


//        dialog.setCancelable(false);
        dialog.setView(view);

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
        MyNoticeBoardRequest noticeBoardRequest = new MyNoticeBoardRequest(getContext(),HomeActivity.mUserId,this);
        noticeBoardRequest.executeRequest();
    }

    private void requestForNearbyNoticeBoard() {
        if (HomeActivity.mLastKnownLocation != null) {
            GetNearestNoticeBoardRequest nearestNoticeBoardRequest = new GetNearestNoticeBoardRequest(getContext(), this, HomeActivity.mLastKnownLocation);
            nearestNoticeBoardRequest.executeRequest();
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

    private void requestDeleteNoticeBoardMessage(NoticeBoardMessage mNoticeBoardMessage) {
        DeleteNoticeBoardMessageRequest request = new DeleteNoticeBoardMessageRequest(getContext(),mNoticeBoardMessage,this);
        request.executeRequest();
    }



    @Override
    public void MyNoticeBoardResponse(CommonRequest.ResponseCode responseCode, List<NoticeBoard> myNoticeBoards, List<NoticeBoard> subscribedNoticeBoardList) {
        swipeRefreshLayout.setRefreshing(false);
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            if (noticeBoardList.size()>0) {
                noticeBoardList.clear();
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
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            if (nearestNoticeBoardList.size()>0) {
                nearestNoticeBoardList.clear();
            }

            if (mNoticeBoards.size() != 0){
                nearestNoticeBoardList.addAll(mNoticeBoards);
                noticeAdapterNearYou.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void GetNoticeBoardMessageResponse(CommonRequest.ResponseCode responseCode, NoticeBoard mNoticeBoard,boolean hasSubscribed) {

        showNoticeBoardDialog(mNoticeBoard,hasSubscribed);
    }

    @Override
    public void PostNoticeBoardResponse(CommonRequest.ResponseCode res, NoticeBoardMessage mNoticeBoardMessage) {
        if (res == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            Toast.makeText(getContext(), "msg post", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void SubscribeUnsubscribeNoticeBoardResponse(CommonRequest.ResponseCode responseCode) {
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteNoticeBoardResponse(CommonRequest.ResponseCode responseCode) {
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            tost("Notice Board delete");
        }

    }

    @Override
    public void deleteNoticeBoardMessageResponse(CommonRequest.ResponseCode responseCode) {
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            tost("Message delete");
        }
    }


    void tost (String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
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
                holder.noticeLastMsg.setText(message.getMsg());
                holder.noticeTime.setText(message.getTimestamp());
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
                        noticeBoardList.remove(adapterPosition);
                        noticeAdapter.notifyDataSetChanged();
                        return true;
                    case R.id.action_unsubscribe:
                        requestSubscribeAndUnsub(noticeBoardList.get(adapterPosition), CommonRequest.RequestType.COMMON_REQUEST_UNSUBSCRIBE_NOTICE_BOARD);
                        noticeBoardList.remove(adapterPosition);
                        noticeAdapter.notifyDataSetChanged();
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
                NoticeBoard noticeBoard = noticeBoardList.get(getAdapterPosition());
                requestForNoticeBoardMsg(noticeBoard, false);
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
            holder.timestamp.setText(noticeBoardMessage.getTimestamp());

            if (HomeActivity.mUserId!=null && HomeActivity.mUserId.equals(mNoticeBoard.getAdminId())) {
                holder.deleteImageView.setVisibility(View.VISIBLE);
            }

            holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestDeleteNoticeBoardMessage(noticeBoardMessage);
                    mNoticeBoard.getMessagesList().remove(position);
                    messageAdapter.notifyDataSetChanged();
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

}
