package com.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkArgument;


/**
 * Created by 4 way on 17-04-2017.
 */

public class utility {


    public static String getTimeAndDate(String milliseconds) {
//        DateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        DateFormat sdfDay = new SimpleDateFormat("d");
        DateFormat sdf = new SimpleDateFormat("MMM yyyy EEEE,  hh:mm a");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliseconds));

        Log.v("date time: ",sdf.format(calendar.getTime()));

        return getDayOfMonthSuffix(Integer.parseInt(sdfDay.format(calendar.getTime())))+" "+sdf.format(calendar.getTime()).replace("AM", "am").replace("PM","pm");
    }

    static  String getDayOfMonthSuffix(final int n) {
        checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return n+"th";
        }
        switch (n % 10) {
            case 1:
                return n+"st";
            case 2:
                return n+"nd";
            case 3:
                return n+"rd";
            default:
                return n+"th";
        }
    }
}
