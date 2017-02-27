package com.fourway.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.fourway.localapp.data.GetFeedRequestData;
import com.fourway.localapp.data.Message;
import com.fourway.localapp.request.helper.VolleyErrorHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;

/**
 * Created by 4 way on 21-02-2017.
 */

public class GetFeedRequest extends CommonRequest {

    public interface GetFeedRequestCallback {
        void GetFeedResponse(CommonRequest.ResponseCode responseCode, GetFeedRequestData data);
    }

    private GetFeedRequestCallback mGetFeedRequestCallback;
    private GetFeedRequestData mRequestData;
    private Context mContext;

    public GetFeedRequest(Context context, LatLng latLng, GetFeedRequestCallback cb) {
        super(context, RequestType.COMMON_REQUEST_FEED, CommonRequestMethod.COMMON_REQUEST_METHOD_GET, null);
        this.mContext = context;
        mRequestData = new GetFeedRequestData();

        String url = getRequestTypeURL(RequestType.COMMON_REQUEST_FEED);
        url += "latitude=" + String.valueOf(latLng.latitude);
        url += "&longitude=" + String.valueOf(latLng.longitude);
        super.setURL(url);

        mGetFeedRequestCallback = cb;
    }

    @Override
    public void onResponseHandler(JSONObject response) {
        JSONArray msgJsonArray = null;
        try {
            msgJsonArray = response.getJSONArray("obj");
            int size = msgJsonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject msgJsonObject = msgJsonArray.getJSONObject(i);

                String mMobile ;
                String mEmail;
                String mText = null;
                String token = null;
                String sentAt;
                String name;
                String timeStamp;
                String mediaURL;
                String speciality;
                LatLng mLatLng;
                int emoji;

                try {
                    token = msgJsonObject.getString("token");
                    mText = msgJsonObject.getString("text");
                }catch (JSONException e) {

                }


                Message message = new Message();
                message.setToken(token);
                message.setmText(mText);

                mRequestData.addMessage(message);

            }

            mGetFeedRequestCallback.GetFeedResponse(ResponseCode.COMMON_RES_SUCCESS, mRequestData);


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
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
            mGetFeedRequestCallback.GetFeedResponse (resCode, mRequestData);
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
            mRequestData.setmErrorMessage(errorMsg);
        }

        mGetFeedRequestCallback.GetFeedResponse (resCode, mRequestData);
    }
}
