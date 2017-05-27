package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.PLAY_SERVICES_MIN_VERSION;

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    public static final String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int currentVersion = 0;
        try {
            currentVersion = getPackageManager().getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "PackageManager.NameNotFoundExc "+e.getMessage());
        }

        // check if current user has a valid version of Google Play Services Installed
        if (currentVersion < PLAY_SERVICES_MIN_VERSION) {
            Log.w(TAG, "Need Play Services "+PLAY_SERVICES_MIN_VERSION+", has "+currentVersion);
            int error = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, error, 1);
            dialog.show();
        } else {
            Log.d(TAG, "Play Service OK "+currentVersion);
            // Start HomePage after SPLASH_TIME_OUT
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Start SplashActivity once the timer is over
                    //Intent intent = new Intent(SplashActivity.this, TestRestAPI.class);
                    Intent intent = new Intent(SplashActivity.this, HomePage.class);
                    startActivity(intent);

                    // Close this Activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }
}
