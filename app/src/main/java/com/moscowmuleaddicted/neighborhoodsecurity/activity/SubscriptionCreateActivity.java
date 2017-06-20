package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.SubscriptionCreateFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_LATITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_LONGITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.MAX_LATITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.MAX_LONGITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.MAX_RADIUS;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.MIN_LATITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.MIN_LONGITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.MIN_RADIUS;

public class SubscriptionCreateActivity extends AppCompatActivity implements SubscriptionCreateFragment.OnFragmentInteractionListener {

    private SubscriptionCreateFragment mFragment;
    public static final String TAG = "SubsCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_create);

        mFragment = (SubscriptionCreateFragment) getSupportFragmentManager().findFragmentById(R.id.subscription_create_fragment);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            if (extras.containsKey(IE_LATITUDE) && extras.containsKey(IE_LONGITUDE)){
                double lat, lon;
                lat = extras.getDouble(IE_LATITUDE);
                lon = extras.getDouble(IE_LONGITUDE);
                mFragment.setLocation(lat, lon);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subscription_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_subscription:
                Double latitude, longitude;
                Integer radius;
                latitude = mFragment.getLatitude();
                longitude = mFragment.getLongitude();
                radius = mFragment.getRadius();

                Log.d(TAG, "creating subscription @ ("+latitude+", "+longitude+") with radius "+radius);

                boolean notNull = (latitude!=null && longitude != null && radius!=null);
                if(!notNull){
                    mFragment.showErrors();
                    return false;
                }

                // check valid values
                if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE || longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE || radius < MIN_RADIUS || radius > MAX_RADIUS) {
                    mFragment.showErrors();
                    return false;
                }

                final ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.progress_subscription_title), getString(R.string.progress_subscription_message), true, false);
                NSService.getInstance(getApplicationContext()).postSubscriptionCenterAndRadius(
                        latitude,
                        longitude,
                        radius,
                        new NSService.MyCallback<MyMessage>() {
                            @Override
                            public void onSuccess(MyMessage myMessage) {
                                progressDialog.dismiss();
                                Log.d(TAG, "subscription created");
                                Toast.makeText(getApplicationContext(), getString(R.string.msg_success_subscription_create), Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();

                            }

                            @Override
                            public void onFailure() {
                                progressDialog.dismiss();
                                Log.w(TAG, "subscription create failure");
                                Toast.makeText(getApplicationContext(), getString(R.string.msg_network_problem_subscription_create), Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onMessageLoad(MyMessage message, int status) {
                                progressDialog.dismiss();
                                Log.w(TAG, "subscription create failure with msg: "+message.toString());
                                String msg = "";
                                switch(status){
                                    case 400:
                                        msg = getString(R.string.msg_400_bad_request_subs);
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


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
