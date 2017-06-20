package com.moscowmuleaddicted.neighborhoodsecurity.utilities;

import java.util.concurrent.TimeUnit;

/**
 * Created by Simone Ripamonti on 20/05/2017.
 */

public class Constants {
    public static final String SHARED_PREFERENCES_NOTIFICATION_COUNT_BY_UID = "com.moscowmuleaddicted.neighborhoodsecurity.counters";
    public static final String SHARED_PREFERENCES_VOTED_EVENTS = "com.moscowmuleaddicted.neighborhoodsecurity.voted_events";
    public static final String SHARED_PREFERENCES_SUBSCRIPTIONS = "com.moscowmuleaddicted.neighborhoodsecurity.subscriptions";

    public static final String NOTIFICATION_COUNT = "notification_count";

    public static final int PLACE_AUTOCOMPLETE_RC = 300;
    public static final int CREATE_EVENT_RC = 301;
    public static final int CREATE_SUBSCRIPTION_RC = 302;
    public static final int PERMISSION_POSITION_RC = 303;

    public static final int PLAY_SERVICES_MIN_VERSION = 10210000; // 10.2.1

    public static final long MILLISECONDS_7_DAYS = TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS);
    public static final int MINUTES_IN_DAY = 1440;
    public static final int MINUTES_IN_HOUR = 60;
    public static final int SECONDS_IN_MINUTE = 60;

    public static final String TAG_DB_CLEAN_JOB = "db-clean-job";
}
