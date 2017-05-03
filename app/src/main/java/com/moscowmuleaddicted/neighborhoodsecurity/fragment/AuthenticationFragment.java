package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.EmailPasswordActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

/**
 * Authentication fragment, provides Facebook, Google and Email authentication
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class AuthenticationFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    /**
     * Tag used in logger
     */
    private static final String TAG = "AuthenticationFragment";
    /**
     * Request code for Google Signin
     */
    private static final int RC_SIGN_IN = 1;
    /**
     * Request code for email signin
     */
    private static final int EMAIL_LOGIN = 2;
    /**
     * The Google API client
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Google Signin button
     */
    private SignInButton googleLoginButton;
    /**
     * Facebook Login button
     */
    private LoginButton facebookLoginButton;
    /**
     * Facebook callback manager
     */
    private CallbackManager callbackManager;
    /**
     * Fragment listener
     */
    private OnFragmentInteractionListener mListener;

    /**
     * Empty constructor
     */
    public AuthenticationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // google signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);

        // Google sign in button setup
        googleLoginButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn(v);
            }
        });

        // Facebook login button setup
        facebookLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.setFragment(this);

        callbackManager = CallbackManager.Factory.create();


        // Callback registration
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(getContext(), "failure", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "facebook:onError", exception);
                Toast.makeText(getContext(), "failure", Toast.LENGTH_SHORT).show();

            }
        });


        // Setup email button
        Button email = (Button) view.findViewById(R.id.auth_email_btn);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailPasswordLogin = new Intent(getActivity(), EmailPasswordActivity.class);
                startActivityForResult(emailPasswordLogin, EMAIL_LOGIN);

            }
        });

        return view;
    }

    /**
     * Obtain the Facebook login button
     * @return Facebook LoginButton instance
     */
    public LoginButton getLoginButtonFB() {
        return facebookLoginButton;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Perform Google signin
     * @param view
     */
    public void googleSignIn(View view) {
        Log.d(TAG, "googleSignIn: button clicked");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        // Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Email
        if (requestCode == EMAIL_LOGIN) {
            // handle exit status
            if (data.getBooleanExtra("LOGGED_IN", false)) {
                // logged in
                mListener.loggedIn();
            }
        }

    }

    /**
     * Handle the result of Google Signin
     * @param result GoogleSignInResult provided by the GoogleAPIClient
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            NSService.getInstance(getContext()).signInWithGoogle(acct, new NSService.MySimpleCallback() {
                @Override
                public void onSuccess(String s) {
                    Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                    mListener.loggedIn();
                }

                @Override
                public void onFailure(String s) {
                    Toast.makeText(getContext(), "failure", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "failure", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the facebook access token
     * @param token AccessToken provided by LoginButton
     */
    public void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        NSService.getInstance(getContext()).signInWithFacebook(token, new NSService.MySimpleCallback() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                mListener.loggedIn();
            }

            @Override
            public void onFailure(String s) {
                Toast.makeText(getContext(), "failure", Toast.LENGTH_SHORT).show();

            }
        });
    }


    /**
     * Fragment callbacks. loggedInWithEmail() is called when user successfully authenticates via one of the
     * provided authentication method
     */
    public interface OnFragmentInteractionListener {
        void loggedIn();
    }

}
