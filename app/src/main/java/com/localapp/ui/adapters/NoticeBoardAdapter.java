package com.localapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.localapp.R;
import com.localapp.models.NoticeBoard;
import com.localapp.models.NoticeBoardMessage;
import com.localapp.network.helper.CommonRequest;
import com.localapp.ui.activities.HomeActivity;
import com.localapp.ui.fragments.NoticeBoardFragment;
import com.localapp.utils.Utility;

import java.util.List;

/**
 * Created by Vijay Kumar on 04-08-2017.
 */


public class NoticeBoardAdapter extends RecyclerView.Adapter<NoticeBoardAdapter.ViewHolder> {
    private Context mContext;
    private List<NoticeBoard> noticeBoardList;
    private NoticeBoardFragment fragment;

    public NoticeBoardAdapter(Context mContext, List<NoticeBoard> noticeBoardList,NoticeBoardFragment fragment) {
        this.mContext = mContext;
        this.noticeBoardList = noticeBoardList;
        this.fragment = fragment;
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
                holder.noticeTime.setText(Utility.getTimeAndDate(message.getTimestamp()));
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
                fragment.requestForNoticeBoardMsg(noticeBoard, true);
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
                    fragment.requestDeleteNoticeBoard(noticeBoardList.get(adapterPosition));
                    return true;
                case R.id.action_unsubscribe:
                    fragment.requestSubscribeAndUnsub(noticeBoardList.get(adapterPosition), CommonRequest.RequestType.COMMON_REQUEST_UNSUBSCRIBE_NOTICE_BOARD);
                    return true;
                default:
            }
            return false;
        }
    }



}