package com.fourway.localapp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4 way on 23-03-2017.
 */

public class NoticeBoardData {
    private String id;
    private String adminId;
    private String name;
    private List<NoticeBoardMessage> messagesList;

    public NoticeBoardData() {
    }

    public NoticeBoardData(String adminId, String name) {
        this.adminId = adminId;
        this.name = name;
    }

    public List<NoticeBoardMessage> getMessagesList() {
        return messagesList;
    }

    public void setMessagesList(List<NoticeBoardMessage> messagesList) {
        this.messagesList = messagesList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
