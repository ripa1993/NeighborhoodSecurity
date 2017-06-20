package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.NSMapFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Event;
import com.scalified.fab.ActionButton;

import java.util.List;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_EVENT_LIST;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_LATITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_LONGITUDE;


public class MapActivity extends AppCompatActivity {

    public static final String TAG = "MapActivity";
    NSMapFragment mapFragment;
    PlaceAutocompleteFragment placeAutocompleteFragment;
    ActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        // Get the MapFragment
        mapFragment = (NSMapFragment) getFragmentManager().findFragmentById(R.id.map);
        // Get the PlacesAutomcomplete
        placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        // Get the FAB
        fab = (ActionButton) findViewById(R.id.new_fab);

        // Get data passed to the intent
        Bundle extras = getIntent().getExtras();
        double initialLat;
        double initialLng;
        List<Event> initialEvents;
        if(extras != null){
            // Set initial position if passed
            if(extras.getSerializable(IE_LATITUDE) != null && extras.getSerializable(IE_LONGITUDE) != null) {
                initialLat = (double) extras.getSerializable(IE_LATITUDE);
                initialLng = (double) extras.getSerializable(IE_LONGITUDE);
                mapFragment.setInitialPosition(new LatLng(initialLat, initialLng));
            }
            // Set initial events if passed
            if(extras.getSerializable(IE_EVENT_LIST) != null) {
                initialEvents = (List<Event>) extras.getSerializable(IE_EVENT_LIST);
                mapFragment.setInitialEvents(initialEvents);
            }
        }

        mapFragment.getMapAsync(mapFragment);

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.w(TAG, "PlaceAutocomplete returned "+place.getLatLng());
                mapFragment.moveCamera(place.getLatLng(), true);
            }

            @Override
            public void onError(Status status) {
                Log.w(TAG, status.getStatusMessage());
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, EventCreateActivity.class);
                startActivity(intent);
            }
        });
    }

}

