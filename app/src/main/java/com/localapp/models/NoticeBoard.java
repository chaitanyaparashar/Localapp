package com.localapp.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Vijay Kumar on 23-03-2017.
 */

public class NoticeBoard {
    private String id;
    private String adminId;
    private String name;
    private List<NoticeBoardMessage> messagesList;
    private String errorMessage;

    public LatLng getLocation() {
        return location;
    }

    private LatLng location;

    public NoticeBoard() {
    }

    public NoticeBoard(String adminId, String name) {
        this.adminId = adminId;
        this.name = name;
    }

    public NoticeBoard(String id) {
        this.id = id;
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


    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
