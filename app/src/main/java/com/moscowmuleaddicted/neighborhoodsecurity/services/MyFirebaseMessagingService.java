package com.moscowmuleaddicted.neighborhoodsecurity.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.EventDetailActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.db.EventDB;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventType;

import org.apache.commons.lang3.math.NumberUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Simone Ripamonti on 25/04/2017.
 * <p>
 * https://firebase.google.com/docs/cloud-messaging/android/receive#sample-receive
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

            int eId = NumberUtils.toInt(remoteMessage.getData().get("id"), 0);
            Date eDate;
            try {
                eDate = inFormat.parse(remoteMessage.getData().get("date"));
            } catch (ParseException e) {
                eDate = new Date();
            }
            EventType eEventType = EventType.valueOf(remoteMessage.getData().get("eventType").toUpperCase());
            String eDescription = remoteMessage.getData().get("description");
            String eCountry = remoteMessage.getData().get("country");
            String eCity = remoteMessage.getData().get("city");
            String eStreet = remoteMessage.getData().get("street");
            Double eLatitude = NumberUtils.toDouble(remoteMessage.getData().get("latitude"), 0);
            Double eLongitude = NumberUtils.toDouble(remoteMessage.getData().get("longitude"), 0);
            int eVotes = NumberUtils.toInt(remoteMessage.getData().get("votes"), 0);
            String eSubmitterId = remoteMessage.getData().get("submitterId");

            Event event = new Event(eId, eDate, eEventType, eDescription, eCountry,
                    eCity, eStreet, eLatitude, eLongitude, eVotes, eSubmitterId);

            // save in local db
            EventDB db = new EventDB(this);
            db.addEvent(event);
            db.close();

            Intent eventDetailIntent = new Intent(this, EventDetailActivity.class);
            eventDetailIntent.putExtra("event", event);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0,
                    eventDetailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_mood_bad)
                    .setContentTitle(eEventType + " @ " + eCity + ", " + eStreet)
                    .setContentText(eDescription)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSound(notificationSound);

            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            mNotifyMgr.notify(eId, mBuilder.build());


        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
}
