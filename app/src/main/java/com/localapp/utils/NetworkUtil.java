package com.localapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.localapp.R;
import com.localapp.background.ConnectivityReceiver;
import com.localapp.network.CrashReportRequest;

/**
 * Created by Vijay Kumar on 17-07-2017.
 */

public class NetworkUtil {
    public static boolean isConnected() {
        return ConnectivityReceiver.isConnected();
    }
    private static boolean isShowing = false;

    public static void ErrorAppDialog(final Context mContext) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setIcon(R.drawable.ic_offline);
        alertDialogBuilder.setTitle("No Internet Connection");
        alertDialogBuilder.setMessage("You are offline please check your internet connection");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
               if (!isShowing && !isConnected()) {
                   ErrorAppDialog(mContext);
               }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isShowing = false;
            }
        });
        if (!isShowing) {
            alertDialog.show();
            isShowing = true;
        }
    }

    public static void CrashReport(Context mContext, String errorMessage, String errorReport) {
        CrashReportRequest request = new CrashReportRequest(mContext,errorMessage,errorReport);
        request.executeRequest();
    }
}
