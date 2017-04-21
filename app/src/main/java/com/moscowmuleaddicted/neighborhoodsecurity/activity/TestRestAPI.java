package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.AuthToken;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventType;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.User;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

public class TestRestAPI extends AppCompatActivity {

    private NSService service;
    private Spinner etSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        EventType.setContext(getApplicationContext());

        setContentView(R.layout.activity_test_rest_api);

        service = NSService.getInstance(getApplicationContext());

        etSpinner = (Spinner) findViewById(R.id.et);
        etSpinner.setAdapter(new ArrayAdapter<EventType>(this, R.layout.support_simple_spinner_dropdown_item, EventType.values()));


    }

    public void getEventsByAreaClicked(View view) {
        float latMin=0, latMax=0, lonMin=0, lonMax=0;
        latMin = NumberUtils.toFloat(((EditText)findViewById(R.id.latMin)).getText().toString(), 0);
        latMax = NumberUtils.toFloat(((EditText)findViewById(R.id.latMax)).getText().toString(), 0);
        lonMin = NumberUtils.toFloat(((EditText)findViewById(R.id.lonMin)).getText().toString(), 0);
        lonMax = NumberUtils.toFloat(((EditText)findViewById(R.id.lonMax)).getText().toString(), 0);

        service.getEventsByArea(latMin, latMax, lonMin, lonMax, new NSService.CallbackEventList() {
            @Override
            public void onEventListLoad(List<Event> events) {
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
        int eventId=0;
        eventId = NumberUtils.toInt(((EditText)findViewById(R.id.eventId)).getText().toString(),0);

        service.getEventById(eventId, new NSService.CallbackEvent() {
            @Override
            public void onEventLoad(Event event) {
                Toast.makeText(getApplicationContext(), event.getDate() + " " + event.getEventType().getLabel(getApplicationContext()) + " " + event.getVotes(), Toast.LENGTH_SHORT).show();
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
        int userId=0;
        userId = NumberUtils.toInt(((EditText)findViewById(R.id.userId)).getText().toString(), 0);

        service.getUserById(userId, new NSService.CallbackUser() {


            @Override
            public void onUserLoad(User user) {
                Toast.makeText(getApplicationContext(), user.getUsername() +" "+user.getEmail(), Toast.LENGTH_SHORT).show();
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

    public void postEventClicked(View view) {
        EventType eventType;
        String description="";
        float lat=0, lon=0;
        eventType = (EventType) etSpinner.getSelectedItem();
        description = ((EditText)findViewById(R.id.desc)).getText().toString();
        lat = NumberUtils.toFloat(((EditText)findViewById(R.id.lat)).getText().toString(), 0);
        lon = NumberUtils.toFloat(((EditText)findViewById(R.id.lon)).getText().toString(), 0);

        service.postEventWithCoordinates(eventType, description, lat, lon, new NSService.CallbackSuccess(){

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

    public void postUserClicked(View view) {
        String username ="", password ="", mail = "";
        username = ((EditText)findViewById(R.id.username)).getText().toString();
        password = ((EditText)findViewById(R.id.password)).getText().toString();
        mail = ((EditText)findViewById(R.id.mail)).getText().toString();

        service.createUserClassic(username, mail, password, new NSService.CallbackSuccess() {

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
        String username ="", password ="";
        username = ((EditText)findViewById(R.id.username)).getText().toString();
        password = ((EditText)findViewById(R.id.password)).getText().toString();

        service.loginClassic(username, password, new NSService.CallbackAuthToken() {
            @Override
            public void onAuthTokenLoad(AuthToken authToken) {
                service.setToken(authToken);
                Toast.makeText(getApplicationContext(), "Token: "+ authToken.getAuthToken(), Toast.LENGTH_SHORT ).show();
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

        service.logout(new NSService.CallbackSuccess() {
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
                service.removeToken();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void delEventClicked(View view){
        int eventId=0;
        eventId = NumberUtils.toInt(((EditText)findViewById(R.id.dEid)).getText().toString(),0);

        service.deleteEvent(eventId, new NSService.CallbackSuccess() {
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

    public void votePlusClicked(View view){
        int eventId=0;
        eventId = NumberUtils.toInt(((EditText)findViewById(R.id.vEid)).getText().toString(),0);
        service.voteEvent(eventId, new NSService.CallbackSuccess() {
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

    public void voteMinusClicked(View view){
        int eventId=0;
        eventId = NumberUtils.toInt(((EditText)findViewById(R.id.vEid)).getText().toString(),0);
        service.unvoteEvent(eventId, new NSService.CallbackSuccess() {
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
}
