package com.localapp.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Vijay Kumar on 25-04-2017.
 */

public class NotificationData {

    private String fcmToke;
    private String title;
    private String message;
    private String img_url;
    private String mobile;
    private String email;
    private LatLng latLng;
    private String profession;
    private Profile profile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public NotificationData(String fcmToke) {
        this.fcmToke = fcmToke;
    }

    public String getFcmToke() {
        return fcmToke;
    }

    public String getTitle() {
        return title;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }


    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }
}
