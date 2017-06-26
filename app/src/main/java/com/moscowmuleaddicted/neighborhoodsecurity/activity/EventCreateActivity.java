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
import com.moscowmuleaddicted.neighborhoodsecurity.model.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.controller.NSService;

import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.IE_LATITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.IE_LONGITUDE;

/**
 * Activity that guides the user through the creation of a new event
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class EventCreateActivity extends AppCompatActivity implements EventCreateFragment.OnFragmentInteractionListener {
    /**
     * Logger's TAG
     */
    public static final String TAG = "EventCreateAct";
    /**
     * The contained fragment
     */
    private EventCreateFragment mEventCreateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        mEventCreateFragment = (EventCreateFragment) getSupportFragmentManager().findFragmentById(R.id.eventCreateFragment);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            if (extras.containsKey(IE_LATITUDE) && extras.containsKey(IE_LONGITUDE)){
                double lat, lon;
                lat = extras.getDouble(IE_LATITUDE);
                lon = extras.getDouble(IE_LONGITUDE);
                mEventCreateFragment.setLocation(lat, lon);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // does nothing
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
