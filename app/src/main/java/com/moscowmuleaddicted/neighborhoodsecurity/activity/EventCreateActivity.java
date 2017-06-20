package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventCreateFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

public class EventCreateActivity extends AppCompatActivity implements EventCreateFragment.OnFragmentInteractionListener {

    EventCreateFragment mEventCreateFragment;
    public static final String TAG = "EventCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        mEventCreateFragment = (EventCreateFragment) getSupportFragmentManager().findFragmentById(R.id.eventCreateFragment);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            if (extras.containsKey("lat") && extras.containsKey("lon")){
                double lat, lon;
                lat = extras.getDouble("lat");
                lon = extras.getDouble("lon");
                mEventCreateFragment.setLocation(lat, lon);
            }
        }
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
        switch (item.getItemId()) {
            case R.id.action_create_event:
                Event e = mEventCreateFragment.getEvent();
                if (e.getDescription().length() > 0 && !e.getLatitude().equals(Double.NEGATIVE_INFINITY) && !e.getLongitude().equals(Double.NEGATIVE_INFINITY)) {
                    final ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.progress_event_title), getString(R.string.progress_event_message), true, false);
                    NSService.getInstance(getApplicationContext()).postEventWithCoordinates(e.getEventType(), e.getDescription(), e.getLatitude(), e.getLongitude(), new NSService.MyCallback<String>() {
                        @Override
                        public void onSuccess(String s) {
                            progressDialog.dismiss();
                            Log.d(TAG, "event created");
                            Toast.makeText(getApplicationContext(), getString(R.string.msg_success_event_create), Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onFailure() {
                            progressDialog.dismiss();
                            Log.w(TAG, "failed to create event");
                            Toast.makeText(getApplicationContext(), getString(R.string.msg_network_problem_event_create), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onMessageLoad(MyMessage message, int status) {
                            progressDialog.dismiss();
                            Log.w(TAG, "failed to create event with message: " + message.toString());
                            String msg = "";
                            switch (status) {
                                case 400:
                                    msg = getString(R.string.msg_400_bad_request_subs);
                                    break;
                                case 401:
                                    msg = getString(R.string.msg_401_unauthorized_subs);
                                    break;
                                case 500:
                                    msg = getString(R.string.msg_500_internal_server_error_subs);
                                    break;
                                default:
                                    msg = getString(R.string.msg_unknown_error);
                                    break;
                            }
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    mEventCreateFragment.showErrors();
                }


                return true;

            default:
                return super.onOptionsItemSelected(item);


        }
    }
}
