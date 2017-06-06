package com.moscowmuleaddicted.neighborhoodsecurity.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.EventDetailActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.db.EventDB;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.EventType;

import org.apache.commons.lang3.math.NumberUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.SHARED_PREFERENCES_SUBSCRIPTIONS;

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

            int subscriptionId = NumberUtils.toInt(remoteMessage.getData().get("subscriptionId"), -1);
            String subscriptionOwner = remoteMessage.getData().get("subscriptionOwner");

            Log.d(TAG, "received notification about subscription "+subscriptionId + " owned by "+subscriptionOwner);
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if(firebaseUser == null){
                Log.w(TAG, "discarding notification because no user is logged in");
                return;
            }

            if(!firebaseUser.getUid().equals(subscriptionOwner)) {
                Log.w(TAG, "discarding notification because it is not for the current user");
                return;
            }

            if(subscriptionId < 0) {
                Log.w(TAG, "discarding notification about an unknown subscription");
                return;
            }

            SharedPreferences sharedPreferencesSubscriptions = getSharedPreferences(SHARED_PREFERENCES_SUBSCRIPTIONS, MODE_PRIVATE);
            boolean subscriptionEnabled = sharedPreferencesSubscriptions.getBoolean(String.valueOf(subscriptionId), true);
            Log.d(TAG, "subscription is enabled? "+subscriptionEnabled);
            if(!subscriptionEnabled) {
                Log.d(TAG, "discarding notification because subscription is disabled");
                return;
            }

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

            // increment counter
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null){
                String uid = user.getUid();
                SharedPreferences sharedPreferencesNotifications = getSharedPreferences(Constants.SHARED_PREFERENCES_NOTIFICATION_COUNT_BY_UID, Context.MODE_PRIVATE);
                int notificationCount = sharedPreferencesNotifications.getInt(uid, 0);
                SharedPreferences.Editor editor = sharedPreferencesNotifications.edit();
                editor.putInt(uid, notificationCount + 1);
                editor.commit();
            }


        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
}
