package com.localapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.localapp.models.GetFeedRequestData;
import com.localapp.models.Message;
import com.localapp.models.ReplyMessage;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.helper.VolleyErrorHelper;
import com.localapp.ui.fragments.FeedFragment;
import com.localapp.ui.activities.HomeActivity;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vijay Kumar on 21-02-2017.
 */

public class GetFeedRequest extends CommonRequest {

    public interface GetFeedRequestCallback {
        void GetFeedResponse(CommonRequest.ResponseCode responseCode, GetFeedRequestData data,GetFeedRequestData emergencyData);
    }

    private GetFeedRequestCallback mGetFeedRequestCallback;
    private GetFeedRequestData mRequestData;
    private GetFeedRequestData mRequestDataEmergency;
    private Context mContext;

    public GetFeedRequest(Context context, LatLng latLng, GetFeedRequestCallback cb) {
        super(context, RequestType.COMMON_REQUEST_FEED, CommonRequestMethod.COMMON_REQUEST_METHOD_GET, null);
        this.mContext = context;
        mRequestData = new GetFeedRequestData();
        mRequestDataEmergency = new GetFeedRequestData();

        String url = getRequestTypeURL(RequestType.COMMON_REQUEST_FEED);
        url += "latitude=" + String.valueOf(latLng.latitude);
        url += "&longitude=" + String.valueOf(latLng.longitude);
        url += "&radius=5";
        super.setURL(url);

        mGetFeedRequestCallback = cb;
    }

    @Override
    public void onResponseHandler(JSONObject response) {
        JSONArray msgJsonArray = null;
        try {
            msgJsonArray = response.getJSONArray("data");
            int size = msgJsonArray.length();
            int index = 0;
            if (size > 50) {
                index = size - 50;//get only last 50 message
            }

            for (int i = index; i < size; i++) {
                JSONObject msgJsonObject = msgJsonArray.getJSONObject(i);

                String mUserID = "";
                String id = "";
                String emergencyId = "";
                String picUrl = "";

                String mediaURL = "";

                FeedFragment.MediaType mediaType = null;
                String mMobile = "";
                String mEmail = "";
                String mText = "";
                String token = "";
                String timeStamp = "";
                FeedFragment.MessageType messageType = null;
                String sentAt;
                String name = "";
                String speciality = "";
                String accepted = "";

                String replyMessageId = null;
                ReplyMessage replyMessage = null;

                int emoji;

                JSONArray latlngJsonArray = new JSONArray(msgJsonObject.getString("longLat"));
                LatLng mLatLng = null;


                try {
                    id = msgJsonObject.getString("id");
                    mUserID = msgJsonObject.getString("userId");
                    emergencyId = msgJsonObject.getString("emergencyId");
                    picUrl = msgJsonObject.getString("picUrl");
                    mediaURL = msgJsonObject.getString("mediaUrl");
                    try {
                        mediaType = FeedFragment.MediaType.values()[Integer.parseInt(msgJsonObject.getString("mediaType"))];
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                    mMobile = msgJsonObject.getString("mobile");
                    mEmail = msgJsonObject.getString("email");
                    mText = msgJsonObject.getString("text");
                    token = msgJsonObject.getString("token");
                    timeStamp = msgJsonObject.getString("timestamp");
                    messageType = FeedFragment.getMessageType(msgJsonObject.getInt("messageType"));
                    name = msgJsonObject.getString("name");
                    speciality = msgJsonObject.getString("speciality");
                    accepted = msgJsonObject.getString("accept");
                    mLatLng = new LatLng(Double.valueOf(latlngJsonArray.getString(0)),Double.valueOf(latlngJsonArray.getString(1)));

                    }catch (JSONException e) {
                    e.printStackTrace();
                }

                //for reply message
                try {
                    replyMessageId = msgJsonObject.getString("replyId");
                    JSONObject replyMessageObject = msgJsonObject.getJSONObject("replyMessage");
                    replyMessage = new ReplyMessage();
                    replyMessage.setId(replyMessageObject.getString("id"));
                    replyMessage.setName(replyMessageObject.getString("name"));
                    replyMessage.setTextMessage(replyMessageObject.getString("text"));
                }catch (JSONException je) {
                    je.printStackTrace();
                }


                Message message = new Message();
                message.setReplyMessageId(replyMessageId);
                message.setReplyMessage(replyMessage);
                message.setId(id);
                message.setmUserID(mUserID);
                message.setMsgIdOnlyForFrontEnd(emergencyId);
                message.setPicUrl(picUrl);
                message.setMediaURL(mediaURL);
                message.setMediaType(mediaType);
                message.setmMobile(mMobile);
                message.setmEmail(mEmail);
                message.setmText(mText);
                message.setToken(token);
                message.setTimeStamp(timeStamp);
                message.setMessageType(messageType);
                message.setName(name);
                message.setSpeciality(speciality);
                message.setAccepted(accepted);
                message.setmLatLng(mLatLng);

                mRequestData.addMessage(message);

                if (message.getMessageType() == FeedFragment.MessageType.EMERGENCY && !message.getAccepted().equals("1") &&
                        !message.getMsgIdOnlyForFrontEnd().equals("null") && HomeActivity.mUserId != null && !HomeActivity.mUserId.equals(message.getmUserID())) {

                    mRequestDataEmergency.addMessage(message);
                }

            }

            mGetFeedRequestCallback.GetFeedResponse(ResponseCode.COMMON_RES_SUCCESS, mRequestData,mRequestDataEmergency);


        }catch (JSONException e) {
            Log.v("GetFeedRequest", e.getMessage());

        }
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
            mGetFeedRequestCallback.GetFeedResponse (resCode, mRequestData, mRequestDataEmergency);
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
            mRequestData.setmErrorMessage(errorMsg);
        }

        mGetFeedRequestCallback.GetFeedResponse (resCode, mRequestData, mRequestDataEmergency);
    }
}
