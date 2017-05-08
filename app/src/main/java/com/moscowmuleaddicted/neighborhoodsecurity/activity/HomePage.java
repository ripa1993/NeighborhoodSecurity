package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    Button bMap, bProfile, bEvents, bSubscriptions;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();

        bMap = (Button) findViewById(R.id.button_map);
        bProfile = (Button) findViewById(R.id.button_profile);
        bEvents = (Button) findViewById(R.id.button_events);
        bSubscriptions = (Button) findViewById(R.id.button_subscriptions);

        bMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(mapIntent);
            }
        });

        bEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent eventIntent = new Intent(getApplicationContext(), EventListActivity.class);
                startActivity(eventIntent);
            }
        });

        bSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent subscriptionIntent = new Intent(getApplicationContext(), SubscriptionListActivity.class);
                startActivity(subscriptionIntent);
            }
        });


    }
}
