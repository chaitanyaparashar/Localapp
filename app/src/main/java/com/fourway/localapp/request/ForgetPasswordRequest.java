package com.fourway.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.fourway.localapp.data.LoginData;
import com.fourway.localapp.request.helper.VolleyErrorHelper;

import org.json.JSONObject;

import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;

/**
 * Created by 4 way on 06-03-2017.
 */

public class ForgetPasswordRequest extends CommonRequest{


    public interface ForgetPasswordRequestCallback {
        void ForgetPasswordResponse(CommonRequest.ResponseCode responseCode);
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
        mPasswordRequestCallback.ForgetPasswordResponse(ResponseCode.COMMON_RES_SUCCESS);
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode = null;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
            mPasswordRequestCallback.ForgetPasswordResponse (resCode);
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
        }

        mPasswordRequestCallback.ForgetPasswordResponse (resCode);
    }
}
