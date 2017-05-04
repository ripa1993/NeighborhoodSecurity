package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Subscription;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubscriptionCreateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubscriptionCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubscriptionCreateFragment extends Fragment {

    private EditText etCountry, etCity, etStreet, etLatitude, etLongitude;
    private TextView tvSeekbarCurValue;
    private SeekBar sbRadius;
    private RadioGroup radioGroup;
    private RadioButton rbAddress;

    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longtitude";

    private Double mLatitude;
    private Double mLongitude;

    private OnFragmentInteractionListener mListener;

    public SubscriptionCreateFragment() {
        // Required empty public constructor
    }

    public static SubscriptionCreateFragment newInstance(Double latitude, Double longitude) {
        SubscriptionCreateFragment fragment = new SubscriptionCreateFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLatitude = getArguments().getDouble(ARG_LATITUDE);
            mLongitude = getArguments().getDouble(ARG_LONGITUDE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_subscription_create, container, false);

        etCountry = (EditText) view.findViewById(R.id.input_country);
        etCity = (EditText) view.findViewById(R.id.input_city);
        etStreet = (EditText) view.findViewById(R.id.input_street);
        etLatitude = (EditText) view.findViewById(R.id.input_latitude);
        etLongitude = (EditText) view.findViewById(R.id.input_longitude);

        tvSeekbarCurValue = (TextView) view.findViewById(R.id.seekbar_title_value);

        sbRadius = (SeekBar) view.findViewById(R.id.seekbar_radius);

        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group_subscription);

        rbAddress = (RadioButton) view.findViewById(R.id.radio_address);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                LinearLayout addressGroup = (LinearLayout) view.findViewById(R.id.layout_address_group);
                RelativeLayout coordinatesGroup = (RelativeLayout) view.findViewById(R.id.layout_coordinates_group);

                if(rbAddress.isChecked()){
                    addressGroup.setVisibility(View.VISIBLE);
                    coordinatesGroup.setVisibility(View.GONE);
                } else {
                    addressGroup.setVisibility(View.GONE);
                    coordinatesGroup.setVisibility(View.VISIBLE);
                }
            }
        });

        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress==1) {
                    tvSeekbarCurValue.setText(String.format(getString(R.string.metres_singular), progress));
                } else {
                    tvSeekbarCurValue.setText(String.format(getString(R.string.metres_plural), progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbRadius.setProgress(500);



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

    public interface OnFragmentInteractionListener {
    }

    public boolean isAddressChecked(){
        return rbAddress.isChecked();
    }

    public int getRadius(){
        return sbRadius.getProgress();
    }

    public Double getLatitude(){
        return NumberUtils.toDouble(etLatitude.getText().toString());
    }

    public Double getLongitude(){
        return NumberUtils.toDouble(etLongitude.getText().toString());
    }

    public String getCountry(){
        return etCountry.getText().toString();
    }

    public String getCity(){
        return etCity.getText().toString();
    }

    public String getStreet(){
        return etStreet.getText().toString();
    }
}
