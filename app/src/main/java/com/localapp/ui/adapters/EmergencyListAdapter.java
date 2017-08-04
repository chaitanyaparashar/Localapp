package com.localapp.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.localapp.R;
import com.localapp.models.Message;
import com.squareup.picasso.Picasso;

import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static com.localapp.ui.adapters.ThreadAdapter.getEmojiResourceIdByMsgType;

/**
 * Created by Vijay Kumar on 04-08-2017.
 */

public class EmergencyListAdapter extends BaseAdapter {
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
