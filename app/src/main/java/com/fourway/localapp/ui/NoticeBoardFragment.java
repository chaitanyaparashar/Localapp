package com.fourway.localapp.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourway.localapp.R;
import com.fourway.localapp.data.NoticeBoardData;
import com.fourway.localapp.data.NoticeBoardMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeBoardFragment extends Fragment {

    private RecyclerView recyclerView, recyclerViewNearYou;
    private List<NoticeBoardData> noticeBoardList;
    private NoticeAdapter noticeAdapter;
    private NoticeAdapterNearYou noticeAdapterNearYou;

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

    private void setupView(View fView) {
        recyclerView = (RecyclerView) fView.findViewById(R.id.notice_board_recyclerView);
        recyclerViewNearYou = (RecyclerView) fView.findViewById(R.id.notice_board_near_you_recyclerView);

        noticeBoardList = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerViewNearYou.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,true));
        recyclerViewNearYou.setItemAnimator(new DefaultItemAnimator());

        dummyData();
        noticeAdapter = new NoticeAdapter(getContext(),noticeBoardList);
        noticeAdapterNearYou = new NoticeAdapterNearYou(getContext(),noticeBoardList);

        recyclerView.setAdapter(noticeAdapter);
        recyclerViewNearYou.setAdapter(noticeAdapterNearYou);



        noticeAdapter = new NoticeAdapter(getContext(),noticeBoardList);
    }

    //for testing
    void dummyData() {
        NoticeBoardData noticeBoardData = new NoticeBoardData();
        NoticeBoardMessage message = new NoticeBoardMessage();
        noticeBoardData.setName("My Notice");
        message.setMsg("this is a dummy message");
        message.setTimestamp("12th feb 2017 Sunday, 12:30 pm");
        List<NoticeBoardMessage> list = new ArrayList();
        list.add(message);
        noticeBoardData.setMessagesList(list);

        for (int i=0;i<20;i++) {

            noticeBoardList.add(noticeBoardData);
        }
    }


    class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
        private Context mContext;
        private List<NoticeBoardData> noticeBoardList;

        public NoticeAdapter(Context mContext, List<NoticeBoardData> noticeBoardList) {
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
            NoticeBoardData noticeBoard = noticeBoardList.get(position);
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
                noticeTime = (TextView) itemView.findViewById(R.id.notice_lastMsg_time_TextView);
                dotsButton = (ImageView) itemView.findViewById(R.id.notice_menu);
            }
        }
    }

    class NoticeAdapterNearYou extends RecyclerView.Adapter<NoticeAdapterNearYou.ViewHolder>{
        private Context mContext;
        private List<NoticeBoardData> noticeBoardList;

        public NoticeAdapterNearYou(Context mContext, List<NoticeBoardData> noticeBoardList) {
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
            NoticeBoardData noticeBoard = noticeBoardList.get(position);

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

}
