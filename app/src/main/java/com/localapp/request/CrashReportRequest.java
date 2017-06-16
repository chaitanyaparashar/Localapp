package com.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 4 way on 14-06-2017.
 */

public class CrashReportRequest extends CommonRequest {
    private Map<String, String> mParams;
    public static final String TAG = "CrashReportRequest";
    public CrashReportRequest(Context mContext, String errorMsg, String stackTrace) {
        super(mContext, RequestType.COMMON_REQUEST_REPORT_CRASH_ERROR, CommonRequestMethod.COMMON_REQUEST_METHOD_POST , null);

        mParams = new HashMap<>();
        mParams.put("errorMessage", errorMsg);
        mParams.put("stackTrace", stackTrace);

        Log.d(TAG, "called");
        super.setParams(mParams);
    }

    @Override
    public void onResponseHandler(JSONObject response) {
        Log.i(TAG, "error reported");
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        Log.e(TAG, "not send :(");
    }
}
