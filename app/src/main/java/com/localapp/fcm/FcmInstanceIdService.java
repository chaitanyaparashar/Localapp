package com.localapp.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.localapp.models.Profile;
import com.localapp.preferences.SessionManager;
import com.localapp.network.UpdateFcmTokenRequest;
import com.localapp.ui.activities.HomeActivity;

/**
 * Created by 4 way on 24-04-2017.
 */

public class FcmInstanceIdService extends FirebaseInstanceIdService{
    SessionManager sessionManager;
    @Override
    public void onTokenRefresh() {
        String fcm_token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM_TOKE",fcm_token);

        sendRegistrationToServer(fcm_token);
    }


    private void sendRegistrationToServer(String token) {
        sessionManager = new SessionManager(this);
        sessionManager.saveFcmToken(token);

        if (sessionManager.isLoggedIn()) {
            Profile profile = new Profile(HomeActivity.mUserId);
            profile.setFcmToken(token);
            profile.setuToken(HomeActivity.mLoginToken);
            UpdateFcmTokenRequest request = new UpdateFcmTokenRequest(this,HomeActivity.mUserId, HomeActivity.mLoginToken,token);
            request.executeRequest();
        }


    }
}
