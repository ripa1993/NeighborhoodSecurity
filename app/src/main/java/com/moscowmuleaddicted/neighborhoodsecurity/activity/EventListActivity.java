package com.moscowmuleaddicted.neighborhoodsecurity.activity;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventListFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.SubscriptionCreateFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity implements EventListFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Bundle extras = getIntent().getExtras();

        boolean sample = true;
        ArrayList<Event> events = new ArrayList<>();
        if(extras != null) {
            if(extras.containsKey("event-list")){
                events = (ArrayList<Event>) extras.getSerializable("event-list");
                sample = false;
            }
        }
        if(sample){
            generateSampleList();
        } else {
            EventListFragment mFragment = EventListFragment.newInstance(1, events);

            // initialize the fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.event_list_fragment, mFragment);
            fragmentTransaction.commit();
        }



    }

    @Override
    public void onListFragmentInteraction(Event event) {
//        Toast.makeText(getApplicationContext(), "clicked event "+event.getId(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);

    }


    private void generateSampleList(){
        NSService.getInstance(getApplicationContext()).getEventsByArea(44d, 46d, 8d, 10d, new NSService.MyCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> receivedEvents) {
                ArrayList<Event> events = new ArrayList<>(receivedEvents);
                EventListFragment mFragment = EventListFragment.newInstance(1, events);

                // initialize the fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.event_list_fragment, mFragment);
                fragmentTransaction.commit();

            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
