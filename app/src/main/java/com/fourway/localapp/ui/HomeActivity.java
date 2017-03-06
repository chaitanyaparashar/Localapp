package com.fourway.localapp.ui;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.fourway.localapp.R;
import com.fourway.localapp.login_session.SessionManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    Fragment fragmentToLaunch;

    SessionManager session;
    public static String mLoginToken = "";
    public static LatLng mLastKnownLocation = null;

    TabLayout tabLayout;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private int[] tabIcons = {
            R.drawable.ic_map,
            R.drawable.ic_broadcast,
            R.drawable.ic_setting
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleColor));
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        session = new SessionManager(this);

        if (session.isLoggedIn()) {
            getLastLoginDetails();
        }

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
                tab.getIcon().setColorFilter(Color.parseColor("#2196f3"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#FF000000"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /*fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        fragmentToLaunch = new MapFragment();
        transaction.add(R.id.mainLayout, fragmentToLaunch, "map");
        transaction.commit();*/

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#2196f3"), PorterDuff.Mode.SRC_IN);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_map:
                if (fragmentManager.findFragmentByTag("map") != null) {
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("map")).commit();
                }else {
                    fragmentManager.beginTransaction().add(R.id.mainLayout, new MapFragment(), "map").commit();
                }

                if (fragmentManager.findFragmentByTag("feed") != null) {
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("feed")).commit();
                }

                if (fragmentManager.findFragmentByTag("signUp") != null) {
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("signUp")).commit();
                }

                return true;

            case R.id.action_feed:
                if (fragmentManager.findFragmentByTag("feed") != null) {
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("feed")).commit();
                }else {
                    fragmentManager.beginTransaction().add(R.id.mainLayout, new FeedFragment(), "feed").commit();
                }

                if (fragmentManager.findFragmentByTag("map") != null) {
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("map")).commit();
                }

                if (fragmentManager.findFragmentByTag("signUp") != null) {
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("signUp")).commit();
                }
                return true;
            case R.id.action_group:

                break;
            case R.id.action_setting:
                if (fragmentManager.findFragmentByTag("signUp") != null) {
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("signUp")).commit();
                }else {
                    fragmentManager.beginTransaction().add(R.id.mainLayout, new SignUpFragment(), "signUp").commit();
                }

                if (fragmentManager.findFragmentByTag("map") != null) {
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("map")).commit();
                }

                if (fragmentManager.findFragmentByTag("feed") != null) {
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("feed")).commit();
                }

                return true;


        }


        return super.onOptionsItemSelected(item);
    }*/



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        int totalPage = 2;

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
                    return new SignUpFragment();

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
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
        try {
            Double lat = Double.valueOf(user.get(SessionManager.KEY_LAT));
            Double lng = Double.valueOf(user.get(SessionManager.KEY_LNG));

            mLastKnownLocation = new LatLng(lat,lng);
        }catch (NullPointerException e){

        }



    }
}
