package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Subscription;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, OnRequestPermissionsResultCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_PERMISSION_LOCATION = 100;
    private static final String TAG = "HomePageActivity";

    Button bMap, bProfile, bEvents, bSubscriptions;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mAuth = FirebaseAuth.getInstance();

        bMap = (Button) findViewById(R.id.button_map);
        bProfile = (Button) findViewById(R.id.button_profile);
        bEvents = (Button) findViewById(R.id.button_events);
        bSubscriptions = (Button) findViewById(R.id.button_subscriptions);

        bMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);

                if (mLastLocation != null) {
                    // start with last known position
                    Log.d(TAG, "starting map activity with current position");
                    mapIntent.putExtra("lat", mLastLocation.getLatitude());
                    mapIntent.putExtra("lng", mLastLocation.getLongitude());

                    NSService.getInstance(getApplicationContext()).getEventsByRadius(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 2000, new NSService.MyCallback<List<Event>>() {
                        @Override
                        public void onSuccess(List<Event> events) {
                            Log.d(TAG, "found " + events.size() + " events to show");
                            mapIntent.putExtra("events", new ArrayList<Event>(events));
                            startActivity(mapIntent);
                        }

                        @Override
                        public void onFailure() {
                            Log.w(TAG, "unknown failure");
                            startActivity(mapIntent);
                        }

                        @Override
                        public void onMessageLoad(MyMessage message, int status) {
                            Log.w(TAG, message.getArgument() + " - " + message.getMessage());
                            startActivity(mapIntent);
                        }
                    });

                } else {
                    // start without last known position
                    Log.d(TAG, "starting map activity without current position");
                    startActivity(mapIntent);
                }

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
                final Intent subscriptionIntent = new Intent(getApplicationContext(), SubscriptionListActivity.class);
                if (mAuth.getCurrentUser() != null) {
                    NSService.getInstance(getApplicationContext()).getSubscriptionsByUser(mAuth.getCurrentUser().getUid(), new NSService.MyCallback<List<Subscription>>() {
                        @Override
                        public void onSuccess(List<Subscription> subscriptions) {
                            Log.d(TAG, "found "+subscriptions.size()+" subscriptions");
                            subscriptionIntent.putExtra("subscription-list", new ArrayList<Subscription>(subscriptions));
                            startActivity(subscriptionIntent);
                        }

                        @Override
                        public void onFailure() {
                            Log.w(TAG, "unknown failure");
                            startActivity(subscriptionIntent);
                        }

                        @Override
                        public void onMessageLoad(MyMessage message, int status) {
                            Log.w(TAG, message.getArgument() + " - " + message.getMessage());
                            startActivity(subscriptionIntent);
                        }
                    });

                } else {
                    Log.d(TAG, "user is not logged in, this is required when accessing subscription list!");
                    Toast.makeText(getApplicationContext(), getString(R.string.login_required_toast), Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    protected void onStart() {
        Log.d(TAG, "onStart: connecting google api client");
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        Log.d(TAG, "onStop: disconnecting google api client");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // request permissions for accessing location, requires SDK >= 23 (marshmellow)
                Log.d(TAG, "onConnected: prompting user to allow location permissions");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            } else {
                Log.w(TAG, "onConnected: SDK version is too low (" + Build.VERSION.SDK_INT + ") to ask permissions at runtime");
                Toast.makeText(getApplicationContext(), "Give location permission to allow application know events around you", Toast.LENGTH_LONG).show();
            }

        } else {
            // permissions already granted
            Log.d(TAG, "onConnected: location permission already granted, requesting last known position");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: location permission granted, requesting last known position");
                //noinspection MissingPermission
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } else {
                Log.d(TAG, "onRequestPermissionsResult: location permission not granted");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: code " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }
}
