package com.moscowmuleaddicted.neighborhoodsecurity.controller.db;

import android.provider.BaseColumns;

/**
 * Helper class to hold prepared statements and table / column names
 *
 * @author Simone Ripamonti
 * @version 1
 */

public class DatabaseContract {

    /**
     * Empty constructor
     */
    private DatabaseContract() {

    }

    /**
     * Event DB table and column name
     */
    public static class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_EVENTTYPE = "eventtype";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_STREET = "street";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_SUBMITTERID = "submitterid";
        public static final String COLUMN_NAME_VOTES = "votes";
        public static final String COLUMN_NAME_STORAGE_DATE = "storage_date";
    }

    /**
     * Subscription DB table and column name
     */
    public static class SubscriptionEntry implements BaseColumns {
        public static final String TABLE_NAME = "subscriptions";
        public static final String COLUMN_NAME_USERID = "userid";
        public static final String COLUMN_NAME_MIN_LATITUDE = "min_latitude";
        public static final String COLUMN_NAME_MAX_LATITUDE = "max_latitude";
        public static final String COLUMN_NAME_MIN_LONGITUDE = "min_longitude";
        public static final String COLUMN_NAME_MAX_LONGITUDE = "max_longitude";
        public static final String COLUMN_NAME_RADIUS = "radius";
        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_STREET = "street";
        public static final String COLUMN_NAME_STORAGE_DATE = "storage_date";
    }

    /**
     * Event DB prepared statements
     */
    public static class EventStatements {
        /**
         * Table creation
         */
        public static final String SQL_CREATE_TABLE_ENTRIES =
                "CREATE TABLE " + EventEntry.TABLE_NAME + " ("
                        + EventEntry._ID + " INTEGER PRIMARY KEY, "
                        + EventEntry.COLUMN_NAME_DATE + " TIMESTAMP, "
                        + EventEntry.COLUMN_NAME_EVENTTYPE + " TEXT, "
                        + EventEntry.COLUMN_NAME_DESCRIPTION + " TEXT, "
                        + EventEntry.COLUMN_NAME_COUNTRY + " TEXT, "
                        + EventEntry.COLUMN_NAME_CITY + " TEXT, "
                        + EventEntry.COLUMN_NAME_STREET + " TEXT, "
                        + EventEntry.COLUMN_NAME_LATITUDE + " DOUBLE, "
                        + EventEntry.COLUMN_NAME_LONGITUDE + " DOUBLE, "
                        + EventEntry.COLUMN_NAME_SUBMITTERID + " TEXT, "
                        + EventEntry.COLUMN_NAME_VOTES + " INTEGER, "
                        + EventEntry.COLUMN_NAME_STORAGE_DATE + " TIMESTAMP )";

        /**
         * Delete all entries
         */
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;

        /**
         * Select by UID
         */
        public static final String SQL_SELECT_BY_UID =
                "SELECT * FROM " + EventEntry.TABLE_NAME + " WHERE "
                        + EventEntry.COLUMN_NAME_SUBMITTERID + " = ?";

        /**
         * Select by AREA
         */
        public static final String SQL_SELECT_BY_AREA =
                "SELECT * FROM " + EventEntry.TABLE_NAME + " WHERE "
                + EventEntry.COLUMN_NAME_LATITUDE + " > ? AND "
                + EventEntry.COLUMN_NAME_LATITUDE + " < ? AND "
                + EventEntry.COLUMN_NAME_LONGITUDE + " > ? AND "
                + EventEntry.COLUMN_NAME_LONGITUDE + " < ?";

        /**
         * Select by ID
         */
        public static final String SQL_SELECT_BY_ID =
                "SELECT * FROM "+ EventEntry.TABLE_NAME + " WHERE "
                + EventEntry._ID + " = ?";

        /**
         * Select Count
         */
        public static final String SQL_SELECT_COUNT =
                "SELECT COUNT(*) FROM "+ EventEntry.TABLE_NAME;
    }

    /**
     * Subscription DB preprared statements
     */
    public static class SubscriptionStatements {
        /**
         * Table creation
         */
        public static final String SQL_CREATE_TABLE_ENTRIES =
                "CREATE TABLE " + SubscriptionEntry.TABLE_NAME + " ("
                        + SubscriptionEntry._ID + " INTEGER PRIMARY KEY, "
                        + SubscriptionEntry.COLUMN_NAME_USERID + " TEXT, "
                        + SubscriptionEntry.COLUMN_NAME_MIN_LATITUDE + " DOUBLE, "
                        + SubscriptionEntry.COLUMN_NAME_MAX_LATITUDE + " DOUBLE, "
                        + SubscriptionEntry.COLUMN_NAME_MIN_LONGITUDE + " DOUBLE, "
                        + SubscriptionEntry.COLUMN_NAME_MAX_LONGITUDE + " DOUBLE, "
                        + SubscriptionEntry.COLUMN_NAME_RADIUS + " INTEGER, "
                        + SubscriptionEntry.COLUMN_NAME_COUNTRY + " TEXT, "
                        + SubscriptionEntry.COLUMN_NAME_CITY + " TEXT, "
                        + SubscriptionEntry.COLUMN_NAME_STREET + " TEXT, "
                        + SubscriptionEntry.COLUMN_NAME_STORAGE_DATE + " TIMESTAMP )";

        /**
         * Delete all entries
         */
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + SubscriptionEntry.TABLE_NAME;

        /**
         * Select by UID
         */
        public static final String SQL_SELECT_BY_UID =
                "SELECT * FROM " + SubscriptionEntry.TABLE_NAME + " WHERE "
                + SubscriptionEntry.COLUMN_NAME_USERID + " = ?";

        /**
         * Select Count
         */
        public static final String SQL_SELECT_COUNT =
                "SELECT COUNT(*) FROM "+SubscriptionEntry.TABLE_NAME + " WHERE "
                        + SubscriptionEntry.COLUMN_NAME_USERID + " = ?";

    }
}
