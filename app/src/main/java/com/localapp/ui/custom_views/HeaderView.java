package com.localapp.ui.custom_views;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.localapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vijay Kumar on 06-07-2017.
 */

public class HeaderView extends LinearLayout {

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.distance)
    TextView distance;
    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void bindTo(String name, String distance) {
        this.name.setText(name);
        this.distance.setText(distance);
    }

    public void setTextColor(@ColorInt int color){
        this.name.setTextColor(color);
    }



    public void setTextSize(float size) {
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }
}
