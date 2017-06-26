package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EmailPasswordFragment;

import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.RC_EMAIL_LOGIN;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.IE_LOGGED_IN;

/**
 * Activity that contains a fragment to allow user to register, signin or reset password using
 * the email authentication system provided by Firebase
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class EmailPasswordActivity extends AppCompatActivity implements EmailPasswordFragment.OnFragmentInteractionListener {
    /**
     * Logger's TAG
     */
    public static final String TAG = "EmailPassAct";
    /**
     * The contained fragment
     */
    private EmailPasswordFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        mFragment = (EmailPasswordFragment) getSupportFragmentManager().findFragmentById(R.id.ep_fragment);
    }

    @Override
    public void onBackPressed() {
        mFragment.pressBackButton();
    }

    @Override
    public void closeFragment() {
        Intent intent = new Intent();
        intent.putExtra(IE_LOGGED_IN, false);
        setResult(RC_EMAIL_LOGIN, intent);
        finish();
    }

    @Override
    public void loggedInWithEmail(){
        Intent intent = new Intent();
        intent.putExtra(IE_LOGGED_IN, true);
        setResult(RC_EMAIL_LOGIN, intent);
        finish();
    }
}
