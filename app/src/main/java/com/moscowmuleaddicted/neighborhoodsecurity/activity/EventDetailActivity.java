package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.moscowmuleaddicted.neighborhoodsecurity.EventDetailListFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.details.Details;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

public class EventDetailActivity extends AppCompatActivity implements EventDetailListFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // get data passed to the intent

        Bundle extras = getIntent().getExtras();
        Event event;
        if(extras != null){
            event = (Event) extras.getSerializable("event");
            if(event == null) {
                event = Event.makeDummy();
            }
        } else {
            event = Event.makeDummy();
        }

        // setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(event.getEventType().toString());
        setSupportActionBar(toolbar);

        // setup map fragment
        final double lat, lon;
        lat = event.getLatitude();
        lon = event.getLongitude();
        GoogleMapOptions gmo = new GoogleMapOptions();
        final LatLng coords = new LatLng(lat,lon);
        CameraPosition cp = new CameraPosition.Builder().target(coords).zoom(16f).build();
        gmo.camera(cp);
        gmo.scrollGesturesEnabled(false);
        gmo.tiltGesturesEnabled(false);
        gmo.rotateGesturesEnabled(false);
        MapFragment mapFragment = MapFragment.newInstance(gmo);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MarkerOptions mo = new MarkerOptions();
                mo.position(coords);
                googleMap.addMarker(mo);
            }
        });

        // setup event detail list fragment
        EventDetailListFragment edl = EventDetailListFragment.newInstance(1, event);


        // initialize the fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.eventDetailListFragment, edl);
        fragmentTransaction.add(R.id.eventDetailMapFragment, mapFragment);
        fragmentTransaction.commit();


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final int eventId = event.getId();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                NSService.getInstance(getApplicationContext()).voteEvent(eventId, new NSService.MyCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        fab.setEnabled(false);
                        fab.setImageDrawable(getDrawable(R.drawable.ic_star));
                        final Snackbar snack = Snackbar.make(view, "Event voted", Snackbar.LENGTH_INDEFINITE);
                        snack.show();
                        snack.setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NSService.getInstance(getApplicationContext()).unvoteEvent(eventId, new NSService.MyCallback<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        snack.dismiss();
                                        fab.setEnabled(true);
                                        fab.setImageDrawable(getDrawable(R.drawable.ic_star_border));
                                    }

                                    @Override
                                    public void onFailure() {
                                        Toast.makeText(getApplicationContext(), "There was some problem...", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onMessageLoad(MyMessage message, int status) {
                                        Toast.makeText(getApplicationContext(), "Error: "+message.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(getApplicationContext(), "There was some problem...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMessageLoad(MyMessage message, int status) {
                        Toast.makeText(getApplicationContext(), "Error: "+message.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onListFragmentInteraction(Details item) {
        Toast.makeText(getApplicationContext(), item.getContent(), Toast.LENGTH_SHORT).show();
    }
}
