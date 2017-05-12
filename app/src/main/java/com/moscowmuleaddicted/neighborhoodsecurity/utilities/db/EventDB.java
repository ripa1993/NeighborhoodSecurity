package com.moscowmuleaddicted.neighborhoodsecurity.utilities.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.moscowmuleaddicted.neighborhoodsecurity.utilities.db.DatabaseContract.*;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Simone Ripamonti on 11/05/2017.
 */

public class EventDB extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Event.db";

    public EventDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.EventStatements.SQL_CREATE_TABLE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DatabaseContract.EventStatements.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addEvent(Event e) {
        ContentValues values = new ContentValues(11);
        values.put(EventEntry._ID, e.getId());
        values.put(EventEntry.COLUMN_NAME_DATE, e.getDate().getTime());
        values.put(EventEntry.COLUMN_NAME_EVENTTYPE, e.getEventType().toStringNotLocalized());
        values.put(EventEntry.COLUMN_NAME_DESCRIPTION, e.getDescription());
        values.put(EventEntry.COLUMN_NAME_COUNTRY, e.getCountry());
        values.put(EventEntry.COLUMN_NAME_CITY, e.getCity());
        values.put(EventEntry.COLUMN_NAME_STREET, e.getStreet());
        values.put(EventEntry.COLUMN_NAME_LATITUDE, e.getLatitude());
        values.put(EventEntry.COLUMN_NAME_LONGITUDE, e.getLongitude());
        values.put(EventEntry.COLUMN_NAME_SUBMITTERID, e.getSubmitterId());
        values.put(EventEntry.COLUMN_NAME_VOTES, e.getVotes());
        getWritableDatabase().insertWithOnConflict(EventEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Get list of events given the uid of the submitter
     * @param uid
     * @return
     */
    public List<Event> getByUID(String uid){
        Cursor cursor = getReadableDatabase().rawQuery(EventStatements.SQL_SELECT_BY_UID, new String[]{uid});
        List<Event> events = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            events.add(toEvent(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return events;
    }

    /**
     * Get list of events given an area to search
     * @param minLat
     * @param maxLat
     * @param minLon
     * @param maxLon
     * @return
     */
    public List<Event> getByArea(Double minLat, Double maxLat, Double minLon, Double maxLon) {
        Cursor cursor = getReadableDatabase().rawQuery(EventStatements.SQL_SELECT_BY_AREA,
                new String[]{minLat.toString(), maxLat.toString(), minLon.toString(), maxLon.toString()});
        List<Event> events = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            events.add(toEvent(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return events;
    }

    /**
     * Get list of events given a center and a radius in metres
     * @param lat
     * @param lon
     * @param radius in metres
     * @return
     */
    public List<Event> getByRadius(Double lat, Double lon, int radius){
        Double radiusCoords =  ((double) radius / 111000);
        return getByArea(lat-radiusCoords, lat+radiusCoords, lon-radiusCoords, lon+radiusCoords);
    }

    /**
     * Get an event given its id
     * @param eventId
     * @return
     * @throws NoEventFoundException
     */
    public Event getById(int eventId) throws NoEventFoundException {
        Cursor cursor = getReadableDatabase().rawQuery(EventStatements.SQL_SELECT_BY_ID,
                new String[]{String.valueOf(eventId)});
        cursor.moveToFirst();
        if(cursor.isAfterLast()){
            cursor.close();
            throw new NoEventFoundException();
        } else {
            Event e = toEvent(cursor);
            cursor.close();
            return e;
        }
    }

    public void deleteById(int eventId){
        getWritableDatabase().delete(EventEntry.TABLE_NAME, EventEntry._ID + " = ?", new String[]{String.valueOf(eventId)});
    }

    public void modifyVote(int eventId, int vote) throws NoEventFoundException {
        Event event = getById(eventId);
        event.setVotes(event.getVotes()+vote);
        addEvent(event);
    }

    /**
     * Convert a cursor row into an Event
     * @param cursor
     * @return Event
     */
    private static Event toEvent(Cursor cursor){
        Event e = new Event();
        e.setId(cursor.getInt(0));
        e.setDate(new Date(cursor.getLong(1)));
        e.setEventType(EventType.valueOf(cursor.getString(2)));
        e.setDescription(cursor.getString(3));
        e.setCountry(cursor.getString(4));
        e.setCity(cursor.getString(5));
        e.setStreet(cursor.getString(6));
        e.setLatitude(cursor.getDouble(7));
        e.setLongitude(cursor.getDouble(8));
        e.setSubmitterId(cursor.getString(9));
        e.setSubmitterId(cursor.getString(10));
        return e;
    }

    public class NoEventFoundException extends Exception {
    }
}
