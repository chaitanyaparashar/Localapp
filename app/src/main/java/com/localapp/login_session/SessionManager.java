package com.localapp.login_session;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.maps.model.LatLng;
import java.util.HashMap;


/**
 * Created by 4 way on 04-03-2017.
 */

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences prefFcm;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor fcmEditor;

    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LoginSession";
    private static final String PREF_FCM = "fcm";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_LOGIN_TOKEN = "LoginToken";
    public static final String KEY_LOGIN_USER_ID = "userId";
    public static final String KEY_LOGIN_USER_PIC_URL = "picUrl";
    public static final String KEY_LAT = "lastKnownLat";
    public static final String KEY_LNG = "lastKnownLng";


    public static final String KEY_FCM_TOKEN = "fcmToken";



    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        prefFcm = _context.getSharedPreferences(PREF_FCM, Context.MODE_PRIVATE);
        fcmEditor = prefFcm.edit();
    }



    /**
     * Create login session
     * */
    public void createLoginSession(String token,String userId,String picUrl, LatLng latLng){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(KEY_LOGIN_TOKEN, token);
        editor.putString(KEY_LOGIN_USER_ID, userId);
        editor.putString(KEY_LOGIN_USER_PIC_URL, picUrl);
        if (latLng != null) {
            editor.putString(KEY_LAT, String.valueOf(latLng.latitude));
            editor.putString(KEY_LNG, String.valueOf(latLng.longitude));
        }

        // commit changes
        editor.commit();
    }


    public void saveFcmToken(String fcmToke) {
        fcmEditor.putString(KEY_FCM_TOKEN, fcmToke);
        fcmEditor.commit();
    }

    public void saveLastLocation (LatLng latLng) {
        if (latLng != null) {
            editor.putString(KEY_LAT, String.valueOf(latLng.latitude));
            editor.putString(KEY_LNG, String.valueOf(latLng.longitude));
            // commit changes
            editor.commit();
        }

    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
           /* // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, loginUi.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);*/
        }

    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_LOGIN_TOKEN, pref.getString(KEY_LOGIN_TOKEN, null));
        user.put(KEY_LOGIN_USER_ID, pref.getString(KEY_LOGIN_USER_ID, null));
        user.put(KEY_LOGIN_USER_PIC_URL, pref.getString(KEY_LOGIN_USER_PIC_URL, null));
        user.put(KEY_LAT, pref.getString(KEY_LAT, null));
        user.put(KEY_LNG, pref.getString(KEY_LNG, null));

        // return user
        return user;
    }


    public String getFcmToken(){
        return prefFcm.getString(KEY_FCM_TOKEN,null);
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        /*// After logout redirect user to Loing Activity
        Intent i = new Intent(_context, loginUi.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);*/
    }

    public void clearFcmToken(){
        fcmEditor.clear();
        fcmEditor.commit();
    }

    /**
     * Quick check for login
    **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
