package com.localapp.network.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.Map;

import static com.localapp.network.helper.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.localapp.network.helper.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.localapp.network.helper.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.localapp.network.helper.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;

/**
 * Created by Vijay Kumar on 19-06-2017.
 */

public class UpdatePostBackRequest extends CommonRequest {
    private Context mContext;
    private Map<String,String> mParams;
    private static final String TAG = "UpdatePostBackRequest";
    public UpdatePostBackRequest(Context mContext, String userId) {
        super(mContext, RequestType.COMMON_REQUEST_UPDATE_POSTBACK_STATUS, CommonRequestMethod.COMMON_REQUEST_METHOD_GET , null);

        this.mContext = mContext;

        String url = getRequestTypeURL(RequestType.COMMON_REQUEST_UPDATE_POSTBACK_STATUS);
        url += "id="+ userId;


        super.setURL(url);
    }

    @Override
    public void onResponseHandler(JSONObject response) {
        Log.i(TAG,"postback update success");
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);
        CommonRequest.ResponseCode resCode = COMMON_RES_INTERNAL_ERROR;



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

        Log.e(TAG, error.getMessage());
    }
}
