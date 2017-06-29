package com.moscowmuleaddicted.neighborhoodsecurity.controller;

import java.util.concurrent.TimeUnit;

/**
 * Collection of constants used in the application
 *
 * @author Simone Ripamonti
 * @version 1
 */

public class Constants {
    // urls and parameters
    public static final String NS_REST_URL = "https://thawing-taiga-87659.herokuapp.com/";
    public static final String MAPS_API_URL = "https://maps.googleapis.com/maps/api/staticmap?center=";
    public static final String MAPS_API_URL_2 = "&zoom=15&size=400x400&markers=size:large%7C";
    public static final String MAPS_API_URL_3 = "&key=";
    public static final String MAPS_API_COORD = "%1$s,%2$s";
    public static final String SERVICE_KEY = "service_key";
    public static final String AUTH_TOKEN = "auth_token";

    // shared preferences
    public static final String SP_NOTIFICATION_COUNT_BY_UID = "com.moscowmuleaddicted.neighborhoodsecurity.counters";
    public static final String SP_VOTED_EVENTS = "com.moscowmuleaddicted.neighborhoodsecurity.voted_events";
    public static final String SP_SUBSCRIPTIONS = "com.moscowmuleaddicted.neighborhoodsecurity.subscriptions";
    public static final String SP_SHOWCASE = "com.moscowmuleaddicted.neighborhoodsecurity.showcase";

    // request codes
    public static final int RC_PLACE_AUTOCOMPLETE = 300;
    public static final int RC_CREATE_EVENT = 301;
    public static final int RC_CREATE_SUBSCRIPTION = 302;
    public static final int RC_PERMISSION_POSITION = 303;
    public static final int RC_EMAIL_LOGIN = 304;
    public static final int RC_AUTHENTICATION = 305;
    public static final int RC_GOOGLE_SIGNIN = 306;

    // intent extras
    public static final String IE_LOGGED_IN = "LOGGED_IN";
    public static final String IE_LATITUDE = "latitude";
    public static final String IE_LONGITUDE = "longitude";
    public static final String IE_EVENT = "event";
    public static final String IE_EVENT_LIST = "event-list";
    public static final String IE_UID = "uid";
    public static final String IE_SUBSCRIPTION = "subscription";
    public static final String IE_SUBSCRIPTION_LIST = "subscription-list";
    public static final String IE_COUNTRY = "country";
    public static final String IE_CITY = "city";
    public static final String IE_STREET = "street";
    public static final String IE_COLUMN_COUNT = "column-count";


    // tag
    public static final String TAG_DB_CLEAN_JOB = "db-clean-job";

    // fcm message data payload
    public static final String FCM_TYPE = "type";
    public static final String FCM_EVENT = "event";
    public static final String FCM_REMOVE_EVENT = "remove_event";
    public static final String FCM_ID = "id";
    public static final String FCM_DATE = "date";
    public static final String FCM_EVENT_TYPE = "eventType";
    public static final String FCM_DESCRIPTION = "description";
    public static final String FCM_COUNTRY = "country";
    public static final String FCM_CITY = "city";
    public static final String FCM_STREET = "street";
    public static final String FCM_LATITUDE = "latitude";
    public static final String FCM_LONGITUDE = "longitude";
    public static final String FCM_VOTES = "votes";
    public static final String FCM_SUBMITTER_ID = "submitterId";
    public static final String FCM_SUBSCRIPTION_ID = "subscriptionId";
    public static final String FCM_SUBSCRIPTION_OWNER = "subscriptionOwner";

    // extra
    public static final long MILLISECONDS_7_DAYS = TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS);
    public static final int MINUTES_IN_DAY = 1440;
    public static final int MINUTES_IN_HOUR = 60;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int PLAY_SERVICES_MIN_VERSION = 10210000; // 10.2.1
    public static final double MAX_LATITUDE = 90d;
    public static final double MIN_LATITUDE = -90d;
    public static final double MAX_LONGITUDE = 180d;
    public static final double MIN_LONGITUDE = -180d;
    public static final int MIN_RADIUS = 0;
    public static final int MAX_RADIUS = 2000;
    public static final double DEFAULT_LATITUDE = 45.477072;
    public static final double DEFAULT_LONGITUDE = 9.226096;
    public static final long DEMO_ACCOUNT_ID = 123123123;
    public static final String FRAGMENT_NAME_TWITTER = "dialog-twitter";

    // showcase played
    public static final String SHOWCASE_HOME = "showcase-home";
}
