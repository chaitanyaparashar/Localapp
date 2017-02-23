package com.fourway.localapp.data;

/**
 * Created by 4 way on 17-02-2017.
 */

public class SignUpData {
    private String mName;
    private String mMobile;
    private String mSpeciality;
    private Double[] mPlace;
    private Double[] mLocation;
    private Byte mType;
    private String mErrorMessage;
    private String mResponseMessage;


    public SignUpData(String mName, String mMobile, String mSpeciality, Double[] mPlace,
                      Double[] mLocation, Byte mType, String mErrorMessage, String mResponseMessage) {
        this.mName = mName;
        this.mMobile = mMobile;
        this.mSpeciality = mSpeciality;
        this.mPlace = mPlace;
        this.mLocation = mLocation;
        this.mType = mType;
        this.mErrorMessage = mErrorMessage;
        this.mResponseMessage = mResponseMessage;
    }

    public Double[] getmPlace() {
        return mPlace;
    }

    public void setmPlace(Double[] mPlace) {
        this.mPlace = mPlace;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmMobile() {
        return mMobile;
    }

    public void setmMobile(String mMobile) {
        this.mMobile = mMobile;
    }

    public String getmSpeciality() {
        return mSpeciality;
    }

    public void setmSpeciality(String mSpeciality) {
        this.mSpeciality = mSpeciality;
    }

    public Double[] getmLocation() {
        return mLocation;
    }

    public void setmLocation(Double[] mLocation) {
        this.mLocation = mLocation;
    }

    public Byte getmType() {
        return mType;
    }

    public void setmType(Byte mType) {
        this.mType = mType;
    }

    public String getmErrorMessage() {
        return mErrorMessage;
    }

    public void setmErrorMessage(String mErrorMessage) {
        this.mErrorMessage = mErrorMessage;
    }

    public String getmResponseMessage() {
        return mResponseMessage;
    }

    public void setmResponseMessage(String mResponseMessage) {
        this.mResponseMessage = mResponseMessage;
    }


}
