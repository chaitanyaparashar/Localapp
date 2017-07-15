package com.localapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.localapp.models.Profile;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.helper.VolleyErrorHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.localapp.network.helper.CommonRequest.ResponseCode.*;

import static com.localapp.network.SignUpRequest.*;

/**
 * Created by 4 way on 19-04-2017.
 */

public class UpdateProfileRequest extends CommonRequest {
    private Profile mProfile;
    private Map<String, String> mParams;
    private Context mContext;
    private UpdateProfileResponseCallback mUpdateProfileResponseCallback;

    public interface UpdateProfileResponseCallback {
        void onUpdateProfileResponse(CommonRequest.ResponseCode responseCode);
    }



    public UpdateProfileRequest(Context mContext, Profile mProfile, UpdateProfileResponseCallback mUpdateProfileResponseCallback) {
        super(mContext, RequestType.COMMON_REQUEST_UPDATE_PROFILE, CommonRequestMethod.COMMON_REQUEST_METHOD_POST, null);
        this.mContext = mContext;
        this.mProfile = mProfile;
        this.mUpdateProfileResponseCallback = mUpdateProfileResponseCallback;

        mParams = new HashMap<>();

        mParams.put(JSON_FIELD_NAME, mProfile.getuName());
        mParams.put(JSON_FIELD_EMAIL_ID, mProfile.getuEmail());
        mParams.put(JSON_FIELD_MOBILE_NUMBER, mProfile.getuMobile());
        mParams.put(JSON_FIELD_SPECIALITY, mProfile.getuSpeciality());
        mParams.put(JSON_FIELD_NOTES, mProfile.getuNotes());
        mParams.put(JSON_FIELD_PROFESSION, mProfile.getProfession());
        mParams.put(JSON_FIELD_MOBILE_PRIVACY, mProfile.getuPrivacy());
        mParams.put("token", mProfile.getuToken());
        mParams.put("fcmToken",mProfile.getFcmToken());

        super.setParams(mParams);
        super.setPostHeader(mParams);
    }

    @Override
    public void onResponseHandler(JSONObject response) {
        mUpdateProfileResponseCallback.onUpdateProfileResponse(ResponseCode.COMMON_RES_SUCCESS);
    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = COMMON_RES_CONNECTION_TIMEOUT;
            mUpdateProfileResponseCallback.onUpdateProfileResponse(resCode);
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

        mUpdateProfileResponseCallback.onUpdateProfileResponse(resCode);
    }




}
