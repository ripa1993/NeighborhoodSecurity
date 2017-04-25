package com.moscowmuleaddicted.neighborhoodsecurity.services;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventType;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by Simone Ripamonti on 25/04/2017.
 *
 * https://firebase.google.com/docs/cloud-messaging/android/receive#sample-receive
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0){
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            EventType et = EventType.valueOf(remoteMessage.getData().get("eventType").toUpperCase());
            String city = remoteMessage.getData().get("city");
            String street = remoteMessage.getData().get("street");
            String description = remoteMessage.getData().get("description");
            int id = NumberUtils.toInt(remoteMessage.getData().get("id"), 0);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_mood_bad)
                    .setContentTitle(et +" @ "+ city+", "+street)
                    .setContentText(description);

            // todo: set PendingIntent, in order to open the intent when clicking on the notification

            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            mNotifyMgr.notify(id, mBuilder.build());


        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
}
