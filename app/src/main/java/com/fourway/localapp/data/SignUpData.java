package com.fourway.localapp.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;

/**
 * Created by 4 way on 17-02-2017.
 */

public class SignUpData {
    private String mName;
    private String mEmail;
    private String mPassword;
    private String mMobile;
    private String mSpeciality;
    private LatLng mPlace;
    private LatLng mLocation;
    private Byte mType;
    private String mErrorMessage;

    private String mDetails;

    private File picFile;

    private String mResponseMessage;


    public SignUpData(String mName, String mEmail, String mPassword,
                      String mMobile, String mSpeciality, String mDetails, LatLng mPlace,
                      LatLng mLocation, Byte mType,File picFile) {
        this.mName = mName;
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mMobile = mMobile;
        this.mSpeciality = mSpeciality;
        this.mPlace = mPlace;
        this.mLocation = mLocation;
        this.mType = mType;
        this.picFile = picFile;
        this.mDetails = mDetails;
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
}
