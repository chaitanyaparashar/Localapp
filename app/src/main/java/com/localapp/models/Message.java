package com.localapp.models;

import android.graphics.Bitmap;

import com.localapp.ui.fragments.FeedFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Vijay Kumar on 21-02-2017.
 */

public class Message {

    private String token;
    private String mMobile;
    private String mEmail;
    private String mText;
    private String name;
    private String timeStamp;
    private String mediaURL;
    private String picUrl;
    private String speciality;
    private LatLng mLatLng;
    private String emoji;
    private String msgIdOnlyForFrontEnd;
    private FeedFragment.MediaType mediaType;
    private String mUserID;
    private String id;
    private FeedFragment.MessageType messageType;
    private Bitmap imgBitmap;
    private String fcmToken;

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    private String accepted;



    public Message() {

    }



    public FeedFragment.MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(FeedFragment.MediaType mediaType) {
        this.mediaType = mediaType;
    }
    public FeedFragment.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(FeedFragment.MessageType messageType) {
        this.messageType = messageType;
    }

    public String getmUserID() {
        return mUserID;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }
    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    public String getMsgIdOnlyForFrontEnd() {
        return msgIdOnlyForFrontEnd;
    }

    public void setMsgIdOnlyForFrontEnd(String msgIdOnlyForFrontEnd) {
        this.msgIdOnlyForFrontEnd = msgIdOnlyForFrontEnd;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
