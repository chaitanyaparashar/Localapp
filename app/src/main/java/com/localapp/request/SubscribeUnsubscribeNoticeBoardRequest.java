package com.localapp.request;

import android.content.Context;
import android.util.Log;

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
 * Created by 4 way on 08-04-2017.
 */

public class SubscribeUnsubscribeNoticeBoardRequest extends CommonRequest{
    private Context mContext;
    private String noticeBoardId;
    private String userId;
    private Map<String,String> mParams;
    private SubscribeUnsubscribeNoticeBoardCallback mUnsubscribeNoticeBoardCallback;


    public SubscribeUnsubscribeNoticeBoardRequest(Context mContext,String noticeBoardId, String userId, RequestType type,SubscribeUnsubscribeNoticeBoardCallback mUnsubscribeNoticeBoardCallback) {
        super(mContext, type, CommonRequestMethod.COMMON_REQUEST_METHOD_POST, null);
        this.mContext = mContext;
        this.noticeBoardId = noticeBoardId;
        this.userId = userId;
        this.mUnsubscribeNoticeBoardCallback = mUnsubscribeNoticeBoardCallback;

        mParams = new HashMap<>();
        mParams.put("noticeBoardId", noticeBoardId);
        mParams.put("userId", userId);
        super.setParams(mParams);
    }


    @Override
    public void onResponseHandler(JSONObject response) {
        mUnsubscribeNoticeBoardCallback.SubscribeUnsubscribeNoticeBoardResponse(ResponseCode.COMMON_RES_SUCCESS, null);
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
            mUnsubscribeNoticeBoardCallback.SubscribeUnsubscribeNoticeBoardResponse(resCode,null);
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

        mUnsubscribeNoticeBoardCallback.SubscribeUnsubscribeNoticeBoardResponse(resCode, errorMsg);

    }


    public  interface SubscribeUnsubscribeNoticeBoardCallback {
        void SubscribeUnsubscribeNoticeBoardResponse(CommonRequest.ResponseCode responseCode, String errorMsg);
    }



}
