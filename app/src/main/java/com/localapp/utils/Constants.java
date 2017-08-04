package com.localapp.utils;

import com.localapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vijay Kumar on 18-07-2017.
 */

public final class Constants {

    private Constants() {
        // restrict instantiation
    }


    public static final String LOCAL_APP_MARKET_URL = "market://details?id=com.localapp";
    public static final String LOCAL_APP_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.localapp";
    public static final String CONNECT_LOCAL_APP_EMAIL = "connect@localapp.org";
    public static final String GMAIL_PACKAGE = "com.google.android.gm";
    public static final String WHATSAPP_PACKAGE = "com.whatsapp";
    public static final String WHATSAPP_MARKET_URL = "market://details?id=com.whatsapp";

    public static final String UTM_SOURCE_EXPLETUS = "expletus";  // sign up utm source

    public static final String BLANK_STRING = "";


    //--------------------------- MapFragment ------------------------//
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000*60*2;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    //----------------------------- FeedFragment ---------------------//
    public static final String[]  emoji_name = {"Straight","Shout","Whisper","Gossip","Murmur","Mumble","Emergency"};
    public static int[] emojiResourceID = {R.drawable.emoji_staright,R.drawable.emoji_shout,R.drawable.emoji_whisper,
            R.drawable.emoji_gossip,R.drawable.emoji_murmer,R.drawable.emoji_mumble,R.drawable.emoji_emergency};


    //-------------------------- PublicProfileActivity ---------------//
    public static final String PIC_URL = "pic_url";
    public static final String UNKNOWN_PROFILE_ID = "594cfef7c44dc502bb16dbe9"; //fake profile for random message


    //------------------------- Facebook permissions ----------------//
    public static final String FB_PERMISSION_PROFILE = "public_profile";
    public static final String FB_PERMISSION_EMAIL = "email";
    public static final String FB_PERMISSION_ABOUT = "user_about_me";
    public static final String FB_PERMISSION_BIRTHDAY = "user_birthday";
    public static final String FB_PERMISSION_LOCATION = "user_location";
    public static final String FB_PERMISSION_RELATIONSHIP = "user_relationships";
    public static final String FB_PERMISSION_WORK_HISTORY = "user_work_history";

    //------------------------- Error Reporting Keys -----------------//
    public static final String ERROR_REPORT = "ERROR_REPORT";
    public static final String ERROR_MESSAGE = "ERROR_MESSAGE";




    /*************** THIS IS STATIC DATA WHICH IS USE IN EXPENDABLE_LIST_VIEW AND IT IS ALSO USE FILTER USER PROFILE BY PROFESSIONS ********/

    public static final String PROFESSION_GROUP_STUDENT = "STUDENT";
    public static final String PROFESSION_GROUP_PROFESSIONALS = "PROFESSIONALS";
    public static final String PROFESSION_GROUP_SKILLS = "SKILLS";
    public static final String PROFESSION_GROUP_HEALTH = "HEALTH AND WELLNESS";
    public static final String PROFESSION_GROUP_REPAIR = "REPAIR AND MAINTENANCE";
    public static final String PROFESSION_GROUP_WEDDING = "WEDDING & EVENTS";
    public static final String PROFESSION_GROUP_BEAUTY = "BEAUTY";
    public static final String PROFESSION_GROUP_HOUSEWIFE = "HOUSEWIFE";

    public static final List<String> PROFESSION_GROUP_STUDENT_LIST = new ArrayList<String>();
    public static final List<String> PROFESSION_GROUP_PROFESSIONALS_LIST = new ArrayList<String>();
    public static final List<String> PROFESSION_GROUP_SKILLS_LIST = new ArrayList<String>();
    public static final List<String> PROFESSION_GROUP_HEALTH_LIST = new ArrayList<String>();
    public static final List<String> PROFESSION_GROUP_REPAIR_LIST = new ArrayList<String>();
    public static final List<String> PROFESSION_GROUP_WEDDING_LIST = new ArrayList<String>();
    public static final List<String> PROFESSION_GROUP_BEAUTY_LIST = new ArrayList<String>();
    public static final List<String> PROFESSION_GROUP_HOUSEWIFE_LIST = new ArrayList<String>();

    static {
        PROFESSION_GROUP_STUDENT_LIST.add("Student");

        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Web Designer");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Social Marketing");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Lawyer");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Real Estate");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Insurance Agent");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("CCTV Camera Installation");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("CA");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Finance");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Operations");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Software Engineer");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Engineer");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Sales Professionals");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Writer");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Interior Designer");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Graphic Designer");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Administrator");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Human Resource");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Security Guard");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Driver");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Doctor");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Adviser");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Architect");
        PROFESSION_GROUP_PROFESSIONALS_LIST.add("Marketeer");

        PROFESSION_GROUP_SKILLS_LIST.add("Salsa");
        PROFESSION_GROUP_SKILLS_LIST.add("Drum");
        PROFESSION_GROUP_SKILLS_LIST.add("Keyboard Lesson");
        PROFESSION_GROUP_SKILLS_LIST.add("Guitar");
        PROFESSION_GROUP_SKILLS_LIST.add("Zumba");

        PROFESSION_GROUP_HEALTH_LIST.add("Dietitian");
        PROFESSION_GROUP_HEALTH_LIST.add("Fitness Trainer");
        PROFESSION_GROUP_HEALTH_LIST.add("Nurse");
        PROFESSION_GROUP_HEALTH_LIST.add("Physiotherapy");
        PROFESSION_GROUP_HEALTH_LIST.add("Yoga Trainer");
        PROFESSION_GROUP_REPAIR_LIST.add("AC Repair");
        PROFESSION_GROUP_REPAIR_LIST.add("Carpenter");
        PROFESSION_GROUP_REPAIR_LIST.add("Construction & Repair");
        PROFESSION_GROUP_REPAIR_LIST.add("Electrician");
        PROFESSION_GROUP_REPAIR_LIST.add("House Painter");
        PROFESSION_GROUP_REPAIR_LIST.add("Laptop Repair");
        PROFESSION_GROUP_REPAIR_LIST.add("Laundry");
        PROFESSION_GROUP_REPAIR_LIST.add("Plumber");

        PROFESSION_GROUP_WEDDING_LIST.add("Decor");
        PROFESSION_GROUP_WEDDING_LIST.add("DJ");
        PROFESSION_GROUP_WEDDING_LIST.add("Corporate Event Planer");
        PROFESSION_GROUP_WEDDING_LIST.add("Bartender");
        PROFESSION_GROUP_WEDDING_LIST.add("Photograph");
        PROFESSION_GROUP_WEDDING_LIST.add("Musician");

        PROFESSION_GROUP_BEAUTY_LIST.add("Saloon");
        PROFESSION_GROUP_BEAUTY_LIST.add("Makeup Artist");
        PROFESSION_GROUP_BEAUTY_LIST.add("Spa");

        PROFESSION_GROUP_HOUSEWIFE_LIST.add("Housewife");
    }
}
