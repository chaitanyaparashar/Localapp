package com.fourway.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.fourway.localapp.data.GetUsersRequestData;
import com.fourway.localapp.data.Profile;
import com.fourway.localapp.request.helper.VolleyErrorHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.fourway.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;

/**
 * Created by 4 way on 20-02-2017.
 */

public class GetUsersRequest extends CommonRequest {


    public interface GetUsersResponseCallback {
        void onGetUsersResponse(CommonRequest.ResponseCode res, GetUsersRequestData data);
    }

    private GetUsersResponseCallback mGetUsersResponseCallback;
    private GetUsersRequestData mRequestData;
    private Context mContext;
    private Map<String, String> mParams;
    private Map<String, String> mHeaders;
    private String mToken;

    public GetUsersRequest(Context context, LatLng latLng, String mToken, GetUsersResponseCallback cb) {
        super(context, RequestType.COMMON_REQUEST_MAP, CommonRequestMethod.COMMON_REQUEST_METHOD_POST, null);
        mContext = context;

        mRequestData = new GetUsersRequestData();

        this.mToken = mToken;

        mParams = new HashMap<>();
        mParams.put("latitude", String.valueOf(latLng.latitude));
        mParams.put("longitude", String.valueOf(latLng.longitude));
        super.setParams(mParams);

        mHeaders = new HashMap<>();
        mHeaders.put("token", mToken);
        super.setPostHeader(mHeaders);

        mGetUsersResponseCallback = cb;


    }


    @Override
    public void onResponseHandler(JSONObject response)  {
        JSONArray profileList = null;
        try {
            profileList = response.getJSONArray("data");
            int size = profileList.length();
            for (int i = 0; i < size; i++) {
                JSONObject profile = profileList.getJSONObject(i);
                String uId = profile.getString("id");
                String uName =  profile.getString("name");
                String uEmail =  profile.getString("email");
                String uPictureURL =  profile.getString("picUrl");
                String uToken =  profile.getString("token");
                String uMobile =  profile.getString("mobile");
                String uSpeciality =  profile.getString("speciality");
                String uNotes =  profile.getString("notes");

                LatLng latLng = null;
                try {
                    JSONArray lngJsonArray = profile.getJSONArray("longlat");
                    if (lngJsonArray.length()>0) {
                        latLng = new LatLng(Double.parseDouble(lngJsonArray.getString(0)), Double.parseDouble(lngJsonArray.getString(1)));
                    }
                }catch (JSONException e){
                    Log.v("Request",e.getMessage());
                }


                Profile mProfile = new Profile(uId);

                mProfile.setuEmail(uEmail);
                mProfile.setuName(uName);
                mProfile.setuPictureURL(uPictureURL);
                mProfile.setuToken(uToken);
                mProfile.setuMobile(uMobile);
                mProfile.setuSpeciality(uSpeciality);
                mProfile.setuNotes(uNotes);
                mProfile.setuLatLng(latLng);


//                if (mProfile.getuPictureURL() != "null") {
                    mRequestData.addProfile(mProfile);
//                }


            }
            mGetUsersResponseCallback.onGetUsersResponse(ResponseCode.COMMON_RES_SUCCESS, mRequestData);
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
            mGetUsersResponseCallback.onGetUsersResponse (resCode, mRequestData);
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
            mRequestData.setmErrorMessage(errorMsg);
        }

        mGetUsersResponseCallback.onGetUsersResponse (resCode, mRequestData);
        
    }


}
