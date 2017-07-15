package com.localapp.models;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4 way on 08-06-2017.
 */

public class MessageNotificationData {
    private int notificationId;

    private String userId;
    private List<String> messageList;

    public int getNotificationId() {
        return notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getMessageList() {
        return messageList;
    }


    public MessageNotificationData(int notificationId, String userId, String message) {
        this.notificationId = notificationId;
        this.userId = userId;
        messageList = new ArrayList<>();
        messageList.add(message);
    }


}
