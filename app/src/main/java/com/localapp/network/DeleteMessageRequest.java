package com.localapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.localapp.models.Message;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.helper.VolleyErrorHelper;
import com.localapp.ui.activities.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vijay Kumar on 01-08-2017.
 */

public class DeleteMessageRequest extends CommonRequest{

    private Context mContext;
    private ArrayList<Message> messageyList;
    private Map<String ,String> mHeaders;
    private Map<String, String> mParames = new HashMap<>();
    private DeleteMessageResponseCallback messageResponseCallback;


    public DeleteMessageRequest(Context mContext, ArrayList<Message> messageyList, DeleteMessageResponseCallback messageResponseCallback) {
        super(mContext, RequestType.COMMON_REQUEST_DELETE_MESSAGE, CommonRequestMethod.COMMON_REQUEST_METHOD_POST, null);

        this.mContext = mContext;
        this.messageyList = messageyList;
        this.messageResponseCallback = messageResponseCallback;

        mHeaders = new HashMap<>();


        JSONArray jsonArray = new JSONArray();

        for (Message message : this.messageyList) {
            if (message.getId() != null) {
                jsonArray.put(message.getId());
            }
        }

        mHeaders.put("token", HomeActivity.mLoginToken);
        mHeaders.put("messageList",jsonArray.toString());


        super.setPostHeader(mHeaders);
        super.setParams(mParames);


    }

    @Override
    public void onResponseHandler(JSONObject response) {
        try {
            messageResponseCallback.onDeleteMessageResponse(ResponseCode.COMMON_RES_SUCCESS,response.getString("data"));
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
            messageResponseCallback.onDeleteMessageResponse(resCode,null);
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

        messageResponseCallback.onDeleteMessageResponse(resCode,null);


    }


    public interface DeleteMessageResponseCallback {
        void onDeleteMessageResponse(CommonRequest.ResponseCode responseCode,String data);
    }
}
