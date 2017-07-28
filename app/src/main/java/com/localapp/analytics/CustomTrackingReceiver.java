package com.localapp.analytics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.localapp.preferences.AppPreferences;
import com.localapp.utils.Constants;
import com.mobiruck.ReferrerReceiver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by Vijay Kumar on 20-06-2017.
 * @author Vijay
 */

public class CustomTrackingReceiver extends BroadcastReceiver {
    private static final String UTM_SOURCE = "utm_source";
    public static final String TAG = "CustomTrackingReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {


        Bundle extras = intent.getExtras();
        String referrerString = extras.getString("referrer");

        new CampaignTrackingReceiver().onReceive(context, intent);

        if (referrerString != null) {

            Log.d(TAG, referrerString);

            try {
                Map receiver = getHashMapFromQuery(referrerString);

                String utmSource = (String) receiver.get(UTM_SOURCE);
                AppPreferences.getInstance(context).setUtm_source(utmSource);

                if (utmSource.equals(Constants.UTM_SOURCE_EXPLETUS)) {
                    new ReferrerReceiver().onReceive(context, intent);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

    }



    public static Map<String, String> getHashMapFromQuery(String query) throws UnsupportedEncodingException {
        LinkedHashMap query_pairs = new LinkedHashMap();
        String[] pairs = query.split("&");
        String[] var3 = pairs;
        int var4 = pairs.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String pair = var3[var5];
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }

        return query_pairs;
    }
}
