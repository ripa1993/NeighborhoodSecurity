package com.moscowmuleaddicted.neighborhoodsecurity.controller.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants;
import com.moscowmuleaddicted.neighborhoodsecurity.model.Subscription;
import com.moscowmuleaddicted.neighborhoodsecurity.controller.db.DatabaseContract.*;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite initialization for Event DB
 *
 * @author Simone Ripamonti
 * @version 2
 */
public class SubscriptionDB extends SQLiteOpenHelper {
    /**
     * Logger's TAG
     */
    public static final String TAG = "SubscriptionDB";
    /**
     * Database version
     */
    public static final int DATABASE_VERSION = 2;
    /**
     * Database name
     */
    public static final String DATABASE_NAME = "Subscription.db";

    /**
     * Constructor
     * @param context
     */
    public SubscriptionDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SubscriptionStatements.SQL_CREATE_TABLE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SubscriptionStatements.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Stores a new subscription
     * @param s
     */
    public void addSubscription(Subscription s){
        ContentValues values = new ContentValues(10);
        values.put(SubscriptionEntry._ID, s.getId());
        values.put(SubscriptionEntry.COLUMN_NAME_USERID, s.getUserId());
        values.put(SubscriptionEntry.COLUMN_NAME_MIN_LATITUDE, s.getMinLat());
        values.put(SubscriptionEntry.COLUMN_NAME_MAX_LATITUDE, s.getMaxLat());
        values.put(SubscriptionEntry.COLUMN_NAME_MIN_LONGITUDE, s.getMinLon());
        values.put(SubscriptionEntry.COLUMN_NAME_MAX_LONGITUDE, s.getMaxLon());
        values.put(SubscriptionEntry.COLUMN_NAME_RADIUS, s.getRadius());
        values.put(SubscriptionEntry.COLUMN_NAME_COUNTRY, s.getCountry());
        values.put(SubscriptionEntry.COLUMN_NAME_CITY, s.getCity());
        values.put(SubscriptionEntry.COLUMN_NAME_STREET, s.getStreet());
        values.put(SubscriptionEntry.COLUMN_NAME_STORAGE_DATE, System.currentTimeMillis());
        getWritableDatabase().insertWithOnConflict(
                SubscriptionEntry.TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Get a subscription based on the UID of the owner
     * @param uid
     * @return
     */
    public List<Subscription> getByUID(String uid){
        List<Subscription> subscriptions = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(SubscriptionStatements.SQL_SELECT_BY_UID,
                new String[]{uid});
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            subscriptions.add(toSubscription(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return subscriptions;
    }

    /**
     * Gets the count of the subscriptions referred to a specific user's UID
     * @param uid
     * @return
     */
    public int getCountByUid(String uid){
        Cursor cursor = getReadableDatabase().rawQuery(SubscriptionStatements.SQL_SELECT_COUNT, new String[]{uid});
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    /**
     * Deletes a subscription based on its id
     * @param subscriptionId
     */
    public void deleteById(int subscriptionId){
        getWritableDatabase().delete(SubscriptionEntry.TABLE_NAME, SubscriptionEntry._ID + " = ?", new String[]{String.valueOf(subscriptionId)});
    }

    /**
     * Deletes subscriptions older than 7 days
     * @return
     */
    public int clearOldSubscriptions(){
        return getWritableDatabase().delete(SubscriptionEntry.TABLE_NAME, SubscriptionEntry.COLUMN_NAME_STORAGE_DATE + " < " + (System.currentTimeMillis() - Constants.MILLISECONDS_7_DAYS), new String[]{});
    }

    /**
     * Converts a cursor row to a subscription
     * @param cursor
     * @return
     */
    private Subscription toSubscription(Cursor cursor){
        Subscription sub = new Subscription();
        sub.setId(cursor.getInt(0));
        sub.setUserId(cursor.getString(1));
        sub.setMinLat(cursor.getDouble(2));
        sub.setMaxLat(cursor.getDouble(3));
        sub.setMinLon(cursor.getDouble(4));
        sub.setMaxLon(cursor.getDouble(5));
        sub.setRadius(cursor.getInt(6));
        sub.setCountry(cursor.getString(7));
        sub.setCity(cursor.getString(8));
        sub.setStreet(cursor.getString(9));
        return sub;
    }

}
