package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start HomePage after SPLASH_TIME_OUT
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start SplashActivity once the timer is over
                Intent intent = new Intent(SplashActivity.this, HomePage.class);
                startActivity(intent);

                // Close this Activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
