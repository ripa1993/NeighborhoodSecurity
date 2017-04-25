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

import com.moscowmuleaddicted.neighborhoodsecurity.EventDetailListFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.details.Details;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventType;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

import java.util.Date;

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
        toolbar.setTitle("New "+event.getEventType());
        setSupportActionBar(toolbar);



        // initialize the fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        EventDetailListFragment edl = EventDetailListFragment.newInstance(1, event);
        fragmentTransaction.add(R.id.eventDetailFragment, edl);
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
