package com.localapp.ui;

import android.content.Context;
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
import android.view.WindowManager;

import com.localapp.R;
import com.localapp.login_session.SessionManager;
import com.google.android.gms.maps.model.LatLng;


import java.util.HashMap;

public class HomeActivity extends AppCompatActivity{

    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    Fragment fragmentToLaunch;

    public static Context mapContext = null;

    SessionManager session;
    public static String mLoginToken = "";
    public static LatLng mLastKnownLocation = null;
    public static String mUserId = "";
    public static String mPicUrl = null;
    public static boolean activityVisibility = false;
//    public static List<Uri> imageList;

    TabLayout tabLayout;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

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
        mViewPager.setOffscreenPageLimit(4);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#2196f3"));
        setupTabIcons();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#2196f3"), PorterDuff.Mode.SRC_IN);

                /*if (tab.getPosition() == 0) {
//                    ((MapFragment)mSectionsPagerAdapter.getItem(1)).gggg();
                    *//*Intent i = new Intent(HomeActivity.this, Camera2Activity.class);
                    i.putExtra("requestCode", 20);
                    startActivityForResult(i, 20);*//*
                }*/
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
        /*imageList = new ArrayList<>();
        ArrayList<File> files = getFilePaths();
        for (File file:files) {
            imageList.add(Uri.fromFile(file));
        }*/
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

//        int totalPage = 2;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
               /* case 0:
                    return new CameraFragment();*/
                case 0:
                    return new MapFragment();
                case 1:
                    return new FeedFragment();
                case 2:
                    return new NoticeBoardFragment();
                case 3:
                    return new SignUpFragment();

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

    public static class CameraFragment extends Fragment {
        public CameraFragment() {
        }
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


    /*public ArrayList<File> getFilePaths() {

        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<String>();
        ArrayList<File> resultIAV = new ArrayList<File>();

        String[] directories = null;
        if (u != null)
        {
            c = managedQuery(u, projection, null, null, null);
        }

        if ((c != null) && (c.moveToFirst()))
        {
            do
            {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try{
                    dirList.add(tempDir);
                }
                catch(Exception e)
                {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for(int i=0;i<dirList.size();i++)
        {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if(imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if(imagePath.isDirectory())
                    {
                        imageList = imagePath.listFiles();

                    }
                    if ( imagePath.getName().contains(".jpg")|| imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg")|| imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
//                            || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
//                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                            )
                    {



                        String path= imagePath.getAbsolutePath();
//                        resultIAV.add(Uri.parse(path));
                        resultIAV.add(imagePath);


                    }
                }
                //  }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(resultIAV, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                Long obj1 = o1.lastModified();
                Long obj2 = o2.lastModified();
                return obj1.compareTo(obj2);
            }
        });

        Collections.reverse(resultIAV);

        return resultIAV;


    }*/



}
