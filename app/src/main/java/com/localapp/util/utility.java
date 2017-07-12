package com.localapp.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.localapp.ui.ExpandableListAdapter;
import com.localapp.ui.HomeActivity;
import com.localapp.ui.public_profile.PublicProfileActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkArgument;


/**
 * Created by 4 way on 17-04-2017.
 */

public class utility {


    public static String getTimeAndDate(String milliseconds) {
//        DateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        try {
            DateFormat sdfDay = new SimpleDateFormat("d");
            DateFormat sdf = new SimpleDateFormat("MMM yyyy EEEE,  hh:mm a");

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(milliseconds));

            Log.v("date time: ",sdf.format(calendar.getTime()));

            return getDayOfMonthSuffix(Integer.parseInt(sdfDay.format(calendar.getTime())))+" "+sdf.format(calendar.getTime()).replace("AM", "am").replace("PM","pm");
        }catch (Exception e) {
            return "";
        }

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

    public static boolean isLocationAvailable(Context mContext) {
        if (HomeActivity.mLastKnownLocation == null) {
            Toast.makeText(mContext, "Please wait for getting your location...", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static String getSmsTime(String milliseconds) {

        try {
            Calendar smsTime = Calendar.getInstance();
            smsTime.setTimeInMillis(Long.parseLong(milliseconds));

            Calendar now = Calendar.getInstance();

            DateFormat sdfTime = new SimpleDateFormat("h:mm aa", Locale.ENGLISH);
            DateFormat sdf = new SimpleDateFormat("d MMMM,  h:mm aa", Locale.ENGLISH);
            DateFormat sdfY = new SimpleDateFormat("d MMMM YYYY,  h:mm aa", Locale.ENGLISH);

            if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
                return "Today, " + sdfTime.format(smsTime.getTime());
            } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
                return "Yesterday, " + sdfTime.format(smsTime.getTime());
            } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
                return sdf.format(smsTime.getTime());
            } else {
                return sdfY.format(smsTime.getTime());
            }
        }catch (NumberFormatException nfe) {
            return "";
        }catch (NullPointerException npe) {
            return "";
        }

    }


    public static List<String> getProfessionList (String professionGroup) {

        switch (professionGroup) {
            case ExpandableListAdapter.PROFESSION_GROUP_STUDENT: return ExpandableListAdapter.PROFESSION_GROUP_STUDENT_LIST;
            case ExpandableListAdapter.PROFESSION_GROUP_PROFESSIONALS: return ExpandableListAdapter.PROFESSION_GROUP_PROFESSIONALS_LIST;
            case ExpandableListAdapter.PROFESSION_GROUP_SKILLS: return ExpandableListAdapter.PROFESSION_GROUP_SKILLS_LIST;
            case ExpandableListAdapter.PROFESSION_GROUP_HEALTH: return ExpandableListAdapter.PROFESSION_GROUP_HEALTH_LIST;
            case ExpandableListAdapter.PROFESSION_GROUP_REPAIR: return ExpandableListAdapter.PROFESSION_GROUP_REPAIR_LIST;
            case ExpandableListAdapter.PROFESSION_GROUP_WEDDING: return ExpandableListAdapter.PROFESSION_GROUP_WEDDING_LIST;
            case ExpandableListAdapter.PROFESSION_GROUP_BEAUTY: return ExpandableListAdapter.PROFESSION_GROUP_BEAUTY_LIST;
            case ExpandableListAdapter.PROFESSION_GROUP_HOUSEWIFE: return ExpandableListAdapter.PROFESSION_GROUP_HOUSEWIFE_LIST;
            default: return null;
        }
    }

    public static boolean isServiceRunning(Context mContext,Class<?> serviceClass){
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        // Loop through the running services
        for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                // If the service is running then return true
                return true;
            }
        }
        return false;
    }




    /*********** calculate distance by google**********/
    /**
     *
     * @param from
     * @param to
     * @param unit
     * @param showUnit
     * @return
     */
    public static String calcDistance(LatLng from, LatLng to, String unit, boolean showUnit) {
        double distance = SphericalUtil.computeDistanceBetween(from, to);
        return formatNumber(distance,unit,showUnit);
    }

    private static String formatNumber(double distance,String unit,boolean showUnit) {
        /*String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }*/

        switch (unit) {
            case "km":
                distance /= 1000;
                unit = "km";
                break;
            case "mm":
                distance *= 1000;
                unit = "mm";
                break;
            default:
                unit = "m";
        }

        if (!showUnit) {
            unit = "";
        }

        return String.format("%4.2f%s", distance, unit);
    }


    /*private static String formatNumber(double distance,String unit) {
        *//*String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }*//*

        switch (unit) {
            case "km":
                distance /= 1000;
                unit = "km";
                break;
            case "mm":
                distance *= 1000;
                unit = "mm";
                break;
            default:
                unit = "m";
        }

        if (!true) {
            unit = "";
        }

        return String.format("%4.2f%s", distance, unit);
    }*/


    public static void openPublicProfile(Context mContext, String userId){
        Intent intent = new Intent(mContext,PublicProfileActivity.class);
        intent.putExtra("action_id",userId);
        mContext.startActivity(intent);

    }






}
