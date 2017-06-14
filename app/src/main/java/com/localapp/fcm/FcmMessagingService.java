package com.localapp.fcm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.localapp.R;
import com.localapp.appcontroller.AppController;
import com.localapp.data.MessageNotificationData;

import com.localapp.feedback.AppPreferences;
import com.localapp.login_session.SessionManager;
import com.localapp.ui.HomeActivity;

import java.util.concurrent.ExecutionException;

import static com.localapp.util.NotificationUtils.clearNotificationTimer;
import static com.localapp.util.NotificationUtils.getCroppedBitmap;
import static com.localapp.util.NotificationUtils.isAppIsInBackground;
import static com.localapp.util.NotificationUtils.notificationList;
import static com.localapp.util.NotificationUtils.numMessage;

/**
 * Created by 4 way on 24-04-2017.
 */

public class FcmMessagingService extends FirebaseMessagingService {

    static final int NotiEmergency = 0;
    private static final int NotiBroadcast = 1;
    final String GROUP_KEY_MESSAGES = "group_key_messages";



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() >0 && SessionManager.getInstance(this).isLoggedIn()) {
            String title, message, img_url, mobile, email, userId;
            int notificationType;



            try {
                notificationType = Integer.parseInt(remoteMessage.getData().get("type"));
            }catch (Exception e){
                notificationType = 0;
            }


            try {
                title = remoteMessage.getData().get("title");
                message = remoteMessage.getData().get("message");
                img_url = remoteMessage.getData().get("img_url");
                userId = remoteMessage.getData().get("userId");
            }catch (NullPointerException ne){
                return;
            }


            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (notificationType == NotiEmergency) {


                try {
                    mobile = remoteMessage.getData().get("mobile");
                    email = remoteMessage.getData().get("email");
                }catch (NullPointerException ne){
                    return;
                }


                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", userId);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                Intent callIntent,emailIntent;
                try {
                    callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", mobile, null));
                    emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
                }catch (NullPointerException ne) {
                    return;
                }

                PendingIntent callPendingIntent = PendingIntent.getActivity(this, 0, callIntent, 0);
                PendingIntent emailPendingIntent = PendingIntent.getActivity(this, 0, emailIntent, 0);



                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setContentTitle(title);
                builder.setContentText(message);
                builder.setContentIntent(pendingIntent);
                builder.addAction(R.drawable.ic_phone, "Call", callPendingIntent);
                builder.addAction(R.drawable.ic_email, "Email", emailPendingIntent);
                builder.setSmallIcon(R.mipmap.ic_localapp);

                builder.setSound(soundUri);
                builder.setColor(Color.parseColor("#2196f3"));
                builder.setAutoCancel(false);
                builder.setOngoing(true);

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
                clearNotificationTimer(0,30*1000*60);//remove automatically after 30 min



            }else if (notificationType == NotiBroadcast && AppPreferences.getInstance(AppController.getAppContext()).isBroadcastNotificationOn() && isAppIsInBackground(this)){
                boolean isContains = false;
                int index = 0;
                for (MessageNotificationData data:notificationList) {
                    if (data.getUserId().equals(userId)){
                        data.getMessageList().add(message);
                        index = notificationList.indexOf(data);
                        isContains = true;
                        break;
                    }
                }

                if (!isContains){
                    notificationList.add(new MessageNotificationData(notificationList.size()+2,userId, message));
                    index = notificationList.size() - 1;
                }




                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("noti","new_message");
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder0 = new NotificationCompat.Builder(this)
                        .setGroup(GROUP_KEY_MESSAGES)
                        .setGroupSummary(true)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.mipmap.ic_localapp)
                        .setColor(Color.parseColor("#2196f3"));

                if (numMessage == 0 && Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    NotificationManagerCompat.from(this).notify(1, builder0.build());
                    numMessage++;
                }


                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setGroup(GROUP_KEY_MESSAGES);
                if (notificationList.get(index).getMessageList().size() == 1) {
                    builder.setContentTitle(title+" ("+ notificationList.get(index).getMessageList().size() +" message)");
                }else {
                    builder.setContentTitle(title+" ("+ notificationList.get(index).getMessageList().size() +" messages)");
                }
                builder.setContentText(message);
                builder.setContentIntent(pendingIntent);
                builder.setSmallIcon(R.mipmap.ic_localapp);
                try {
                    builder.setLargeIcon(getCroppedBitmap(Glide.with(AppController.getAppContext()).load(img_url).asBitmap().into(300,300).get()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                try {
                    NotificationCompat.InboxStyle inboxStyle = new android.support.v4.app.NotificationCompat.InboxStyle();
                    inboxStyle.setBigContentTitle(title);
                    for (int i =0; i < notificationList.get(index).getMessageList().size(); i++) {
                        if (i < 3) {
                            inboxStyle.addLine(notificationList.get(index).getMessageList().get(i));
                            inboxStyle.setSummaryText(notificationList.get(index).getMessageList().size()+" new message");
                        }else {
                            inboxStyle.addLine("+"+(notificationList.get(index).getMessageList().size() -3)+" more");
                            break;
                        }
                    }

                    builder.setStyle(inboxStyle);
                }catch (Exception e){
                    e.printStackTrace();
                }



                builder.setSound(soundUri);
                builder.setColor(Color.parseColor("#2196f3"));
                builder.setAutoCancel(true);

                NotificationManagerCompat.from(this).notify(index + 2, builder.build());
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(index + 2, builder.build());



            }


        }



    }








}
