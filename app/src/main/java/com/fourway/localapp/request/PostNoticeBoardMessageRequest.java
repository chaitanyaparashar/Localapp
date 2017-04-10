package com.fourway.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.fourway.localapp.data.NoticeBoardMessage;
import com.fourway.localapp.request.helper.VolleyErrorHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;

/**
 * Created by 4 way on 08-04-2017.
 */

public class PostNoticeBoardMessageRequest extends CommonRequest {
    private Context mContext;
    private NoticeBoardMessage mNoticeBoardMessage;
    private Map<String, String> mParams;
    private PostNoticeBoardMessageResponseCallback callback;


    public interface PostNoticeBoardMessageResponseCallback {
        void PostNoticeBoardResponse(CommonRequest.ResponseCode res,NoticeBoardMessage mNoticeBoardMessage);
    }

    public PostNoticeBoardMessageRequest(Context mContext, NoticeBoardMessage mNoticeBoardMessage, PostNoticeBoardMessageResponseCallback callback) {
        super(mContext, RequestType.COMMON_REQUEST_POST_NOTICE_BOARD_MSG, CommonRequest.CommonRequestMethod.COMMON_REQUEST_METHOD_POST, null);
        this.mContext = mContext;
        this.mNoticeBoardMessage = mNoticeBoardMessage;
        this.callback = callback;

        mParams = new HashMap<>();
        mParams.put("noticeBoardId",mNoticeBoardMessage.getAdminId());
        mParams.put("text",mNoticeBoardMessage.getMsg());
        super.setParams(mParams);
    }


    @Override
    public void onResponseHandler(JSONObject response) {
        callback.PostNoticeBoardResponse(ResponseCode.COMMON_RES_SUCCESS,mNoticeBoardMessage);
    }

    @Override
    public void onErrorHandler(VolleyError error) {

        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
            callback.PostNoticeBoardResponse(resCode,mNoticeBoardMessage);
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

        callback.PostNoticeBoardResponse(resCode,mNoticeBoardMessage);
    }
}
