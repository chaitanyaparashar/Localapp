package com.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.localapp.data.NoticeBoard;
import com.localapp.data.NoticeBoardMessage;
import com.localapp.request.helper.VolleyErrorHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SUCCESS;

/**
 * Created by 4 way on 31-03-2017.
 */

public class MyNoticeBoardRequest extends CommonRequest{
    private Context mContext;
    private List<NoticeBoard> myNoticeBoardList;
    private List<NoticeBoard> subscribedNoticeBoardList;
    private MyNoticeBoardRequestCallback myNoticeBoardRequestCallback;


    public MyNoticeBoardRequest(Context mContext, String userID, MyNoticeBoardRequestCallback cb) {
        super(mContext, RequestType.COMMON_REQUEST_MY_NOTICE_BOARD, CommonRequestMethod.COMMON_REQUEST_METHOD_GET, null);
        this.mContext = mContext;
        this.myNoticeBoardList = new ArrayList<>();
        this.subscribedNoticeBoardList = new ArrayList<>();

        String url = getRequestTypeURL(RequestType.COMMON_REQUEST_MY_NOTICE_BOARD);
        url += "/"+userID;
        super.setURL(url);

        myNoticeBoardRequestCallback = cb;

    }

    @Override
    public void onResponseHandler(JSONObject response) {
        try {
            JSONObject mJsonArray = response.getJSONObject("data");
            JSONArray createdNoticeBoardArray = mJsonArray.getJSONArray("CreatedNoticeBoard");
            JSONArray subcribedNoticeBoardArray = null;
            try {
                subcribedNoticeBoardArray = mJsonArray.getJSONArray("SuscribedNoticeBoard");
            }catch (JSONException e) {
                e.printStackTrace();
            }

            int size = createdNoticeBoardArray.length();

            for (int i = 0;i<size; i++) {

                try {
                    JSONObject noticeJsonObject = createdNoticeBoardArray.getJSONObject(i);
                    JSONObject lastMsgObject = null;

                    try{
                        lastMsgObject = noticeJsonObject.getJSONObject("lastMessage");
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String noticeBoardId = noticeJsonObject.getString("id");
                    String noticeBoardName = noticeJsonObject.getString("name");
                    String noticeBoardAdminId = noticeJsonObject.getString("adminId");
                    JSONArray latlngJsonArray = null;
                    LatLng latLng = null;
                    try {
                        latlngJsonArray = new JSONArray(noticeJsonObject.getString("longlat"));
                        latLng = new LatLng(Double.valueOf(latlngJsonArray.getString(0)), Double.valueOf(latlngJsonArray.getString(1)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String msgID = null;
                    String msgText = null;



                    NoticeBoard mNoticeBoard = new NoticeBoard(noticeBoardAdminId,noticeBoardName);
                    List<NoticeBoardMessage> message = new ArrayList<>();


                    mNoticeBoard.setId(noticeBoardId);
                    mNoticeBoard.setLocation(latLng);

                    if (lastMsgObject != null) {
                        msgID = lastMsgObject.getString("id");
                        msgText = lastMsgObject.getString("text");
                        message.add(new NoticeBoardMessage(msgID,msgText));
                    }

                    mNoticeBoard.setMessagesList(message);


                    myNoticeBoardList.add(mNoticeBoard);



                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (subcribedNoticeBoardArray != null) {
                int sNoticeSize = subcribedNoticeBoardArray.length();
                for (int i =0; i<sNoticeSize; i++) {


                    JSONObject noticeJsonObject = subcribedNoticeBoardArray.getJSONObject(i);
                    JSONObject lastMsgObject = null;
                    try {
                        lastMsgObject = noticeJsonObject.getJSONObject("lastMessage");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }


                    String noticeBoardId = noticeJsonObject.getString("id");
                    String noticeBoardName = noticeJsonObject.getString("name");
                    String noticeBoardAdminId = noticeJsonObject.getString("adminId");
                    JSONArray latlngJsonArray = null;
                    LatLng latLng = null;
                    try {
                        latlngJsonArray = new JSONArray(noticeJsonObject.getString("longlat"));
                        latLng = new LatLng(Double.valueOf(latlngJsonArray.getString(0)), Double.valueOf(latlngJsonArray.getString(1)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String msgID = null;
                    String msgText = null;
                    if (lastMsgObject != null) {
                        msgID = lastMsgObject.getString("id");
                        msgText = lastMsgObject.getString("text");
                    }


                    NoticeBoard mNoticeBoard = new NoticeBoard(noticeBoardAdminId,noticeBoardName);
                    List<NoticeBoardMessage> message = new ArrayList<>();
                    message.add(new NoticeBoardMessage(msgID,msgText));

                    mNoticeBoard.setId(noticeBoardId);
                    mNoticeBoard.setLocation(latLng);
                    mNoticeBoard.setMessagesList(message);


                    subscribedNoticeBoardList.add(mNoticeBoard);

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        myNoticeBoardRequestCallback.MyNoticeBoardResponse(COMMON_RES_SUCCESS,myNoticeBoardList,subscribedNoticeBoardList);

    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
//            mGetFeedRequestCallback.GetFeedResponse (resCode, mRequestData, mRequestDataEmergency);
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

       myNoticeBoardRequestCallback.MyNoticeBoardResponse(COMMON_RES_SUCCESS,myNoticeBoardList,subscribedNoticeBoardList);
    }

    public interface MyNoticeBoardRequestCallback {
        void MyNoticeBoardResponse(CommonRequest.ResponseCode responseCode, List<NoticeBoard> myNoticeBoards, List<NoticeBoard> subscribedNoticeBoardList);
    }


}
