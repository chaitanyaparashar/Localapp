package com.localapp.network;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.VolleyError;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.helper.VolleyErrorHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vijay Kumar on 13-06-2017.
 */

public class LocationUpdateBackgroundRequest extends CommonRequest {
    private static final String TAG = "LocationBackground";
    private Context mContext;
    private Map<String, String> mParams;
    private Map<String, String> mHeader;

    public LocationUpdateBackgroundRequest(Context mContext, Location mLocation, String userId, String token) {
        super(mContext, RequestType.COMMON_REQUEST_LOCATION_UPDATE_IN_BACKGROUND, CommonRequestMethod.COMMON_REQUEST_METHOD_POST, null);
        this.mContext = mContext;

        mParams = new HashMap<>();
        mParams.put("latitude", String.valueOf(mLocation.getLatitude()));
        mParams.put("longitude", String.valueOf(mLocation.getLongitude()));
        mParams.put("userId",userId);


        mHeader = new HashMap<>();
        mHeader.put("token",token);

        super.setParams(mParams);
        super.setPostHeader(mHeader);

        super.setShouldCache(false);
    }

    @Override
    public void onResponseHandler(JSONObject response) {
        Log.i(TAG, "Location update in background");
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.e("onErrorHandler","error is" + error);
        Log.e("onErrorHandler","error is" + errorMsg);

        CommonRequest.ResponseCode resCode;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
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
        }



    }
}
