package com.moscowmuleaddicted.neighborhoodsecurity.services;

import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

/**
 * Created by Simone Ripamonti on 25/04/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh(){
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
