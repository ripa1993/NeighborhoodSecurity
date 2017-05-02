package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.NSMapFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;

import java.util.List;


public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.fragment_ns_map);

        // Get the MapFragment
        NSMapFragment mapFragment = (NSMapFragment) getFragmentManager().findFragmentById(R.id.map);

        // Get data passed to the intent
        Bundle extras = getIntent().getExtras();
        double initialLat;
        double initialLng;
        List<Event> initialEvents;
        if(extras != null){
            // Set initial position if passed
            if(extras.getSerializable("lat") != null && extras.getSerializable("lng") != null) {
                initialLat = (double) extras.getSerializable("lat");
                initialLng = (double) extras.getSerializable("lng");
                mapFragment.setInitialPosition(new LatLng(initialLat, initialLng));
            }
            // Set initial events if passed
            if(extras.getSerializable("events") != null) {
                initialEvents = (List<Event>) extras.getSerializable("events");
                mapFragment.setInitialEvents(initialEvents);
            }
        }

        mapFragment.getMapAsync(mapFragment);

    }

}

