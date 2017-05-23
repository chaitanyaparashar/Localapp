package com.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.localapp.request.helper.VolleyErrorHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 4 way on 23-05-2017.
 */

public class UpdateFcmToken extends CommonRequest{
    private Map<String, String> mParams;
    private Context mContext;

    public UpdateFcmToken(Context mContext,String uId, String uToken, String fcmToken ) {
        super(mContext, RequestType.COMMON_REQUEST_UPDATE_FCM_TOKEN, CommonRequestMethod.COMMON_REQUEST_METHOD_POST, null);

        this.mContext = mContext;
        mParams = new HashMap<>();
        mParams.put("id",uId);
        mParams.put("token",uToken);
        mParams.put("fcmToken",fcmToken);

        super.setParams(mParams);
    }

    @Override
    public void onResponseHandler(JSONObject response) {
        Log.i("UpdateFcmToken","fcm update success");
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.d("UpdateFcmToken",errorMsg);
        Log.d("UpdateFcmToken",error.getMessage());
    }
}
