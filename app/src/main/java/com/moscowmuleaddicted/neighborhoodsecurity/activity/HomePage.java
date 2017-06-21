package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest.NSService;
import com.ogaclejapan.arclayout.ArcLayout;

import java.util.ArrayList;
import java.util.List;

import me.toptas.fancyshowcase.DismissListener;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.DEMO_ACCOUNT_ID;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_LATITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_LONGITUDE;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_UID;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.RC_AUTHENTICATION;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.RC_PERMISSION_POSITION;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.SHOWCASE_HOME;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.SP_SHOWCASE;

/**
 * Homepage that is the initial point of interaction between user and application
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class HomePage extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, OnRequestPermissionsResultCallback, GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener {
    /**
     * Logger's TAG
     */
    public static final String TAG = "HomePageAct";
    /**
     * Button that represents map function
     */
    private Button bMap;
    /**
     * Button that represents user's subscriptions
     */
    private Button bSubscriptions;
    /**
     * Imageview that represents user's event summary
     */
    private ImageView mEventSummary;
    /**
     * Imageview that represents user's subscription summary
     */
    private ImageView mSubscriptionSummary;
    /**
     * Imageview that represents application intro
     */
    private ImageView mHelp;
    /**
     * Side drawer menu
     */
    private Drawer mDrawer;
    /**
     * Account header in drawer
     */
    private AccountHeader mAccountHeader;
    /**
     * Logout item in drawer
     */
    private PrimaryDrawerItem mLogoutDrawerItem;
    /**
     * Authentication item in drawer
     */
    private PrimaryDrawerItem mAuthDrawerItem;
    /**
     * User's submitted event in drawer
     */
    private PrimaryDrawerItem mUserEventDrawerItem;
    /**
     * New event link in drawer
     */
    private PrimaryDrawerItem mNewEventDrawerItem;
    /**
     * New subscription link in drawer
     */
    private PrimaryDrawerItem mNewSubscriptionDrawerItem;
    /**
     * Arc layout that contains the three imageviews: mEventSummary, mSubscriptionSummary and mHelp
     */
    private ArcLayout mArcLayout;
    /**
     * Google api client, used to access location provider
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Firebase auth instance, used to decide wheter a user is logged in or not
     */
    private FirebaseAuth mAuth;
    /**
     * Device last known location
     */
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);

        bMap = (Button) findViewById(R.id.button_map);
        bSubscriptions = (Button) findViewById(R.id.button_subscriptions);

        bMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mapIntent = new Intent(HomePage.this, MapActivity.class);

                if (mLastLocation != null) {
                    // start with last known position
                    Log.d(TAG, "starting map activity with current position");
                    mapIntent.putExtra(IE_LATITUDE, mLastLocation.getLatitude());
                    mapIntent.putExtra(IE_LONGITUDE, mLastLocation.getLongitude());
                } else {
                    // start without last known position
                    Log.d(TAG, "starting map activity without current position");
                }

                startActivity(mapIntent);

            }
        });

        bSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    Intent subscriptionIntent = new Intent(getApplicationContext(), SubscriptionListActivity.class);
                    subscriptionIntent.putExtra(IE_UID, mAuth.getCurrentUser().getUid());
                    startActivity(subscriptionIntent);
                } else {
                    Log.d(TAG, "user is not logged in, this is required when accessing subscription list!");
                    Toast.makeText(getApplicationContext(), getString(R.string.login_required_toast), Toast.LENGTH_LONG).show();
                }
            }
        });

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });


        AccountHeaderBuilder mHeaderBuilder = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.account_header)
                .withAlternativeProfileHeaderSwitching(false)
                .withSelectionListEnabled(false);

        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "initializing profile: mail=" + mAuth.getCurrentUser().getEmail() + ", name=" + mAuth.getCurrentUser().getDisplayName() + ", photo=" + mAuth.getCurrentUser().getPhotoUrl());
            ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem()
                    .withEmail(mAuth.getCurrentUser().getEmail())
                    .withName(mAuth.getCurrentUser().getDisplayName());
            if (mAuth.getCurrentUser().getPhotoUrl() != null) {
                profileDrawerItem.withIcon(mAuth.getCurrentUser().getPhotoUrl());
            }
            mHeaderBuilder.addProfiles(profileDrawerItem);

        }


        mAccountHeader = mHeaderBuilder.build();


        mAuthDrawerItem = new PrimaryDrawerItem()
                .withIdentifier(1000)
                .withName(R.string.drawer_login_register)
                .withSelectable(false)
                .withIcon(R.drawable.ic_login)
                .withIconColorRes(R.color.drawer_icon)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent authIntent = new Intent(HomePage.this, AuthenticationActivity.class);
                        startActivityForResult(authIntent, RC_AUTHENTICATION);
                        return false;
                    }
                });

        mLogoutDrawerItem = new PrimaryDrawerItem()
                .withIdentifier(1001)
                .withName(R.string.drawer_logout)
                .withSelectable(false)
                .withIcon(R.drawable.ic_exit)
                .withIconColorRes(R.color.drawer_icon)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        NSService.getInstance(getApplicationContext()).logout(new NSService.MyCallback<String>() {
                            @Override
                            public void onSuccess(String s) {
                                Log.d(TAG, "logged out");
                                Toast.makeText(getApplicationContext(), R.string.drawer_logout_ok, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Log.w(TAG, "logout failed without msg");
                                Toast.makeText(getApplicationContext(), R.string.drawer_logout_fail, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onMessageLoad(MyMessage message, int status) {
                                // should never happen
                                Log.w(TAG, "logout "+status+": "+message);
                                Toast.makeText(getApplicationContext(), R.string.drawer_logout_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return false;
                    }
                });

        mNewEventDrawerItem = new PrimaryDrawerItem()
                .withIdentifier(2000)
                .withName(R.string.drawer_new_event)
                .withSelectable(false)
                .withIcon(R.drawable.alert_decagram)
                .withIconColorRes(R.color.drawer_icon)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent intentNewEvent = new Intent(HomePage.this, EventCreateActivity.class);
                        startActivity(intentNewEvent);
                        return false;
                    }
                });
        mNewSubscriptionDrawerItem = new PrimaryDrawerItem()
                .withIdentifier(2001)
                .withName(R.string.drawer_new_subscription)
                .withSelectable(false)
                .withIcon(R.drawable.bell_ring)
                .withIconColorRes(R.color.drawer_icon)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent intentNewSubscription = new Intent(HomePage.this, SubscriptionCreateActivity.class);
                        startActivity(intentNewSubscription);
                        return false;
                    }
                });

        mUserEventDrawerItem = new PrimaryDrawerItem()
                .withIdentifier(2002)
                .withName(R.string.drawer_my_events)
                .withSelectable(false)
                .withIcon(R.drawable.ic_account_circle)
                .withIconColorRes(R.color.drawer_icon)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent intentMyEvents = new Intent(HomePage.this, EventListActivity.class);
                        intentMyEvents.putExtra(IE_UID, mAuth.getCurrentUser().getUid());
                        startActivity(intentMyEvents);
                        return false;
                    }
                });


        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withDrawerLayout(R.layout.material_drawer_fits_not)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(false)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(mAccountHeader)
                .addDrawerItems(
                        mNewEventDrawerItem,
                        mNewSubscriptionDrawerItem,
                        mUserEventDrawerItem
                )
                .withSelectedItemByPosition(-1)
                .build();

        if (mAuth.getCurrentUser() == null) {
            updateDrawerLoggedOut();
        } else {
            updateDrawerLoggedIn();
        }


        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer.getDrawerLayout(), R.string.open, R.string.close);
        mDrawer.setActionBarDrawerToggle(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        mArcLayout = (ArcLayout) findViewById(R.id.arc_layout);
        mArcLayout.setVisibility(View.VISIBLE);
        List<Animator> animList = new ArrayList<>();
        for (int i = 0, len = mArcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(mArcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(1000);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.playTogether(animList);
        animSet.start();

        mEventSummary = (ImageView) findViewById(R.id.icon_burglar);
        mSubscriptionSummary = (ImageView) findViewById(R.id.icon_bell);
        mHelp = (ImageView) findViewById(R.id.icon_question);

        final AlertDialog ad = new AlertDialog.Builder(this)
                .setMessage("Msg")
                .setTitle("Title")
                .setCancelable(true)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        final AlertDialog adHelp = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.help_ns))
                .setTitle(getString(R.string.help))
                .setIcon(R.drawable.ic_005_question)
                .setCancelable(true)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Showcase", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runShowcase();
                    }
                })
                .create();

        mEventSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.setTitle(getString(R.string.event_summary));
                ad.setIcon(R.drawable.ic_007_burglar);
                int eventCount = NSService.getInstance(getApplicationContext()).getNumStoredEvents();
                ad.setMessage(String.format(getString(R.string.event_summary_text), eventCount));
                ad.show();
            }
        });

        mSubscriptionSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.setTitle(getString(R.string.subscription_summary));
                ad.setIcon(R.drawable.ic_006_school_bell);
                if (mAuth.getCurrentUser() != null) {
                    String uid = mAuth.getCurrentUser().getUid();
                    int subscriptionCount = NSService.getInstance(getApplicationContext()).getNumStoredSubscriptions(uid);
                    int notificationCount = NSService.getInstance(getApplicationContext()).getNumReceivedNotifications(uid);
                    ad.setMessage(String.format(getString(R.string.subscription_summary_text), subscriptionCount, notificationCount));
                } else {
                    ad.setMessage(getString(R.string.subscription_summary_error));
                }
                ad.show();
            }
        });

        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adHelp.show();
            }
        });

        // run showcase
        SharedPreferences sharedPreferences = getSharedPreferences(SP_SHOWCASE, MODE_PRIVATE);
        boolean alreadyShowcased = sharedPreferences.getBoolean(SHOWCASE_HOME, false);
        Log.d(TAG, "alreadyShowcased = "+alreadyShowcased);
        if(!alreadyShowcased){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SHOWCASE_HOME, true);
            editor.commit();
            runShowcase();
        }

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: connecting google api client");
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: disconnecting google api client");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // request permissions for accessing location, requires SDK >= 23 (marshmellow)
                Log.d(TAG, "onConnected: prompting user to allow location permissions");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_PERMISSION_POSITION);
            } else {
                Log.w(TAG, "onConnected: SDK version is too low (" + Build.VERSION.SDK_INT + ") to ask permissions at runtime");
                Toast.makeText(getApplicationContext(), "Give location permission to allow application know events around you", Toast.LENGTH_LONG).show();
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
        Log.d(TAG, "onConnectionSuspended: code " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: changing drawer state (open/close)");
                if (mDrawer.isDrawerOpen()) {
                    mDrawer.closeDrawer();
                } else {
                    mDrawer.openDrawer();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        updateDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult request=" + requestCode + " result=" + resultCode);
        if (requestCode == RC_AUTHENTICATION && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult preparing to refresh drawer");
            updateDrawer();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Updates the drawer according to the change in the logged in user
     */
    private void updateDrawer() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (mAccountHeader.getProfiles().size() > 0) {
                // update profile
                mAccountHeader.updateProfile(
                        new ProfileDrawerItem()
                                .withEmail(user.getEmail())
                                .withName(user.getDisplayName())
                                .withIcon(user.getPhotoUrl())
                );
            } else {
                // add new profile
                mAccountHeader.addProfiles(
                        new ProfileDrawerItem()
                                .withEmail(user.getEmail())
                                .withName(user.getDisplayName())
                                .withIcon(user.getPhotoUrl())
                );
            }
            updateDrawerLoggedIn();

        } else {
            if (mAccountHeader.getProfiles().size() > 0) {
                // remove user profile
                mAccountHeader.removeProfile(mAccountHeader.getActiveProfile());
            } else {
                // do nothing
            }
            updateDrawerLoggedOut();
        }
    }

    /**
     * Updates the drawer items when no user is currently logged in
     */
    private void updateDrawerLoggedOut() {
        mDrawer.removeAllStickyFooterItems();
        mDrawer.addStickyFooterItem(mAuthDrawerItem);

        mDrawer.removeAllItems();

        mDrawer.removeHeader();
    }

    /**
     * Updates the drawer items when a user is currently logged in
     */
    private void updateDrawerLoggedIn() {
        mDrawer.removeAllStickyFooterItems();
        mDrawer.addStickyFooterItem(mLogoutDrawerItem);

        mDrawer.removeAllItems();
        mDrawer.addItems(mNewEventDrawerItem, mNewSubscriptionDrawerItem, mUserEventDrawerItem);

        mDrawer.setHeader(mAccountHeader.getView());
    }

    /**
     * Creates an animation for the image views contained in the arc layout.
     * The view will appear rotating from above
     * @param item the view that needs to be animated
     * @return the animation
     */
    private Animator createShowItemAnimator(View item) {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float dx = 0;
        float dy = -1 * metrics.widthPixels;

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                PropertyValuesHolder.ofFloat("rotation", 0f, 720f),
                PropertyValuesHolder.ofFloat("translationX", dx, 0f),
                PropertyValuesHolder.ofFloat("translationY", dy, 0f)
        );

        return anim;
    }

    /**
     * Runs showcase to introduce the user to the various functionalities
     */
    private void runShowcase(){

        // showcase home

        FancyShowCaseView showcaseIntro = new FancyShowCaseView.Builder(this)
                .backgroundColor(R.color.colorPrimaryDark)
                .title(getString(R.string.showcase_intro))
                .build();

        FancyShowCaseView showcaseMap = new FancyShowCaseView.Builder(this)
                .focusOn(bMap)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .backgroundColor(R.color.colorPrimaryDark)
                .title(getString(R.string.showcase_map))
                .build();

        FancyShowCaseView showcaseSubscription = new FancyShowCaseView.Builder(this)
                .focusOn(bSubscriptions)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .backgroundColor(R.color.colorPrimaryDark)
                .title(getString(R.string.showcase_subscription))
                .build();

        FancyShowCaseView showcaseArcLayout = new FancyShowCaseView.Builder(this)
                .focusOn(mArcLayout)
                .focusShape(FocusShape.CIRCLE)
                .title(getString(R.string.showcase_stats))
                .backgroundColor(R.color.colorPrimaryDark)
                .titleGravity(Gravity.BOTTOM | Gravity.CENTER)
                .dismissListener(new DismissListener() {
                    @Override
                    public void onDismiss(String id) {
                        // run second part
                        runShowcaseDrawer();
                    }

                    @Override
                    public void onSkipped(String id) {
                        // do nothing
                    }
                })
                .build();

        new FancyShowCaseQueue()
                .add(showcaseIntro)
                .add(showcaseSubscription)
                .add(showcaseMap)
                .add(showcaseArcLayout)
                .show();

    }

    /**
     * Runs showcase to introduce the user to the various functionalities nested into the drawer
     */
    private void runShowcaseDrawer(){
        final ProfileDrawerItem demoProfile = new ProfileDrawerItem()
                .withEmail("marco.rossi@email.it")
                .withName("Marco Rossi")
                .withIcon(R.drawable.marmotta)
                .withIdentifier(DEMO_ACCOUNT_ID);

        FancyShowCaseView showcaseIntro = new FancyShowCaseView.Builder(this)
                .title(getString(R.string.showcase_drawer_intro))
                .backgroundColor(R.color.colorPrimaryDark)
                .titleGravity(Gravity.BOTTOM | Gravity.CENTER)
                .build();

        FancyShowCaseView showcaseAccount = new FancyShowCaseView.Builder(this)
                .focusOn(mDrawer.getHeader())
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .title(getString(R.string.showcase_drawer_account))
                .backgroundColor(R.color.colorPrimaryDark)
                .build();

        FancyShowCaseView showcaseAuth =  new FancyShowCaseView.Builder(this)
                .focusOn(mDrawer.getStickyFooter())
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .title(getString(R.string.showcase_drawer_createaccount))
                .backgroundColor(R.color.colorPrimaryDark)
                .dismissListener(new DismissListener() {
                    @Override
                    public void onDismiss(String id) {
                        mAccountHeader.removeProfileByIdentifier(DEMO_ACCOUNT_ID);
                        updateDrawer();
                        mDrawer.closeDrawer();
                    }

                    @Override
                    public void onSkipped(String id) {
                        // do nothing
                    }
                })
                .build();

        mAccountHeader.removeProfile(0);
        mAccountHeader.addProfiles(demoProfile);
        updateDrawerLoggedIn();
        mDrawer.removeAllStickyFooterItems();
        mDrawer.addStickyFooterItem(mAuthDrawerItem);
        mDrawer.openDrawer();

        new FancyShowCaseQueue()
                .add(showcaseIntro)
                .add(showcaseAccount)
                .add(showcaseAuth)
                .show();

    }
}
