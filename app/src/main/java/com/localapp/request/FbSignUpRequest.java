package com.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.localapp.data.FbLoginError;
import com.localapp.data.SignUpData;
import com.localapp.request.helper.CommonFileUpload;
import com.localapp.request.helper.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.localapp.request.CommonRequest.HOST_ADDRESS;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;
import static com.localapp.request.SignUpRequest.JSON_FIELD_EMAIL_ID;
import static com.localapp.request.SignUpRequest.JSON_FIELD_NAME;

/**
 * Created by 4 way on 04-05-2017.
 */

public class FbSignUpRequest {

    private static final String JSON_FIELD_FB_ID = "fbId";
    private static final String JSON_FIELD_FB_TOKEN = "fbToken";

    private Context mContext;

    private Map<String, String> mParams;
    private SignUpData mSignUpData;

    private CommonFileUpload mFileUpload;

    public interface FbSignUpResponseCallback {
        void onFbSignUpResponse(CommonRequest.ResponseCode res, SignUpData data);
    }
    private FbSignUpResponseCallback mSignUpResponseCallback;


    public FbSignUpRequest(Context context, SignUpData data, FbSignUpResponseCallback cb) {

        mContext = context;
        mSignUpData = data;
        mParams = new HashMap<>();
        /*mParams.put("Content-Type", "multipart/form-data");*/
        mParams.put(JSON_FIELD_NAME, data.getmName());
        mParams.put(JSON_FIELD_EMAIL_ID, data.getmEmail());
        mParams.put(JSON_FIELD_FB_ID, data.getFbId());
        mParams.put(JSON_FIELD_FB_TOKEN, data.getFbToken());

//        mParams.put(JSON_FIELD_TYPE, data.getmType());

        mSignUpResponseCallback = cb;
    }


    public void executeRequest() {

        final String url = HOST_ADDRESS + "/registerWithFacebook";//url for registration with pic

        Log.d("registerWithFacebook", "registerWithFacebook is called");

        Response.Listener<NetworkResponse> listener = new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String jsonStr = new String(response.data);

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(jsonStr);
                    JSONObject dataObject = new JSONObject(jsonObject.getString("data"));
                    String token = dataObject.getString("token");
                    String picUrl = dataObject.getString("picUrl");
                    String userID = dataObject.getString("UserId");
                    mSignUpData.setPicUrl(picUrl);
                    mSignUpData.setmToken(token);
                    mSignUpData.setmUserId(userID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSignUpResponseCallback.onFbSignUpResponse(CommonRequest.ResponseCode.COMMON_RES_SUCCESS, mSignUpData);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
                Log.v("onErrorHandler","error is" + error);
                CommonRequest.ResponseCode resCode = COMMON_RES_INTERNAL_ERROR;
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    resCode = COMMON_RES_CONNECTION_TIMEOUT;
                    mSignUpResponseCallback.onFbSignUpResponse(resCode,mSignUpData);
                    return;
                }else if (error.networkResponse != null && error.networkResponse.statusCode == 422) {

                    NetworkResponse response = error.networkResponse;
                    String errorResponse = new String(response.data);

                    try {
                        JSONObject obj = new JSONObject(errorResponse);
                        JSONObject errorObject = obj.getJSONObject("error");
                        mSignUpData.setFbLoginError(new FbLoginError(errorObject.getInt("code"), errorObject.getString("message")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    resCode = COMMON_RES_SERVER_ERROR_WITH_MESSAGE;
                    mSignUpResponseCallback.onFbSignUpResponse(resCode, mSignUpData);
                    return;
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
                    mSignUpData.setmErrorMessage(errorMsg);
                }

                mSignUpResponseCallback.onFbSignUpResponse(resCode, mSignUpData);
            }
        };

        mFileUpload = new CommonFileUpload(mContext, mSignUpData.getPicFile(), CommonFileUpload.FileType.COMMON_UPLOAD_FILE_TYPE_IMAGE,
                mSignUpData.getmEmail(),url,null,listener,errorListener);

        mFileUpload.setRetryPolicy(60000,0);

        mFileUpload.setParam(mParams);
        mFileUpload.uploadFile();
    }
}
