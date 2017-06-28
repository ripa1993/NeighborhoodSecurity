package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.EventCreateActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.EventDetailActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.EventListActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.activity.SubscriptionCreateActivity;
import com.moscowmuleaddicted.neighborhoodsecurity.model.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.controller.NSService;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.DEFAULT_LATITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.DEFAULT_LONGITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.FRAGMENT_NAME_TWITTER;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.IE_EVENT;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.IE_EVENT_LIST;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.IE_LATITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.IE_LONGITUDE;

/**
 * Extension of {@link MapFragment} to support the dynamic insertion of markers related to events
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class NSMapFragment extends MapFragment implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<NSMapFragment.EventClusterItem>, ClusterManager.OnClusterItemClickListener<NSMapFragment.EventClusterItem>, GoogleMap.OnCameraIdleListener, GoogleMap.OnMapLongClickListener {
    /**
     * Logger's TAG
     */
    public static final String TAG = "NSMapFrag";
    /**
     * Current shown google map
     */
    private GoogleMap currentMap;
    /**
     * Reference to application data service
     */
    private NSService service;
    /**
     * Ids that have already been shown
     */
    private Set<Integer> idsAlreadyIn;
    /**
     * Tweets that are already in the map
     */
    private Set<Long> tweetsAlreadyIn;
    /**
     * Map initial position
     */
    private LatLng initialPosition;
    /**
     * Map initial position set flag
     */
    private boolean initialPositionSet = false;
    /**
     * Map initial events
     */
    private List<Event> initialEvents;
    /**
     * Map initial events flag
     */
    private boolean initialEventsSet = false;
    /**
     * Map default initial position
     */
    private LatLng defaultPosition = new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
    /**
     * Cluster manager
     */
    private ClusterManager<EventClusterItem> mClusterManager;
    /**
     * FirebaseAuth instance
     */
    private FirebaseAuth mAuth;

    /**
     * Sets the initial position of the map
     *
     * @param initialPosition center coordinates
     */
    public void setInitialPosition(LatLng initialPosition) {
        this.initialPosition = initialPosition;
        initialPositionSet = true;
    }

    /**
     * Sets the initial events to be shown
     *
     * @param initialEvents events to beshown
     */
    public void setInitialEvents(List<Event> initialEvents) {
        this.initialEvents = initialEvents;
        initialEventsSet = true;
    }

    /**
     * Computes the center of the map given a list of events
     *
     * @param events
     * @return coordinates where to center the map
     */
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

    /**
     * Centers the map in the barycenter of the events
     *
     * @param events
     * @param animate true to animate, false to simply move
     */
    private void centerCameraInBarycenterOfEvents(List<Event> events, boolean animate) {
        if (animate)
            currentMap.animateCamera(CameraUpdateFactory.newLatLngZoom(computeCenterEventList(events), 11));
        else
            currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(computeCenterEventList(events), 11));
    }

    /**
     * Centers the map to display all the events
     *
     * @param events
     * @param animate true to animate, false to simply move
     */
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

    /**
     * Adds a list of events to the cluster manager, only if they are not duplicates
     *
     * @param events to be displayed
     */
    private synchronized void addToClusterEvent(List<Event> events) {
        Log.d(TAG, "Found " + events.size() + " events");
        List<EventClusterItem> eventClusterItems = new ArrayList<>();
        for (Event e : events) {
            if (idsAlreadyIn.add(e.getId())) {
                eventClusterItems.add(new EventClusterItem(e));
            }
        }
        mClusterManager.addItems(eventClusterItems);
    }

    private synchronized void addToClusterTweet(List<Tweet> tweets) {
        Log.d(TAG, "Found " + tweets.size() + " tweets");
        List<EventClusterItem> tweetEvents = new ArrayList<>();
        for (Tweet t : tweets) {
            if (tweetsAlreadyIn.add(t.getId())) {
                tweetEvents.add(new TweetEvent(t));
            }
        }
        mClusterManager.addItems(tweetEvents);
    }

    /**
     * Moves the camera to the specified position
     *
     * @param latLng  coordinates of the center
     * @param animate true to animate, false to simply move
     */
    public void moveCamera(LatLng latLng, boolean animate) {
        if (animate) {
            currentMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));
        } else {
            currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));
        }
        onCameraIdle();
    }

    @Override
    public boolean onClusterClick(Cluster<EventClusterItem> cluster) {
        Collection<EventClusterItem> eventClusterItems = cluster.getItems();
        ArrayList<Event> events = new ArrayList<>();
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (EventClusterItem e : eventClusterItems) {
            if (e.getEvent() != null) {
                // add only non tweets
                events.add(e.getEvent());
            } else {
                tweets.add(((TweetEvent) e).getTweet());
            }
        }
        if (events.size() > 0) {
            Intent showEventList = new Intent(getActivity(), EventListActivity.class);
            showEventList.putExtra(IE_EVENT_LIST, events);
            startActivity(showEventList);
            return false;
        } else if (tweets.size() > 0) {
            showTweet(tweets.get(0));
            return false;
        }
        return true;
    }

    @Override
    public boolean onClusterItemClick(EventClusterItem eventClusterItem) {
        if (eventClusterItem instanceof TweetEvent) {
            Tweet tweet = ((TweetEvent) eventClusterItem).getTweet();
            showTweet(tweet);
            return false;
        } else {
            Event event = eventClusterItem.getEvent();
            Intent showEventDetail = new Intent(getActivity(), EventDetailActivity.class);
            showEventDetail.putExtra(IE_EVENT, event);
            startActivity(showEventDetail);
            return false;
        }
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
                addToClusterEvent(events);
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
        addToClusterEvent(localEvents);

        // get tweet
        LatLng center = currentMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        service.getTweetsByCoordinates(center.latitude, center.longitude, new NSService.TwitterCallback() {
            @Override
            public void onSuccess(List<Tweet> tweets) {
                Log.d(TAG, "found " + tweets.size() + " tweets");
                addToClusterTweet(tweets);
            }

            @Override
            public void onFailure(String s) {
                Log.w(TAG, s);
            }
        });

        mClusterManager.onCameraIdle();
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        Log.d(TAG, "map long click");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.map_dialog)
                .setItems(new String[]{getString(R.string.map_dialog_event), getString(R.string.map_dialog_subscription), getString(R.string.map_dialog_whats_there)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (mAuth.getCurrentUser() != null) {
                                    Log.d(TAG, "starting event creation");
                                    Intent intentEvent = new Intent(getApplicationContext(), EventCreateActivity.class);
                                    intentEvent.putExtra(IE_LATITUDE, latLng.latitude);
                                    intentEvent.putExtra(IE_LONGITUDE, latLng.longitude);
                                    startActivity(intentEvent);
                                    return;
                                } else {
                                    Log.d(TAG, "user is not logged in, this is required when creating event!");
                                    Toast.makeText(getApplicationContext(), getString(R.string.login_required_toast), Toast.LENGTH_LONG).show();
                                    return;
                                }
                            case 1:
                                if (mAuth.getCurrentUser() != null) {
                                    Log.d(TAG, "starting subscription creation");
                                    Intent intentSubscription = new Intent(getApplicationContext(), SubscriptionCreateActivity.class);
                                    intentSubscription.putExtra(IE_LATITUDE, latLng.latitude);
                                    intentSubscription.putExtra(IE_LONGITUDE, latLng.longitude);
                                    startActivity(intentSubscription);
                                    return;
                                } else {
                                    Log.d(TAG, "user is not logged in, this is required when creating a subscription!");
                                    Toast.makeText(getApplicationContext(), getString(R.string.login_required_toast), Toast.LENGTH_LONG).show();
                                    return;
                                }
                            case 2:
                                Log.d(TAG, "starting event list");
                                Intent intentEventList = new Intent(getApplicationContext(), EventListActivity.class);
                                intentEventList.putExtra(IE_LATITUDE, latLng.latitude);
                                intentEventList.putExtra(IE_LONGITUDE, latLng.longitude);
                                startActivity(intentEventList);
                                return;
                            default:
                                Log.d(TAG, "nothing to do");
                                return;
                        }
                    }
                }).setCancelable(true).create().show();

    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        service = NSService.getInstance(getActivity());
        idsAlreadyIn = new HashSet<Integer>();
        tweetsAlreadyIn = new HashSet<>();
        mAuth = FirebaseAuth.getInstance();
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
            addToClusterEvent(initialEvents);
        }

        // set initial position
        if (initialPositionSet) {
            moveCamera(initialPosition, true);
        } else if (initialEventsSet) {
            centerCameraToContainAllEvents(initialEvents, true);
        } else {
            moveCamera(defaultPosition, true);
        }

        // set long click listener
        googleMap.setOnMapLongClickListener(this);
    }

    /**
     * Implementation of {@link ClusterItem} in order to display {@link Event} marker on the map
     */
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

        public EventClusterItem() {
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

    /**
     * Tweet marker to be shown
     */
    public class TweetEvent extends EventClusterItem {

        private Tweet tweet;

        public TweetEvent(Tweet tweet) {
            super();
            if (tweet.coordinates != null) {
                super.mPosition = new LatLng(tweet.coordinates.getLatitude(), tweet.coordinates.getLongitude());
            } else if (tweet.place != null) {
                List<LatLng> coordinates = new ArrayList<>();
                List<List<Double>> polygon = tweet.place.boundingBox.coordinates.get(0);
                for(List<Double> point: polygon){
                    coordinates.add(new LatLng(point.get(1), point.get(0)));
                }
                super.mPosition = getCentroid(coordinates);

                Log.d(TAG, super.mPosition.toString());
            }
            this.tweet = tweet;

        }

        private LatLng getCentroid(List<LatLng> coords){
            double centroidLat = 0, centroidLon = 0;
            for(LatLng coord: coords){
                centroidLat+=coord.latitude;
                centroidLon+=coord.longitude;
            }
            return new LatLng(centroidLat/coords.size(), centroidLon/coords.size());
        }

        public Tweet getTweet() {
            return tweet;
        }

        public void setTweet(Tweet tweet) {
            this.tweet = tweet;
        }
    }

    /**
     * Extension of {@link DefaultClusterRenderer} in order to customize the shown markers
     */
    private class EventClusterRenderer extends DefaultClusterRenderer<EventClusterItem> {

        /**
         * Creator
         */
        public EventClusterRenderer() {
            super(getApplicationContext(), currentMap, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(EventClusterItem item, MarkerOptions markerOptions) {
            if (item instanceof TweetEvent) {
                // display tweet
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_twitter_minimal));
            } else {
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

    public void showTweet(Tweet tweet) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(FRAGMENT_NAME_TWITTER);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        TwitterFragment newFragment = new TwitterFragment();
        newFragment.addTweet(tweet);
        newFragment.show(ft, FRAGMENT_NAME_TWITTER);
    }

}
