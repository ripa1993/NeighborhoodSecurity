package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.AuthenticationFragment;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_LOGGED_IN;

/**
 * Activity containing a fragment that allows the user to choose between the available login methods
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class AuthenticationActivity extends AppCompatActivity implements AuthenticationFragment.OnFragmentInteractionListener {
    /**
     * Logger's TAG
     */
    public static final String TAG = "AuthenticationAct";
    /**
     * The contained fragment
     */
    private AuthenticationFragment mFragment;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        
        mFragment = (AuthenticationFragment) getSupportFragmentManager().findFragmentById(R.id.authentication_fragment);
    }

    @Override
    public void loggedIn() {
        // a user is logged in
        Log.d(TAG, "logged in, exiting AuthenticationActivity");
        Intent data = new Intent();
        data.putExtra(IE_LOGGED_IN, true);
        setResult(RESULT_OK, data);
        finish();
    }
}
