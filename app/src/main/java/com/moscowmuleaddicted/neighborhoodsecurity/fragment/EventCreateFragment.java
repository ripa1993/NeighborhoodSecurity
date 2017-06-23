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

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_CITY;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_COUNTRY;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_LATITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_LONGITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_STREET;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.RC_PERMISSION_POSITION;

/**
 * Fragment containing the fields required for the creation of a new {@link Event}
 *
 * @author Simone Ripamonti
 * @version 2
 */
public class EventCreateFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * Logger's TAG
     */
    private static final String TAG = "EventCreateFrag";
    /**
     * The locally created event
     */
    final private Event event;
    /**
     * Description edit text
     */
    private EditText etDescription;
    /**
     * Latitude edit text
     */
    private EditText etLatitude;
    /**
     * Longitude edit text
     */
    private EditText etLongitude;
    /**
     * Description input layout
     */
    private TextInputLayout ilDescription;
    /**
     * Latitude input layout
     */
    private TextInputLayout ilLatitude;
    /**
     * Longitude input layout
     */
    private TextInputLayout ilLongitude;
    /**
     * Event type spinner
     */
    private LabelledSpinner lsEventType;
    /**
     * Radio group containing address radio and coordinates radio
     */
    private RadioGroup radioGroup;
    /**
     * Address radio button
     */
    private RadioButton rbAddress;
    /**
     * Image view showing a compass
     */
    private ImageView ivGetPosition;
    /**
     * Google API client to obtain position services location
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Last known location
     */
    private Location mLastLocation;
    /**
     * Latitude obtained by coordinates or by Google Places API
     */
    private Double latitude;
    /**
     * Longitude obtained by coordinates or by Google Places API
     */
    private Double longitude;
    /**
     * Fragment interaction listener
     */
    private OnFragmentInteractionListener mListener;
    /**
     * Google Places API autocomplete fragment
     */
    private SupportPlaceAutocompleteFragment placeAutocompleteFragment;

    /**
     * Creator
     */
    public EventCreateFragment() {
        // Required empty public constructor
        event = new Event();
    }

    /**
     * Builder that initializes a particular coordinates position
     * @param lat
     * @param lon
     * @return
     */
    public static EventCreateFragment newInstanceWithCoordinates(Double lat, Double lon) {
        EventCreateFragment fragment = new EventCreateFragment();
        Bundle args = new Bundle();
        args.putDouble(IE_LATITUDE, lat);
        args.putDouble(IE_LONGITUDE, lon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(IE_LONGITUDE) && getArguments().containsKey(IE_LATITUDE)) {
                latitude = getArguments().getDouble(IE_LATITUDE);
                longitude = getArguments().getDouble(IE_LONGITUDE);
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
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupEventCreate);
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
                    Toast.makeText(getContext(), String.format(getString(R.string.last_known_location_ok), (int)mLastLocation.getAccuracy()), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.last_known_location_fail, Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // request permissions for accessing location, requires SDK >= 23 (marshmellow)
                Log.d(TAG, "onConnected: prompting user to allow location permissions");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_PERMISSION_POSITION);
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
        if (requestCode == RC_PERMISSION_POSITION) {
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
        // do nothing
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // do nothing
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

    /**
     * Get the event characterized by the value contained in the different views
     * @return the event
     */
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

    /**
     * Checks if the event is built using a Place or Coordinates
     * @return true if Place, else false
     */
    public boolean eventUsesAddress(){
        if (rbAddress.isChecked()){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Shows errors
     */
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

    /**
     * Sets the location manually to the specified coordinates
     * @param lat latitude
     * @param lon longitude
     */
    public void setLocation(double lat, double lon) {
        radioGroup.check(R.id.radio_coordinates_event);
        etLatitude.setText(String.valueOf(lat));
        etLongitude.setText(String.valueOf(lon));
    }

    /**
     * Fragment listener interface
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
