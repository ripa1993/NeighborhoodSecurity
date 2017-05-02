package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.AuthenticationFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EmailPasswordFragment;

public class AuthenticationActivity extends AppCompatActivity implements AuthenticationFragment.OnFragmentInteractionListener {
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
}
