package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.app.ActionBar;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.event_create_toolbar);
//        setSupportActionBar(myToolbar);

        mEventCreateFragment = (EventCreateFragment) getFragmentManager().findFragmentById(R.id.eventCreateFragment);
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
                if(mEventCreateFragment.eventUsesAddress()){
                    NSService.getInstance(getApplicationContext()).postEventWithAddress(e.getEventType(), e.getDescription(), e.getCountry(), e.getCity(), e.getStreet(), new NSService.MyCallback<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onMessageLoad(MyMessage message, int status) {
                            Toast.makeText(getApplicationContext(), "("+status+") ["+message.getArgument()+"] "+message.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    NSService.getInstance(getApplicationContext()).postEventWithCoordinates(e.getEventType(), e.getDescription(), e.getLatitude(), e.getLongitude(), new NSService.MyCallback<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onMessageLoad(MyMessage message, int status) {
                            Toast.makeText(getApplicationContext(), "("+status+") ["+message.getArgument()+"] "+message.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                return true;

            default:
                return super.onOptionsItemSelected(item);


        }
    }
}
