package com.fourway.localapp.data;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by 4 way on 03-03-2017.
 */

public class LoginData {
    private String mEmail;
    private String mPassword;
    private String mAccessToken;


    private String userId;
    private String mName;
    private String mMobile;
    private String mSpeciality;
    private String picUrl;
    private String mErrorMessage;

    public LoginData(String mEmail, String mPassword) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getmMobile() {
        return mMobile;
    }

    public void setmMobile(String mMobile) {
        this.mMobile = mMobile;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmSpeciality() {
        return mSpeciality;
    }

    public void setmSpeciality(String mSpeciality) {
        this.mSpeciality = mSpeciality;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String mErrorMessage) {
        this.mErrorMessage = mErrorMessage;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String mAccessToken) {
        this.mAccessToken = mAccessToken;
    }
}
