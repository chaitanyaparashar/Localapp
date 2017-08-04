package com.localapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.localapp.R;
import com.localapp.models.NoticeBoard;
import com.localapp.ui.fragments.NoticeBoardFragment;

import java.util.List;

/**
 * Created by Vijay Kumar on 04-08-2017.
 */

public class NoticeBoardAdapterNearYou extends RecyclerView.Adapter<NoticeBoardAdapterNearYou.ViewHolder>{
    private Context mContext;
    private List<NoticeBoard> noticeBoardList;
    private NoticeBoardFragment noticeBoardFragment;

    public NoticeBoardAdapterNearYou(Context mContext, List<NoticeBoard> noticeBoardList, NoticeBoardFragment fragment) {
        this.mContext = mContext;
        this.noticeBoardList = noticeBoardList;
        this.noticeBoardFragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notice_near_you_card, parent, false);
        return new NoticeBoardAdapterNearYou.ViewHolder(itemView);
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
                noticeBoardFragment.requestForNoticeBoardMsg(noticeBoard, false);
            }catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }
    }
}
