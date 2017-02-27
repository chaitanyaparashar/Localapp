package com.fourway.localapp.data;

import java.util.ArrayList;

/**
 * Created by 4 way on 20-02-2017.
 */

public class GetUsersRequestData {

    private ArrayList<Profile> mProfileList;

    private String mErrorMessage;

    public GetUsersRequestData() {
        mProfileList = new ArrayList<>();
    }

    public void addProfile(Profile p) {
        mProfileList.add(p);
    }

    public ArrayList<Profile> getProfileList (){return mProfileList;}
    public int getTotalNumOfProfiles (){return mProfileList.size();}

    public String getmErrorMessage() {
        return mErrorMessage;
    }

    public void setmErrorMessage(String mErrorMessage) {
        this.mErrorMessage = mErrorMessage;
    }
}
