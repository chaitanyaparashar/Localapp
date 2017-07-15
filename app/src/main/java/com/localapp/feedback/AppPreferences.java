package com.localapp.feedback;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 4 way on 08-05-2017.
 */

public class AppPreferences {
    private static AppPreferences sInstance;
    private SharedPreferences mPrefs;
    private static final String PREF_APP_RATE = "pref_app_rate";
    private static final String PREF_LAUNCH_COUNT = "pref_launch_count";
    private static final String PREF_LAUNCH_TOUR = "pref_launch_tour";
    private static final String PREF_LAUNCH_MAP_TOOLTIP = "pref_launch_map_toll_tip";
    private static final String PREF_LAUNCH_BROADCAST_TOOLTIP = "pref_launch_broadcast_toll_tip";
    private static final String PREF_LAUNCH_MSG_MODE_TOOLTIP = "pref_launch_msg_mode_toll_tip";
    private static final String PREF_LAUNCH_NOTICEBOARD_TOOLTIP = "pref_launch_notice_toll_tip";
    private static final String PREF_BROADCAST_NOTIFICATION_SETTING = "pref_broadcast_setting";
    private static final String PREF_MOBIRUCK_SIGNUP_POSTBACK = "pref_mobiruck_postback";
    public static final String PREF_UTM_SOURCE = "utm_source";

    private AppPreferences(Context paramContext) {
        this.mPrefs = paramContext.getSharedPreferences("app_prefs", 0);
    }

    public static AppPreferences getInstance(Context paramContext) {
        if (sInstance == null) {
            sInstance = new AppPreferences(paramContext);
        }
        return sInstance;
    }

    public boolean getAppRate() {
        return this.mPrefs.getBoolean(PREF_APP_RATE, true);
    }

    public void setAppRate(boolean paramBoolean) {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.putBoolean(PREF_APP_RATE, paramBoolean);
        localEditor.commit();
    }

    public int getLaunchCount() {
        return this.mPrefs.getInt(PREF_LAUNCH_COUNT, 0);
    }

    public void incrementLaunchCount() {
        int i = getLaunchCount();
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.putInt(PREF_LAUNCH_COUNT, i + 1);
        localEditor.commit();
    }

    public void resetLaunchCount() {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.remove(PREF_LAUNCH_COUNT);
        localEditor.commit();
    }

    public boolean isTourLaunched() {
        return this.mPrefs.getBoolean(PREF_LAUNCH_TOUR, false);
    }

    public void tourLaunched () {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.putBoolean(PREF_LAUNCH_TOUR, true);
        localEditor.commit();
    }

    public boolean isLaunchedMapToolTip () {
        return this.mPrefs.getBoolean(PREF_LAUNCH_MAP_TOOLTIP, false);
    }

    public void mapToolTipLaunched() {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.putBoolean(PREF_LAUNCH_MAP_TOOLTIP, true);
        localEditor.commit();
    }


    public boolean isLaunchedBroadcastToolTip () {
        return this.mPrefs.getBoolean(PREF_LAUNCH_BROADCAST_TOOLTIP, false);
    }

    public void broadcastToolTipLaunched() {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.putBoolean(PREF_LAUNCH_BROADCAST_TOOLTIP, true);
        localEditor.commit();
    }


    public boolean isLaunchedSmsModeToolTip () {
        return this.mPrefs.getBoolean(PREF_LAUNCH_MSG_MODE_TOOLTIP, false);
    }

    public void smsToolTipLaunched () {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.putBoolean(PREF_LAUNCH_MSG_MODE_TOOLTIP, true);
        localEditor.apply();
    }

    public boolean isLaunchedNoticeboardToolTip () {
        return this.mPrefs.getBoolean(PREF_LAUNCH_NOTICEBOARD_TOOLTIP, false);
    }

    public void noticeboardToolTipLaunched() {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.putBoolean(PREF_LAUNCH_NOTICEBOARD_TOOLTIP, true);
        localEditor.commit();
    }

    public boolean isBroadcastNotificationOn () {
        return this.mPrefs.getBoolean(PREF_BROADCAST_NOTIFICATION_SETTING, true);
    }

    public void setBroadcastNotificationOn() {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.putBoolean(PREF_BROADCAST_NOTIFICATION_SETTING, true);
        localEditor.commit();
    }

    public void setBroadcastNotificationOff() {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.putBoolean(PREF_BROADCAST_NOTIFICATION_SETTING, false);
        localEditor.commit();
    }

    public boolean isMobiruckPostBack() {
        return this.mPrefs.getBoolean(PREF_MOBIRUCK_SIGNUP_POSTBACK, false);
    }

    public void setMobiruckSignupPostback(boolean postback) {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.putBoolean(PREF_MOBIRUCK_SIGNUP_POSTBACK, postback);
        localEditor.commit();
    }


    public void setUtm_source (String source) {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.putString(PREF_UTM_SOURCE, source);
        localEditor.commit();
    }

    public String getUtmSource() {
        String utm_source;
        utm_source = this.mPrefs.getString(PREF_UTM_SOURCE, "");
        return utm_source;
    }
}
