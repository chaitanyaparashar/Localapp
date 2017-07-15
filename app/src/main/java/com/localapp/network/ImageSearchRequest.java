package com.localapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.localapp.models.Profile;
import com.localapp.network.helper.CommonFileUpload;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.helper.VolleyErrorHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4 way on 06-04-2017.
 */

public class ImageSearchRequest {

    Context mContext;
    private File imageFile;
    private CommonFileUpload mFileUpload;
    private Profile userProfile;
    private List<Profile> profileList = new ArrayList<>();

    private ImageSearchResponseCallback mImageSearchResponseCallback;


    public ImageSearchRequest(Context mContext, File imageFile, ImageSearchResponseCallback mImageSearchResponseCallback) {
        this.mContext = mContext;
        this.imageFile = imageFile;
        this.mImageSearchResponseCallback = mImageSearchResponseCallback;
    }

    public interface ImageSearchResponseCallback {
        void ImageSearchResponse(CommonRequest.ResponseCode responseCode, List<Profile> uProfile, String errorMsg);
    }

    public void executeRequest() {

//        final String url = DOMAIN + "/searchImage";
        final String url = CommonRequest.DOMAIN + "/searchUsersByImage";

        /*Response.Listener<NetworkResponse> listener = new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String jsonStr = new String(response.data);
                String picUrl = null;

                JSONObject profile;

                try {
                    profile = new JSONObject(jsonStr).getJSONObject("data");

                    String uId = profile.getString("id");
                    String uName = profile.getString("name");
                    String uEmail = profile.getString("email");
                    String uPictureURL = profile.getString("picUrl");
                    String uToken = profile.getString("token");
                    String uMobile = profile.getString("mobile");
                    String uSpeciality = profile.getString("speciality");
                    String uNotes = profile.getString("notes");
                    String uProfession = profile.getString("profession");
                    String mPrivacy = null;
                    try {
                        mPrivacy = profile.getString("mobilePrivacy");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    LatLng latLng = null;
                    try {
                        JSONArray lngJsonArray = profile.getJSONArray("longlat");
                        if (lngJsonArray.length() > 0) {
                            latLng = new LatLng(Double.parseDouble(lngJsonArray.getString(0)), Double.parseDouble(lngJsonArray.getString(1)));
                        }
                    } catch (JSONException e) {
                        Log.v("Request", e.getMessage());
                    }


                    userProfile = new Profile(uId);

                    userProfile.setuEmail(uEmail);
                    userProfile.setuName(uName);
                    userProfile.setuPictureURL(uPictureURL);
                    userProfile.setuToken(uToken);
                    userProfile.setuMobile(uMobile);
                    userProfile.setuSpeciality(uSpeciality);
                    userProfile.setuNotes(uNotes);
                    userProfile.setuLatLng(latLng);
                    userProfile.setProfession(uProfession);
                    userProfile.setuPrivacy(mPrivacy);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mImageSearchResponseCallback.ImageSearchResponse(CommonRequest.ResponseCode.COMMON_RES_SUCCESS, userProfile, null);
            }
        };*/


        Response.Listener<NetworkResponse> listener = new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String jsonStr = new String(response.data);
                String picUrl = null;

                String uId = null;
                String uName  = null;
                String uEmail = null;
                String uPictureURL = null;
                String uToken  = null;
                String uMobile  = null;
                String uSpeciality  = null;
                String uNotes  = null;
                String uProfession = null;
                String mPrivacy = null;
                LatLng latLng = null;

                JSONArray jsonArray;

                try {
                    jsonArray = new JSONObject(jsonStr).getJSONArray("data");

                    for (int i = 0; i< jsonArray.length();i++){
                        JSONObject profile = jsonArray.getJSONObject(i);


                        uId = profile.getString("id");
                        uName = profile.getString("name");
                        uEmail = profile.getString("email");
                        uPictureURL = profile.getString("picUrl");
                        uToken = profile.getString("token");
                        uMobile = profile.getString("mobile");
                        uSpeciality = profile.getString("speciality");
                        uNotes = profile.getString("notes");
                        uProfession = profile.getString("profession");
                        mPrivacy = null;
                        try {
                            mPrivacy = profile.getString("mobilePrivacy");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        try {
                            JSONArray lngJsonArray = profile.getJSONArray("longlat");
                            if (lngJsonArray.length() > 0) {
                                latLng = new LatLng(Double.parseDouble(lngJsonArray.getString(0)), Double.parseDouble(lngJsonArray.getString(1)));
                            }
                        } catch (JSONException e) {
                            Log.v("Request", e.getMessage());
                        }





                        userProfile = new Profile(uId);

                        userProfile.setuEmail(uEmail);
                        userProfile.setuName(uName);
                        userProfile.setuPictureURL(uPictureURL);
                        userProfile.setuToken(uToken);
                        userProfile.setuMobile(uMobile);
                        userProfile.setuSpeciality(uSpeciality);
                        userProfile.setuNotes(uNotes);
                        userProfile.setuLatLng(latLng);
                        userProfile.setProfession(uProfession);
                        userProfile.setuPrivacy(mPrivacy);



                        profileList.add(userProfile);



                    }











                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mImageSearchResponseCallback.ImageSearchResponse(CommonRequest.ResponseCode.COMMON_RES_SUCCESS, profileList, null);
            }
        };



        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
                Log.v("onErrorHandler","error is" + error);
                CommonRequest.ResponseCode resCode = CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    resCode = CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
                    mImageSearchResponseCallback.ImageSearchResponse(resCode,profileList,null);
                    return;
                }
                if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_TIMEOUT)
                {
                    resCode = CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
                }
                else if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_UNKNOWN){
                    resCode = CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
                }
                else if (errorMsg == VolleyErrorHelper.COMMON_NETWORK_ERROR_NO_INTERNET){
                    resCode = CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
                }else {
                    resCode = CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;
//                    mSignUpData.setmErrorMessage(errorMsg);
                }

                mImageSearchResponseCallback.ImageSearchResponse(resCode,profileList,errorMsg);
            }
        };

        mFileUpload = new CommonFileUpload(mContext, imageFile, CommonFileUpload.FileType.COMMON_UPLOAD_FILE_TYPE_IMAGE,
                "file", url, null, listener, errorListener);

        mFileUpload.uploadFile();

    }
}
