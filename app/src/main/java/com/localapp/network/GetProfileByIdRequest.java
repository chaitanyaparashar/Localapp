package com.localapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.localapp.models.Profile;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.helper.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Vijay Kumar on 29-04-2017.
 */

public class GetProfileByIdRequest extends CommonRequest {

    private Profile mProfile;
    private Map<String, String> mParams;
    Context mContext;
    private GetProfileByIdRequestCallback mGetProdileByIdRequestCallback;

    public interface GetProfileByIdRequestCallback {
        void onProfileIdResponse(CommonRequest.ResponseCode responseCode, Profile mProfile);
    }

    public GetProfileByIdRequest(Context mContext, Profile mProfile,GetProfileByIdRequestCallback mGetProdileByIdRequestCallback) {
        super(mContext,RequestType.COMMON_REQUEST_GET_PROFILE_BY_ID,CommonRequestMethod.COMMON_REQUEST_METHOD_GET,null);

        this.mProfile = mProfile;
        this.mContext = mContext;
        String url = getRequestTypeURL(RequestType.COMMON_REQUEST_GET_PROFILE_BY_ID);
        url += "id=" + mProfile.getuId();
        super.setURL(url);

        this.mGetProdileByIdRequestCallback = mGetProdileByIdRequestCallback;

    }



    @Override
    public void onResponseHandler(JSONObject response) {
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

            LatLng latLng = null;
            try {
                JSONArray lngJsonArray = jsonObject.getJSONArray("longlat");
                if (lngJsonArray.length()>0) {
                    latLng = new LatLng(Double.parseDouble(lngJsonArray.getString(0)), Double.parseDouble(lngJsonArray.getString(1)));
                }
            }catch (JSONException e){
                Log.v("Request",e.getMessage());
            }

            mProfile.setuLatLng(latLng);


            mGetProdileByIdRequestCallback.onProfileIdResponse(ResponseCode.COMMON_RES_SUCCESS, mProfile);
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
            resCode = ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
            mGetProdileByIdRequestCallback.onProfileIdResponse(resCode, mProfile);
        }
        if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_TIMEOUT)
        {
            resCode = ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
        }
        else if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_UNKNOWN){
            resCode = ResponseCode.COMMON_RES_INTERNAL_ERROR;
        }
        else if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_NO_INTERNET){
            resCode = ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
        }else
        {
            resCode = ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;
            mProfile.setErrorMsg(errorMsg);
        }

        mGetProdileByIdRequestCallback.onProfileIdResponse(resCode, mProfile);
    }


}
