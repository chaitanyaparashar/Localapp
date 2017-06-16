package com.localapp.appcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.localapp.request.CrashReportRequest;

/**
 * Created by 4 way on 14-06-2017.
 */

public class CrashActivity extends Activity {
    public static final String ERROR_REPORT = "ERROR_REPORT";
    public static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        final String errorReport = intent.getExtras().getString(ERROR_REPORT);
        final String errorMessage = intent.getExtras().getString(ERROR_MESSAGE);
        Log.d(errorMessage,"called");

        CrashReportRequest request = new CrashReportRequest(this,errorMessage,errorReport);
        request.executeRequest();

    }
}
