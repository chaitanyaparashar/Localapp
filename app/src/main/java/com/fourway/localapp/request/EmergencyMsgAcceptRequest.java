package com.fourway.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fourway.localapp.data.GetFeedRequestData;
import com.fourway.localapp.request.helper.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;

/**
 * Created by 4 way on 30-03-2017.
 */

public class EmergencyMsgAcceptRequest {

    Context mContext;
    private String emergencyID;
    private String acceptStr;
    private EmergencyMsgAcceptResponseCallback cb;

    public EmergencyMsgAcceptRequest(Context mContext, String emergencyID, String acceptStr, EmergencyMsgAcceptResponseCallback cb) {
        this.mContext = mContext;
        this.emergencyID = emergencyID;
        this.acceptStr = acceptStr;
        this.cb = cb;
    }

    public interface EmergencyMsgAcceptResponseCallback {
        void EmergencyMsgAcceptResponse(CommonRequest.ResponseCode responseCode);
    }

    public void executeRequest () {
        String url = "http://ec2-52-53-110-212.us-west-1.compute.amazonaws.com:8080/message/update";

        JSONObject js = new JSONObject();
        try {
            js.put("emergencyId",emergencyID);
            js.put("accept",acceptStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                cb.EmergencyMsgAcceptResponse(CommonRequest.ResponseCode.COMMON_RES_SUCCESS);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onErrorHandler(error);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, js, listener, errorListener) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }


    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);
        CommonRequest.ResponseCode resCode = COMMON_RES_INTERNAL_ERROR;
        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
            cb.EmergencyMsgAcceptResponse(resCode);
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
        }

        cb.EmergencyMsgAcceptResponse (resCode);

    }



}
