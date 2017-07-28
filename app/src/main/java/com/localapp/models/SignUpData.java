package com.localapp.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;

/**
 * Created by Vijay Kumar on 17-02-2017.
 */

public class SignUpData {
    private FbLoginError fbLoginError;

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    private String mName;
    private String mEmail;
    private String mPassword;
    private String mMobile;
    private String mSpeciality;
    private LatLng mPlace;
    private LatLng mLocation;
    private Byte mType;
    private String mErrorMessage;
    private String mPrivacy;
    private String mUserId;
    private String mToken;
    private String profession;
    private String mDetails;

    public void setPicFile(File picFile) {
        this.picFile = picFile;
    }

    private File picFile;
    private String picUrl;
    private String mResponseMessage;
    private String fbId;
    private String fbToken;


    public SignUpData(String mName, String mEmail, String profession, String mPassword,
                      String mMobile,String privacy, String mSpeciality, String mDetails, LatLng mPlace,
                      LatLng mLocation, Byte mType,File picFile) {
        this.mName = mName;
        this.mEmail = mEmail;
        this.profession = profession;
        this.mPassword = mPassword;
        this.mMobile = mMobile;
        this.mPrivacy = privacy;
        this.mSpeciality = mSpeciality;
        this.mPlace = mPlace;
        this.mLocation = mLocation;
        this.mType = mType;
        this.picFile = picFile;
        this.mDetails = mDetails;
    }

    public SignUpData() {
    }

    public void setmSpeciality(String mSpeciality) {
        this.mSpeciality = mSpeciality;
    }

    public String getmPrivacy() {
        return mPrivacy;
    }

    public void setmPrivacy(String mPrivacy) {
        this.mPrivacy = mPrivacy;
    }
    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }
    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    public String getmToken() {
        return mToken;
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }
    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
    public String getmDetails() {
        return mDetails;
    }

    public File getPicFile() {
        return picFile;
    }

    public String getmName() {
        return mName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public String getmMobile() {
        return mMobile;
    }

    public String getmSpeciality() {
        return mSpeciality;
    }

    public LatLng getmPlace() {
        return mPlace;
    }

    public LatLng getmLocation() {
        return mLocation;
    }

    public Byte getmType() {
        return mType;
    }

    public String getmErrorMessage() {
        return mErrorMessage;
    }

    public String getmResponseMessage() {
        return mResponseMessage;
    }

    public void setmErrorMessage(String mErrorMessage) {
        this.mErrorMessage = mErrorMessage;
    }

    public void setmResponseMessage(String mResponseMessage) {
        this.mResponseMessage = mResponseMessage;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getFbToken() {
        return fbToken;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }

    public void setFbLoginError(FbLoginError fbLoginError) {
        this.fbLoginError = fbLoginError;
    }

    public FbLoginError getFbLoginError() {
        return fbLoginError;
    }
}
