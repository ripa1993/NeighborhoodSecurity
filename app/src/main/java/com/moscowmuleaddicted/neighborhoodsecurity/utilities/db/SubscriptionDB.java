package com.moscowmuleaddicted.neighborhoodsecurity.utilities.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Subscription;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.db.DatabaseContract.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simone Ripamonti on 11/05/2017.
 */

public class SubscriptionDB extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Subscription.db";
    public static final String TAG = "SubscriptionDB";

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

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

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

    public int getCountByUid(String uid){
        Cursor cursor = getReadableDatabase().rawQuery(SubscriptionStatements.SQL_SELECT_COUNT, new String[]{uid});
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public void deleteById(int subscriptionId){
        getWritableDatabase().delete(SubscriptionEntry.TABLE_NAME, SubscriptionEntry._ID + " = ?", new String[]{String.valueOf(subscriptionId)});
    }

    public int clearOldSubscriptions(){
        return getWritableDatabase().delete(SubscriptionEntry.TABLE_NAME, SubscriptionEntry.COLUMN_NAME_STORAGE_DATE + " < " + (System.currentTimeMillis() - Constants.MILLISECONDS_7_DAYS), new String[]{});
    }

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
