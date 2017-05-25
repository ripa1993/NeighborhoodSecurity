package com.moscowmuleaddicted.neighborhoodsecurity.activity;


import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.adapter.MyEventRecyclerViewAdapter;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventListFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Subscription;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;
import com.scalified.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.CREATE_EVENT_REQUEST_CODE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE;
import static xdroid.core.Global.getContext;

/**
 * Activity that shows a list of Events
 * @author Simone Ripamonti
 */
public class EventListActivity extends AppCompatActivity implements EventListFragment.OnListFragmentInteractionListener {
    /**
     * Source of the event
     */
    private enum UpdateType{
        UID, SUBSCRIPTION, LOCATION, NONE
    }
    /**
     * Log tag
     */
    private static final String TAG = "EventListActivity";
    /**
     * The contained fragment
     */
    private EventListFragment mFragment;
    /**
     * Floating action button
     */
    private ActionButton mFab;
    /**
     * Swipe Refresh Layout used to update the shown Events
     */
    private SwipeRefreshLayout mSwipe;
    /**
     * Source of the Event shown
     */
    private UpdateType updateType = UpdateType.NONE;
    /**
     * Auxiliary info about source SUBSCRIPTION
     */
    private Subscription sub;
    /**
     * Auxiliary info about source UID
     */
    private String uid;
    /**
     * Auxiliary info about source LOCATION
     */
    private Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Bundle extras = getIntent().getExtras();


        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_event_list);
        mSwipe.setEnabled(false);

        ArrayList<Event> events = new ArrayList<>();
        if(extras != null) {
            if(extras.containsKey("event-list")){
                // if an event list is provided
                Log.d(TAG, "creating fragment from provided event list");
                events = (ArrayList<Event>) extras.getSerializable("event-list");
                mFragment = EventListFragment.newInstance(1, events);
                updateType = UpdateType.NONE;
                setTitle(getString(R.string.title_event_list_generic));
                Log.d(TAG, "fragment created");
            } else if (extras.containsKey("UID")){
                // if an uid is provided
                Log.d(TAG, "creating fragment from provided UID");
                mSwipe.setRefreshing(true);
                mSwipe.setEnabled(true);
                updateType = UpdateType.UID;
                uid = extras.getString("UID");
                events.addAll(getByUid());
                mFragment = EventListFragment.newInstance(1, events);
                setTitle(getString(R.string.title_event_list_uid));
                Log.d(TAG, "fragment created");
            } else if (extras.containsKey("subscription")){
                // if a subscription is provided
                Log.d(TAG, "creating fragment from provided subscription");
                sub = (Subscription) extras.getSerializable("subscription");
                mSwipe.setRefreshing(true);
                mSwipe.setEnabled(true);
                updateType = UpdateType.SUBSCRIPTION;
                events.addAll(getBySub());
                mFragment = EventListFragment.newInstance(1, events);
                setTitle(getString(R.string.title_event_list_subscription));
                Log.d(TAG, "fragment created");
            }
        } else {
            // if nothing is provided
            mFragment = new EventListFragment();
            Log.d(TAG, "fragment created");
        }

        // initialize the fragment
        Log.d(TAG, "showing fragment using support fragment manager");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.event_list_fragment, mFragment);
        fragmentTransaction.commit();

        mFab = (ActionButton) findViewById(R.id.event_create_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EventCreateActivity.class);
                startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
            }
        });

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
    }

    private void refreshList() {
        switch (updateType){
            case NONE:
                // should not be enabled
                mSwipe.setEnabled(false);
                mSwipe.setRefreshing(false);
                return;
            case UID:
                getByUid();
                return;
            case SUBSCRIPTION:
                getBySub();
                return;
            case LOCATION:
                getByLocation();
                return;
            default:
                mSwipe.setEnabled(false);
                mSwipe.setRefreshing(false);
                return;
        }
    }

    @Override
    public void onListFragmentInteraction(Event event) {
        Log.d(TAG, event.toString());

        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);

    }

    @Override
    public void scrollingUp() {
        if (!mFab.isHidden()) {
            mFab.hide();
        }
    }

    @Override
    public void scrollingDown() {
        if (mFab.isHidden()) {
            mFab.show();
        }
    }

    /**
     * Auxiliary method to retrieve fresh Events from NSService using UID and update the list shown
     * @return the events found locally on the SQLite DB
     */
    private List<Event> getByUid(){
        return NSService.getInstance(getApplicationContext()).getEventsByUser(uid, new NSService.MyCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                Log.d(TAG, "events from UID: found "+events.size()+ " events");
                RecyclerView recyclerView = mFragment.getRecyclerView();
                ((MyEventRecyclerViewAdapter) recyclerView.getAdapter()).addEvents(events);
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                Log.w(TAG, "events from UID: failure");
                Toast.makeText(getApplicationContext(), getString(R.string.msg_network_problem_events_upd), Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Log.w(TAG, "events from UID: "+message);
                String msg = "";
                switch (status){
                    case 400:
                        msg = getString(R.string.msg_400_bad_request_events);
                        break;
                    case 404:
                        msg = getString(R.string.msg_404_not_found_user_events);
                        break;
                    case 500:
                        msg = getString(R.string.msg_500_internal_server_error_events);
                        break;
                    default:
                        msg = getString(R.string.msg_unknown_error);
                        break;
                }
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }
        });
    }

    /**
     * Auxiliary method to retrieve fresh Events from NSService using Subscription and update the list shown
     * @return the events found locally on the SQLite DB
     */
    private List<Event> getBySub(){
        return NSService.getInstance(getApplicationContext()).getEventsByArea(sub.getMinLat(), sub.getMaxLat(), sub.getMinLon(), sub.getMaxLon(), new NSService.MyCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                Log.d(TAG, "events from sub: found "+events.size()+ " events");
                RecyclerView recyclerView = mFragment.getRecyclerView();
                ((MyEventRecyclerViewAdapter) recyclerView.getAdapter()).addEvents(events);
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                Log.w(TAG, "events from sub: failure");
                Toast.makeText(getContext(), getString(R.string.msg_network_problem_events_upd), Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Log.w(TAG, "events from sub: "+message);
                String msg = "";
                switch (status){
                    case 400:
                        msg = getString(R.string.msg_400_bad_request_events);
                        break;
                    case 500:
                        msg = getString(R.string.msg_500_internal_server_error_events);
                        break;
                    default:
                        msg = getString(R.string.msg_unknown_error);
                        break;
                }
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }
        });
    }

    private List<Event> getByLocation(){
        return NSService.getInstance(getContext()).getEventsByRadius(latitude, longitude, 2000, new NSService.MyCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                Log.d(TAG, "events from location: found "+events.size()+ " events");
                RecyclerView recyclerView = mFragment.getRecyclerView();
                ((MyEventRecyclerViewAdapter) recyclerView.getAdapter()).addEvents(events);
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                Log.w(TAG, "events from location: failure");
                Toast.makeText(getContext(), getString(R.string.msg_network_problem_events_upd), Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Log.w(TAG, "events from location: "+message);
                String msg = "";
                switch (status){
                    case 400:
                        msg = getString(R.string.msg_400_bad_request_events);
                        break;
                    case 500:
                        msg = getString(R.string.msg_500_internal_server_error_events);
                        break;
                    default:
                        msg = getString(R.string.msg_unknown_error);
                        break;
                }
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }
        });
    }

    public void findPlace() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CREATE_EVENT_REQUEST_CODE && resultCode == RESULT_OK){
            refreshList();
        }

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());

                // set refreshing
                mSwipe.setEnabled(true);
                mSwipe.setRefreshing(true);

                // set update type
                updateType = UpdateType.LOCATION;

                // clear the list
                RecyclerView recyclerView = mFragment.getRecyclerView();
                MyEventRecyclerViewAdapter adapter = (MyEventRecyclerViewAdapter) recyclerView.getAdapter();
                adapter.clear();

                // update lat lng
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                // set title
                String title = String.format(getString(R.string.title_event_list_location), place.getName());
                setTitle(title);

                // update list
                adapter.addEvents(getByLocation());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.w(TAG, "PlaceAutocomplete error "+status.getStatusMessage());
                Toast.makeText(getContext(), getString(R.string.msg_unknown_error), Toast.LENGTH_SHORT).show();
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "PlaceAutocomplete canceled");
                // The user canceled the operation.
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                findPlace();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_list, menu);
        return true;
    }
}
