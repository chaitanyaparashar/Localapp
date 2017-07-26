package com.localapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.localapp.models.Profile;
import com.localapp.network.helper.CommonFileUpload;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.helper.VolleyErrorHelper;
import com.localapp.ui.activities.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 4 way on 25-07-2017.
 */

public class UpdateProfilePicRequest {

    private Context mContext;
    private File imageFile;
    private CommonFileUpload mFileUpload;
    private UpdateProfilePicResponseCallback mResponseCallback;

    public interface UpdateProfilePicResponseCallback {
        void UpdateProfilePicResponse(CommonRequest.ResponseCode responseCode, String picUrl);
    }

    public UpdateProfilePicRequest (Context mContext, File imageFile, UpdateProfilePicResponseCallback mResponseCallback) {
        this.mContext = mContext;
        this.imageFile = imageFile;

        this.mResponseCallback = mResponseCallback;


    }

    public void executeRequest() {
        final String url = CommonRequest.DOMAIN + "/updateProfilePic";

        Response.Listener<NetworkResponse> listener = new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String jsonStr = new String(response.data);
                JSONObject jsonObject;
                String picUrl = null;
                try {
                    jsonObject = new JSONObject(jsonStr);

                    picUrl = jsonObject.getString("data");
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                mResponseCallback.UpdateProfilePicResponse(CommonRequest.ResponseCode.COMMON_RES_SUCCESS,picUrl);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
                Log.v("onErrorHandler","error is" + error);
                CommonRequest.ResponseCode resCode;
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    resCode = CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
                    mResponseCallback.UpdateProfilePicResponse(resCode ,null);
                    return;
                }
                if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_TIMEOUT)
                {
                    resCode = CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
                }
                else if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_UNKNOWN){
                    resCode = CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
                }
                else if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_NO_INTERNET){
                    resCode = CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
                }else {
                    resCode = CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;
//
                }

                mResponseCallback.UpdateProfilePicResponse(resCode ,null);
            }
        };

        mFileUpload = new CommonFileUpload(mContext, imageFile, CommonFileUpload.FileType.COMMON_UPLOAD_FILE_TYPE_IMAGE,
                HomeActivity.mUserId + System.currentTimeMillis(), url, null, listener, errorListener );

        Map<String,String> mHeader = new HashMap<>();
        mHeader.put("token",HomeActivity.mLoginToken);

        mFileUpload.setRetryPolicy(60000,0);

        mFileUpload.setHeader(mHeader);
        mFileUpload.uploadFile();
    }
}
