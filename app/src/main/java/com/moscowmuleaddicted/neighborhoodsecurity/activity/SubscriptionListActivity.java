package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.adapter.SubscriptionRecyclerViewAdapter;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.SubscriptionListFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Subscription;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;
import com.scalified.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_SUBSCRIPTION_LIST;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_UID;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.RC_CREATE_SUBSCRIPTION;

/**
 * Activity that shows a list of Subscription items
 * @author Simone Ripamonti
 */
public class SubscriptionListActivity extends AppCompatActivity implements SubscriptionListFragment.OnListFragmentInteractionListener {
    /**
     * Logger's TAG
     */
    private static final String TAG = "SubscriptionListAct";
    /**
     * FirebaseAuth instance
     */
    private FirebaseAuth mAuth;
    /**
     * Enumeration used to specify what is the source of Subscriptions
     */
    private enum UpdateType {
        NONE, UID;
    }
    /**
     * Floating action button
     */
    private ActionButton mFabNewSubscription;
    /**
     * Contained fragment
     */
    private SubscriptionListFragment mFragment;
    /**
     * Swipe Refresh Layout used to refresh fragment content
     */
    private SwipeRefreshLayout mSwipe;
    /**
     * Source of the Subscription items
     */
    private UpdateType mUpdateType = UpdateType.NONE;
    /**
     * Auxiliary data for the source UID
     */
    private String uid;
    /**
     * Tell if the activity is in front, to prevent showing toasts in wrong activity
     */
    private boolean isInFront = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_list);

        Bundle extras = getIntent().getExtras();
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_subscription_list);
        mSwipe.setEnabled(false);
        ArrayList<Subscription> mSubscriptions = new ArrayList<>();


        if (extras != null) {
            if (extras.containsKey(IE_SUBSCRIPTION_LIST)) {
                // if subscription list is provided
                Log.d(TAG, "creating fragment using provided subscription list");
                mSubscriptions = (ArrayList<Subscription>) extras.getSerializable(IE_SUBSCRIPTION_LIST);
                mFragment = SubscriptionListFragment.newInstance(1, mSubscriptions);
                mUpdateType = UpdateType.NONE;
                Log.d(TAG, "fragment created");
            } else if (extras.containsKey(IE_UID)) {
                // if UID is provided
                Log.d(TAG, "creating fragment using provided UID");
                mSwipe.setRefreshing(true);
                mSwipe.setEnabled(true);
                mUpdateType = UpdateType.UID;
                uid = extras.getString(IE_UID);
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
        Log.d(TAG, "initializing fragment using support fragment manager");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.subscription_list_fragment, mFragment);
        fragmentTransaction.commit();

        mAuth = FirebaseAuth.getInstance();
        mFabNewSubscription = (ActionButton) findViewById(R.id.subscription_create_fab);
        mFabNewSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "FAB clicked");
                if(mAuth.getCurrentUser()!=null) {
                    Intent intent = new Intent(getApplicationContext(), SubscriptionCreateActivity.class);
                    startActivityForResult(intent, RC_CREATE_SUBSCRIPTION);
                } else {
                    Log.d(TAG, "user is not logged in, this is required when accessing subscription create!");
                    Toast.makeText(getApplicationContext(), getString(R.string.login_required_toast), Toast.LENGTH_LONG).show();
                }
            }
        });

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "swiped to refresh");
                refreshList();
            }
        });


    }

    @Override
    public void onListItemClick(Subscription item) {
        Intent intent = new Intent(SubscriptionListActivity.this, EventListActivity.class);
        intent.putExtra("subscription", item);
        startActivity(intent);
    }

    @Override
    public void scrollingUp() {
        if (!mFabNewSubscription.isHidden()) {
            mFabNewSubscription.hide();
        }
    }

    @Override
    public void scrollingDown() {
        if (mFabNewSubscription.isHidden()) {
            mFabNewSubscription.show();
        }
    }

    @Override
    public boolean onListItemLongClick(final Subscription mItem, View v) {
        // show  menu

        PopupMenu popupMenu = new PopupMenu(getApplication(), v, Gravity.CENTER);
        popupMenu.inflate(R.menu.menu_delete_subscription);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_delete_subscription:
                        NSService.getInstance(getApplicationContext()).deleteSubscriptionById(mItem.getId(), new NSService.MyCallback<MyMessage>() {
                            @Override
                            public void onSuccess(MyMessage s) {
                                mFragment.removeSubscription(mItem);
                                Toast.makeText(getApplicationContext(), getString(R.string.msg_subscription_deleted), Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure() {
                                String toastMessage = getString(R.string.msg_unknown_error);
                                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onMessageLoad(MyMessage message, int status) {
                                String toastMessage = "";
                                switch (status){
                                    case 400:
                                        toastMessage = getString(R.string.msg_400_bad_request_delete_subscription);
                                        break;
                                    case 401:
                                        toastMessage = getString(R.string.msg_401_unauthorized_delete_subscription);
                                        break;
                                    case 404:
                                        toastMessage = getString(R.string.msg_404_not_found_delete_subscription);
                                        mFragment.removeSubscription(mItem);
                                        break;
                                    case 500:
                                        toastMessage = getString(R.string.msg_500_internal_server_error_delete_subscription);
                                        break;
                                    default:
                                        toastMessage = getString(R.string.msg_unknown_error);
                                }
                                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return true;

                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CREATE_SUBSCRIPTION && resultCode == RESULT_OK) {
            refreshList();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        isInFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }

    /**
     * How to refresh the RecyclerView after a swipe by the user. We discard locally found
     * subscriptions since we are only interested in new events that might be available on the
     * remote server
     */
    private void refreshList() {
        switch (mUpdateType) {
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

    /**
     * Auxiliary method to perform a call to NSService to obtain a fresh list of subscriptions
     * @return subscriptions retrieved on the local sqlite db
     */
    private List<Subscription> getByUid() {
        return NSService.getInstance(getApplicationContext()).getSubscriptionsByUser(uid, new NSService.MyCallback<List<Subscription>>() {
            @Override
            public void onSuccess(List<Subscription> subscriptions) {
                Log.d(TAG, "subscriptions from UID: found " + subscriptions.size() + " subscriptions");
                RecyclerView recyclerView = mFragment.getRecyclerView();
                ((SubscriptionRecyclerViewAdapter) recyclerView.getAdapter()).addSubscriptions(subscriptions);
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                Log.w(TAG, "subscriptions from UID: failure");
                if(isInFront)
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_network_problem_subscriptions_upd), Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Log.w(TAG, "subscriptions from UID: " + message);
                String msg = "";
                switch (status) {
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
                if(isInFront)
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                mSwipe.setRefreshing(false);
            }
        });
    }
}
