package com.moscowmuleaddicted.neighborhoodsecurity.activity;


import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.adapter.MyEventRecyclerViewAdapter;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventListFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Subscription;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;
import com.scalified.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

import static xdroid.core.Global.getContext;

public class EventListActivity extends AppCompatActivity implements EventListFragment.OnListFragmentInteractionListener {

    private static final String TAG = "EventListActivity";
    private EventListFragment mFragment;
    private ActionButton mFab;
    private SwipeRefreshLayout mSwipe;

    private UpdateType updateType = UpdateType.NONE;

    private enum UpdateType{
        UID, SUBSCRIPTION, NONE
    }

    private Subscription sub;
    private String uid;

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
                events = (ArrayList<Event>) extras.getSerializable("event-list");
                mFragment = EventListFragment.newInstance(1, events);
                updateType = UpdateType.NONE;

            } else if (extras.containsKey("UID")){
                // if an uid is provided
                mSwipe.setRefreshing(true);
                mSwipe.setEnabled(true);
                updateType = UpdateType.UID;

                uid = extras.getString("UID");

                events.addAll(getByUid());
                mFragment = EventListFragment.newInstance(1, events);
                Log.d(TAG, "fragment created");

            } else if (extras.containsKey("subscription")){
                // if a subscription is provided
                sub = (Subscription) extras.getSerializable("subscription");

                mSwipe.setRefreshing(true);
                mSwipe.setEnabled(true);
                updateType = UpdateType.SUBSCRIPTION;
                events.addAll(getBySub());
                mFragment = EventListFragment.newInstance(1, events);
                Log.d(TAG, "fragment created");

            }
        } else {
            // if nothing is provided
            mFragment = new EventListFragment();
            Log.d(TAG, "fragment created");

        }

        // initialize the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.event_list_fragment, mFragment);
        fragmentTransaction.commit();

        mFab = (ActionButton) findViewById(R.id.event_create_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), EventCreateActivity.class);
                startActivity(intent);
            }
        });

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                    default:
                        mSwipe.setEnabled(false);
                        mSwipe.setRefreshing(false);
                        return;
                }
            }
        });
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
}
