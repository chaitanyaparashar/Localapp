package com.localapp.ui;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import com.localapp.R;
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


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        session = new SessionManager(this);

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


}
