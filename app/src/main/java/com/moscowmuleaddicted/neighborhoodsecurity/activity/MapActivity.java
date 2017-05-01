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


public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.fragment_ns_map);

        // Get the MapFragment and request notification
        // when the map is ready to be used.
        NSMapFragment mapFragment = (NSMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(mapFragment);
    }

}

