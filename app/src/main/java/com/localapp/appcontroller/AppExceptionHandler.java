package com.localapp.appcontroller;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;


import com.localapp.ui.activities.HomeActivity;
import com.localapp.utils.Constants;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Created by 4 way on 13-06-2017.
 */

public class AppExceptionHandler implements UncaughtExceptionHandler {
    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
     */
    private Context context;

    public AppExceptionHandler(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));

        final StringBuilder report = new StringBuilder();
        final String lineSeperator = "-----------------------------------\n\n";
        final String SINGLE_LINE_SEP = "\n";
        final String DOUBLE_LINE_SEP = "\n\n";

        report.append(DOUBLE_LINE_SEP);
        report.append("-------------- CAUSE OF ERROR--------------\n\n");
        report.append(stackTrace.toString());

        report.append("\n---------- DEVICE INFORMATION -----------\n\n");
        report.append("Brand: ");
        report.append(Build.BRAND);
        report.append(SINGLE_LINE_SEP);
        report.append("Device: ");
        report.append(Build.DEVICE);
        report.append(SINGLE_LINE_SEP);
        report.append("Model: ");
        report.append(Build.MODEL);
        report.append(SINGLE_LINE_SEP);
        report.append("Id: ");
        report.append(Build.ID);
        report.append(SINGLE_LINE_SEP);
        report.append("Product: ");
        report.append(Build.PRODUCT);
        report.append(SINGLE_LINE_SEP);
        report.append(lineSeperator);
        report.append("----------- FIRMWARE  -----------\n\n");
        report.append("SDK: ");
        report.append(Build.VERSION.SDK_INT);
        report.append(SINGLE_LINE_SEP);
        report.append("Release: ");
        report.append(Build.VERSION.RELEASE);
        report.append(SINGLE_LINE_SEP);
        report.append("Incremental: ");
        report.append(Build.VERSION.INCREMENTAL);
        report.append(SINGLE_LINE_SEP);
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            report.append("versionName: ");
            report.append(packageInfo.versionName);
            report.append(SINGLE_LINE_SEP);
            report.append("versionCode: ");
            report.append(packageInfo.versionCode);
            report.append(SINGLE_LINE_SEP);

        } catch (PackageManager.NameNotFoundException nnf) {
            nnf.printStackTrace();
        }
        report.append(lineSeperator);


        Log.e("Report ::", report.toString());
        Log.e("Report ::", "\n\n"+ e.getMessage());

        Intent crashedIntent = new Intent(context, HomeActivity.class);
        crashedIntent.putExtra(Constants.ERROR_REPORT, report.toString());
        crashedIntent.putExtra(Constants.ERROR_MESSAGE, e.getMessage());
        crashedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(crashedIntent);


        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}
