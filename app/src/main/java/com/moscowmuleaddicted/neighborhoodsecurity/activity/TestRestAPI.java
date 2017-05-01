package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventType;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.User;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

import static com.google.firebase.auth.FirebaseAuth.*;

public class TestRestAPI extends AppCompatActivity {

    private NSService service;
    private Spinner etSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_rest_api);

        service = NSService.getInstance(getApplicationContext());

        etSpinner = (Spinner) findViewById(R.id.et);
        etSpinner.setAdapter(new ArrayAdapter<EventType>(this, R.layout.support_simple_spinner_dropdown_item, EventType.values()));


    }

    public void getEventsByAreaClicked(View view) {
        float latMin = 0, latMax = 0, lonMin = 0, lonMax = 0;
        latMin = NumberUtils.toFloat(((EditText) findViewById(R.id.latMin)).getText().toString(), 0);
        latMax = NumberUtils.toFloat(((EditText) findViewById(R.id.latMax)).getText().toString(), 0);
        lonMin = NumberUtils.toFloat(((EditText) findViewById(R.id.lonMin)).getText().toString(), 0);
        lonMax = NumberUtils.toFloat(((EditText) findViewById(R.id.lonMax)).getText().toString(), 0);

        service.getEventsByArea(latMin, latMax, lonMin, lonMax, new NSService.MyCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                Toast.makeText(getApplicationContext(), "Received " + events.size() + " events", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), status + " " + message.getArgument() + " " + message.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getEventClicked(View view) {
        int eventId = 0;
        eventId = NumberUtils.toInt(((EditText) findViewById(R.id.eventId)).getText().toString(), 0);

        service.getEventById(eventId, new NSService.MyCallback<Event>() {
            @Override
            public void onSuccess(Event event) {
                Intent i = new Intent(TestRestAPI.this, EventDetailActivity.class);
                i.putExtra("event", event);
                startActivity(i);

            }


            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), status + " " + message.getArgument() + " " + message.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getUserClicked(View view) {
        String userId = "";
        userId = ((EditText) findViewById(R.id.userId)).getText().toString();

        service.getUserById(userId, new NSService.MyCallback<User>() {


            @Override
            public void onSuccess(User user) {
                Toast.makeText(getApplicationContext(), user.getName() + " " + user.getEmail(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), status + " " + message.getArgument() + " " + message.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getThisUserClicked(View v) {
        FirebaseUser user = getInstance().getCurrentUser();
        if (user != null) {
            Toast.makeText(getApplicationContext(), "Provider: " + user.getProviderId() +
                    "\nDisplay name: " + user.getDisplayName() +
                    "\nUID: " + user.getUid() +
                    "\nEmail: " + user.getEmail(), Toast.LENGTH_LONG).show();
            ((EditText) findViewById(R.id.userId)).setText(user.getUid(), TextView.BufferType.EDITABLE);
        } else {
            Toast.makeText(getApplicationContext(), "Not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void postEventClicked(View view) {
        EventType eventType;
        String description = "";
        float lat = 0, lon = 0;
        eventType = (EventType) etSpinner.getSelectedItem();
        description = ((EditText) findViewById(R.id.desc)).getText().toString();
        lat = NumberUtils.toFloat(((EditText) findViewById(R.id.lat)).getText().toString(), 0);
        lon = NumberUtils.toFloat(((EditText) findViewById(R.id.lon)).getText().toString(), 0);

        service.postEventWithCoordinates(eventType, description, lat, lon, new NSService.MyCallback<String>() {

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), status + " " + message.getArgument() + " " + message.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void loginClassicClicked(View view) {
        String email = "", password = "";
        email = ((EditText) findViewById(R.id.mail)).getText().toString();
        password = ((EditText) findViewById(R.id.password)).getText().toString();

        service.signInWithEmail(email, password, new NSService.MyCallback<String>() {
            @Override
            public void onSuccess(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), status + " " + message.getArgument() + " " + message.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void signupClassicClicked(View view){
        String email = "", password = "", name="";
        email = ((EditText) findViewById(R.id.mail)).getText().toString();
        password = ((EditText) findViewById(R.id.password)).getText().toString();
        name = ((EditText) findViewById(R.id.username)).getText().toString();

        service.signUpWithEmail(name, email, password, new NSService.MyCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), status + " " + message.getArgument() + " " + message.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void logoutClicked(View view) {

        service.logout(new NSService.MyCallback<String>() {
            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), status + " " + message.getArgument() + " " + message.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void delEventClicked(View view) {
        int eventId = 0;
        eventId = NumberUtils.toInt(((EditText) findViewById(R.id.dEid)).getText().toString(), 0);

        service.deleteEvent(eventId, new NSService.MyCallback<String>() {
            @Override
            public void onSuccess(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), status + " " + message.getArgument() + " " + message.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void votePlusClicked(View view) {
        int eventId = 0;
        eventId = NumberUtils.toInt(((EditText) findViewById(R.id.vEid)).getText().toString(), 0);
        service.voteEvent(eventId, new NSService.MyCallback<String>() {
            @Override
            public void onSuccess(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), status + " " + message.getArgument() + " " + message.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void voteMinusClicked(View view) {
        int eventId = 0;
        eventId = NumberUtils.toInt(((EditText) findViewById(R.id.vEid)).getText().toString(), 0);
        service.unvoteEvent(eventId, new NSService.MyCallback<String>() {
            @Override
            public void onSuccess(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), status + " " + message.getArgument() + " " + message.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateFcmClicked(View view){
        final String token = FirebaseInstanceId.getInstance().getToken();
        service.updateFcm(token, new NSService.MyCallback<MyMessage>() {
            @Override
            public void onSuccess(MyMessage myMessage) {
                Toast.makeText(getApplicationContext(), "fcm updated to "+token, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "fcm update failure", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getApplicationContext(), status + " " + message.getArgument()+ " " + message.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void postSubscriptionClicked(View view){
        int radius = 0;
        float lat, lon;
        radius = NumberUtils.toInt(((EditText) findViewById(R.id.radS)).getText().toString(), 1);
        lat = NumberUtils.toFloat(((EditText) findViewById(R.id.latS)).getText().toString(), 45);
        lon = NumberUtils.toFloat(((EditText) findViewById(R.id.lonS)).getText().toString(), 9);
        service.postSubscriptionCenterAndRadius(lat, lon, radius, new NSService.MyCallback<MyMessage>() {
            @Override
            public void onSuccess(MyMessage myMessage) {
                Toast.makeText(getApplicationContext(), "subscribed", Toast.LENGTH_SHORT).show();
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

    public void goToMap(View view){

        // Start MapsActivity
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);

        // Close this Activity
        // finish();
    }
}
