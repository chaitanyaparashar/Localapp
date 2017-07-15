package com.localapp.models;

import com.facebook.login.LoginResult;

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



    private String mNotes;
    private String mProfession;
    private FbLoginError fbLoginError;
    private LoginResult fbLoginResult;

    public LoginData(String mEmail, String mPassword) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
    }

    public LoginData() {
    }

    public String getmNotes() {
        return mNotes;
    }

    public void setmNotes(String mNotes) {
        this.mNotes = mNotes;
    }

    public String getmProfession() {
        return mProfession;
    }

    public void setmProfession(String mProfession) {
        this.mProfession = mProfession;
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


    public FbLoginError getFbLoginError() {
        return fbLoginError;
    }

    public void setFbLoginError(FbLoginError fbLoginError) {
        this.fbLoginError = fbLoginError;
    }

    public void setFbLoginResult(LoginResult fbLoginResult) {
        this.fbLoginResult = fbLoginResult;
    }

    public LoginResult getFbLoginResult() {
        return fbLoginResult;
    }
}
