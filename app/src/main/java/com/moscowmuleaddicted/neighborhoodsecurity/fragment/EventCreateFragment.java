package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.EventType;
import com.satsuware.usefulviews.LabelledSpinner;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.Date;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.PERMISSION_POSITION_REQUEST_CODE;

public class EventCreateFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final private Event event;

    private static final String TAG = "EventCreateFragment";
    private EditText etDescription, etLatitude, etLongitude;
    private TextInputLayout ilDescription, ilLatitude, ilLongitude;
    private LabelledSpinner lsEventType;
    private RadioButton rbAddress;
    private ImageView ivGetPosition;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

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

    public static EventCreateFragment newInstanceWithAddress(String country, String city, String street) {
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
            if (getArguments().containsKey(ARG_LONGITUDE) && getArguments().containsKey(ARG_LATITUDE)) {
                latitude = getArguments().getDouble(ARG_LATITUDE);
                longitude = getArguments().getDouble(ARG_LONGITUDE);
            }
        }
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
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
        ilDescription = (TextInputLayout) view.findViewById(R.id.input_layout_description);
        ilLatitude = (TextInputLayout) view.findViewById(R.id.input_layout_latitude);
        ilLongitude = (TextInputLayout) view.findViewById(R.id.input_layout_longitude);
        lsEventType = (LabelledSpinner) view.findViewById(R.id.labelled_spinner_event_type);
        rbAddress = (RadioButton) view.findViewById(R.id.radio_address_event);
        placeAutocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        ivGetPosition = (ImageView) view.findViewById(R.id.event_get_position);

        // setup spinner
        lsEventType.setItemsArray(Arrays.asList(EventType.values()));
        lsEventType.setColor(android.R.color.tertiary_text_dark);

        // setup radio
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupEventCreate);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButtonAddress = (RadioButton) group.findViewById(R.id.radio_address_event);
                LinearLayout addressGroup = (LinearLayout) view.findViewById(R.id.layout_address_group);
                RelativeLayout coordinatesGroup = (RelativeLayout) view.findViewById(R.id.layout_coordinates_group);

                if (radioButtonAddress.isChecked()) {
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
        if (latitude != null && longitude != null) {
            RadioButton radioButtonCoordinates = (RadioButton) view.findViewById(R.id.radio_coordinates_event);
            radioButtonCoordinates.setChecked(true);

            etLatitude.setText(latitude.toString());
            etLongitude.setText(longitude.toString());

        } else {
            RadioButton radioButtonAddress = (RadioButton) view.findViewById(R.id.radio_address_event);
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

        ivGetPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLastLocation != null){
                    radioGroup.check(R.id.radio_coordinates_event);
                    etLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
                    etLongitude.setText(String.valueOf(mLastLocation.getLongitude()));
                    Toast.makeText(getContext(), "location set with accuracy of "+(int)mLastLocation.getAccuracy()+" metres", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "still acquiring position", Toast.LENGTH_SHORT).show();
                }
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
        if(!eventUsesAddress()){
            event.setLatitude(NumberUtils.toDouble(etLatitude.getText().toString(), Double.NEGATIVE_INFINITY));
            event.setLongitude(NumberUtils.toDouble(etLongitude.getText().toString(), Double.NEGATIVE_INFINITY));

            placeAutocompleteFragment.setText("");
        }
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


    public void showErrors(){
        if(etDescription.getText().length() == 0){
            ilDescription.setError(getString(R.string.msg_insert_valid_description));
        } else {
            ilDescription.setError(null);
        }

        if(eventUsesAddress()){
            placeAutocompleteFragment.setText("");

        } else {
            if(etLatitude.getText().length() == 0){
                ilLatitude.setError(getString(R.string.msg_insert_valid_latitude));
            } else {
                ilLatitude.setError(null);
            }

            if(etLongitude.getText().length() == 0){
                ilLongitude.setError(getString(R.string.msg_insert_valid_longitude));
            } else {
                ilLongitude.setError(null);
            }
        }


    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // request permissions for accessing location, requires SDK >= 23 (marshmellow)
                Log.d(TAG, "onConnected: prompting user to allow location permissions");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_POSITION_REQUEST_CODE);
            } else {
                Log.w(TAG, "onConnected: SDK version is too low (" + Build.VERSION.SDK_INT + ") to ask permissions at runtime");
                Toast.makeText(getContext(), "Give location permission to allow application know events around you", Toast.LENGTH_LONG).show();
            }

        } else {
            // permissions already granted
            Log.d(TAG, "onConnected: location permission already granted, requesting last known position");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_POSITION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: location permission granted, requesting last known position");
                //noinspection MissingPermission
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } else {
                Log.d(TAG, "onRequestPermissionsResult: location permission not granted");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}
