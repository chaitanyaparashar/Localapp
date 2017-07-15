package com.localapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.localapp.models.NoticeBoard;
import com.localapp.network.helper.CommonRequest;
import com.localapp.network.helper.VolleyErrorHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 4 way on 08-04-2017.
 */

public class DeleteNoticeBoardRequest extends CommonRequest {
    private Context mContext;
    private NoticeBoard mNoticeBoard;
    private Map<String,String> mParams;
    private DeleteNoticeBoardResponseCallback mDeleteNoticeBoardResponseCallback;



    public interface DeleteNoticeBoardResponseCallback {
        void deleteNoticeBoardResponse(CommonRequest.ResponseCode responseCode);
    }

    public DeleteNoticeBoardRequest(Context mContext, NoticeBoard mNoticeBoard,DeleteNoticeBoardResponseCallback cb) {
        super(mContext,RequestType.COMMON_REQUEST_DELETE_NOTICE_BOARD,CommonRequestMethod.COMMON_REQUEST_METHOD_POST,null);
        this.mContext = mContext;
        this.mNoticeBoard = mNoticeBoard;

        this.mDeleteNoticeBoardResponseCallback = cb;

        mParams = new HashMap<>();
        mParams.put("noticeBoardId", mNoticeBoard.getId());
        mParams.put("adminId", mNoticeBoard.getAdminId());
        super.setParams(mParams);
    }

    @Override
    public void onResponseHandler(JSONObject response) {
        mDeleteNoticeBoardResponseCallback.deleteNoticeBoardResponse(ResponseCode.COMMON_RES_SUCCESS);

    }

    @Override
    public void onErrorHandler(VolleyError error) {
        String errorMsg = VolleyErrorHelper.getMessage(error, mContext);
        Log.v("onErrorHandler","error is" + error);

        CommonRequest.ResponseCode resCode;

        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
            resCode = ResponseCode.COMMON_RES_CONNECTION_TIMEOUT;
            mDeleteNoticeBoardResponseCallback.deleteNoticeBoardResponse(resCode);
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
//            mRequestData.setmErrorMessage(errorMsg);
        }

        mDeleteNoticeBoardResponseCallback.deleteNoticeBoardResponse(resCode);
    }
}
