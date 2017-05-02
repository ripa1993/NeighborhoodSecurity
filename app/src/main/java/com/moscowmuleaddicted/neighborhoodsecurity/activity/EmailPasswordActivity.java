package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EmailPasswordFragment;

public class EmailPasswordActivity extends AppCompatActivity implements EmailPasswordFragment.OnFragmentInteractionListener {
    private final static int EMAIL_LOGIN = 2;
    EmailPasswordFragment mFragment;

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
        intent.putExtra("LOGGED_IN", false);
//        intent.putExtra("REGISTERED", false);
//        intent.putExtra("FORGOT_MAIL", false);
        setResult(EMAIL_LOGIN, intent);
        finish();
    }

    @Override
    public void loggedIn(){
        Intent intent = new Intent();
        intent.putExtra("LOGGED_IN", true);
//        intent.putExtra("REGISTERED", false);
//        intent.putExtra("FORGOT_MAIL", false);
        setResult(EMAIL_LOGIN, intent);
        finish();
    }
}
