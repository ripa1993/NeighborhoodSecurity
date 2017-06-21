package com.moscowmuleaddicted.neighborhoodsecurity.services;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

/**
 * Extension of {@link FirebaseInstanceIdService} that is used to send the new FCM token to the
 * remote server when it changes, so that the device can receive notificatinos
 */

public class FCMTokenRefreshService extends FirebaseInstanceIdService {
    /**
     * Logger's TAG
     */
    public static final String TAG = "FCMTokenRefresh";

    @Override
    public void onTokenRefresh(){
        Log.d(TAG, "refreshing fcm token");
        String token = FirebaseInstanceId.getInstance().getToken();
        NSService.getInstance(getApplicationContext()).updateFcm(token, new NSService.MyCallback<MyMessage>() {
            @Override
            public void onSuccess(MyMessage myMessage) {
                Toast.makeText(getApplicationContext(), "success sending fcm token", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "failure sending fcm token", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), "fcm token ("+status+") "+message.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
