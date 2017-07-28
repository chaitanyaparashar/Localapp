package com.localapp.fcm;

import android.content.Context;
import android.util.Log;

import com.localapp.R;
import com.localapp.models.NotificationData;
import com.localapp.ui.activities.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static com.localapp.fcm.FcmMessagingService.NotiEmergency;


/**
 * Created by Vijay Kumar on 25-04-2017.
 */

public class FcmNotificationRequest {
    private Context mContext;
    private NotificationData mNotificationData;
    private JSONObject mJsonObject;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public FcmNotificationRequest(Context mContext, NotificationData mData) {
        this.mContext = mContext;
        this.mNotificationData = mData;


        mJsonObject = new JSONObject();
        JSONObject param = new JSONObject();

        try {
            param.put("title", mData.getName() + " is coming to help you.");
            param.put("message","You can contact him.");
            param.put("img_url",mData.getImg_url());
            param.put("mobile",mData.getMobile());
            param.put("email",mData.getEmail());
            param.put("userId", HomeActivity.mUserId);
            param.put("type", NotiEmergency);

            mJsonObject.put("data", param);
            mJsonObject.put("to", mData.getFcmToke());//"eFePheo6GIw:APA91bHeXiDGOavcP8THQ95KcjvK_srchXzKbnOcrnQioroXzQrNhoQzkFDkbgd6ubYnZICdRD4jsldzzixRkIRFC5-54MlADNcrIS-3MBcroQgOclxH3C_vDiMHvmjjCbyfzA691jeW");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void executeRequest(){
        String url = "https://fcm.googleapis.com/fcm/send";

        post(mContext,url, mJsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Something went wrong
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseStr = response.body().string();
                            Log.d("Response", responseStr);
                            // Do what you want to do with the response.
                        } else {
                            // Request not successful
                        }
                    }
                }
        );
    }




    private Call post(Context mContext,String url, String json, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization", mContext.getString(R.string.FCM_AUTHORIZATION_KEY))
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;

    }
}