package com.fourway.localapp.request;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fourway.localapp.data.GetUsersRequestData;
import com.fourway.localapp.data.Profile;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public GetUsersRequest(Context context, LatLng latLng, GetUsersResponseCallback cb) {
        super(context, RequestType.COMMON_REQUEST_USERS, CommonRequestMethod.COMMON_REQUEST_METHOD_GET, null);
        mContext = context;

        mRequestData = new GetUsersRequestData();

        String url = getRequestTypeURL(RequestType.COMMON_REQUEST_USERS);
        url += "latitude=" + String.valueOf(latLng.latitude);
        url += "&longitude=" + String.valueOf(latLng.longitude);
        super.setURL(url);

        mGetUsersResponseCallback = cb;


    }


    @Override
    public void onResponseHandler(JSONObject response)  {
        JSONArray profileList = null;
        try {
            profileList = response.getJSONArray("obj");
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


                mRequestData.addProfile(mProfile);


            }
            mGetUsersResponseCallback.onGetUsersResponse(ResponseCode.COMMON_RES_SUCCESS, mRequestData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onErrorHandler(VolleyError error) {
        Toast.makeText(mContext, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
    }


}
