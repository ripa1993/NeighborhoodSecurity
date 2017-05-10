package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.AuthenticationFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EmailPasswordFragment;

/**
 * Activity containing a fragment that allows the user to choose between the available login methods
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class AuthenticationActivity extends AppCompatActivity implements AuthenticationFragment.OnFragmentInteractionListener {
    /**
     * The contained fragment
     */
    AuthenticationFragment mFragment;

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
        Toast.makeText(getApplicationContext(), "user logged in", Toast.LENGTH_SHORT).show();

        Intent data = new Intent();
        data.putExtra("LOGGED_IN", true);
        setResult(RESULT_OK, data);
        finish();
    }
}
