package com.localapp.appcontroller;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

/**
 * Created by 4 way on 12-04-2017.
 */

public class AppController extends Application {

    @SuppressLint("StaticFieldLeak")
    private static AppController mInstance;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        this.setAppContext(getApplicationContext());

        Thread.setDefaultUncaughtExceptionHandler(new AppExceptionHandler(getAppContext()));
    }
    public static synchronized AppController getInstance(){
        return mInstance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }



    public static  Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context mAppContext) {
        AppController.mAppContext = mAppContext;
    }

    private static boolean activityVisible;
}
