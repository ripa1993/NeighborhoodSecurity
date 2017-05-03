package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EmailPasswordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmailPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

enum FragmentState{
    LOGIN, REGISTER, PASSWORD_RESET;
}


public class EmailPasswordFragment extends Fragment {

    FragmentState state;

    TextInputLayout inputLayoutUsername, inputLayoutPassword, inputLayoutEmail;
    EditText etUsername, etPassword, etEmail;
    Button buttonSignin, buttonSignup, buttonResetPassword;
    TextView tvForgotPassword, tvRegister;

    private OnFragmentInteractionListener mListener;

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

    public void signInClicked(View view){
        String email, password;
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        if (email.length()>0 && password.length()>0){
            NSService.getInstance(getContext()).signInWithEmail(email, password, new NSService.MyCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                    mListener.loggedIn();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getContext(), "failure", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onMessageLoad(MyMessage message, int status) {
                    Toast.makeText(getContext(), message.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "check fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void signUpClicked(View view){
        String email, password, username;
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        username = etUsername.getText().toString();
        if(email.length()>0 && password.length()>0 && username.length()>0){
            NSService.getInstance(getContext()).signUpWithEmail(username, email, password, new NSService.MyCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getContext(), "failure", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onMessageLoad(MyMessage message, int status) {
                    Toast.makeText(getContext(), message.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            Toast.makeText(getContext(), "check fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void resetPasswordClicked(View view){
        String email;
        email = etEmail.getText().toString();
        if (email.length() > 0){
            NSService.getInstance(getContext()).sendPasswordResetEmail(email, new NSService.MySimpleCallback() {
                @Override
                public void onSuccess(String s) {
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String s) {
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "check fields", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Change layout to "RESET_PASSWORD"
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
     * Change layout to "REGISTER"
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
     * Change layout to "LOGIN"
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
     * closeFragment(): called when asked to change the view
     */
    public interface OnFragmentInteractionListener {

        void closeFragment();
        void loggedIn();
    }
}
