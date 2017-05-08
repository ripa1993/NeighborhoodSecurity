package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventType;
import com.satsuware.usefulviews.LabelledSpinner;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.Date;

public class EventCreateFragment extends Fragment {

    final private Event event;

    private static final String TAG = "EventCreateFragment";
    private EditText etDescription, etLatitude, etLongitude;
    private LabelledSpinner lsEventType;
    private RadioButton rbAddress;

    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_COUNTRY = "country";
    private static final String ARG_CITY = "city";
    private static final String ARG_STREET = "street";


    private Double latitude, longitude;
    private String country, city, street;

    private OnFragmentInteractionListener mListener;

    private SupportPlaceAutocompleteFragment placeAutocompleteFragment;

    public EventCreateFragment() {
        // Required empty public constructor
        event = new Event();
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
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_event_create, container, false);

        // assign local variables

        etDescription = (EditText) view.findViewById(R.id.input_description);
        etLatitude = (EditText) view.findViewById(R.id.input_latitude);
        etLongitude = (EditText) view.findViewById(R.id.input_longitude);
        lsEventType = (LabelledSpinner) view.findViewById(R.id.labelled_spinner_event_type);
        rbAddress = (RadioButton) view.findViewById(R.id.radioAddress);
        placeAutocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        // setup spinner
        lsEventType.setItemsArray(Arrays.asList(EventType.values()));
        lsEventType.setColor(android.R.color.tertiary_text_dark); // todo: check color

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

            etLatitude.setText(latitude.toString());
            etLongitude.setText(longitude.toString());

        } else {
            RadioButton radioButtonAddress = (RadioButton) view.findViewById(R.id.radioAddress);
            radioButtonAddress.setChecked(true);

        }

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                LatLng ll = place.getLatLng();
                event.setLatitude(ll.latitude);
                event.setLongitude(ll.longitude);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });


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

    public Event getEvent(){
        event.setDate(new Date());
        event.setDescription(etDescription.getText().toString());
        event.setEventType((EventType) lsEventType.getSpinner().getSelectedItem());
        return event;
    }

    public boolean eventUsesAddress(){
        if (rbAddress.isChecked()){
            return true;
        } else {
            return false;
        }
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
