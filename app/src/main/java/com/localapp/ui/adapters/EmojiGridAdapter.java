package com.localapp.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.localapp.R;
import com.localapp.utils.Constants;

/**
 * Created by Vijay Kumar on 04-08-2017.
 */

public  class EmojiGridAdapter extends ArrayAdapter {
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
        imageView.setImageResource(Constants.emojiResourceID[position]);

        return view;
    }
}
