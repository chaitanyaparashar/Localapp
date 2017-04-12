package com.localapp.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.localapp.data.NoticeBoard;
import com.localapp.request.helper.VolleyErrorHelper;
import com.localapp.ui.HomeActivity;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_FAILED_TO_CONNECT;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_INTERNAL_ERROR;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE;
import static com.localapp.request.CommonRequest.ResponseCode.COMMON_RES_SUCCESS;

/**
 * Created by 4 way on 08-04-2017.
 */

public class GetNearestNoticeBoardRequest extends CommonRequest {
    private Context mContext;
    private List<NoticeBoard> mNoticeBoardList;
    private GetNearestNoticeBoardRequestCallback mRequestCallback;
    private LatLng mLatLng;


    public interface GetNearestNoticeBoardRequestCallback {
        void GetNearestNoticeBoardResponse(CommonRequest.ResponseCode responseCode, List<NoticeBoard> mNoticeBoards);
    }

    public GetNearestNoticeBoardRequest(Context mContext, GetNearestNoticeBoardRequestCallback mRequestCallback, LatLng mLatLng) {
        super(mContext, RequestType.COMMON_REQUEST_NEAREST_NOTICE_BOARD, CommonRequestMethod.COMMON_REQUEST_METHOD_GET, null);
        this.mContext = mContext;
        this.mRequestCallback = mRequestCallback;
        this.mLatLng = mLatLng;

        mNoticeBoardList = new ArrayList<>();

        String url = getRequestTypeURL(RequestType.COMMON_REQUEST_NEAREST_NOTICE_BOARD);
        url += "latitude="+mLatLng.latitude;
        url += "&longitude="+mLatLng.longitude;
        url += "&radius=3";
        super.setURL(url);
    }

    @Override
    public void onResponseHandler(JSONObject response) {

        try {
            JSONArray mJsonArray = response.getJSONArray("data");
            int size = mJsonArray.length();
            for (int i = 0; i<size ;i ++) {
                JSONObject jsonObject = mJsonArray.getJSONObject(i);

                String noticeBoardId = jsonObject.getString("id");
                String noticeBoardName = jsonObject.getString("name");
                String noticeBoardAdminId = jsonObject.getString("adminId");

                NoticeBoard mNoticeBoard = new NoticeBoard(noticeBoardAdminId,noticeBoardName);
                mNoticeBoard.setId(noticeBoardId);

                if (HomeActivity.mPicUrl == null || !HomeActivity.mUserId.equals(mNoticeBoard.getAdminId()))
                mNoticeBoardList.add(mNoticeBoard);
            }

            mRequestCallback.GetNearestNoticeBoardResponse(COMMON_RES_SUCCESS,mNoticeBoardList);

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
            mRequestCallback.GetNearestNoticeBoardResponse(resCode,mNoticeBoardList);
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
//            mRequestData.setmErrorMessage(errorMsg);
        }

        mRequestCallback.GetNearestNoticeBoardResponse(resCode,mNoticeBoardList);
    }
}
