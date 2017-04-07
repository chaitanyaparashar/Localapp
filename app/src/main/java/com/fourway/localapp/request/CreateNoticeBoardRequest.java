package com.fourway.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.fourway.localapp.data.NoticeBoard;
import com.fourway.localapp.data.NoticeBoardMessage;
import com.fourway.localapp.request.helper.VolleyErrorHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;

/**
 * Created by 4 way on 31-03-2017.
 */

public class CreateNoticeBoardRequest extends CommonRequest {
    private Context mContext;
    private NoticeBoard mNoticeBoard;
    private Map<String, String> mParams;
    private CreateNoticeBoardResponseCallback mCreateNoticeBoardResponseCallback;

    public interface CreateNoticeBoardResponseCallback {
        void createNoticeBoardResponse(CommonRequest.ResponseCode responseCode, NoticeBoard data);
    }

    public CreateNoticeBoardRequest(Context mContext, NoticeBoard mNoticeBoard, CreateNoticeBoardResponseCallback cb) {
        super(mContext, RequestType.COMMON_REQUEST_CREATE_NOTICE_BOARD, CommonRequestMethod.COMMON_REQUEST_METHOD_POST, null);
        this.mContext = mContext;
        this.mNoticeBoard = mNoticeBoard;
        this.mCreateNoticeBoardResponseCallback = cb;

        mParams = new HashMap<>();
        mParams.put("name", mNoticeBoard.getName());
        mParams.put("adminId", mNoticeBoard.getAdminId());
        mParams.put("text", mNoticeBoard.getMessagesList().get(0).getMsg());
        super.setParams(mParams);
    }

    @Override
    public void onResponseHandler(JSONObject response) {
        JSONObject jsonObject = null;

        try {
            jsonObject = response.getJSONObject("data");

            JSONObject noticeBoardObject = jsonObject.getJSONObject("noticeBoard");
            JSONObject messageObject = noticeBoardObject.getJSONObject("lastMessage");

            String noticeBoardId = noticeBoardObject.getString("id");
            String noticeBoardName = noticeBoardObject.getString("name");
            String noticeBoardAdminId = noticeBoardObject.getString("adminId");
            JSONArray latlngJsonArray = null;
            LatLng latLng = null;
            try {
                latlngJsonArray = new JSONArray(noticeBoardObject.getString("longlat"));
                latLng = new LatLng(Double.valueOf(latlngJsonArray.getString(0)),Double.valueOf(latlngJsonArray.getString(1)));
            }catch (JSONException e){
                e.printStackTrace();
            }

            String msgID = null;
            String msgText = null;
            if (messageObject != null) {
                msgID = messageObject.getString("id");
                msgText = messageObject.getString("text");
            }


            mNoticeBoard.setId(noticeBoardId);
            mNoticeBoard.setAdminId(noticeBoardAdminId);
            mNoticeBoard.setName(noticeBoardName);
            mNoticeBoard.setLocation(latLng);

            List<NoticeBoardMessage> message = new ArrayList<>();
            message.add(new NoticeBoardMessage(msgID,msgText));
            mNoticeBoard.setMessagesList(message);

            mCreateNoticeBoardResponseCallback.createNoticeBoardResponse(ResponseCode.COMMON_RES_SUCCESS,mNoticeBoard);



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
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
            mCreateNoticeBoardResponseCallback.createNoticeBoardResponse (resCode, mNoticeBoard);
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
            mNoticeBoard.setErrorMessage(errorMsg);
        }

        mCreateNoticeBoardResponseCallback.createNoticeBoardResponse (resCode, mNoticeBoard);

    }


}
