package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.common.GoogleApiAvailability;
import com.moscowmuleaddicted.neighborhoodsecurity.services.DatabaseCleanJobService;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.MINUTES_IN_DAY;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.MINUTES_IN_HOUR;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.PLAY_SERVICES_MIN_VERSION;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.SECONDS_IN_MINUTE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.TAG_DB_CLEAN_JOB;

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    public static final String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load play services
        int currentVersion = 0;
        try {
            currentVersion = getPackageManager().getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "PackageManager.NameNotFoundExc "+e.getMessage());
        }

        // check if current user has a valid version of Google Play Services Installed
        if (currentVersion < PLAY_SERVICES_MIN_VERSION) {
            // if invalid
            Log.w(TAG, "Need Play Services "+PLAY_SERVICES_MIN_VERSION+", has "+currentVersion);
            int error = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, error, 1);
            dialog.show();
        } else {
            // if valid
            Log.d(TAG, "Play Service OK "+currentVersion);

            // warm up server
            Log.d(TAG, "warming up rest server");
            NSService.getInstance(getApplicationContext()).warmUp();

            // prepare database clean job
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));
            dispatcher.cancel(TAG_DB_CLEAN_JOB);
            Calendar calendar = new GregorianCalendar();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            int waitSeconds = (MINUTES_IN_DAY - hour*MINUTES_IN_HOUR - minutes)*SECONDS_IN_MINUTE;
            Job dbCleanJob = dispatcher.newJobBuilder()
                    .setService(DatabaseCleanJobService.class)
                    .setTag(Constants.TAG_DB_CLEAN_JOB)
                    .setRecurring(true)
                    .setLifetime(Lifetime.FOREVER)
                    .setTrigger(Trigger.executionWindow(waitSeconds, waitSeconds+300))
                    .build();
            dispatcher.mustSchedule(dbCleanJob);
            Log.d(TAG, "scheduled clean job");

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
