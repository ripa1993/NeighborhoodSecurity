package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.EventCreateActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.EventDetailActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.EventListActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.MapActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.SubscriptionCreateActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NSMapFragment extends MapFragment implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<NSMapFragment.EventClusterItem>, ClusterManager.OnClusterItemClickListener<NSMapFragment.EventClusterItem>, GoogleMap.OnCameraIdleListener, GoogleMap.OnMapLongClickListener {

    public static final String TAG = "NSMapFragment";

    private GoogleMap currentMap;
    private NSService service;
    private Set<Integer> idsAlreadyIn;

    // Initial position or events
    private LatLng initialPosition;
    private boolean initialPositionSet = false;
    private List<Event> initialEvents;
    private boolean initialEventsSet = false;
    private LatLng defaultPosition = new LatLng(45.477072, 9.226096); // Milano

    public void setInitialPosition(LatLng initialPosition) {
        this.initialPosition = initialPosition;
        initialPositionSet = true;
    }

    public void setInitialEvents(List<Event> initialEvents) {
        this.initialEvents = initialEvents;
        initialEventsSet = true;
    }

    // Declare a variable for the cluster manager.
    private ClusterManager<EventClusterItem> mClusterManager;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        service = NSService.getInstance(getActivity());
        idsAlreadyIn = new HashSet<Integer>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Set local map variable
        currentMap = googleMap;
        //noinspection MissingPermission
        currentMap.setMyLocationEnabled(false);
        currentMap.setTrafficEnabled(false);
        currentMap.setIndoorEnabled(false);
        currentMap.setBuildingsEnabled(false);
        currentMap.getUiSettings().setMapToolbarEnabled(false);

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(getActivity(), currentMap);
        mClusterManager.setRenderer(new EventClusterRenderer());

        // set listeners
        currentMap.setOnCameraIdleListener(this);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        currentMap.setOnMarkerClickListener(mClusterManager);

        // load initial events
        if (initialEventsSet) {
            addToCluster(initialEvents);
        }

        // set initial position
        if (initialPositionSet) {
            moveCamera(initialPosition, true);
        } else if (initialEventsSet){
            centerCameraToContainAllEvents(initialEvents, true);
        } else {
            moveCamera(defaultPosition, true);
        }

        // set long click listener
        googleMap.setOnMapLongClickListener(this);
    }


    private LatLng computeCenterEventList(List<Event> events) {

        double x = 0;
        double y = 0;
        double z = 0;
        int count = events.size();

        for (Event event : events) {
            double latitude = event.getLatitude() * Math.PI / 180;
            double longitude = event.getLongitude() * Math.PI / 180;

            x += Math.cos(latitude) * Math.cos(longitude);
            y += Math.cos(latitude) * Math.sin(longitude);
            z += Math.sin(latitude);
        }

        x = x / count;
        y = y / count;
        z = z / count;

        double centralLongitude = Math.atan2(y, x);
        double centralSquareRoot = Math.sqrt(x * x + y * y);
        double centralLatitude = Math.atan2(z, centralSquareRoot);

        return new LatLng(centralLatitude * 180 / Math.PI, centralLongitude * 180 / Math.PI);
    }


    private void centerCameraInBarycenterOfEvents(List<Event> events, boolean animate) {
        if (animate)
            currentMap.animateCamera(CameraUpdateFactory.newLatLngZoom(computeCenterEventList(events), 11));
        else
            currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(computeCenterEventList(events), 11));
    }

    private void centerCameraToContainAllEvents(List<Event> events, boolean animate) {
        //Calculate the markers to get their position
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (Event event : events) {
            b.include(new LatLng(event.getLatitude(), event.getLongitude()));
        }
        LatLngBounds bounds = b.build();

        if (animate)
            currentMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 5));
        else
            currentMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 5));
    }

    private synchronized void addToCluster(List<Event> events) {
        Log.d(TAG, "Found " + events.size() + " events");
        List<EventClusterItem> eventClusterItems = new ArrayList<>();
        for (Event e : events) {
            if (idsAlreadyIn.add(e.getId())) {
                eventClusterItems.add(new EventClusterItem(e));
            }
        }
        mClusterManager.addItems(eventClusterItems);
    }

    @Override
    public boolean onClusterClick(Cluster<EventClusterItem> cluster) {
        Collection<EventClusterItem> eventClusterItems = cluster.getItems();
        ArrayList<Event> events = new ArrayList<>();
        for(EventClusterItem e: eventClusterItems){
            events.add(e.getEvent());
        }
        Intent showEventList = new Intent(getActivity(), EventListActivity.class);
        showEventList.putExtra("event-list", events);
        startActivity(showEventList);
        return false;
    }

    @Override
    public boolean onClusterItemClick(EventClusterItem eventClusterItem) {
        Event event = eventClusterItem.getEvent();
        Intent showEventDetail = new Intent(getActivity(), EventDetailActivity.class);
        showEventDetail.putExtra("event", event);
        startActivity(showEventDetail);
        return false;
    }

    public void moveCamera(LatLng latLng, boolean animate){
        if(animate){
            currentMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));
        } else {
            currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));
        }
        onCameraIdle();
    }

    @Override
    public void onCameraIdle() {
        Log.d(TAG, "Camera stopped!");
        LatLng northEast = currentMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        LatLng southWest = currentMap.getProjection().getVisibleRegion().latLngBounds.southwest;

        Double latMin, latMax, lonMin, lonMax;
        latMin = southWest.latitude;
        latMax = northEast.latitude;
        lonMin = southWest.longitude;
        lonMax = northEast.longitude;

        final List<Event> localEvents = new ArrayList<Event>();
        localEvents.addAll(service.getEventsByArea(latMin, latMax, lonMin, lonMax, new NSService.MyCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                Log.d(TAG, "Found " + events.size() + " events after the API call");
                addToCluster(events);
            }

            @Override
            public void onFailure() {
                Log.w(TAG, "onFailure: getEventsByArea");
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Log.w(TAG, "onMessageLoad: getEventsByArea " + message);
            }
        }));
        Log.d(TAG, "Found " + localEvents.size() + " events in the local db");
        addToCluster(localEvents);
        mClusterManager.onCameraIdle();
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        Log.d(TAG, "map long click");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.map_dialog)
            .setItems(new String[]{getString(R.string.map_dialog_event), getString(R.string.map_dialog_subscription)}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            Log.d(TAG, "starting event creation");
                            Intent intentEvent = new Intent(getApplicationContext(), EventCreateActivity.class);
                            intentEvent.putExtra("lat", latLng.latitude);
                            intentEvent.putExtra("lon", latLng.longitude);
                            startActivity(intentEvent);
                            return;
                        case 1:
                            Log.d(TAG, "starting subscription creation");
                            Intent intentSubscription = new Intent(getApplicationContext(), SubscriptionCreateActivity.class);
                            intentSubscription.putExtra("lat", latLng.latitude);
                            intentSubscription.putExtra("lon", latLng.longitude);
                            startActivity(intentSubscription);
                            return;
                        default:
                            return;
                    }
                }
            }).setCancelable(true).create().show();

    }

    public class EventClusterItem implements ClusterItem {
        private Event mEvent;
        private LatLng mPosition;
        private String mTitle;
        private String mSnippet;

        public EventClusterItem(Event event) {
            mEvent = event;
            mPosition = new LatLng(event.getLatitude(), event.getLongitude());
        }

        public EventClusterItem(double lat, double lng, String title, String snippet, Event event) {
            mPosition = new LatLng(lat, lng);
            mTitle = title;
            mSnippet = snippet;
            mEvent = event;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public String getTitle() {
            return mTitle;
        }

        @Override
        public String getSnippet() {
            return mSnippet;
        }

        public Event getEvent() {
            return mEvent;
        }
    }

    private class EventClusterRenderer extends DefaultClusterRenderer<EventClusterItem> {

        public EventClusterRenderer() {
            super(getApplicationContext(), currentMap, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(EventClusterItem item, MarkerOptions markerOptions) {
            switch (item.getEvent().getEventType()) {
                case CARJACKING:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_carjacking));
                    break;
                case BURGLARY:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_burglary));
                    break;
                case ROBBERY:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_robbery));
                    break;
                case THEFT:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_theft));
                    break;
                case SHADY_PEOPLE:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_shady_people));
                    break;
                case SCAMMERS:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_scammers));
                    break;
                default:
                    break;
            }
        }
    }

}
