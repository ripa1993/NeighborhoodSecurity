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

    private enum UpdateType{
        NONE, UID;
    }

    private UpdateType updateType = UpdateType.NONE;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_list);

        Bundle extras = getIntent().getExtras();

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_subscription_list);
        mSwipe.setEnabled(false);

        ArrayList<Subscription> mSubscriptions = new ArrayList<>();


        if (extras != null) {
            if (extras.containsKey("subscription-list")) {
                // if subscription list is provided
                mSubscriptions = (ArrayList<Subscription>) extras.getSerializable("subscription-list");
                mFragment = SubscriptionListFragment.newInstance(1, mSubscriptions);

                updateType = UpdateType.NONE;

                Log.d(TAG, "fragment created");

            } else if (extras.containsKey("UID")){
                // if UID is provided
                Log.d(TAG, "contains UID");
                mSwipe.setRefreshing(true);
                mSwipe.setEnabled(true);
                updateType = UpdateType.UID;
                uid = extras.getString("UID");
               mSubscriptions.addAll(getByUid());
                mFragment = SubscriptionListFragment.newInstance(1, mSubscriptions);
                Log.d(TAG, "fragment created");
            }
        } else {
            // nothing to show....
            Log.d(TAG, "no subscriptions to show");
            mFragment = new SubscriptionListFragment();
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

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (updateType){
                    case NONE:
                        // should not be activated
                        mSwipe.setEnabled(false);
                        mSwipe.setRefreshing(false);
                        return;
                    case UID:
                        getByUid();
                        return;
                    default:
                        mSwipe.setEnabled(false);
                        mSwipe.setRefreshing(false);
                        return;
                }
            }
        });


    }

    @Override
    public void onListFragmentInteraction(Subscription item) {
        Intent intent = new Intent(SubscriptionListActivity.this, EventListActivity.class);
        intent.putExtra("subscription", item);
        startActivity(intent);
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

    private List<Subscription> getByUid(){
        return NSService.getInstance(getApplicationContext()).getSubscriptionsByUser(uid, new NSService.MyCallback<List<Subscription>>() {
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
                Toast.makeText(getApplicationContext(), getString(R.string.msg_network_problem_subscriptions_upd), Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Log.w(TAG, "subscriptions from UID: "+message);
                String msg = "";
                switch(status){
                    case 404:
                        msg = getString(R.string.msg_404_not_found_user_subs);
                        break;
                    case 500:
                        msg = getString(R.string.msg_500_internal_server_error_subs);
                        break;
                    default:
                        msg = getString(R.string.msg_unknown_error);
                        break;
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }
        });
    }

}
