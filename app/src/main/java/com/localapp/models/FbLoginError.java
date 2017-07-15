package com.localapp.models;

/**
 * Created by 4 way on 04-05-2017.
 */

public class FbLoginError {
    public static final int ERROR_USER_NOT_FOUND = 0;
    public static final int ERROR_FB_OUTH_FAILD = 1;
    public static final int ERROR_FB_FACE_NOT_FOUND = 2;
    public static final int ERROR_FB_FACE_SERVER_PROBLEM = 3;





    private int statusCode;
    private String errorMessage;

    public FbLoginError(int statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
