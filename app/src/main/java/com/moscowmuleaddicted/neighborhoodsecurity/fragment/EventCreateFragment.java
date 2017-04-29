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

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventType;
import com.satsuware.usefulviews.LabelledSpinner;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventCreateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventCreateFragment extends Fragment {

    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_COUNTRY = "country";
    private static final String ARG_CITY = "city";
    private static final String ARG_STREET = "street";


    private Double latitude, longitude;
    private String country, city, street;

    private OnFragmentInteractionListener mListener;

    public EventCreateFragment() {
        // Required empty public constructor
    }

    public static EventCreateFragment newInstanceWithCoordinates(Double lat, Double lon) {
        EventCreateFragment fragment = new EventCreateFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, lat);
        args.putDouble(ARG_LONGITUDE, lon);
        fragment.setArguments(args);
        return fragment;
    }

    public static EventCreateFragment newInstanceWithAddress(String country, String city, String street){
        EventCreateFragment fragment = new EventCreateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COUNTRY, country);
        args.putString(ARG_CITY, city);
        args.putString(ARG_STREET, street);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_LONGITUDE) && getArguments().containsKey(ARG_LATITUDE)){
                latitude = getArguments().getDouble(ARG_LATITUDE);
                longitude = getArguments().getDouble(ARG_LONGITUDE);
            } else if(getArguments().containsKey(ARG_COUNTRY) && getArguments().containsKey(ARG_CITY) && getArguments().containsKey(ARG_STREET) ){
                country = getArguments().getString(ARG_COUNTRY);
                city = getArguments().getString(ARG_CITY);
                street = getArguments().getString(ARG_STREET);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_event_create, container, false);

        // setup spinner
        LabelledSpinner eventTypeSpinnner = (LabelledSpinner) view.findViewById(R.id.labelled_spinner_event_type);
        eventTypeSpinnner.setItemsArray(Arrays.asList(EventType.values()));
        eventTypeSpinnner.setColor(android.R.color.tertiary_text_dark); // todo: check color

        // setup radio
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupEventCreate);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButtonAddress = (RadioButton) group.findViewById(R.id.radioAddress);
                LinearLayout addressGroup = (LinearLayout) view.findViewById(R.id.layout_address_group);
                RelativeLayout coordinatesGroup = (RelativeLayout) view.findViewById(R.id.layout_coordinates_group);

                if(radioButtonAddress.isChecked()){
                    // enable address input
                    coordinatesGroup.setVisibility(RelativeLayout.GONE);
                    addressGroup.setVisibility(LinearLayout.VISIBLE);
                } else {
                    // enable coordinates input
                    addressGroup.setVisibility(LinearLayout.GONE);
                    coordinatesGroup.setVisibility(RelativeLayout.VISIBLE);

                }
            }
        });
        // set default radio according to provided values
        if (latitude != null && longitude != null){
            RadioButton radioButtonCoordinates = (RadioButton) view.findViewById(R.id.radioCoordinates);
            radioButtonCoordinates.setChecked(true);

            EditText editTextLatitude = (EditText) view.findViewById(R.id.input_latitude);
            EditText editTextLongitude = (EditText) view.findViewById(R.id.input_longitude);

            editTextLatitude.setText(latitude.toString());
            editTextLongitude.setText(longitude.toString());

        } else {
            RadioButton radioButtonAddress = (RadioButton) view.findViewById(R.id.radioAddress);
            radioButtonAddress.setChecked(true);

            if (country != null && city != null && street != null){
                EditText editTextCountry = (EditText) view.findViewById(R.id.input_country);
                EditText editTextCity = (EditText) view.findViewById(R.id.input_city);
                EditText editTextStreet = (EditText) view.findViewById(R.id.input_street);

                editTextCountry.setText(country);
                editTextCity.setText(city);
                editTextStreet.setText(street);
            }

        }


        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
