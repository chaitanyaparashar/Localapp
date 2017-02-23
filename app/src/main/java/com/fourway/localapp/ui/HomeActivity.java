package com.fourway.localapp.ui;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.fourway.localapp.R;

public class HomeActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    Fragment fragmentToLaunch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleColor));
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        fragmentToLaunch = new MapFragment();
        transaction.add(R.id.mainLayout, fragmentToLaunch, "map");
        transaction.commit();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
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
    }
}
