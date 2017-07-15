package com.localapp.ui;

import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.localapp.R;
import com.localapp.appcontroller.AppController;
import com.localapp.background.LocationService;
import com.localapp.feedback.AppPreferences;
import com.localapp.login_session.SessionManager;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,View.OnClickListener{
    SessionManager session;
    public FrameLayout container;
    protected View view;
    private Button btnNext, btnFinish, btnStarted;
    private ViewPager intro_images;
    private LinearLayout pager_indicator, btn_layout;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;

    private int[] mImageResources = {
            R.mipmap.page_item_1,
            R.mipmap.page_item_2,
            R.mipmap.page_item_3,
            R.mipmap.page_item_4,
            R.mipmap.page_item_5
    };

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*try {
            getSupportActionBar().hide();
        }catch (NullPointerException e){
            e.printStackTrace();
        }*/
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_main);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);// FirebaseAnalytics




        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && AppPreferences.getInstance(AppController.getAppContext()).getLaunchCount() == 0){
            startService(new Intent(this, LocationService.class));
        }



        if (AppPreferences.getInstance(AppController.getAppContext()).isTourLaunched()) {
            startApp();
            finish();
        }


        container = (FrameLayout) findViewById(R.id.container);

        setReference();




    }

    private void startApp() {
        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        finish();
    }


    public void setReference() {

        view = LayoutInflater.from(this).inflate(R.layout.activity_viewpager,container);
        intro_images = (ViewPager) view.findViewById(R.id.pager_introduction);
        btnNext = (Button) view.findViewById(R.id.btn_skip);
        btnFinish = (Button) view.findViewById(R.id.btn_next);
        btnStarted = (Button) view.findViewById(R.id.btn_start);

        btn_layout = (LinearLayout) view.findViewById(R.id.control_layout);
        pager_indicator = (LinearLayout) view.findViewById(R.id.viewPagerCountDots);


        btnNext.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        btnStarted.setOnClickListener(this);

        mAdapter = new ViewPagerAdapter(this, mImageResources);
        intro_images.setPageTransformer(true,new FadePageTransformer());
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0,true);
        intro_images.setOnPageChangeListener(this);
        setUiPageViewController();



    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }


    /**
     * This method will be invoked when the current page is scrolled, either as part
     * of a programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * @param position             Position index of the first page currently being displayed.
     *                             Page position+1 will be visible if positionOffset is nonzero.
     * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
     * @param positionOffsetPixels Value in pixels indicating the offset from position.
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * This method will be invoked when a new page becomes selected. Animation is not
     * necessarily complete.
     *
     * @param position Position index of the new selected page.
     */
    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

        if (position == 4){
            btnStarted.setVisibility(View.VISIBLE);
            btn_layout.setVisibility(View.GONE);
        }else {
            btnStarted.setVisibility(View.GONE);
            btn_layout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param state The new scroll state.
     * @see ViewPager#SCROLL_STATE_IDLE
     * @see ViewPager#SCROLL_STATE_DRAGGING
     * @see ViewPager#SCROLL_STATE_SETTLING
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_skip: AppPreferences.getInstance(AppController.getAppContext()).tourLaunched();startApp();
                /*intro_images.setCurrentItem((intro_images.getCurrentItem() < dotsCount)
                        ? intro_images.getCurrentItem() - 1 : 0);*/
                break;

            case R.id.btn_next:
                intro_images.setCurrentItem((intro_images.getCurrentItem() < dotsCount) ? intro_images.getCurrentItem() + 1 : 0,true);
                break;
            case R.id.btn_start: AppPreferences.getInstance(AppController.getAppContext()).tourLaunched();startApp();
                break;
        }
    }

    public class FadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            view.setTranslationX(view.getWidth() * -position);

            if(position <= -1.0F || position >= 1.0F) {
                view.setAlpha(0.0F);
            } else if( position == 0.0F ) {
                view.setAlpha(1.0F);
            } else {
                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                view.setAlpha(1.0F - Math.abs(position));
            }
        }
    }
}
