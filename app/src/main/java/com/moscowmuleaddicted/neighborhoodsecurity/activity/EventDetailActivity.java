package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.moscowmuleaddicted.neighborhoodsecurity.adapter.MyEventDetailRecyclerViewAdapter;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventDetailListFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.details.Details;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

public class EventDetailActivity extends AppCompatActivity implements EventDetailListFragment.OnListFragmentInteractionListener{

    public static final String TAG = "EventDetailAct";

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
                        Log.d(TAG, "success in voting the event");
                        fab.setEnabled(false);
                        fab.setImageDrawable(getDrawable(R.drawable.ic_star));
                        final MyEventDetailRecyclerViewAdapter adapter =(MyEventDetailRecyclerViewAdapter) ((RecyclerView) findViewById(R.id.eventDetailRecyclerView)).getAdapter();
                        adapter.updateVotes(1);
                        final Snackbar snack = Snackbar.make(view, getString(R.string.event_voted), Snackbar.LENGTH_INDEFINITE);
                        snack.show();
                        snack.setAction(getString(R.string.event_voted_undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NSService.getInstance(getApplicationContext()).unvoteEvent(eventId, new NSService.MyCallback<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        Log.d(TAG, "success in unvoting the event");
                                        snack.dismiss();
                                        fab.setEnabled(true);
                                        fab.setImageDrawable(getDrawable(R.drawable.ic_star_border));
                                        adapter.updateVotes(-1);
                                    }

                                    @Override
                                    public void onFailure() {
                                        Log.w(TAG, "failure in unvoting the event");
                                        Toast.makeText(getApplicationContext(), getString(R.string.msg_network_problem_event_unvote), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onMessageLoad(MyMessage message, int status) {
                                        Log.w(TAG, "failure in unvoting the event with msg: "+message);
                                        String msg = "";
                                        switch(status){
                                            case 204:
                                                msg = getString(R.string.msg_204_no_content_event_unvote);
                                                break;
                                            case 400:
                                                msg = getString(R.string.msg_400_bad_request_event_vote);
                                                break;
                                            case 401:
                                                msg = getString(R.string.msg_401_unauthorized_event_vote);
                                                break;
                                            case 404:
                                                msg = getString(R.string.msg_404_not_found_event_vote);
                                                break;
                                            case 500:
                                                msg = getString(R.string.msg_500_internal_server_error_event_vote);
                                                break;
                                            default:
                                                msg = getString(R.string.msg_unknown_error);
                                                break;
                                        }
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        Log.w(TAG, "failure in voting the event");
                        Toast.makeText(getApplicationContext(), getString(R.string.msg_network_problem_event_vote), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMessageLoad(MyMessage message, int status) {
                        Log.w(TAG, "failure in voting the event with msg: "+message);
                        String msg = "";
                        switch(status){
                            case 204:
                                msg = getString(R.string.msg_204_no_content_event_vote);
                                break;
                            case 400:
                                msg = getString(R.string.msg_400_bad_request_event_vote);
                                break;
                            case 401:
                                msg = getString(R.string.msg_401_unauthorized_event_vote);
                                break;
                            case 404:
                                msg = getString(R.string.msg_404_not_found_event_vote);
                                break;
                            case 500:
                                msg = getString(R.string.msg_500_internal_server_error_event_vote);
                                break;
                            default:
                                msg = getString(R.string.msg_unknown_error);
                                break;
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();                    }
                });
            }
        });
    }

    @Override
    public void onListFragmentInteraction(Details item) {

    }
}
