package com.moscowmuleaddicted.neighborhoodsecurity.activity;

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

public class SubscriptionCreateActivity extends AppCompatActivity implements SubscriptionCreateFragment.OnFragmentInteractionListener {

    private SubscriptionCreateFragment mFragment;
    public static final String TAG = "SubsCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_create);

        mFragment = (SubscriptionCreateFragment) getSupportFragmentManager().findFragmentById(R.id.subscription_create_fragment);
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

                NSService.getInstance(getApplicationContext()).postSubscriptionCenterAndRadius(
                        mFragment.getLatitude(),
                        mFragment.getLongitude(),
                        mFragment.getRadius(),
                        new NSService.MyCallback<MyMessage>() {
                            @Override
                            public void onSuccess(MyMessage myMessage) {
                                Log.d(TAG, "subscription created");
                                Toast.makeText(getApplicationContext(), getString(R.string.msg_success_subscription_create), Toast.LENGTH_SHORT).show();
                                finish();

                            }

                            @Override
                            public void onFailure() {
                                Log.w(TAG, "subscription create failure");
                                Toast.makeText(getApplicationContext(), getString(R.string.msg_network_problem_subscription_create), Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onMessageLoad(MyMessage message, int status) {
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
