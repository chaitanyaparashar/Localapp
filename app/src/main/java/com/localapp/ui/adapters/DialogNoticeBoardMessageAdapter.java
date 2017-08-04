package com.localapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.localapp.R;
import com.localapp.models.NoticeBoard;
import com.localapp.models.NoticeBoardMessage;
import com.localapp.ui.activities.HomeActivity;
import com.localapp.ui.fragments.NoticeBoardFragment;
import com.localapp.utils.Utility;

/**
 * Created by Vijay Kumar on 04-08-2017.
 */

public class DialogNoticeBoardMessageAdapter extends RecyclerView.Adapter<DialogNoticeBoardMessageAdapter.ViewHolder> {
    private Context mContext;
    private NoticeBoard mNoticeBoard;
    private OnMessageClickListener onMessageClickListener;

    public DialogNoticeBoardMessageAdapter(Context mContext, NoticeBoard mNoticeBoard) {
        this.mContext = mContext;
        this.mNoticeBoard = mNoticeBoard;
    }

    public DialogNoticeBoardMessageAdapter(Context mContext, NoticeBoard mNoticeBoard, OnMessageClickListener messageClickListener) {
        this.mContext = mContext;
        this.mNoticeBoard = mNoticeBoard;
        this.onMessageClickListener = messageClickListener;
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
    public void onBindViewHolder(DialogNoticeBoardMessageAdapter.ViewHolder holder, final int position) {
        final NoticeBoardMessage noticeBoardMessage = mNoticeBoard.getMessagesList().get(position);
        holder.noticeMessage.setText(noticeBoardMessage.getMsg());
        holder.timestamp.setText(Utility.getTimeAndDate(noticeBoardMessage.getTimestamp()));

        if (HomeActivity.mUserId != null && HomeActivity.mUserId.equals(mNoticeBoard.getAdminId())) {
            holder.deleteImageView.setVisibility(View.VISIBLE);
        }

        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMessageClickListener != null) {
                    onMessageClickListener.onClick(v,position);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onMessageClickListener != null) {
                    onMessageClickListener.onClick(v,position);
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


    public interface OnMessageClickListener {
        void onClick(View view, int position);
    }


}
