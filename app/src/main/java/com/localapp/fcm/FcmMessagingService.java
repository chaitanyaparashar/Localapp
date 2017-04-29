package com.localapp.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.localapp.R;
import com.localapp.data.Profile;
import com.localapp.login_session.SessionManager;
import com.localapp.ui.HomeActivity;

/**
 * Created by 4 way on 24-04-2017.
 */

public class FcmMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() >0 && SessionManager.getInstance(this).isLoggedIn()) {
            String title,message,img_url,mobile,email,userId;


            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("message");
            img_url = remoteMessage.getData().get("img_url");
            mobile = remoteMessage.getData().get("mobile");
            email = remoteMessage.getData().get("email");
            userId = remoteMessage.getData().get("userId");



            Intent intent =  new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("userId",userId);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);


            Intent callIntent = new Intent(Intent.ACTION_CALL,Uri.fromParts("tel", mobile, null));
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto",email, null));

            PendingIntent callPendingIntent = PendingIntent.getActivity(this, 0,callIntent , 0);
            PendingIntent emailPendingIntent = PendingIntent.getActivity(this, 0,emailIntent , 0);

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle(title);
            builder.setContentText(message);
            builder.setContentIntent(pendingIntent);
            builder.addAction(R.drawable.ic_phone,"Call",callPendingIntent);
            builder.addAction(R.drawable.ic_email,"Email",emailPendingIntent);
            builder.setSmallIcon(R.mipmap.ic_localapp);

            /*builder.setStyle(new NotificationCompat.DecoratedMediaCustomViewStyle().setShowActionsInCompactView(0));
            builder.setStyle(new NotificationCompat.DecoratedMediaCustomViewStyle().setShowActionsInCompactView(1));*/
            builder.setSound(soundUri);
            builder.setColor(Color.parseColor("#2196f3"));
            builder.setAutoCancel(true);

            NotificationManager notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0,builder.build());


        }
    }
}
