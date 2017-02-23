package com.fourway.localapp.data;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by 4 way on 21-02-2017.
 */

public class Message {

    private String token;
    private String mMobile;
    private String mEmail;
    private String mText;
    private String sentAt;
    private String name;
    private String timeStamp;
    private String mediaURL;
    private String speciality;
    private LatLng mLatLng;
    private String emoji;

    public Message() {

    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    public void setmMobile(String mMobile) {
        this.mMobile = mMobile;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public void setmLatLng(LatLng mLatLng) {
        this.mLatLng = mLatLng;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getmMobile() {
        return mMobile;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmText() {
        return mText;
    }

    public String getSentAt() {
        return sentAt;
    }

    public String getName() {
        return name;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public String getSpeciality() {
        return speciality;
    }

    public LatLng getmLatLng() {
        return mLatLng;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
