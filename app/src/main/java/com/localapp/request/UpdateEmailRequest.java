package com.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.localapp.request.helper.VolleyErrorHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;

/**
 * Created by 4 way on 18-05-2017.
 */

public class UpdateEmailRequest extends CommonRequest{
    private Context mContext;
    private Map<String, String> mHeaders;
    private UpdateEmailRequestCallback mUpdateEmailRequestCallback;

    public interface UpdateEmailRequestCallback {
        void UpdateEmailResponse(ResponseCode responseCode, String statusCode);
    }

    public UpdateEmailRequest(Context mContext,String newEmail, String uToken, UpdateEmailRequestCallback cb ) {
        super(mContext, RequestType.COMMON_REQUEST_UPDATE_EMAIL, CommonRequestMethod.COMMON_REQUEST_METHOD_GET, null);

        this.mContext = mContext;
        String url = getRequestTypeURL(RequestType.COMMON_REQUEST_UPDATE_EMAIL);
        url += "email=" + newEmail;
        setURL(url);

        mHeaders = new HashMap<>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("token", uToken);

        super.setPostHeader(mHeaders);

        this.mUpdateEmailRequestCallback = cb;
    }



    @Override
    public void onResponseHandler(JSONObject response) {
        mUpdateEmailRequestCallback.UpdateEmailResponse(ResponseCode.COMMON_RES_SUCCESS,"1");
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
            mUpdateEmailRequestCallback.UpdateEmailResponse (resCode,null);
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
//            mRequestData.setmErrorMessage(errorMsg);
        }

        mUpdateEmailRequestCallback.UpdateEmailResponse (resCode,errorMsg);
    }
}
