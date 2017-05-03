package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.EventDetailActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NSMapFragment extends MapFragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private GoogleMap currentMap;
    private NSService service;
    private Set<Integer> idsAlreadyIn;

    // Initial position or events
    private LatLng initialPosition;
    private boolean initialPositionSet = false;
    private List<Event> initialEvents;
    private boolean initialEventsSet = false;

    public void setInitialPosition(LatLng initialPosition) {
        this.initialPosition = initialPosition;
        initialPositionSet = true;
    }

    public void setInitialEvents(List<Event> initialEvents) {
        this.initialEvents = initialEvents;
        initialEventsSet = true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        idsAlreadyIn = new HashSet<Integer>();
        // Set local map variable
        currentMap = googleMap;
        // Set a listener for marker click.
        currentMap.setOnMarkerClickListener(this);

        if (initialEventsSet) {
            addEventListMarkers(initialEvents);
            if (initialPositionSet)
                currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 11));
            else
                centerCameraToContainAllEvents(initialEvents, false);
        } else if(initialPositionSet){
            currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 11));
        } else{
            test();
        }
    }


    // Add marker of an event
    private void addEventMarker(Event event) {

        if(idsAlreadyIn.add(event.getId())) { // true if this set did not already contain the specified element

            LatLng coords = new LatLng(event.getLatitude(), event.getLongitude());
            String title = event.getId() + ") " + event.getEventType().toString();

            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(event.getLatitude(), event.getLongitude()));
            options.title(title);

            // Select icon
            switch (event.getEventType()) {
                case CARJACKING:
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_carjacking));
                    break;
                case BURGLARY:
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_burglary));
                    break;
                case ROBBERY:
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_robbery));
                    break;
                case THEFT:
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_theft));
                    break;
                case SHADY_PEOPLE:
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_shady_people));
                    break;
                case SCAMMERS:
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_scammers));
                    break;
                default:
                    break;
            }

            // Add the marker
            Marker newMarker = currentMap.addMarker(options);

            // Save the Event object in marker's Tag
            newMarker.setTag(event);
        }
    }


    // Add all markers from a list of events
    public void addEventListMarkers(List<Event> events) {

        for (Event event : events) {
            addEventMarker(event);
        }

    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve Event object.
        Event event = (Event) marker.getTag();

        Intent i = new Intent(getActivity(), EventDetailActivity.class);
        i.putExtra("event", event);
        startActivity(i);

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
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
        if(animate)
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

        if(animate)
            currentMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 5));
        else
            currentMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 5));
    }


    private void test() {
        Double latMin = 0d, latMax = 0d, lonMin = 0d, lonMax = 0d;
        latMin = 45d;
        latMax = 46d;
        lonMin = 9d;
        lonMax = 10d;

        service = NSService.getInstance(getActivity());

        service.getEventsByArea(latMin, latMax, lonMin, lonMax, new NSService.MyCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                addEventListMarkers(events);
                centerCameraToContainAllEvents(events, true);
            }

            @Override
            public void onFailure() {
                Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageLoad(MyMessage message, int status) {
                Toast.makeText(getActivity(), status + " " + message.getArgument() + " " + message.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
