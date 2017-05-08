package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.SubscriptionCreateFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

public class SubscriptionCreateActivity extends AppCompatActivity implements SubscriptionCreateFragment.OnFragmentInteractionListener {

    private SubscriptionCreateFragment mFragment;

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
                                Toast.makeText(getApplicationContext(), myMessage.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onMessageLoad(MyMessage message, int status) {
                                Toast.makeText(getApplicationContext(), "(" + status + ") [" + message.getArgument() + "] " + message.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
