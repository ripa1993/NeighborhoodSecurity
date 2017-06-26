package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.NSMapFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.model.Event;
import com.scalified.fab.ActionButton;

import java.util.List;

import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.IE_EVENT_LIST;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.IE_LATITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.IE_LONGITUDE;

/**
 * Activity that shows a map that is refreshed when it is moved
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class MapActivity extends AppCompatActivity {
    /**
     * Logger's TAG
     */
    public static final String TAG = "MapAct";
    /**
     * The customized Google Map fragment
     */
    private NSMapFragment mMapFragment;
    /**
     * The place autocomplete fragment, used to search locations
     */
    private PlaceAutocompleteFragment mPlaceAutocompleteFragment;
    /**
     * Floating action button as a shortcut to event creation
     */
    private ActionButton mFabNewEvent;
    /**
     * FirebaseAuth instance
     */
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        mAuth = FirebaseAuth.getInstance();

        // Get the MapFragment
        mMapFragment = (NSMapFragment) getFragmentManager().findFragmentById(R.id.map);
        // Get the PlacesAutomcomplete
        mPlaceAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        // Get the FAB
        mFabNewEvent = (ActionButton) findViewById(R.id.new_fab);

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
                mMapFragment.setInitialPosition(new LatLng(initialLat, initialLng));
            }
            // Set initial events if passed
            if(extras.getSerializable(IE_EVENT_LIST) != null) {
                initialEvents = (List<Event>) extras.getSerializable(IE_EVENT_LIST);
                mMapFragment.setInitialEvents(initialEvents);
            }
        }

        mMapFragment.getMapAsync(mMapFragment);

        mPlaceAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.w(TAG, "PlaceAutocomplete returned "+place.getLatLng());
                mMapFragment.moveCamera(place.getLatLng(), true);
            }

            @Override
            public void onError(Status status) {
                Log.w(TAG, status.getStatusMessage());
            }
        });

        mFabNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()!=null) {
                    Intent intent = new Intent(MapActivity.this, EventCreateActivity.class);
                    startActivity(intent);
                }else {
                    Log.d(TAG, "user is not logged in, this is required to access create event!");
                    Toast.makeText(getApplicationContext(), getString(R.string.login_required_toast), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}

