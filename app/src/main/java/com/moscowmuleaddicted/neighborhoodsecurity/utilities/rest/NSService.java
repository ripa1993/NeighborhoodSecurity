package com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
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
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.AuthToken;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventType;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Subscription;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.User;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.internal.zzt.TAG;

/**
 * Created by Simone Ripamonti on 12/04/2017.
 */

public class NSService {

    private static final String baseUrl = "thawing-taiga-87659.herokuapp.com";
    private FirebaseAuth mAuth;
    private static AuthToken authToken;
    private static NSService instance;
    private static NSRestService restInterface;
    private static Converter<ResponseBody, MyMessage> converter;

    private NSService(Context context) {

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new HeaderRequestInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://thawing-taiga-87659.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        restInterface = retrofit.create(NSRestService.class);

        converter = retrofit.responseBodyConverter(MyMessage.class, new Annotation[0]);

        mAuth = FirebaseAuth.getInstance();

    }

    public static synchronized NSService getInstance(Context context) {
        if (instance == null) {
            instance = new NSService(context);
        }
        return instance;
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
    public void getEventsByArea(Double latitudeMin, Double latitudeMax, Double longitudeMin, Double longitudeMax, final MyCallback<List<Event>> callback) {
        restInterface.getEventsByArea(latitudeMin, latitudeMax, longitudeMin, longitudeMax).enqueue(new retrofit2.Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                logResponse(response);

                if (response.isSuccessful()) {
                    List<Event> eventList = response.body();
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
                System.err.println(t.getMessage());
                callback.onFailure();
            }

        });
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
    public void getEventsByRadius(Double latitude, Double longitude, int radius, final MyCallback<List<Event>> callback) {
        restInterface.getEventsByRadius(latitude, longitude, radius).enqueue(new retrofit2.Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                logResponse(response);

                if (response.isSuccessful()) {
                    List<Event> eventList = response.body();
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
                System.err.println(t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Retrieves an event given the id
     * GET /events/{id}
     *
     * @param id
     * @param callback onEventLoad if 200 OK,
     *                 onMessageLoad if 400 BAD REQUEST or 404 NOT FOUND or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public void getEventById(int id, final MyCallback<Event> callback) {
        Log.i(TAG, "getEventById: querying for event " + id);
        restInterface.getEventById(id).enqueue(new retrofit2.Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {

                logResponse(response);

                if (response.isSuccessful()) {
                    Event event = response.body();
                    callback.onSuccess(event);
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
            public void onFailure(Call<Event> call, Throwable t) {
                System.err.println(t.getMessage());
                callback.onFailure();
            }
        });
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
                System.err.println(t.getMessage());
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
                System.err.println(t.getMessage());
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
    public void deleteEvent(int id, final MyCallback<String> callback) {
        restInterface.deleteEvent(id).enqueue(new retrofit2.Callback<MyMessage>() {

            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);

                if (response.isSuccessful()) {
                    callback.onSuccess("ok");
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
                System.err.println(t.getMessage());
                callback.onFailure();
            }
        });
    }

    /**
     * Vote an event using its id
     * POST /events/{id}/vote
     *
     * @param id
     * @param callback onSuccess if 204 NO CONTENT (idempotent),
     *                 onMessageLoad if 400 BAD REQUEST or 401 UNAUTHORIZED or 404 NOT FOUND or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public void voteEvent(int id, final MyCallback<String> callback) {
        restInterface.voteEvent(id).enqueue(new retrofit2.Callback<MyMessage>() {
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);

                if (response.isSuccessful()) {
                    callback.onSuccess("ok");
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
                System.err.println(t.getMessage());
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
    public void unvoteEvent(int id, final MyCallback<String> callback) {
        restInterface.unvoteEvent(id).enqueue(new retrofit2.Callback<MyMessage>() {
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);

                if (response.isSuccessful()) {
                    callback.onSuccess("ok");
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
                System.err.println(t.getMessage());
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
                System.err.println(t.getMessage());
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
    public void getEventsByUser(String id, final MyCallback<List<Event>> callback) {
        restInterface.getEventByUser(id).enqueue(new retrofit2.Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                logResponse(response);

                if (response.isSuccessful()) {
                    List<Event> eventList = response.body();
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
                System.err.println(t.getMessage());
                callback.onFailure();
            }
        });
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

;
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
                            Log.w(TAG, "signInWithEmail: fcm registration ("+status+") ["+message.getArgument()+"] "+message.getMessage());
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
        callback.onSuccess("ok");
    }

    /**
     * Gets the subscription of the user
     * GET /subscriptions/{id}
     *
     * @param id
     * @param callback
     */
    public void getSubscriptionsByUser(String id, final MyCallback<List<Subscription>> callback) {
        restInterface.getSubscriptionsByUser(id).enqueue(new retrofit2.Callback<List<Subscription>>() {
            @Override
            public void onResponse(Call<List<Subscription>> call, Response<List<Subscription>> response) {
                logResponse(response);


                if (response.isSuccessful()) {
                    List<Subscription> subs = response.body();
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
                System.err.println(t.getMessage());
                callback.onFailure();
            }
        });
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
                System.err.println(t.getMessage());

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
                System.err.println(t.getMessage());
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
    public void deleteSubscriptionById(int id, final MyCallback<MyMessage> callback) {
        restInterface.deleteSubscriptionById(id).enqueue(new retrofit2.Callback<MyMessage>() {
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
                System.err.println(t.getMessage());
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
                System.err.println(t.getMessage());
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
     * @param callback
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
                System.err.println(t.getMessage());
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
                System.err.println(t.getMessage());
                callback.onFailure();
            }
        });
    }

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

    public static interface MyCallback<T> extends CallbackMessage {
        public void onSuccess(T t);
    }

    public static interface MySimpleCallback{
        public void onSuccess(String s);
        public void onFailure(String s);
    }

    private void logResponse(Response<?> response) {
        System.out.println("Response: " + response.message());
        System.out.println("Content: " + response.raw());
        Map<String, List<String>> map = response.headers().toMultimap();
        for (String s : map.keySet()
                ) {
            System.out.println("Headers: " + s + " - " + map.get(s));
        }
    }

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


}
