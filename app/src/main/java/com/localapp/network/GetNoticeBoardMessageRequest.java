package com.localapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.localapp.models.NoticeBoard;
import com.localapp.models.NoticeBoardMessage;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.helper.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 4 way on 08-04-2017.
 */

public class GetNoticeBoardMessageRequest extends CommonRequest {
    private Context mContext;
    private boolean hasSubscribed;
    private List<NoticeBoardMessage> mNoticeBoardMessageList;
    private GetNoticeBoardMessageRequestCallback messageRequestCallback;
    private NoticeBoard mNoticeBoard;
    private Map<String,String> mParams;

    public interface GetNoticeBoardMessageRequestCallback {
        void GetNoticeBoardMessageResponse(CommonRequest.ResponseCode responseCode, NoticeBoard mNoticeBoard, boolean hasSubscribed);
    }

    public GetNoticeBoardMessageRequest(Context mContext,NoticeBoard mNoticeBoard,boolean hasSubscribed, GetNoticeBoardMessageRequestCallback messageRequestCallback) {
        super(mContext, RequestType.COMMON_REQUEST_GET_NOTICE_BOARD_MSG, CommonRequestMethod.COMMON_REQUEST_METHOD_GET, null);
        this.mContext = mContext;
        this.mNoticeBoard = mNoticeBoard;
        this.hasSubscribed = hasSubscribed;
        this.messageRequestCallback = messageRequestCallback;

        mNoticeBoardMessageList = new ArrayList<>();

        mParams = new HashMap<>();
        mParams.put("noticeBoardId",mNoticeBoard.getId());
        super.setPostHeader(mParams);
    }

    @Override
    public void onResponseHandler(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray("data");
            int size = jsonArray.length();
            for (int i = 0;i< size;i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String msgID = jsonObject.getString("id");
                String msgText = jsonObject.getString("text");
                String timeStamp = jsonObject.getString("timestamp");

                NoticeBoardMessage message =new NoticeBoardMessage(msgID,msgText);
                message.setTimestamp(timeStamp);
                mNoticeBoardMessageList.add(message);

            }

            mNoticeBoard.setMessagesList(mNoticeBoardMessageList);

            messageRequestCallback.GetNoticeBoardMessageResponse(ResponseCode.COMMON_RES_SUCCESS,mNoticeBoard, hasSubscribed);
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
            resCode = ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
            messageRequestCallback.GetNoticeBoardMessageResponse(resCode,mNoticeBoard, hasSubscribed);
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

        messageRequestCallback.GetNoticeBoardMessageResponse(resCode,mNoticeBoard, hasSubscribed);
    }
}
