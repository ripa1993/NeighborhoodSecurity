package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.CurvedChildLayoutManager;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.adapter.MainRecyclerViewAdapter;
import com.moscowmuleaddicted.neighborhoodsecurity.extra.ItemType;
import com.moscowmuleaddicted.neighborhoodsecurity.extra.MainItem;
import com.moscowmuleaddicted.neighborhoodsecurity.extra.MyCurvedChildLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.moscowmuleaddicted.neighborhoodsecurity.extra.Constants.CAPABILITY_PHONE_APP;

public class MainActivity extends WearableActivity implements MainRecyclerViewAdapter.OnItemInteractionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, CapabilityApi.CapabilityListener {

    public static final String TAG = "MainActivity";

    private WearableRecyclerView mRecyclerView;
    private MyCurvedChildLayoutManager mChildLayoutManager;
    private List<MainItem> mActions;
    private Node mAndroidPhoneNodeWithApp;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActions = new ArrayList<>();
        mActions.add(new MainItem(getDrawable(R.mipmap.ic_launcher), getString(R.string.new_event), ItemType.NEW_EVENT));
        mActions.add(new MainItem(getDrawable(R.mipmap.ic_launcher), getString(R.string.nearby), ItemType.NEARBY));
        mActions.add(new MainItem(getDrawable(R.mipmap.ic_launcher), getString(R.string.map), ItemType.MAP));
        mActions.add(new MainItem(getDrawable(R.mipmap.ic_launcher), "DEMO", null));
        mActions.add(new MainItem(getDrawable(R.mipmap.ic_launcher), "DEMO", null));
        mActions.add(new MainItem(getDrawable(R.mipmap.ic_launcher), "DEMO", null));
        mActions.add(new MainItem(getDrawable(R.mipmap.ic_launcher), "DEMO", null));


        mRecyclerView = (WearableRecyclerView) findViewById(R.id.recycler_view_main);
        mChildLayoutManager = new MyCurvedChildLayoutManager(getApplicationContext());

        mRecyclerView.setCenterEdgeItems(true);
        mRecyclerView.setLayoutManager(mChildLayoutManager);
        mRecyclerView.setCircularScrollingGestureEnabled(true);
        mRecyclerView.setAdapter(new MainRecyclerViewAdapter(mActions, this));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }

    @Override
    public void onInteraction(MainItem item) {
        switch(item.getType()){
            case NEW_EVENT:
                break;
            case MAP:
                Intent mapsIntent = new Intent(this, MapsActivity.class);
                startActivity(mapsIntent);
                break;
            case NEARBY:
                break;
            default:
                break;
        }

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();

        if ((mGoogleApiClient != null) && mGoogleApiClient.isConnected()) {
            Wearable.CapabilityApi.removeCapabilityListener(
                    mGoogleApiClient,
                    this,
                    CAPABILITY_PHONE_APP);

            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected()");

        // Set up listeners for capability changes (install/uninstall of remote app).
        Wearable.CapabilityApi.addCapabilityListener(
                mGoogleApiClient,
                this,
                CAPABILITY_PHONE_APP);

        checkIfPhoneHasApp();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended(): connection to location client suspended: " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed(): " + connectionResult);
    }

    /*
     * Updates UI when capabilities change (install/uninstall phone app).
     */
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        Log.d(TAG, "onCapabilityChanged(): " + capabilityInfo);

        mAndroidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.getNodes());
        verifyNodeAndUpdateUI();
    }

    private void checkIfPhoneHasApp() {
        Log.d(TAG, "checkIfPhoneHasApp()");

        PendingResult<CapabilityApi.GetCapabilityResult> pendingResult =
                Wearable.CapabilityApi.getCapability(
                        mGoogleApiClient,
                        CAPABILITY_PHONE_APP,
                        CapabilityApi.FILTER_ALL);

        pendingResult.setResultCallback(new ResultCallback<CapabilityApi.GetCapabilityResult>() {

            @Override
            public void onResult(@NonNull CapabilityApi.GetCapabilityResult getCapabilityResult) {
                Log.d(TAG, "onResult(): " + getCapabilityResult);

                if (getCapabilityResult.getStatus().isSuccess()) {
                    CapabilityInfo capabilityInfo = getCapabilityResult.getCapability();
                    mAndroidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.getNodes());
                    verifyNodeAndUpdateUI();

                } else {
                    Log.d(TAG, "Failed CapabilityApi: " + getCapabilityResult.getStatus());
                }
            }
        });
    }

    private void verifyNodeAndUpdateUI() {

        if (mAndroidPhoneNodeWithApp == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.please_install_app), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /*
 * There should only ever be one phone in a node set (much less w/ the correct capability), so
 * I am just grabbing the first one (which should be the only one).
 */
    private Node pickBestNodeId(Set<Node> nodes) {
        Log.d(TAG, "pickBestNodeId(): " + nodes);

        Node bestNodeId = null;
        // Find a nearby node/phone or pick one arbitrarily. Realistically, there is only one phone.
        for (Node node : nodes) {
            bestNodeId = node;
        }
        return bestNodeId;
    }

}
