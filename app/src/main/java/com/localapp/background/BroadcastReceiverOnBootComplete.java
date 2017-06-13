package com.localapp.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.localapp.fcm.FcmMessagingService;

/**
 * Created by 4 way on 12-06-2017.
 */

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive","Called");

        context.startService(new Intent(context, LocationService.class));

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, FcmMessagingService.class);
            context.startService(serviceIntent);
        }
    }
}
