package com.fourway.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.fourway.localapp.data.LoginData;
import com.fourway.localapp.request.helper.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SUCCESS;

/**
 * Created by 4 way on 03-03-2017.
 */

public class LoginRequest extends CommonRequest {

    private static final String JSON_FIELD_EMAIL = "email";
    private static final String JSON_FIELD_PASSWORD = "password";

    private LoginData mLoginData;
    private Map<String, String> mParams;
    Context mContext;
    private LoginResponseCallback mLoginResponseCallback;


    public interface LoginResponseCallback {
        void onLoginResponse(ResponseCode responseCode, LoginData data);
    }

    public LoginRequest(Context context, LoginData data, LoginResponseCallback cb) {
        super(context, RequestType.COMMON_REQUEST_LOGIN, CommonRequestMethod.COMMON_REQUEST_METHOD_POST, null);
        mContext = context;
        mLoginData = data;

        mParams = new HashMap<>();
        mParams.put(JSON_FIELD_EMAIL, mLoginData.getEmail());
        mParams.put(JSON_FIELD_PASSWORD, mLoginData.getPassword());
        super.setParams(mParams);

        mLoginResponseCallback = cb;
    }

    @Override
    public void onResponseHandler(JSONObject response) {
        //TODO: Need to change parsing as per response from server
        try {
            JSONObject jsonObject = response.getJSONObject("obj");
            mLoginData.setAccessToken(jsonObject.getString("token"));
            mLoginData.setmName(jsonObject.getString("name"));
            mLoginData.setmMobile(jsonObject.getString("mobile"));
            mLoginData.setmSpeciality(jsonObject.getString("speciality"));
            mLoginData.setPicUrl(jsonObject.getString("picUrl"));

            mLoginResponseCallback.onLoginResponse(COMMON_RES_SUCCESS, mLoginData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
            mLoginResponseCallback.onLoginResponse(resCode, mLoginData);
        }
        if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_TIMEOUT)
        {
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
        }
        else if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_UNKNOWN){
            resCode = COMMON_RES_INTERNAL_ERROR;
        }
        else if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_NO_INTERNET){
            resCode = COMMON_RES_FAILED_TO_CONNECT;
        }else
        {
            resCode = COMMON_RES_SERVER_ERROR_WITH_MESSAGE;
            mLoginData.setErrorMessage(errorMsg);
        }

        mLoginResponseCallback.onLoginResponse(resCode, mLoginData);
    }
}
