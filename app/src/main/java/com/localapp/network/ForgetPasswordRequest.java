package com.localapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.helper.VolleyErrorHelper;

import org.json.JSONObject;

/**
 * Created by 4 way on 06-03-2017.
 */

public class ForgetPasswordRequest extends CommonRequest {


    public interface ForgetPasswordRequestCallback {
        void ForgetPasswordResponse(CommonRequest.ResponseCode responseCode,String message);
    }

    private Context mContext;

    private ForgetPasswordRequestCallback mPasswordRequestCallback;


    public ForgetPasswordRequest(Context context, String mEmail, ForgetPasswordRequestCallback cb) {
        super(context, RequestType.COMMON_REQUEST_PASSWORD, CommonRequestMethod.COMMON_REQUEST_METHOD_GET, null);

        this.mContext = context;
        String url = getRequestTypeURL(RequestType.COMMON_REQUEST_PASSWORD);
        url += "email=" + mEmail;

        super.setURL(url);

        mPasswordRequestCallback = cb;
    }

    @Override
    public void onResponseHandler(JSONObject response) {
        mPasswordRequestCallback.ForgetPasswordResponse(ResponseCode.COMMON_RES_SUCCESS,null);
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode = null;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
            mPasswordRequestCallback.ForgetPasswordResponse (resCode, null);
            return;
        }
        if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_TIMEOUT)
        {
            resCode = ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
        }
        else if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_UNKNOWN){
            resCode = ResponseCode.COMMON_RES_INTERNAL_ERROR;
        }
        else if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_NO_INTERNET){
            resCode = ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
        }else {
            resCode = ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;

        }

        mPasswordRequestCallback.ForgetPasswordResponse (resCode,errorMsg);
    }
}
