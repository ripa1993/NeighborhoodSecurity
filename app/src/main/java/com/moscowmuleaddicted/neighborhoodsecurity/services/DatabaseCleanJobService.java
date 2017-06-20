package com.moscowmuleaddicted.neighborhoodsecurity.services;


import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.db.EventDB;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.db.SubscriptionDB;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.MINUTES_IN_DAY;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.MINUTES_IN_HOUR;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.SECONDS_IN_MINUTE;

/**
 * Created by Simone Ripamonti on 20/06/2017.
 */

public class DatabaseCleanJobService extends JobService {

    public static final String TAG = "DatabaseCleanJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "starting");

        SubscriptionDB subscriptionDB = new SubscriptionDB(getApplicationContext());
        EventDB eventDB = new EventDB(getApplicationContext());

        int removedSubscriptions = subscriptionDB.clearOldSubscriptions();
        int removedEvents =  eventDB.clearOldEvents();

        Log.d(TAG, "deleted "+removedSubscriptions+" subscriptions and "+removedEvents+" events because they were older than 7 days");

        Log.d(TAG, "scheduling next job");
        // prepare database clean job
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));
        dispatcher.cancelAll();
        int waitSeconds = SECONDS_IN_MINUTE*MINUTES_IN_HOUR*MINUTES_IN_DAY;
        Job dbCleanJob = dispatcher.newJobBuilder()
                .setService(DatabaseCleanJobService.class)
                .setTag(Constants.TAG_DB_CLEAN_JOB)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(waitSeconds, waitSeconds))
                .build();
        dispatcher.mustSchedule(dbCleanJob);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "stopping");
        return false;
    }
}
