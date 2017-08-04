package com.localapp.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.localapp.ui.fragments.FeedFragment;
import com.localapp.ui.fragments.MapFragment;
import com.localapp.ui.fragments.NoticeBoardFragment;
import com.localapp.ui.fragments.ProfileFragment;

/**
 * Created by Vijay Kumar on 04-08-2017.
 */


/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

        int totalPage = 4;


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
        return totalPage;
    }


}
