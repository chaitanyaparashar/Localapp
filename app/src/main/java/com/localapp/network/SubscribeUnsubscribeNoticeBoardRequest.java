package com.localapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.helper.VolleyErrorHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vijay Kumar on 08-04-2017.
 */

public class SubscribeUnsubscribeNoticeBoardRequest extends CommonRequest {
    private Context mContext;
    private String noticeBoardId;
    private String userId;
    private Map<String,String> mParams;
    private RequestType mRequestType;
    private SubscribeUnsubscribeNoticeBoardCallback mUnsubscribeNoticeBoardCallback;


    public SubscribeUnsubscribeNoticeBoardRequest(Context mContext,String noticeBoardId, String userId, RequestType type,SubscribeUnsubscribeNoticeBoardCallback mUnsubscribeNoticeBoardCallback) {
        super(mContext, type, CommonRequestMethod.COMMON_REQUEST_METHOD_POST, null);
        this.mContext = mContext;
        this.noticeBoardId = noticeBoardId;
        this.userId = userId;
        this.mUnsubscribeNoticeBoardCallback = mUnsubscribeNoticeBoardCallback;
        this.mRequestType = type;

        mParams = new HashMap<>();
        mParams.put("noticeBoardId", noticeBoardId);
        mParams.put("userId", userId);
        super.setParams(mParams);
    }


    @Override
    public void onResponseHandler(JSONObject response) {
        mUnsubscribeNoticeBoardCallback.SubscribeUnsubscribeNoticeBoardResponse(ResponseCode.COMMON_RES_SUCCESS, mRequestType, null);
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
            mUnsubscribeNoticeBoardCallback.SubscribeUnsubscribeNoticeBoardResponse(resCode, mRequestType, null);
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
        }else
        {
            resCode = ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;
//            mRequestData.setmErrorMessage(errorMsg);
        }

        mUnsubscribeNoticeBoardCallback.SubscribeUnsubscribeNoticeBoardResponse(resCode, mRequestType,  errorMsg);

    }


    public  interface SubscribeUnsubscribeNoticeBoardCallback {
        void SubscribeUnsubscribeNoticeBoardResponse(CommonRequest.ResponseCode responseCode,RequestType mRequestType, String errorMsg);
    }



}
