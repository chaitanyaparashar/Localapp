package com.localapp.request;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.localapp.request.helper.VolleyErrorHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;

/**
 * Created by 4 way on 13-06-2017.
 */

public class LocationUpdateBackgroundRequest extends CommonRequest{
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
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
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
        }



    }
}
