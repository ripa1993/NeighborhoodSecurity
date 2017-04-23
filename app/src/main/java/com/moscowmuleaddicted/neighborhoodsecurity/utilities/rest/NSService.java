package com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.AuthToken;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventType;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.User;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

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
     *
     * @param latitudeMin
     * @param latitudeMax
     * @param longitudeMin
     * @param longitudeMax
     * @param callback     onEventListLoad if 200 OK,
     *                     onMessageLoad if 400 BAD REQUEST or 500 INTERNAL SERVER ERROR,
     *                     onFailure if exception
     */
    public void getEventsByArea(float latitudeMin, float latitudeMax, float longitudeMin, float longitudeMax, final MyCallback<List<Event>> callback) {
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
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @param callback  onEventListLoad if 200 OK,
     *                  onMessageLoad if 400 BAD REQUEST or 500 INTERNAL SERVER ERROR,
     *                  onFailure if exception
     */
    public void getEventsByRadius(float latitude, float longitude, float radius, final MyCallback<List<Event>> callback) {
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
     *
     * @param id
     * @param callback onEventLoad if 200 OK,
     *                 onMessageLoad if 400 BAD REQUEST or 404 NOT FOUND or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public void getEventById(int id, final MyCallback<Event> callback) {
        Log.i(TAG, "getEventById: querying for event "+id);
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
        restInterface.postEventWithAddress(eventType, description, country, city, street).enqueue(new retrofit2.Callback<MyMessage>() {
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
     *
     * @param eventType
     * @param description
     * @param latitude
     * @param longitude
     * @param callback    onSuccess if 201 CREATED,
     *                    onMessageLoad if 400 BAD REQUEST or 401 UNAUTHORIZED or 500 INTERNAL SERVER ERROR,
     *                    onFailure if exception
     */
    public void postEventWithCoordinates(EventType eventType, String description, float latitude, float longitude, final MyCallback<String> callback) {
        restInterface.postEventWithCoordinates(eventType, description, latitude, longitude).enqueue(new retrofit2.Callback<MyMessage>() {
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
                if(task.isSuccessful()){
                    Log.d(TAG, "signUpWithEmail: success");
                    FirebaseUser user = task.getResult().getUser();
                    UserProfileChangeRequest upcr = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                    user.updateProfile(upcr).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
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
                            Log.w(TAG, "signUpWithEmail: ("+status+") "+message);
                        }
                    });
                } else {
                    Log.w(TAG, "signUpWithEmail:failure", task.getException());
                    callback.onFailure();
                }
            }
        });

    }

    /**
     * Perform login using username and password
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
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
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

    public static interface MyCallback<T> extends CallbackMessage{
        public void onSuccess(T t);
    }

    private void logResponse(Response<?> response) {
        System.out.println("Response: " + response.message());
        System.out.println("Content: " + response.raw());
        Map<String, List<String>> map = response.headers().toMultimap();
        for (String s: map.keySet()
                ) {
            System.out.println("Headers: " +s+" - "+map.get(s));
        }
    }

    private void postUser(String id, String name, String email, final MyCallback<String> callback){
        restInterface.postUser(id, name, email).enqueue( new retrofit2.Callback<MyMessage>(){

            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                logResponse(response);
                if(response.isSuccessful()){
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
