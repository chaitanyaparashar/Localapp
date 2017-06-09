package com.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.localapp.data.Profile;
import com.localapp.request.helper.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SUCCESS;

/**
 * Created by 4 way on 19-04-2017.
 */

public class GetProfileRequest extends CommonRequest {
    private Profile mProfile;
    private Map<String, String> mParams;
    Context mContext;
    private GetProfileRequestCallback mGetProfileRequestCallback;


    public interface GetProfileRequestCallback {
        void onProfileResponse(CommonRequest.ResponseCode responseCode,Profile mProfile);
    }

    public GetProfileRequest(Context mContext, Profile mProfile,GetProfileRequestCallback mGetProfileRequestCallback) {
        super(mContext,RequestType.COMMON_REQUEST_GET_PROFILE,CommonRequestMethod.COMMON_REQUEST_METHOD_POST,null);



        this.mProfile = mProfile;
        this.mContext = mContext;

        mParams = new HashMap<>();
        mParams.put("userId",mProfile.getuId());
        mParams.put("token",mProfile.getuToken());
        super.setParams(mParams);

        this.mGetProfileRequestCallback = mGetProfileRequestCallback;

    }


    @Override
    public void onResponseHandler(JSONObject response) {
//TODO: Need to change parsing as per response from server
        try {
            JSONObject jsonObject = response.getJSONObject("data");
            mProfile.setuId(jsonObject.getString("id"));
            mProfile.setuToken(jsonObject.getString("token"));
            mProfile.setuName(jsonObject.getString("name"));
            mProfile.setuMobile(jsonObject.getString("mobile"));
            mProfile.setuEmail(jsonObject.getString("email"));
            mProfile.setuSpeciality(jsonObject.getString("speciality"));
            mProfile.setuPictureURL(jsonObject.getString("picUrl"));
            mProfile.setProfession(jsonObject.getString("profession"));
            mProfile.setuNotes(jsonObject.getString("notes"));
            mProfile.setuPrivacy(jsonObject.getString("mobilePrivacy"));

            try {
                String fcmToken = jsonObject.getString("fcmToken");
                if (fcmToken == null || fcmToken.equals("null")) {     //update fcm token if fcm token is null
                    fcmToken = FirebaseInstanceId.getInstance().getToken();
                    UpdateFcmTokenRequest request = new UpdateFcmTokenRequest(mContext,mProfile.getuId(), mProfile.getuToken(),fcmToken);
                    request.executeRequest();
                }

            }catch (JSONException ignore){
            }


            mGetProfileRequestCallback.onProfileResponse(COMMON_RES_SUCCESS, mProfile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
            mGetProfileRequestCallback.onProfileResponse(resCode, mProfile);
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
            mProfile.setErrorMsg(errorMsg);
        }

        mGetProfileRequestCallback.onProfileResponse(resCode, mProfile);
    }
}
