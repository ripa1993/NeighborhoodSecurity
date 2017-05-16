package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventCreateFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

public class EventCreateActivity extends AppCompatActivity implements EventCreateFragment.OnFragmentInteractionListener {

    EventCreateFragment mEventCreateFragment;
    public static final String TAG = "EventCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.event_create_toolbar);
//        setSupportActionBar(myToolbar);

        mEventCreateFragment = (EventCreateFragment) getSupportFragmentManager().findFragmentById(R.id.eventCreateFragment);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_create_event:
                Event e = mEventCreateFragment.getEvent();
                    NSService.getInstance(getApplicationContext()).postEventWithCoordinates(e.getEventType(), e.getDescription(), e.getLatitude(), e.getLongitude(), new NSService.MyCallback<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Log.d(TAG, "event created");
                            Toast.makeText(getApplicationContext(), getString(R.string.msg_success_event_create), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure() {
                            Log.w(TAG, "failed to create event");
                            Toast.makeText(getApplicationContext(), getString(R.string.msg_network_problem_event_create), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onMessageLoad(MyMessage message, int status) {
                            Log.w(TAG, "failed to create event with message: "+message.toString());
                            // TODO: make toast
                        }
                    });
//                }


                return true;

            default:
                return super.onOptionsItemSelected(item);


        }
    }
}
