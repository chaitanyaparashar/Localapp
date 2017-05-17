package com.localapp.request;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.localapp.appcontroller.AppController;
import com.localapp.request.helper.VolleyErrorHelper;

import java.util.Map;

import static com.localapp.request.CommonRequest.HOST_ADDRESS;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;

/**
 * Created by 4 way on 16-05-2017.
 */

public class LocalappInviteRequest{

    private Context mContext;
    private Map<String, String> mParams;
    private LocalappInviteRequestCallback mLocalappInviteRequestCallback;
    private String email;

    public LocalappInviteRequest(Context mContext,@NonNull String email, LocalappInviteRequestCallback mLocalappInviteRequestCallback) {
        this.mContext = mContext;
        this.mLocalappInviteRequestCallback = mLocalappInviteRequestCallback;
        this.email = email;
    }





    public void onResponseHandler(String response) {
        mLocalappInviteRequestCallback.InviteResponse(CommonRequest.ResponseCode.COMMON_RES_SUCCESS, null);
    }


    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
            mLocalappInviteRequestCallback.InviteResponse(resCode, null);
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

        }

        mLocalappInviteRequestCallback.InviteResponse(resCode, errorMsg);
    }

    public void executeRequest() {
        String url = HOST_ADDRESS + "/email/doMail?";
        url += "email=" + email;
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onResponseHandler(response);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onErrorHandler(error);
            }
        };

        RequestQueue queue = Volley.newRequestQueue(AppController.getAppContext());
        StringRequest stringRequest =new StringRequest(url,listener,errorListener) {

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }


    public interface LocalappInviteRequestCallback {
        void InviteResponse(CommonRequest.ResponseCode responseCode, String errorMsg);
    }
}
