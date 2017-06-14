package com.localapp.request;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.localapp.appcontroller.AppController;
import com.localapp.request.helper.CustomRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by 4 way on 17-02-2017.
 */

public abstract class
CommonRequest {

    static final String DOMAIN = "http://13.56.50.98:8080";
//    static final String DOMAIN = "http://192.172.2.178:8080";//localhost
    private static final String LOGIN_REQUEST_URL = DOMAIN + "/login";
//    private static final String LOGIN_REQUEST_URL = "http://192.172.3.78:8080/login";//local
    private static final String MAP_REQUEST_URL = DOMAIN + "/pinOnMap";
    private static final String FEED_REQUEST_URL = DOMAIN + "/feed?";
    private static final String BROADCAST_REQUEST_URL = DOMAIN + "/broadcast";
    private static final String PASSWORD_REQUEST_URL = DOMAIN + "/forgot/Password?";
    private static final String MSG_ACCEPT_URL = DOMAIN + "/message/update";
    private static final String CREATE_NOTICE_BOARD_URL = DOMAIN + "/createNoticeBoard";
    private static final String GET_MY_NOTICE_BOARD_URL = DOMAIN + "/getAllCreatedNoticeBoard";
    private static final String POST_NOTICE_BOARD_MSG_URL = DOMAIN + "/postMessageInNoticeBoard";
    private static final String GET_NEAREST_NOTICE_BOARD_URL = DOMAIN + "/nearestNoticeBoard?";
    private static final String GET_NOTICE_BOARD_MSG_URL = DOMAIN + "/getMessageFromNoticeBoard";
    private static final String SUBSCRIBE_NOTICE_BOARD_URL = DOMAIN + "/suscribeNoticeBoard";
    private static final String UNSUBSCRIBE_NOTICE_BOARD_URL = DOMAIN + "/unsuscribeNoticeBoard";
    private static final String DELETE_NOTICE_BOARD_URL = DOMAIN + "/DeleteNoticeBoard";
    private static final String DELETE_NOTICE_BOARD_MSG_URL = DOMAIN + "/deleteMessageFromNoticeBoard";
    private static final String GET_PROFILE_REQUEST_URL = DOMAIN + "/getUserById";
    private static final String GET_PROFILE_BY_ID_REQUEST_URL = DOMAIN + "/getProfileById?";
    private static final String UPDATE_PROFILE_REQUEST_URL = DOMAIN + "/update";
    private static final String UPDATE_EMAIL_REQUEST_URL = DOMAIN + "/updateEmail?";
    private static final String FB_LOGIN_REQUEST_URL = DOMAIN + "/facebookLogin";
    private static final String UPDATE_FCM_TOKEN_REQUEST_URL = DOMAIN + "/updateFcmToken";
    private static final String FCM_PUSH_NOTIFICATION_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String LOCALAPP_INVITE_URL = DOMAIN + "/email/doMail?";
    private static final String LOCATION_UPDATE_IN_BACKGROUND_URL = DOMAIN + "/locationUpdate";

    public enum RequestType {
        COMMON_REQUEST_LOGIN,
        COMMON_REQUEST_MAP,
        COMMON_REQUEST_BROADCAST,
        COMMON_REQUEST_FEED,
        COMMON_REQUEST_PASSWORD,
        COMMON_REQUEST_MSG_ACCEPT,
        COMMON_REQUEST_CREATE_NOTICE_BOARD,
        COMMON_REQUEST_MY_NOTICE_BOARD,
        COMMON_REQUEST_POST_NOTICE_BOARD_MSG,
        COMMON_REQUEST_NEAREST_NOTICE_BOARD,
        COMMON_REQUEST_GET_NOTICE_BOARD_MSG,
        COMMON_REQUEST_SUBSCRIBE_NOTICE_BOARD,
        COMMON_REQUEST_UNSUBSCRIBE_NOTICE_BOARD,
        COMMON_REQUEST_DELETE_NOTICE_BOARD,
        COMMON_REQUEST_DELETE_NOTICE_BOARD_MSG,
        COMMON_REQUEST_GET_PROFILE,
        COMMON_REQUEST_UPDATE_PROFILE,
        COMMON_REQUEST_FCM_PUSH_NOTIFICATION,
        COMMON_REQUEST_GET_PROFILE_BY_ID,
        COMMON_REQUEST_FB_LOGIN,
        COMMON_REQUEST_LOCALAPP_INVITE,
        COMMON_REQUEST_UPDATE_EMAIL,
        COMMON_REQUEST_UPDATE_FCM_TOKEN,
        COMMON_REQUEST_LOCATION_UPDATE_IN_BACKGROUND

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
            case COMMON_REQUEST_MSG_ACCEPT:
                url = MSG_ACCEPT_URL;
                break;
            case COMMON_REQUEST_CREATE_NOTICE_BOARD:
                url = CREATE_NOTICE_BOARD_URL;
                break;
            case COMMON_REQUEST_MY_NOTICE_BOARD:
                url = GET_MY_NOTICE_BOARD_URL;
                break;

            case COMMON_REQUEST_POST_NOTICE_BOARD_MSG:
                url = POST_NOTICE_BOARD_MSG_URL;
                break;

            case COMMON_REQUEST_NEAREST_NOTICE_BOARD:
                url = GET_NEAREST_NOTICE_BOARD_URL;
                break;
            case COMMON_REQUEST_GET_NOTICE_BOARD_MSG:
                url = GET_NOTICE_BOARD_MSG_URL;
                break;
            case COMMON_REQUEST_SUBSCRIBE_NOTICE_BOARD:
                url = SUBSCRIBE_NOTICE_BOARD_URL;
                break;
            case COMMON_REQUEST_UNSUBSCRIBE_NOTICE_BOARD:
                url = UNSUBSCRIBE_NOTICE_BOARD_URL;
                break;

            case COMMON_REQUEST_DELETE_NOTICE_BOARD:
                url = DELETE_NOTICE_BOARD_URL;
                break;

            case COMMON_REQUEST_DELETE_NOTICE_BOARD_MSG:
                url = DELETE_NOTICE_BOARD_MSG_URL;
                break;
            case COMMON_REQUEST_GET_PROFILE:
                url = GET_PROFILE_REQUEST_URL;
                break;
            case COMMON_REQUEST_UPDATE_PROFILE:
                url = UPDATE_PROFILE_REQUEST_URL;
                break;

            case COMMON_REQUEST_FCM_PUSH_NOTIFICATION:
                url = FCM_PUSH_NOTIFICATION_URL;
                break;
            case COMMON_REQUEST_GET_PROFILE_BY_ID:
                url = GET_PROFILE_BY_ID_REQUEST_URL;
                break;

            case COMMON_REQUEST_FB_LOGIN:
                 url = FB_LOGIN_REQUEST_URL;
                 break;
            case COMMON_REQUEST_LOCALAPP_INVITE:
                 url = LOCALAPP_INVITE_URL;
                 break;

            case COMMON_REQUEST_UPDATE_EMAIL:
                 url = UPDATE_EMAIL_REQUEST_URL;
                 break;

            case COMMON_REQUEST_UPDATE_FCM_TOKEN:
                 url = UPDATE_FCM_TOKEN_REQUEST_URL;
                 break;

            case COMMON_REQUEST_LOCATION_UPDATE_IN_BACKGROUND:
                url = LOCATION_UPDATE_IN_BACKGROUND_URL;
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


//        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        RequestQueue requestQueue = Volley.newRequestQueue(AppController.getAppContext());

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


            try {
                requestQueue.add(jsonObjRequest);
            }catch (Exception e){
                e.printStackTrace();
            }

        }



    }

}
