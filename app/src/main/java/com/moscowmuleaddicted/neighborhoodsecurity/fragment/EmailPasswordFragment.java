package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * Fragment containing login / registration / password reset for email authentication
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class EmailPasswordFragment extends Fragment {
    /**
     * Logger's TAG
     */
    public static final String TAG = "EmailPasswordFrag";
    /**
     * Fragment state to handle the behaviour of back button
     */
    private enum FragmentState {
        LOGIN, REGISTER, PASSWORD_RESET;
    }
    /**
     * The fragment state
     */
    private FragmentState state;
    /**
     * Text input layout username
     */
    private TextInputLayout inputLayoutUsername;
    /**
     * Text input layout password
     */
    private TextInputLayout inputLayoutPassword;
    /**
     * Text input layout email
     */
    private TextInputLayout inputLayoutEmail;
    /**
     * Edit text username
     */
    private EditText etUsername;
    /**
     * Edit text password
     */
    private EditText etPassword;
    /**
     * Edit text email
     */
    private EditText etEmail;
    /**
     * Button signin
     */
    private Button buttonSignin;
    /**
     * Button signup
     */
    private Button buttonSignup;
    /**
     * Button reset password
     */
    private Button buttonResetPassword;
    /**
     * Text view forgot password, clickable
     */
    private TextView tvForgotPassword;
    /**
     * Text view register, clickable
     */
    private TextView tvRegister;
    /**
     * Fragment listener
     */
    private OnFragmentInteractionListener mListener;
    /**
     * Empty constructor
     */
    public EmailPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_email_password, container, false);

        inputLayoutEmail = (TextInputLayout) view.findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) view.findViewById(R.id.input_layout_password);
        inputLayoutUsername = (TextInputLayout) view.findViewById(R.id.input_layout_username);

        etUsername = (EditText) view.findViewById(R.id.input_username);
        etPassword = (EditText) view.findViewById(R.id.input_password);
        etEmail = (EditText) view.findViewById(R.id.input_email);

        buttonSignin = (Button) view.findViewById(R.id.ep_signin);
        buttonSignup = (Button) view.findViewById(R.id.ep_signup);
        buttonResetPassword = (Button) view.findViewById(R.id.ep_reset_password);

        tvForgotPassword = (TextView) view.findViewById(R.id.ep_forgot_password);
        tvRegister = (TextView) view.findViewById(R.id.ep_register);

        state = FragmentState.LOGIN;

        buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInClicked(v);
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpClicked(v);
            }
        });

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPasswordClicked(v);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayoutForgotPassword(v);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayoutRegister(v);
            }
        });

        return view;
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

    /**
     * Perform login via email if possibile. If success, loggedInWithEmail callback
     * @param view
     */
    public void signInClicked(View view){
        String email, password;
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        boolean emailOK = EmailValidator.getInstance().isValid(email);
        boolean passwordOK = password.length()>=6;

        if (passwordOK && emailOK){
            NSService.getInstance(getContext()).signInWithEmail(email, password, new NSService.MyCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "logged in with email");
                    Toast.makeText(getContext(), getString(R.string.msg_success_login), Toast.LENGTH_SHORT).show();
                    mListener.loggedInWithEmail();
                }

                @Override
                public void onFailure() {
                    Log.w(TAG, "failed to login");
                    Toast.makeText(getContext(), getString(R.string.msg_problem_login), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onMessageLoad(MyMessage message, int status) {
                    Log.w(TAG, "failed to login with msg: "+message);
                    Toast.makeText(getContext(), getString(R.string.msg_unknown_error), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if(!emailOK){
                inputLayoutEmail.setError(getString(R.string.msg_insert_valid_email));
            } else {
                inputLayoutEmail.setError(null);
            }

            if(!passwordOK){
                inputLayoutPassword.setError(getString(R.string.msg_insert_valid_password));
            } else {
                inputLayoutPassword.setError(null);
            }

        }
    }

    /**
     * Perform registration via email if possible.
     * @param view
     */
    public void signUpClicked(View view){
        String email, password, username;
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        username = etUsername.getText().toString();

        boolean emailOK = EmailValidator.getInstance().isValid(email);
        boolean passwordOK = password.length()>=6;
        boolean usernameOK = username.length() > 0;

        if(emailOK && passwordOK && usernameOK){
            NSService.getInstance(getContext()).signUpWithEmail(username, email, password, new NSService.MyCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "registered successfully");
                    Toast.makeText(getContext(), getString(R.string.msg_success_register), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    Log.w(TAG, "failed to register");
                    Toast.makeText(getContext(), getString(R.string.msg_problem_register), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onMessageLoad(MyMessage message, int status) {
                    Log.w(TAG, "failed to register with msg: "+message);
                    Toast.makeText(getContext(), getString(R.string.msg_unknown_error), Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            if(!emailOK){
                inputLayoutEmail.setError(getString(R.string.msg_insert_valid_email));
            } else {
                inputLayoutEmail.setError(null);
            }

            if(!passwordOK){
                inputLayoutPassword.setError(getString(R.string.msg_insert_valid_password));
            } else {
                inputLayoutPassword.setError(null);
            }

            if(!usernameOK){
                inputLayoutUsername.setError(getString(R.string.msg_insert_valid_username));
            } else {
                inputLayoutUsername.setError(null);
            }
        }
    }

    /**
     * Send password reset email, if possible
     * @param view
     */
    public void resetPasswordClicked(View view){
        String email;
        email = etEmail.getText().toString();
        boolean emailOK = EmailValidator.getInstance().isValid(email);
        if (emailOK){
            NSService.getInstance(getContext()).sendPasswordResetEmail(email, new NSService.MySimpleCallback() {
                @Override
                public void onSuccess(String s) {
                    Toast.makeText(getContext(), getString(R.string.msg_success_resetpassword), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String s) {
                    Toast.makeText(getContext(), getString(R.string.msg_problem_resetpassword), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if(!emailOK){
                inputLayoutEmail.setError(getString(R.string.msg_insert_valid_email));
            } else {
                inputLayoutEmail.setError(null);
            }
        }
    }

    /**
     * Change layout to "password reset"
     * @param view
     */
    public void changeLayoutForgotPassword(View view){
        inputLayoutEmail.setVisibility(View.VISIBLE);
        inputLayoutPassword.setVisibility(View.GONE);
        inputLayoutUsername.setVisibility(View.GONE);

        buttonSignin.setVisibility(View.GONE);
        buttonSignup.setVisibility(View.GONE);
        buttonResetPassword.setVisibility(View.VISIBLE);

        tvForgotPassword.setVisibility(View.GONE);
        tvRegister.setVisibility(View.GONE);

        state = FragmentState.PASSWORD_RESET;
    }

    /**
     * Change layout to "registration"
     * @param view
     */
    public void changeLayoutRegister(View view){
        inputLayoutEmail.setVisibility(View.VISIBLE);
        inputLayoutPassword.setVisibility(View.VISIBLE);
        inputLayoutUsername.setVisibility(View.VISIBLE);

        buttonSignin.setVisibility(View.GONE);
        buttonSignup.setVisibility(View.VISIBLE);
        buttonResetPassword.setVisibility(View.GONE);

        tvForgotPassword.setVisibility(View.GONE);
        tvRegister.setVisibility(View.GONE);

        state = FragmentState.REGISTER;
    }

    /**
     * Change layout to "login"
     * @param view
     */
    public void changeLayoutLogin(View view){
        inputLayoutEmail.setVisibility(View.VISIBLE);
        inputLayoutPassword.setVisibility(View.VISIBLE);
        inputLayoutUsername.setVisibility(View.GONE);

        buttonSignin.setVisibility(View.VISIBLE);
        buttonSignup.setVisibility(View.GONE);
        buttonResetPassword.setVisibility(View.GONE);

        tvForgotPassword.setVisibility(View.VISIBLE);
        tvRegister.setVisibility(View.VISIBLE);

        state = FragmentState.LOGIN;
    }

    /**
     * Called by the activity, bound to the BACK button pressed
     */
    public void pressBackButton(){
        switch(state){
            case LOGIN:
                mListener.closeFragment();
                return;
            case REGISTER:
                changeLayoutLogin(null);
                return;
            case PASSWORD_RESET:
                changeLayoutLogin(null);
                return;
        }

        return;
    }


    /**
     * closeFragment(): when exiting the fragment without having logged in
     * loggedInWithEmail(): when exiting after having logged in
     */
    public interface OnFragmentInteractionListener {

        void closeFragment();
        void loggedInWithEmail();
    }
}

