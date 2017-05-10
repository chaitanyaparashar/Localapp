package com.localapp.ui;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RatingBar;

import com.localapp.R;
import com.localapp.feedback.AppPreferences;
import com.localapp.login_session.SessionManager;
import com.google.android.gms.maps.model.LatLng;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity{

    SessionManager session;
    public static String mLoginToken = "";
    public static LatLng mLastKnownLocation = null;
    public static String mUserId = "";
    public static String mPicUrl = null;
    TabLayout tabLayout;

    public static SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public static ViewPager mViewPager;

    private int[] tabIcons = {
            R.drawable.ic_map,
            R.drawable.ic_broadcast,
            R.drawable.ic_notice_board,
            R.drawable.ic_setting
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleColor));
        setSupportActionBar(toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        AppPreferences.getInstance(this).incrementLaunchCount();
        showRateAppDialogIfNeeded();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        session = SessionManager.getInstance(this);

//        if (session.isLoggedIn()) {
            getLastLoginDetails();
//        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#2196f3"));
        setupTabIcons();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    tab.getIcon().setColorFilter(Color.parseColor("#2196f3"), PorterDuff.Mode.SRC_IN);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#FF000000"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void setupTabIcons() {
        try {
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
            tabLayout.getTabAt(3).setIcon(tabIcons[3]);

            tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#2196f3"), PorterDuff.Mode.SRC_IN);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

//        int totalPage = 2;


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    return new MapFragment();
                case 1:
                    return new FeedFragment();
                case 2:
                    return new NoticeBoardFragment();
                case 3:
                    return new ProfileFragment();

            }
            return null;
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

       /* @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "S1";
                case 1:
                    return "S2";
                case 2:
                    return "S3";
            }
            return null;
        }*/

    }


    void getLastLoginDetails() {

        HashMap<String, String> user = session.getUserDetails();
        mLoginToken = user.get(SessionManager.KEY_LOGIN_TOKEN);
        mUserId = user.get(SessionManager.KEY_LOGIN_USER_ID);
//        mUserId = "58b909b1f81fde3f9ce5ea31";//hardcoded
        mPicUrl = user.get(SessionManager.KEY_LOGIN_USER_PIC_URL);
        try {
            Double lat = Double.valueOf(user.get(SessionManager.KEY_LAT));
            Double lng = Double.valueOf(user.get(SessionManager.KEY_LNG));


            mLastKnownLocation = new LatLng(lat,lng);
        }catch (NullPointerException e){
            e.printStackTrace();
        }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



    //============================================== feedback ===================================//

    private void showRateAppDialogIfNeeded() {
        boolean bool = AppPreferences.getInstance(getApplicationContext()).getAppRate();
        int i = AppPreferences.getInstance(getApplicationContext()).getLaunchCount();
        if ((bool) && (i == 3)) {
            new CountDownTimerTask(10000,10000).start();

        }

        if ((bool) && (i%50 == 0)) {
            new CountDownTimerTask(10000,10000).start();
        }
    }

    private AlertDialog createAppRatingDialog(String rateAppTitle, String rateAppMessage) {

       final View view = LayoutInflater.from(this).inflate(R.layout.app_rating,null);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);






        final AlertDialog dialog  = new AlertDialog.Builder(this).setPositiveButton(getString(R.string.dialog_app_rate), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                openAppInPlayStore(HomeActivity.this);
                AppPreferences.getInstance(HomeActivity.this.getApplicationContext()).setAppRate(false);
            }
        }).setNegativeButton(getString(R.string.dialog_your_feedback), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                openFeedback(HomeActivity.this);
                AppPreferences.getInstance(HomeActivity.this.getApplicationContext()).setAppRate(false);
            }
        }).setNeutralButton(getString(R.string.dialog_ask_later), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.dismiss();
                if (AppPreferences.getInstance(HomeActivity.this).getLaunchCount() < 4) {
                    AppPreferences.getInstance(HomeActivity.this.getApplicationContext()).resetLaunchCount();
                }
            }
        }).setMessage(rateAppMessage).setTitle(rateAppTitle).setView(view).setCancelable(false).create();


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating = Math.round(rating);
                ratingBar.setRating(rating);
                if (rating > 2) {
                    openAppInPlayStore(HomeActivity.this);
                }else {
                    openFeedback(HomeActivity.this);
                }

                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static void openAppInPlayStore(Context paramContext) {
        try {
            paramContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.localapp")));
        }catch (ActivityNotFoundException e){
            paramContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.localapp")));
        }
    }

    public static void openFeedback(Context paramContext) {
        Intent localIntent = new Intent(Intent.ACTION_SEND);
        localIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"connect@localapp.org"});
        localIntent.putExtra(Intent.EXTRA_CC, "");
        String str = null;
        try {
            str = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionName;
            localIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Your Android App");
            localIntent.putExtra(Intent.EXTRA_TEXT, "\n\n----------------------------------\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + str + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER);
            localIntent.setType("message/rfc822");
            paramContext.startActivity(Intent.createChooser(localIntent, "Choose an Email for feedback :"));
        } catch (Exception e) {
            Log.d("OpenFeedback", e.getMessage());
        }
    }


    private class CountDownTimerTask extends CountDownTimer {


        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountDownTimerTask(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d("CountDownTimerTask",": "+millisUntilFinished / 1000);
        }


        @Override
        public void onFinish() {
            try {
                createAppRatingDialog(getString(R.string.rate_app_title), getString(R.string.rate_app_message)).show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }



}
