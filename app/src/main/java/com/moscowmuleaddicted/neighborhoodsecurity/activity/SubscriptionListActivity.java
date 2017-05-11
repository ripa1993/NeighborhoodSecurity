package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.internal.view.SupportSubMenu;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.adapter.MySubscriptionRecyclerViewAdapter;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventListFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.SubscriptionListFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Subscription;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;
import com.scalified.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionListActivity extends AppCompatActivity implements SubscriptionListFragment.OnListFragmentInteractionListener {

    private static final String TAG = "SubscriptionListAct";
    private ActionButton mFab;
    private SubscriptionListFragment mFragment;
    private SwipeRefreshLayout mSwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_list);

        Bundle extras = getIntent().getExtras();

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_subscription_list);
        mSwipe.setEnabled(false);

        ArrayList<Subscription> mSubscriptions = new ArrayList<>();

        mFragment = new SubscriptionListFragment();

        if (extras != null) {
            if (extras.containsKey("subscription-list")) {
                // if subscription list is provided
                mSubscriptions = (ArrayList<Subscription>) extras.getSerializable("subscription-list");
                mFragment.showData(mSubscriptions);

            } else if (extras.containsKey("UID")){
                // if UID is provided
                mFragment = new SubscriptionListFragment();
                mSwipe.setRefreshing(true);
                NSService.getInstance(getApplicationContext()).getSubscriptionsByUser(extras.getString("UID"), new NSService.MyCallback<List<Subscription>>() {
                    @Override
                    public void onSuccess(List<Subscription> subscriptions) {
                        Log.d(TAG, "subscriptions from UID: found "+subscriptions.size()+" subscriptions");
                        RecyclerView recyclerView = mFragment.getRecyclerView();
                        ((MySubscriptionRecyclerViewAdapter) recyclerView.getAdapter()).addSubscriptions(subscriptions);
                        mSwipe.setRefreshing(false);
                    }

                    @Override
                    public void onFailure() {
                        Log.w(TAG, "subscriptions from UID: failure");
                        mSwipe.setRefreshing(false);
                    }

                    @Override
                    public void onMessageLoad(MyMessage message, int status) {
                        Log.w(TAG, "subscriptions from UID: "+message);
                        mSwipe.setRefreshing(false);
                    }
                });
            }
        } else {
            // nothing to show....
            Log.d(TAG, "no subscriptions to show");
        }

        // initialize fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.subscription_list_fragment, mFragment);
        fragmentTransaction.commit();



        mFab = (ActionButton) findViewById(R.id.subscription_create_fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SubscriptionCreateActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onListFragmentInteraction(Subscription item) {
        Toast.makeText(getApplicationContext(), String.valueOf(item.getId()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void scrollingUp() {
        if (!mFab.isHidden()) {
            mFab.hide();
        }
    }

    @Override
    public void scrollingDown() {
        if (mFab.isHidden()) {
            mFab.show();
        }
    }

}
