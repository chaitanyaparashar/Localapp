package com.localapp.data;

import java.util.ArrayList;

/**
 * Created by 4 way on 21-02-2017.
 */

public class GetFeedRequestData {

    private ArrayList<Message> mMessageList;


    private String mErrorMessage;

    public GetFeedRequestData() {
        mMessageList = new ArrayList<>();
    }

    public void addMessage(Message msg) {
        mMessageList.add(msg);
    }

    public ArrayList<Message> getMessageList (){return mMessageList;}
    public int getTotalNumOfMessage (){return mMessageList.size();}

    public String getmErrorMessage() {
        return mErrorMessage;
    }

    public void setmErrorMessage(String mErrorMessage) {
        this.mErrorMessage = mErrorMessage;
    }
}
