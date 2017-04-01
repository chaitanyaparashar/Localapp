package com.fourway.localapp.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourway.localapp.R;
import com.fourway.localapp.data.NoticeBoard;
import com.fourway.localapp.data.NoticeBoardMessage;
import com.fourway.localapp.util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeBoardFragment extends Fragment {

    private RecyclerView recyclerView, recyclerViewNearYou;
    private List<NoticeBoard> noticeBoardList;
    private NoticeAdapter noticeAdapter;
    private NoticeAdapterNearYou noticeAdapterNearYou;
    private FloatingActionButton noticeCreateFab;

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

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerViewNearYou.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        recyclerViewNearYou.setItemAnimator(new DefaultItemAnimator());

        dummyData();
        noticeAdapter = new NoticeAdapter(getContext(), noticeBoardList);
        noticeAdapterNearYou = new NoticeAdapterNearYou(getContext(), noticeBoardList);

        recyclerView.setAdapter(noticeAdapter);
        recyclerViewNearYou.setAdapter(noticeAdapterNearYou);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, recyclerItemClickListener));
        recyclerViewNearYou.addOnItemTouchListener(new RecyclerTouchListener(getContext(),recyclerViewNearYou,recyclerItemClickListener));

        noticeCreateFab = (FloatingActionButton) fView.findViewById(R.id.fab);
        noticeCreateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),CreateNoticeActivity.class));
            }
        });

    }

    /**
     * RecyclerView click Listener
     */
    public RecyclerTouchListener.ClickListener recyclerItemClickListener = new RecyclerTouchListener.ClickListener() {
        @Override
        public void onClick(View view, int position) {
            NoticeBoard noticeBoard = noticeBoardList.get(position);
            showNoticeBoardDialog(noticeBoard);
        }

        @Override
        public void onLongClick(View view, int position) {

        }
    };

    public void showNoticeBoardDialog(NoticeBoard noticeBoard) {

        String msg, title, btnText;
        DialogNoticeBoardMessageAdapter messageAdapter;

            msg = "Your location Settings is set to 'OFF'. \nPlease Enable Location to " +
                    "use this app";
            title = noticeBoard.getName();
            btnText = "Location Settings";

        View view = LayoutInflater.from(getContext()).inflate(R.layout.notice_board_dialog,null);
        TextView noticeName = (TextView)view.findViewById(R.id.notice_board_name_textView);
        RecyclerView messageRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewDialog);

        messageAdapter = new DialogNoticeBoardMessageAdapter(getContext(),noticeBoard);

        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        messageRecyclerView.setAdapter(messageAdapter);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
            NoticeBoardMessage message = noticeBoard.getMessagesList().get(size-1);

            holder.noticeName.setText(noticeBoard.getName());
            holder.noticeLastMsg.setText(message.getMsg());
            holder.noticeTime.setText(message.getTimestamp());
        }

        @Override
        public int getItemCount() {
            return noticeBoardList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView noticeName,noticeLastMsg,noticeTime;
            public ImageView dotsButton;

            public ViewHolder(View itemView) {
                super(itemView);
                noticeName = (TextView) itemView.findViewById(R.id.notice_name_TextView);
                noticeLastMsg = (TextView) itemView.findViewById(R.id.notice_lastMsg_TextView);
                noticeTime = (TextView) itemView.findViewById(R.id.notice_Msg_time_TextView);
                dotsButton = (ImageView) itemView.findViewById(R.id.notice_menu);
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

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView noticeName;
            public ViewHolder(View itemView) {
                super(itemView);
                noticeName = (TextView) itemView.findViewById(R.id.notice_name_nearyou_TextView);
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
        public void onBindViewHolder(ViewHolder holder, int position) {
            NoticeBoardMessage noticeBoardMessage = mNoticeBoard.getMessagesList().get(position);
            holder.noticeMessage.setText(noticeBoardMessage.getMsg());
            holder.timestamp.setText(noticeBoardMessage.getTimestamp());
        }

        @Override
        public int getItemCount() {
            return mNoticeBoard.getMessagesList().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView noticeMessage;
            public TextView timestamp;
            public ViewHolder(View itemView) {
                super(itemView);
                noticeMessage = (TextView) itemView.findViewById(R.id.notice_Msg_TextView);
                timestamp = (TextView) itemView.findViewById(R.id.notice_Msg_time_TextView);
            }
        }
    }

}
