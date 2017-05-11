package com.moscowmuleaddicted.neighborhoodsecurity.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.adapter.MyEventRecyclerViewAdapter;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventCreateFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventListFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.SubscriptionCreateFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;
import com.scalified.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

import static xdroid.core.Global.getContext;

public class EventListActivity extends AppCompatActivity implements EventListFragment.OnListFragmentInteractionListener {

    private static final String TAG = "EventListActivity";
    private EventListFragment mFragment;
    private ActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Bundle extras = getIntent().getExtras();

        ArrayList<Event> events = new ArrayList<>();
        if(extras != null) {
            if(extras.containsKey("event-list")){
                // if an event list is provided
                events = (ArrayList<Event>) extras.getSerializable("event-list");
                mFragment = EventListFragment.newInstance(1, events);

            } else if (extras.containsKey("UID")){
                // if an uid is provided
                mFragment = new EventListFragment();
                final ProgressDialog progress = new ProgressDialog(this);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                NSService.getInstance(getApplicationContext()).getEventsByUser(extras.getString("UID"), new NSService.MyCallback<List<Event>>() {
                    @Override
                    public void onSuccess(List<Event> events) {
                        Log.d(TAG, "events from UID: found "+events.size()+ " events");
                        RecyclerView recyclerView = mFragment.getRecyclerView();
                        recyclerView.swapAdapter(new MyEventRecyclerViewAdapter(events, EventListActivity.this, getContext()), false);
                        progress.cancel();
                    }

                    @Override
                    public void onFailure() {
                        Log.w(TAG, "events from UID: failure");
                        progress.cancel();
                    }

                    @Override
                    public void onMessageLoad(MyMessage message, int status) {
                        Log.w(TAG, "events from UID: "+message);
                        progress.cancel();
                    }
                });
            }
        } else {
            // if nothing is provided
            mFragment = new EventListFragment();
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


    }

    @Override
    public void onListFragmentInteraction(Event event) {
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

}
