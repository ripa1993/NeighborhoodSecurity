package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventCreateFragment;

public class EventCreateActivity extends AppCompatActivity implements EventCreateFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
