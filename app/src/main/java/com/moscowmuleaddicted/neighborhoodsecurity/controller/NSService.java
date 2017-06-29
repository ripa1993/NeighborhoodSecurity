package com.moscowmuleaddicted.neighborhoodsecurity.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.moscowmuleaddicted.neighborhoodsecurity.controller.db.EventDB;
import com.moscowmuleaddicted.neighborhoodsecurity.controller.db.SubscriptionDB;
import com.moscowmuleaddicted.neighborhoodsecurity.model.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.model.EventType;
import com.moscowmuleaddicted.neighborhoodsecurity.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.model.Subscription;
import com.moscowmuleaddicted.neighborhoodsecurity.model.User;
import com.moscowmuleaddicted.neighborhoodsecurity.controller.rest.HeaderRequestInterceptor;
import com.moscowmuleaddicted.neighborhoodsecurity.controller.rest.NSRestService;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.NS_REST_URL;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.SP_SUBSCRIPTIONS;
import static com.moscowmuleaddicted.neighborhoodsecurity.controller.Constants.SP_VOTED_EVENTS;

/**
 * Class that gives access to all the data available locally and on the remote service.
 * Singleton
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class NSService {
    /**
     * Logger's TAG
     */
    public static final String TAG = "NSService";
    /**
     * The Twitter API client
     */
    private final TwitterApiClient twitterApiClient;
    /**
     * The twitter API search service
     */
    private final SearchService searchService;
    /**
     * Firabase Authentication instance
     */
    private FirebaseAuth mAuth;
    /**
     * Singleton instance
     */
    private static NSService instance;
    /**
     * Interface to Retrofit client
     */
    private static NSRestService restInterface;
    /**
     * Converter for Retrofit
     */
    private static Converter<ResponseBody, MyMessage> converter;
    /**
     * The application context
     */
    private Context context;
    /**
     * Instance of the event DB
     */
    private static EventDB eventDB;
    /**
     * Instance of the subscription DB
     */
    private static SubscriptionDB subscriptionDB;

    /**
     * Private constructor
     * @param mContext
     */
    private NSService(Context mContext) {
        context = mContext;

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new HeaderRequestInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NS_REST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        restInterface = retrofit.create(NSRestService.class);

        converter = retrofit.responseBodyConverter(MyMessage.class, new Annotation[0]);

        mAuth = FirebaseAuth.getInstance();

        eventDB = new EventDB(mContext);
        subscriptionDB = new SubscriptionDB(mContext);

        Twitter.initialize(mContext);
        twitterApiClient = TwitterCore.getInstance().getApiClient();
        searchService = twitterApiClient.getSearchService();
    }

    /**
     * Singleton instance creator
     *
     * @param context
     * @return
     */
    public static synchronized NSService getInstance(Context context) {
        if (instance == null) {
            instance = new NSService(context);
        }
        return instance;
    }

    /**
     * Boots the server by doing a dummy request.
     * This is required since our provider currently shuts down the service due to inactivity
     */
    public void warmUp(){
        restInterface.getEventById(1).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Log.d(TAG, "warmUp: server is ready");
                return;
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.w(TAG, "warmUp: server is still booting");
                return;
            }
        });
    }

    /**
     * Retrieves events using an square area
     * GET /events
     *
     * @param latitudeMin
     * @param latitudeMax
     * @param longitudeMin
     * @param longitudeMax
     * @param callback     onEventListLoad if 200 OK,
     *                     onMessageLoad if 400 BAD REQUEST or 500 INTERNAL SERVER ERROR,
     *                     onFailure if exception
     */
    public List<Event> getEventsByArea(Double latitudeMin, Double latitudeMax, Double longitudeMin, Double longitudeMax, final MyCallback<List<Event>> callback) {
        restInterface.getEventsByArea(latitudeMin, latitudeMax, longitudeMin, longitudeMax).enqueue(new retrofit2.Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                logResponse(response);

                if (response.isSuccessful()) {
                    List<Event> eventList = response.body();

                    Event[] eventArray = new Event[eventList.size()];
                    eventArray = eventList.toArray(eventArray);
                    new StoreEventsTask().execute(eventArray);

                    callback.onSuccess(eventList);
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }

        });
        return eventDB.getByArea(latitudeMin, latitudeMax, longitudeMin, longitudeMax);
    }

    /**
     * Retrieves events using a circular area
     * GET /events
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @param callback  onEventListLoad if 200 OK,
     *                  onMessageLoad if 400 BAD REQUEST or 500 INTERNAL SERVER ERROR,
     *                  onFailure if exception
     */
    public List<Event> getEventsByRadius(Double latitude, Double longitude, int radius, final MyCallback<List<Event>> callback) {
        restInterface.getEventsByRadius(latitude, longitude, radius).enqueue(new retrofit2.Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                logResponse(response);

                if (response.isSuccessful()) {
                    List<Event> eventList = response.body();

                    Event[] eventArray = new Event[eventList.size()];
                    eventArray = eventList.toArray(eventArray);
                    new StoreEventsTask().execute(eventArray);

                    callback.onSuccess(eventList);
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });

        return eventDB.getByRadius(latitude, longitude, radius);
    }

    /**
     * Retrieves an event given the id
     * GET /events/{id}
     *
     * @throws com.moscowmuleaddicted.neighborhoodsecurity.controller.db.EventDB.NoEventFoundException if the event is not available locally
     * @param id
     * @param callback onEventLoad if 200 OK,
     *                 onMessageLoad if 400 BAD REQUEST or 404 NOT FOUND or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public Event getEventById(final int id, final MyCallback<Event> callback) throws EventDB.NoEventFoundException {
        Log.i(TAG, "getEventById: querying for event " + id);
        restInterface.getEventById(id).enqueue(new retrofit2.Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {

                logResponse(response);

                if (response.isSuccessful()) {
                    Event event = response.body();

                    new StoreEventsTask().execute(event);

                    callback.onSuccess(event);
                } else {
                    try {
                        if (response.code() == 404){
                            eventDB.deleteById(id);
                        }
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });


        return eventDB.getById(id);

    }

    /**
     * Post an event given the address
     * POST /events
     *
     * @param eventType
     * @param description
     * @param country
     * @param city
     * @param street
     * @param callback    onSuccess if 201 CREATED,
     *                    onMessageLoad if 400 BAD REQUEST or 401 UNAUTHORIZED or 500 INTERNAL SERVER ERROR,
     *                    onFailure if exception
     */
    public void postEventWithAddress(EventType eventType, String description, String country, String city, String street, final MyCallback<String> callback) {
        restInterface.postEventWithAddress(eventType.toStringNotLocalized(), description, country, city, street).enqueue(new retrofit2.Callback<MyMessage>() {
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {

                logResponse(response);

                if (response.isSuccessful()) {

                    callback.onSuccess(response.headers().get("Content-Location"));
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Post an event given the coordinates
     * POST /events
     *
     * @param eventType
     * @param description
     * @param latitude
     * @param longitude
     * @param callback    onSuccess if 201 CREATED,
     *                    onMessageLoad if 400 BAD REQUEST or 401 UNAUTHORIZED or 500 INTERNAL SERVER ERROR,
     *                    onFailure if exception
     */
    public void postEventWithCoordinates(EventType eventType, String description, Double latitude, Double longitude, final MyCallback<String> callback) {
        restInterface.postEventWithCoordinates(eventType.toStringNotLocalized(), description, latitude, longitude).enqueue(new retrofit2.Callback<MyMessage>() {
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);

                if (response.isSuccessful()) {
                    callback.onSuccess(response.headers().get("location"));
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Deletes an event given the id
     * DELETE /events/{id}
     *
     * @param id
     * @param callback onSuccess if 204 NO CONTENT,
     *                 onMessageLoad if 400 BAD REQUEST or 401 UNAUTHORIZED or 404 NOT FOUND or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public void deleteEvent(final int id, final MyCallback<String> callback) {
        restInterface.deleteEvent(id).enqueue(new retrofit2.Callback<MyMessage>() {

            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);

                if (response.isSuccessful()) {
                    eventDB.deleteById(id);
                    callback.onSuccess("ok");
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        if(response.code() == 404){
                            eventDB.deleteById(id);
                        }
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Vote an event using its id
     * POST /events/{id}/vote
     *
     * @param id
     * @param callback onSuccess if 200 if created
     *                 onMessageLoad if 400 BAD REQUEST or 401 UNAUTHORIZED or 404 NOT FOUND or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public void voteEvent(final int id, final MyCallback<String> callback) {
        restInterface.voteEvent(id).enqueue(new retrofit2.Callback<MyMessage>() {
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);

                if (response.isSuccessful()) {
                    addVoteSharedPreferences(id);
                    if(response.code() == 200){
                        try {
                            eventDB.modifyVote(id, 1);
                        } catch (EventDB.NoEventFoundException e) {
                            // do nothing
                        }
                        callback.onSuccess("ok");
                    } else {
                        if (response.code() == 404){
                            eventDB.deleteById(id);
                        }
                        callback.onMessageLoad(new MyMessage(), 204);
                    }

                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Unvote an event using its id
     * DELETE /events/{id}/vote
     *
     * @param id
     * @param callback onSuccess if 204 NO CONTENT (idempotent),
     *                 onMessageLoad if 400 BAD REQUEST or 401 UNAUTHORIZED or 404 NOT FOUND or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public void unvoteEvent(final int id, final MyCallback<String> callback) {
        restInterface.unvoteEvent(id).enqueue(new retrofit2.Callback<MyMessage>() {
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);

                if (response.isSuccessful()) {
                    removeVoteSharedPreferences(id);
                    if(response.code() == 200){
                        try {
                            eventDB.modifyVote(id, -1);
                        } catch (EventDB.NoEventFoundException e) {
                            // do nothing
                        }
                        callback.onSuccess("ok");
                    } else {
                        if (response.code() == 404){
                            eventDB.deleteById(id);
                        }
                        callback.onMessageLoad(new MyMessage(), 204);
                    }
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Retrieves an user given its id
     * GET /users/{id}
     *
     * @param id
     * @param callback onUserLoad if 200 OK,
     *                 onMessageLoad if 400 BAD REQUEST or 404 NOT FOUND or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public void getUserById(String id, final MyCallback<User> callback) {
        restInterface.getUserById(id).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                logResponse(response);


                if (response.isSuccessful()) {
                    User user = response.body();
                    callback.onSuccess(user);
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Retrieves events of an user given its id
     * GET /users/{id}/events
     *
     * @param id
     * @param callback onEventListLoad if 200 OK,
     *                 onMessageLoad if 400 BAD REQUEST or 404 NOT FOUND or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public List<Event> getEventsByUser(String id, final MyCallback<List<Event>> callback) {
        restInterface.getEventByUser(id).enqueue(new retrofit2.Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                logResponse(response);

                if (response.isSuccessful()) {
                    List<Event> eventList = response.body();

                    Event[] eventArray = new Event[eventList.size()];
                    eventArray = eventList.toArray(eventArray);
                    new StoreEventsTask().execute(eventArray);

                    callback.onSuccess(eventList);
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });

        return eventDB.getByUID(id);
    }

    /**
     * Creates an user account using username, password, email
     *
     * @param username
     * @param email
     * @param password
     * @param callback onSuccess if 201 CREATED,
     *                 onMessageLoad if 400 BAD REQUEST or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public void signUpWithEmail(final String username, final String email, String password, final MyCallback<String> callback) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signUpWithEmail: success");
                    FirebaseUser user = task.getResult().getUser();
                    UserProfileChangeRequest upcr = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                    user.updateProfile(upcr).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signUpWithEmail: profile updated with username");
                            } else {
                                Log.w(TAG, "signUpWithEmail: failure in updating user profile", task.getException());
                            }
                        }
                    });

                    // store user info on remote db
                    postUser(user.getUid(), username, email, new MyCallback<String>() {
                        @Override
                        public void onSuccess(String msg) {
                            Log.d(TAG, "signUpWithEmail: user posted on rest service");
                        }

                        @Override
                        public void onFailure() {
                            Log.w(TAG, "signUpWithEmail: failure in posting user on rest service");
                        }

                        @Override
                        public void onMessageLoad(MyMessage message, int status) {
                            Log.w(TAG, "signUpWithEmail: (" + status + ") " + message);
                        }
                    });
                    callback.onSuccess("success");
                } else {
                    Log.w(TAG, "signUpWithEmail:failure", task.getException());
                    callback.onFailure();
                }
            }
        });

    }

    /**
     * Perform login using username and password
     * FirebaseAuth + POST /users
     *
     * @param email
     * @param password
     * @param callback onSuccess if 200 OK,
     *                 onMessageLoad if 401 UNAUHTORIZED or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public void signInWithEmail(String email, String password, final MyCallback<String> callback) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail: success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    // update fcm, just in case
                    String fcmToken = FirebaseInstanceId.getInstance().getToken();
                    updateFcm(fcmToken, new MyCallback<MyMessage>() {
                        @Override
                        public void onSuccess(MyMessage myMessage) {
                            Log.d(TAG, "signInWithEmail: registered fcm token with server");
                        }

                        @Override
                        public void onFailure() {
                            Log.w(TAG, "signInWithEmail: failed to register fcm token with server");
                        }

                        @Override
                        public void onMessageLoad(MyMessage message, int status) {
                            Log.w(TAG, "signInWithEmail: fcm registration "+message);
                        }
                    });

                    callback.onSuccess(user.getDisplayName() + " " + user.getUid());
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    callback.onFailure();
                }
            }
        });
    }

    /**
     * Logs out, invalidating the auth token
     *
     * @param callback onSuccess if 200 OK,
     *                 onMessageLoad if 401 UNAUHTORIZED or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public void logout(final MyCallback<String> callback) {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        callback.onSuccess("ok");
    }

    /**
     * Gets the subscription of the user
     * GET /subscriptions/{id}
     *
     * @param id
     * @param callback onSuccess if 200,
     *                 onMessageLoad if 404 or 500
     */
    public List<Subscription> getSubscriptionsByUser(String id, final MyCallback<List<Subscription>> callback) {

        restInterface.getSubscriptionsByUser(id).enqueue(new retrofit2.Callback<List<Subscription>>() {
            @Override
            public void onResponse(Call<List<Subscription>> call, Response<List<Subscription>> response) {
                logResponse(response);


                if (response.isSuccessful()) {
                    List<Subscription> subs = response.body();

                    Subscription[] subscriptionArray = new Subscription[subs.size()];
                    subscriptionArray = subs.toArray(subscriptionArray);
                    new StoreSubscriptionsTask().execute(subscriptionArray);

                    callback.onSuccess(subs);
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Subscription>> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });
        Log.d(TAG, "finding data on db");
        return subscriptionDB.getByUID(id);
    }

    /**
     * Updates FCM token of the user
     * PUT /users/fcm
     *
     * @param fcm
     * @param callback
     */
    public void updateFcm(String fcm, final MyCallback<MyMessage> callback) {
        restInterface.updateFcm(fcm).enqueue(new retrofit2.Callback<MyMessage>() {
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);


                if (response.isSuccessful()) {
                    MyMessage msg = response.body();
                    callback.onSuccess(msg);
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }

            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t) {
                Log.w(TAG, t.getMessage());

                callback.onFailure();
            }
        });
    }

    /**
     * Gets details of a subscription given the id
     * GET /subscriptions/{id}
     *
     * @param id
     * @param callback
     */
    public void getSubscriptionById(int id, final MyCallback<Subscription> callback) {
        restInterface.getSubscriptionById(id).enqueue(new retrofit2.Callback<Subscription>() {
            @Override
            public void onResponse(Call<Subscription> call, Response<Subscription> response) {
                logResponse(response);


                if (response.isSuccessful()) {
                    Subscription sub = response.body();

                    new StoreSubscriptionsTask().execute(sub);

                    callback.onSuccess(sub);
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }

            }

            @Override
            public void onFailure(Call<Subscription> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Delete subscription given its id
     * DELETE /subscription/{id}
     *
     * @param id
     * @param callback
     */
    public void deleteSubscriptionById(final int id, final MyCallback<MyMessage> callback) {
        restInterface.deleteSubscriptionById(id).enqueue(new retrofit2.Callback<MyMessage>() {
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);


                if (response.isSuccessful()) {
                    MyMessage msg = response.body();
                    subscriptionDB.deleteById(id);
                    callback.onSuccess(msg);
                } else {
                    try {
                        if (response.code() == 404){
                            subscriptionDB.deleteById(id);
                        }
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }

            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Post subscription using rectangle are
     * POST /subscriptions
     * @param minLat
     * @param maxLat
     * @param minLon
     * @param maxLon
     * @param callback
     */
    public void postSubscriptionArea(Double minLat, Double maxLat, Double minLon, Double maxLon, final MyCallback<MyMessage> callback) {
        restInterface.postSubscriptionArea(minLat, maxLat, minLon, maxLon).enqueue(new retrofit2.Callback<MyMessage>() {
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);


                if (response.isSuccessful()) {
                    MyMessage msg = response.body();
                    callback.onSuccess(msg);
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }

            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Post subscription using center and radius
     * POST /subscriptions
     * @param lat
     * @param lon
     * @param radius
     * @param callback onSuccess if 201,
     *                 onMessageLoad if 400 or 500
     *
     *
     */
    public void postSubscriptionCenterAndRadius(Double lat, Double lon, int radius, final MyCallback<MyMessage> callback){
        restInterface.postSubscriptionCenterAndRadius(lat, lon, radius).enqueue(new retrofit2.Callback<MyMessage>() {
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);


                if (response.isSuccessful()) {
                    MyMessage msg = response.body();
                    callback.onSuccess(msg);
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }

            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Post subscription using addres
     * POST /subscriptions
     * @param country
     * @param city
     * @param street
     * @param radius
     * @param callback
     */
    public void postSubscriptionAddress(String country, String city, String street, int radius, final MyCallback<MyMessage> callback){
        restInterface.postSubscriptionAddress(country,city,street,radius).enqueue(new retrofit2.Callback<MyMessage>(){
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);


                if (response.isSuccessful()) {
                    MyMessage msg = response.body();
                    callback.onSuccess(msg);
                } else {
                    try {
                        MyMessage msg = converter.convert(response.errorBody());
                        callback.onMessageLoad(msg, response.code());
                    } catch (IOException e) {
                        callback.onFailure();
                    }
                }

            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t) {
                Log.w(TAG, t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Asks Firebase Authentication service to send a password reset email to the user's email
     * @param email
     * @param callback
     */
    public void sendPasswordResetEmail(String email, final MySimpleCallback callback){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "sendPasswordResetEmail: success");
                    callback.onSuccess("success");
                } else {
                    Log.w(TAG, "sendPasswordResetEmail: failure");
                    callback.onFailure(task.getException().getMessage());
                }

            }
        });
    }

    /**
     * Try to login using Google credentials to the Firebase Authentication service
     * @param acct
     * @param callback
     */
    public void signInWithGoogle(GoogleSignInAccount acct, final MySimpleCallback callback){
        Log.d(TAG, "signInWithGoogle: " + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential: success");

                            final String id = mAuth.getCurrentUser().getUid();
                            final String email = mAuth.getCurrentUser().getEmail();
                            final String name = mAuth.getCurrentUser().getDisplayName();
                            final String fcmToken = FirebaseInstanceId.getInstance().getToken();

                            getUserById(id, new MyCallback<User>() {
                                @Override
                                public void onSuccess(User user) {
                                    // user already exists in the rest db, simply update the fcm token

                                    updateFcm(fcmToken, new MyCallback<MyMessage>() {
                                        @Override
                                        public void onSuccess(MyMessage myMessage) {
                                            Log.d(TAG, "signInWithGoogle: registered fcm token with server");
                                        }

                                        @Override
                                        public void onFailure() {
                                            Log.w(TAG, "signInWithGoogle: failed to register fcm token with server");
                                        }

                                        @Override
                                        public void onMessageLoad(MyMessage message, int status) {
                                            Log.w(TAG, "signInWithGoogle: fcm registration ("+status+") ["+message.getArgument()+"] "+message.getMessage());
                                        }
                                    });

                                }

                                @Override
                                public void onFailure() {
                                    // post profile on REST user db and update fcm token
                                    postUser(id, name, email, new MyCallback<String>() {
                                        @Override
                                        public void onSuccess(String msg) {
                                            Log.d(TAG, "signInWithGoogle: user posted on rest service");

                                            updateFcm(fcmToken, new MyCallback<MyMessage>() {
                                                @Override
                                                public void onSuccess(MyMessage myMessage) {
                                                    Log.d(TAG, "signInWithGoogle: registered fcm token with server");
                                                }

                                                @Override
                                                public void onFailure() {
                                                    Log.w(TAG, "signInWithGoogle: failed to register fcm token with server");
                                                }

                                                @Override
                                                public void onMessageLoad(MyMessage message, int status) {
                                                    Log.w(TAG, "signInWithGoogle: fcm registration ("+status+") ["+message.getArgument()+"] "+message.getMessage());
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure() {
                                            Log.w(TAG, "signInWithGoogle: failure in posting user on rest service");
                                        }

                                        @Override
                                        public void onMessageLoad(MyMessage message, int status) {
                                            Log.w(TAG, "signInWithGoogle: (" + status + ") " + message);
                                        }
                                    });
                                }

                                @Override
                                public void onMessageLoad(MyMessage message, int status) {
                                    // try anyway
                                    onFailure();
                                }
                            });
                            callback.onSuccess("success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential: failure", task.getException());
                            callback.onFailure("failure");

                        }
                    }
                });

    }

    /**
     * Try to login using Facebook credentials to the Firebase Authentication service
     * @param token
     * @param callback
     */
    public void signInWithFacebook(AccessToken token, final MySimpleCallback callback){
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential: success");

                            final String id = mAuth.getCurrentUser().getUid();
                            final String email = mAuth.getCurrentUser().getEmail();
                            final String name = mAuth.getCurrentUser().getDisplayName();
                            final String fcmToken = FirebaseInstanceId.getInstance().getToken();

                            getUserById(id, new MyCallback<User>() {
                                @Override
                                public void onSuccess(User user) {
                                    // user already exists in the rest db

                                    updateFcm(fcmToken, new MyCallback<MyMessage>() {
                                        @Override
                                        public void onSuccess(MyMessage myMessage) {
                                            Log.d(TAG, "signInWithFacebook: registered fcm token with server");
                                        }

                                        @Override
                                        public void onFailure() {
                                            Log.w(TAG, "signInWithFacebook: failed to register fcm token with server");
                                        }

                                        @Override
                                        public void onMessageLoad(MyMessage message, int status) {
                                            Log.w(TAG, "signInWithFacebook: fcm registration ("+status+") ["+message.getArgument()+"] "+message.getMessage());
                                        }
                                    });

                                }

                                @Override
                                public void onFailure() {
                                    // update profile on REST user db
                                    postUser(id, name, email, new MyCallback<String>() {
                                        @Override
                                        public void onSuccess(String msg) {
                                            Log.d(TAG, "signInWithFacebook: user posted on rest service");
                                            updateFcm(fcmToken, new MyCallback<MyMessage>() {
                                                @Override
                                                public void onSuccess(MyMessage myMessage) {
                                                    Log.d(TAG, "signInWithFacebook: registered fcm token with server");
                                                }

                                                @Override
                                                public void onFailure() {
                                                    Log.w(TAG, "signInWithFacebook: failed to register fcm token with server");
                                                }

                                                @Override
                                                public void onMessageLoad(MyMessage message, int status) {
                                                    Log.w(TAG, "signInWithFacebook: fcm registration ("+status+") ["+message.getArgument()+"] "+message.getMessage());
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure() {
                                            Log.w(TAG, "signInWithFacebook: failure in posting user on rest service");
                                        }

                                        @Override
                                        public void onMessageLoad(MyMessage message, int status) {
                                            Log.w(TAG, "signInWithFacebook: (" + status + ") " + message);
                                        }
                                    });
                                }

                                @Override
                                public void onMessageLoad(MyMessage message, int status) {
                                    // try anyway
                                    onFailure();
                                }
                            });
                            callback.onSuccess("success");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential: failure", task.getException());
                            callback.onFailure("failure");

                        }
                    }
                });
    }

    /**
     * Gets the tweets related to the application given a location
     * @param latitude
     * @param longitude
     * @param callback
     */
    public void getTweetsByCoordinates(double latitude, double longitude, final TwitterCallback callback){
        Log.d(TAG, "performing tweet search");
        searchService.tweets("%23neighborhoodsecurity", new Geocode(latitude, longitude, 2, Geocode.Distance.KILOMETERS),
                null, null, "mixed", null, null, null, null, false).enqueue(new com.twitter.sdk.android.core.Callback<Search>() {
            @Override
            public void success(Result<Search> result) {
                callback.onSuccess(result.data.tweets);
            }

            @Override
            public void failure(TwitterException exception) {
                callback.onFailure(exception.getLocalizedMessage());
            }
        });
    }

    /**
     * Callback interface, successfull response is a text message
     */
    public static interface CallbackMessage {
        /**
         * an exception has occurred
         */
        public void onFailure();

        /**
         * service replied with a message
         *
         * @param message
         * @param status  http status code
         */
        public void onMessageLoad(MyMessage message, int status);

    }

    /**
     * Callback interface, successfull response is an object of class T
     * @param <T> type of the response object
     */
    public static interface MyCallback<T> extends CallbackMessage {
        public void onSuccess(T t);
    }

    /**
     * Simple callback containing text messages
     */
    public static interface MySimpleCallback{
        public void onSuccess(String s);
        public void onFailure(String s);
    }

    /**
     * Callback for twitter
     */
    public static interface TwitterCallback{
        public void onSuccess(List<Tweet> tweets);
        public void onFailure(String s);
    }

    /**
     * Logs the response of a Retrofit request
     * @param response
     */
    private void logResponse(Response<?> response) {
        Log.d(TAG, "rest response content: " + response.raw());
    }

    /**
     * Creates a new user on Neighborhood Security webservice
     * @param id uid of the user
     * @param name full name or username
     * @param email
     * @param callback
     */
    private void postUser(String id, String name, String email, final MyCallback<String> callback) {
        // store user in users table
        restInterface.postUser(id, name, email).enqueue(new retrofit2.Callback<MyMessage>() {

            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);
                if (response.isSuccessful()) {
                    callback.onSuccess("ok");
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t) {
                callback.onFailure();
            }
        });


    }

    /**
     * {@link AsyncTask} that is used to store obtained {@link Event} as a response of a Retrofit request
     */
    public class StoreEventsTask extends AsyncTask<Event, Integer, Integer> {

        public static final String TAG = "StoreEventsTask";

        @Override
        protected Integer doInBackground(Event... params) {
            int count = params.length;
            int i;
            for (i = 0; i < count; i++){
                eventDB.addEvent(params[i]);
                publishProgress((int) ((i / (float) count) * 100));
                if (isCancelled()) break;
            }
            return i;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, "storing events: "+progress[0]+"%");
        }

        protected void onPostExecute(Integer result) {
            Log.d(TAG, "finished storing "+result+" events");
        }
    }

    /**
     * {@link AsyncTask} that is used to store obtained {@link Subscription} as a response of a Retrofit request
     */
    public class StoreSubscriptionsTask extends AsyncTask<Subscription, Integer, Integer>{
        public static final String TAG = "StoreSubsTask";

        @Override
        protected Integer doInBackground(Subscription... params) {
            int count = params.length;
            int i;
            SharedPreferences sharedPreferences = context.getSharedPreferences(SP_SUBSCRIPTIONS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (i = 0; i < count; i++){
                subscriptionDB.addSubscription(params[i]);
                if(!sharedPreferences.contains(String.valueOf(params[i].getId()))){
                    editor.putBoolean(String.valueOf(params[i].getId()), true);
                }
                publishProgress((int) ((i / (float) count) * 100));
                if (isCancelled()) {
                    editor.commit();
                    break;
                }
            }
            editor.commit();
            return i;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, "storing subscriptions: "+progress[0]+"%");
        }

        protected void onPostExecute(Integer result) {
            Log.d(TAG, "finished storing "+result+" subscriptions");
        }
    }

    /**
     * Returns the number of stored events in the local db
     * @return
     */
    public int getNumStoredEvents(){
        return eventDB.getCount();
    }

    /**
     * Returns the number of stored subscription by the user in the local db
     * @param uid
     * @return
     */
    public int getNumStoredSubscriptions(String uid){
        return subscriptionDB.getCountByUid(uid);
    }

    /**
     * Returns the number of received notifications by the user
     * @param uid
     * @return
     */
    public int getNumReceivedNotifications(String uid) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SP_NOTIFICATION_COUNT_BY_UID, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(uid, 0);
    }

    /**
     * Store in a shared preference that the user has voted a particular event
     * @param eventId
     */
    private void addVoteSharedPreferences(int eventId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_VOTED_EVENTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(String.valueOf(eventId), true);
        editor.commit();
    }

    /**
     * Store in a shared preference that the user has voted a particular event
     * @param eventId
     */
    private void removeVoteSharedPreferences(int eventId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_VOTED_EVENTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(String.valueOf(eventId));
        editor.commit();
    }

}
