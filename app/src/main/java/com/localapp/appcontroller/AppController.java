package com.localapp.appcontroller;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.localapp.analytics.AnalyticsTrackers;
import com.localapp.background.ConnectivityReceiver;

import static com.localapp.background.ConnectivityReceiver.connectivityReceiverListener;
import static com.localapp.background.ConnectivityReceiver.connectivityReceiverListeners;

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


        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

        this.setAppContext(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(new ConnectivityReceiver(),
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

//        Thread.setDefaultUncaughtExceptionHandler(new AppExceptionHandler(getAppContext()));
    }

    /**
     * Set the base context for this ContextWrapper.  All calls will then be
     * delegated to the base context.  Throws
     * IllegalStateException if a base context has already been set.
     *
     * @param base The new base context for this wrapper.
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public static synchronized AppController getInstance(){
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }



    @Deprecated
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        connectivityReceiverListener = listener;
    }

    public void addConnectivityListener(@NonNull ConnectivityReceiver.ConnectivityReceiverListener listener) {
        if (!connectivityReceiverListeners.contains(listener)) {
            connectivityReceiverListeners.add(listener);
            Log.d("AppController","addConnectivityListener " + listener.getClass().getName());
        }
    }

    /**
     *
     * @param listener listener to remove
     */
    public void removeConnectivityListener(@NonNull ConnectivityReceiver.ConnectivityReceiverListener listener){
        connectivityReceiverListeners.remove(listener);
    }

    public void clearConnectivityListener(){
        connectivityReceiverListeners.clear();
        Log.d("AppController","clearConnectivityListener");
    }





    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
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
