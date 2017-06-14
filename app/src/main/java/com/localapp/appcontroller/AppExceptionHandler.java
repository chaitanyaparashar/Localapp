package com.localapp.appcontroller;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        StackTraceElement[] arr = e.getStackTrace();
        List<String> strings =  new ArrayList<>();

        String d = strings.toString();
        final StringBuffer report = new StringBuffer(e.toString());
        final String lineSeperator = "-----------------------------------\n\n";
        final String SINGLE_LINE_SEP = "\n";
        final String DOUBLE_LINE_SEP = "\n\n";
        report.append(DOUBLE_LINE_SEP);
        int length = arr.length;
        Log.e("llllll",""+length);
        length = (length > 20) ? 20:length;
        Log.e("llllll",""+length);
        report.append("----------- Stack trace -----------\n\n");
        for (int i = 0; i < length; i++) {
            report.append( "    ");
            report.append(arr[i].toString());
            report.append(SINGLE_LINE_SEP);
        }
        report.append(SINGLE_LINE_SEP);
        report.append(lineSeperator);
        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
//                report.append("--------- Cause ---------\n\n");
        Throwable cause = e.getCause();
        if (cause != null) {
            report.append("----------- Cause -----------\n\n");
            report.append(cause.toString());
            report.append(DOUBLE_LINE_SEP);
            arr = cause.getStackTrace();
            for (int i = 0; i < arr.length; i++) {
                report.append("    ");
                report.append(arr[i].toString());
                report.append(SINGLE_LINE_SEP);
            }

            report.append(lineSeperator);
        }
        // Getting the Device brand,model and sdk verion details.
        report.append("----------- Device -----------\n\n");
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
        report.append("----------- Firmware -----------\n\n");
        report.append(SINGLE_LINE_SEP);
        report.append("Release: ");
        report.append(Build.VERSION.RELEASE);
        report.append(SINGLE_LINE_SEP);
        report.append("Incremental: ");
        report.append(Build.VERSION.INCREMENTAL);
        report.append(SINGLE_LINE_SEP);
        report.append(lineSeperator);

        Log.e("Report ::", report.toString());

        /*Intent crashedIntent = new Intent(context, ExceptionActivity.class);
        crashedIntent.putExtra(ExceptionActivity.ERROR_REPORT, report.toString());*/
        System.exit(2);
    }
}
