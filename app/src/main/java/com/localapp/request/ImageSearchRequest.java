package com.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.localapp.data.Profile;
import com.localapp.request.helper.CommonFileUpload;
import com.localapp.request.helper.VolleyErrorHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static com.localapp.request.CommonRequest.HOST_ADDRESS;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;

/**
 * Created by 4 way on 06-04-2017.
 */

public class ImageSearchRequest {

    Context mContext;
    private File imageFile;
    private CommonFileUpload mFileUpload;
    private Profile userProfile;

    private ImageSearchResponseCallback mImageSearchResponseCallback;


    public ImageSearchRequest(Context mContext, File imageFile, ImageSearchResponseCallback mImageSearchResponseCallback) {
        this.mContext = mContext;
        this.imageFile = imageFile;
        this.mImageSearchResponseCallback = mImageSearchResponseCallback;
    }

    public interface ImageSearchResponseCallback {
        void ImageSearchResponse(CommonRequest.ResponseCode responseCode, Profile uProfile, String errorMsg);
    }

    public void executeRequest() {

        final String url = HOST_ADDRESS + "/searchImage";

        Response.Listener<NetworkResponse> listener = new Response.Listener<NetworkResponse>() {
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
        };



        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
                Log.v("onErrorHandler","error is" + error);
                CommonRequest.ResponseCode resCode = COMMON_RES_INTERNAL_ERROR;
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    resCode = COMMON_RES_CONNECTION_TIMEOUT;
                    mImageSearchResponseCallback.ImageSearchResponse(resCode,userProfile,null);
                    return;
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
                }else {
                    resCode = COMMON_RES_SERVER_ERROR_WITH_MESSAGE;
//                    mSignUpData.setmErrorMessage(errorMsg);
                }

                mImageSearchResponseCallback.ImageSearchResponse(resCode,userProfile,errorMsg);
            }
        };

        mFileUpload = new CommonFileUpload(mContext, imageFile, CommonFileUpload.FileType.COMMON_UPLOAD_FILE_TYPE_IMAGE,
                "file", url, null, listener, errorListener);

        mFileUpload.uploadFile();

    }
}
