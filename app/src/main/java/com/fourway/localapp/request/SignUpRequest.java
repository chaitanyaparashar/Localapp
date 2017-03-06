package com.fourway.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fourway.localapp.data.SignUpData;
import com.fourway.localapp.request.helper.CommonFileUpload;
import com.fourway.localapp.request.helper.VolleyErrorHelper;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;

/**
 * Created by 4 way on 27-02-2017.
 */

public class SignUpRequest {

    private static final String JSON_FIELD_NAME = "name";
    private static final String JSON_FIELD_PASSWORD = "password";
    private static final String JSON_FIELD_EMAIL_ID = "email";
    private static final String JSON_FIELD_MOBILE_NUMBER = "mobile";
    private static final String JSON_FIELD_SPECIALITY = "speciality";
    private static final String JSON_FIELD_NOTES = "notes";
    private static final String JSON_FIELD_FILE = "file";
    private static final String JSON_FIELD_M_PLACE = "place";
    private static final String JSON_FIELD_M_LOCATION = "location";
    private static final String JSON_FIELD_TYPE = "type";


    private Context mContext;

    private Map<String, String> mParams;
    private SignUpData mSignUpData;

    private CommonFileUpload mFileUpload;

    public interface SignUpResponseCallback {
        void onSignUpResponse(CommonRequest.ResponseCode res);
    }
    private SignUpResponseCallback mSignUpResponseCallback;

    public SignUpRequest(Context context, SignUpData data, SignUpResponseCallback cb) {

        mContext = context;
        mSignUpData = data;
        mParams = new HashMap<>();
        /*mParams.put("Content-Type", "multipart/form-data");*/
        mParams.put(JSON_FIELD_NAME, data.getmName());
        mParams.put(JSON_FIELD_EMAIL_ID, data.getmEmail());
        mParams.put(JSON_FIELD_MOBILE_NUMBER, data.getmMobile());
        mParams.put(JSON_FIELD_PASSWORD, data.getmPassword());
        mParams.put(JSON_FIELD_SPECIALITY, data.getmSpeciality());
        mParams.put(JSON_FIELD_NOTES, data.getmDetails());
//        mParams.put(JSON_FIELD_TYPE, data.getmType());

        mSignUpResponseCallback = cb;
    }


    public void executeRequest() {
        String url = "http://ec2-52-53-110-212.us-west-1.compute.amazonaws.com:8080/registerPic";//url for registration with pic

        Response.Listener<NetworkResponse> listener = new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String jsonStr = new String(response.data);

                if (jsonStr.equals("file saved successfully")) {
                    mSignUpResponseCallback.onSignUpResponse(CommonRequest.ResponseCode.COMMON_RES_SUCCESS);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
                Log.v("onErrorHandler","error is" + error);
                CommonRequest.ResponseCode resCode = COMMON_RES_INTERNAL_ERROR;
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    resCode = COMMON_RES_CONNECTION_TIMEOUT;
                    mSignUpResponseCallback.onSignUpResponse(resCode);
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

                mSignUpResponseCallback.onSignUpResponse (resCode);
            }
        };

        mFileUpload = new CommonFileUpload(mContext, mSignUpData.getPicFile(), CommonFileUpload.FileType.COMMON_UPLOAD_FILE_TYPE_IMAGE,
                mSignUpData.getmEmail(),url,null,listener,errorListener);

        mFileUpload.setParam(mParams);
        mFileUpload.uploadFile();
    }
}
