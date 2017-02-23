package com.fourway.localapp.data;

import java.util.ArrayList;

/**
 * Created by 4 way on 21-02-2017.
 */

public class GetFeedRequestData {
    private ArrayList<Message> mMessageList;

    public GetFeedRequestData() {
        mMessageList = new ArrayList<>();
    }

    public void addMessage(Message msg) {
        mMessageList.add(msg);
    }

    public ArrayList<Message> getMessageList (){return mMessageList;}
    public int getTotalNumOfMessage (){return mMessageList.size();}
}
