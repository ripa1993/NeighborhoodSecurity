package com.moscowmuleaddicted.neighborhoodsecurity.activity;


import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_EVENT;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_EVENT_LIST;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_SUBSCRIPTION;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_UID;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.RC_CREATE_EVENT;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.RC_PLACE_AUTOCOMPLETE;
import static xdroid.core.Global.getContext;

/**
 * Activity that shows a list of Events
 *
 * @author Simone Ripamonti
 */
public class EventListActivity extends AppCompatActivity implements EventListFragment.OnListFragmentInteractionListener {
    /**
     * Source of the event
     */
    private enum UpdateType {
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
    /**
     * Tell if the activity is in front, to prevent showing toasts in wrong activity
     */
    private boolean isInFront = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Bundle extras = getIntent().getExtras();


        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_event_list);
        mSwipe.setEnabled(false);

        ArrayList<Event> events = new ArrayList<>();
        if (extras != null) {
            if (extras.containsKey(IE_EVENT_LIST)) {
                // if an event list is provided
                Log.d(TAG, "creating fragment from provided event list");
                events = (ArrayList<Event>) extras.getSerializable(IE_EVENT_LIST);
                mFragment = EventListFragment.newInstance(1, events);
                updateType = UpdateType.NONE;
                setTitle(getString(R.string.title_event_list_generic));
                Log.d(TAG, "fragment created");
            } else if (extras.containsKey(IE_UID)) {
                // if an uid is provided
                Log.d(TAG, "creating fragment from provided UID");
                mSwipe.setRefreshing(true);
                mSwipe.setEnabled(true);
                updateType = UpdateType.UID;
                uid = extras.getString(IE_UID);
                events.addAll(getByUid());
                mFragment = EventListFragment.newInstance(1, events);
                setTitle(getString(R.string.title_event_list_uid));
                Log.d(TAG, "fragment created");
            } else if (extras.containsKey(IE_SUBSCRIPTION)) {
                // if a subscription is provided
                Log.d(TAG, "creating fragment from provided subscription");
                sub = (Subscription) extras.getSerializable(IE_SUBSCRIPTION);
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
                startActivityForResult(intent, RC_CREATE_EVENT);
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
        switch (updateType) {
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
    public void onListItemClick(Event event) {
        Log.d(TAG, event.toString());

        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra(IE_EVENT, event);
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

    @Override
    public boolean onListItemLongClick(final Event mItem, View view) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            // user not logged in
            return false;
        }
        if (!mUser.getUid().equals(mItem.getSubmitterId())) {
            // user is not the owner!
            return false;
        }
        // show  menu
        PopupMenu popupMenu = new PopupMenu(getApplication(), view, Gravity.CENTER);
        popupMenu.inflate(R.menu.menu_delete_event);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete_event:
                        NSService.getInstance(getApplicationContext()).deleteEvent(mItem.getId(), new NSService.MyCallback<String>() {
                            @Override
                            public void onSuccess(String s) {
                                mFragment.removeEvent(mItem);
                                Toast.makeText(getApplicationContext(), getString(R.string.msg_event_deleted), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                String toastMessage = getString(R.string.msg_unknown_error);
                                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onMessageLoad(MyMessage message, int status) {
                                String toastMessage = "";
                                switch (status) {
                                    case 400:
                                        toastMessage = getString(R.string.msg_400_bad_request_delete_event);
                                        break;
                                    case 401:
                                        toastMessage = getString(R.string.msg_401_unauthorized_delete_event);
                                        break;
                                    case 404:
                                        toastMessage = getString(R.string.msg_404_not_found_delete_event);
                                        mFragment.removeEvent(mItem);
                                        break;
                                    case 500:
                                        toastMessage = getString(R.string.msg_500_internal_server_error_delete_event);
                                        break;
                                    default:
                                        toastMessage = getString(R.string.msg_unknown_error);
                                }
                                Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return true;

                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
        return true;
    }

    /**
     * Auxiliary method to retrieve fresh Events from NSService using UID and update the list shown
     *
     * @return the events found locally on the SQLite DB
     */
    private List<Event> getByUid() {
        return NSService.getInstance(getApplicationContext()).getEventsByUser(uid, new NSService.MyCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                Log.d(TAG, "events from UID: found " + events.size() + " events");
                RecyclerView recyclerView = mFragment.getRecyclerView();
                ((MyEventRecyclerViewAdapter) recyclerView.getAdapter()).addEvents(events);
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                Log.w(TAG, "events from UID: failure");
                if (isInFront)
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_network_problem_events_upd), Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Log.w(TAG, "events from UID: " + message);
                String msg = "";
                switch (status) {
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
                if (isInFront)
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }
        });
    }

    /**
     * Auxiliary method to retrieve fresh Events from NSService using Subscription and update the list shown
     *
     * @return the events found locally on the SQLite DB
     */
    private List<Event> getBySub() {
        return NSService.getInstance(getApplicationContext()).getEventsByArea(sub.getMinLat(), sub.getMaxLat(), sub.getMinLon(), sub.getMaxLon(), new NSService.MyCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                Log.d(TAG, "events from sub: found " + events.size() + " events");
                RecyclerView recyclerView = mFragment.getRecyclerView();
                ((MyEventRecyclerViewAdapter) recyclerView.getAdapter()).addEvents(events);
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                Log.w(TAG, "events from sub: failure");
                if (isInFront)
                    Toast.makeText(getContext(), getString(R.string.msg_network_problem_events_upd), Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Log.w(TAG, "events from sub: " + message);
                String msg = "";
                switch (status) {
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
                if (isInFront)
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }
        });
    }

    private List<Event> getByLocation() {
        return NSService.getInstance(getContext()).getEventsByRadius(latitude, longitude, 2000, new NSService.MyCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                Log.d(TAG, "events from location: found " + events.size() + " events");
                RecyclerView recyclerView = mFragment.getRecyclerView();
                ((MyEventRecyclerViewAdapter) recyclerView.getAdapter()).addEvents(events);
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                Log.w(TAG, "events from location: failure");
                if (isInFront)
                    Toast.makeText(getContext(), getString(R.string.msg_network_problem_events_upd), Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Log.w(TAG, "events from location: " + message);
                String msg = "";
                switch (status) {
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
                if (isInFront)
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
            startActivityForResult(intent, RC_PLACE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d(TAG, "findPlace: repairable error " + e.getMessage());
            if (isInFront)
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "findPlace: play service not available error " + e.getMessage());
            if (isInFront)
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CREATE_EVENT && resultCode == RESULT_OK) {
            refreshList();
        }

        if (requestCode == RC_PLACE_AUTOCOMPLETE) {
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
                Log.w(TAG, "PlaceAutocomplete error " + status.getStatusMessage());
                if (isInFront)
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

    @Override
    public void onResume() {
        super.onResume();
        isInFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }
}
