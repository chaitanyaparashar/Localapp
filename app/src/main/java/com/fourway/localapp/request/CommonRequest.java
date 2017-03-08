package com.fourway.localapp.request;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fourway.localapp.request.helper.CustomRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by 4 way on 17-02-2017.
 */

public abstract class CommonRequest {

    private static final String SIGN_UP_REQUEST_URL = "";
    private static final String LOGIN_REQUEST_URL = "http://ec2-52-53-110-212.us-west-1.compute.amazonaws.com:8080/login";
    private static final String MAP_REQUEST_URL = "http://ec2-52-53-110-212.us-west-1.compute.amazonaws.com:8080/pinOnMap";
    private static final String FEED_REQUEST_URL = "http://52.172.157.120:8080/feed?";
    private static final String BROADCAST_REQUEST_URL = "http://52.172.157.120:8080/broadcast";
    private static final String PASSWORD_REQUEST_URL = "http://ec2-52-53-110-212.us-west-1.compute.amazonaws.com:8080/forgot/password?";

    public enum RequestType {
        COMMON_REQUEST_LOGIN,
        COMMON_REQUEST_SIGNUP,
        COMMON_REQUEST_MAP,
        COMMON_REQUEST_BROADCAST,
        COMMON_REQUEST_FEED,
        COMMON_REQUEST_PASSWORD
    }

    public enum ResponseCode  {
        COMMON_RES_SUCCESS,
        COMMON_RES_INTERNAL_ERROR,
        COMMON_RES_CONNECTION_TIMEOUT,
        COMMON_RES_FAILED_TO_CONNECT,
        COMMON_RES_IMAGE_NOT_FOUND,
        COMMON_RES_SERVER_ERROR_WITH_MESSAGE,
        COMMON_RES_FAILED_TO_UPLOAD,
        COMMON_REQUEST_END // WARNING: Add all request types above this line only
    }

    public enum CommonRequestMethod {
        COMMON_REQUEST_METHOD_GET,
        COMMON_REQUEST_METHOD_POST,

        COMMON_REQUEST_METHOD_END,
        COMMON_REQUEST_METHOD_PUT
    }

    /*---------------------------- Member variables -----------------------------------*/
    private String mURL;
    private CommonRequestMethod mMethod;
    private Map<String, String> mParams;
    private Map<String, String> mPostHeader;
    private JSONObject mJSONParams;
    private RequestType mRequestType;
    private Context mContext;


    public CommonRequest (Context context,RequestType type,
                          CommonRequestMethod reqMethod, Map<String, String> param){
        mContext = context; mRequestType = type; mMethod = reqMethod; mParams = param;
        mURL = getRequestTypeURL (mRequestType);
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setURL(String mURL) {
        this.mURL = mURL;
    }

    public String getURL() {
        return mURL;
    }

    public void setMethod(CommonRequestMethod mMethod) {
        this.mMethod = mMethod;
    }

    public void setParams(Map<String, String> mParams) {
        this.mParams = mParams;
    }

    public void setPostHeader(Map<String, String> mPostHeader) {
        this.mPostHeader = mPostHeader;
    }

    public void setJSONParams(JSONObject mJSONParams) {
        this.mJSONParams = mJSONParams;
    }

    public void setRequestType(RequestType mRequestType) {
        this.mRequestType = mRequestType;
    }


    public abstract void onResponseHandler (JSONObject response) ;
    public abstract void onErrorHandler (VolleyError error);

    public String getRequestTypeURL (RequestType type) {
        String url = null;

        switch (type) {
            case COMMON_REQUEST_LOGIN:
                url = LOGIN_REQUEST_URL;
                break;

            case COMMON_REQUEST_SIGNUP:
                url = SIGN_UP_REQUEST_URL;
                break;

            case COMMON_REQUEST_MAP:
                url = MAP_REQUEST_URL;
                break;
            case COMMON_REQUEST_BROADCAST:
                url = BROADCAST_REQUEST_URL;
                break;

            case COMMON_REQUEST_FEED:
                url = FEED_REQUEST_URL;
                break;
            case COMMON_REQUEST_PASSWORD:
                url = PASSWORD_REQUEST_URL;
                break;

        }

        return url;
    }

    public void executeRequest() {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onResponseHandler(response);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onErrorHandler(error);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        CustomRequest jsonObjRequest;

        if (mMethod == CommonRequestMethod.COMMON_REQUEST_METHOD_GET) {
            jsonObjRequest = new CustomRequest(mURL, null, listener, errorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return ((mPostHeader != null)? mPostHeader : super.getHeaders());
                }
            };
            requestQueue.add(jsonObjRequest);
        }else {
            jsonObjRequest = new CustomRequest(Request.Method.POST, mURL, mParams,listener, errorListener) {
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return ((mPostHeader != null)? mPostHeader : super.getHeaders());
                }
            };
            requestQueue.add(jsonObjRequest);
        }

        /*if (mMethod == CommonRequestMethod.COMMON_REQUEST_METHOD_PUT) {
            jsonObjRequest = new CustomRequest(Request.Method.PUT, mURL, mParams, listener, errorListener) {
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return ((mPostHeader != null)? mPostHeader : super.getHeaders());
                }
            };

            requestQueue.add(jsonObjRequest);
        }*/

    }

}
