package com.localapp.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.localapp.fcm.FcmMessagingService;
import com.localapp.utils.Utility;


/**
 * Created by 4 way on 12-06-2017.
 */

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive","Called");

        if (!Utility.isServiceRunning(context,LocationService.class)){
            context.startService(new Intent(context, LocationService.class));
        }

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, FcmMessagingService.class);
            context.startService(serviceIntent);
        }
    }
}
