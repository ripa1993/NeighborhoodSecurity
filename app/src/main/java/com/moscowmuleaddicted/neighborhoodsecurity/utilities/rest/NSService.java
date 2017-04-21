package com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest;

import android.content.Context;

import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.AuthToken;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventList;
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

/**
 * Created by Simone Ripamonti on 12/04/2017.
 */

public class NSService {

    private static final String baseUrl = "thawing-taiga-87659.herokuapp.com";

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

        removeToken();
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
    public void getEventsByArea(float latitudeMin, float latitudeMax, float longitudeMin, float longitudeMax, final CallbackEventList callback) {
        restInterface.getEventsByArea(latitudeMin, latitudeMax, longitudeMin, longitudeMax).enqueue(new retrofit2.Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                logResponse(response);

                if (response.isSuccessful()) {
                    List<Event> eventList = response.body();
                    callback.onEventListLoad(eventList);
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
    public void getEventsByRadius(float latitude, float longitude, float radius, final CallbackEventList callback) {
        restInterface.getEventsByRadius(latitude, longitude, radius).enqueue(new retrofit2.Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                logResponse(response);

                if (response.isSuccessful()) {
                    List<Event> eventList = response.body();
                    callback.onEventListLoad(eventList);
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
    public void getEventById(int id, final CallbackEvent callback) {
        restInterface.getEventById(id).enqueue(new retrofit2.Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {

                logResponse(response);

                if (response.isSuccessful()) {
                    Event event = response.body();
                    callback.onEventLoad(event);
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
    public void postEventWithAddress(String eventType, String description, String country, String city, String street, final CallbackSuccess callback) {
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
    public void postEventWithCoordinates(String eventType, String description, float latitude, float longitude, final CallbackSuccess callback) {
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
    public void deleteEvent(int id, final CallbackSuccess callback) {
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
    public void voteEvent(int id, final CallbackSuccess callback) {
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
    public void unvoteEvent(int id, final CallbackSuccess callback) {
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
    public void getUserById(int id, final CallbackUser callback) {
        restInterface.getUserById(id).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                logResponse(response);


                if (response.isSuccessful()) {
                    User user = response.body();
                    callback.onUserLoad(user);
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
    public void getEventsByUser(int id, final CallbackEventList callback) {
        restInterface.getEventByUser(id).enqueue(new retrofit2.Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                logResponse(response);

                if (response.isSuccessful()) {
                    List<Event> eventList = response.body();
                    callback.onEventListLoad(eventList);
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
    public void createUserClassic(String username, String email, String password, final CallbackSuccess callback) {
        restInterface.createUserClassic(username, email, password).enqueue(new retrofit2.Callback<MyMessage>() {
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
     * Perform login using username and password
     *
     * @param username
     * @param password
     * @param callback onSuccess if 200 OK,
     *                 onMessageLoad if 401 UNAUHTORIZED or 500 INTERNAL SERVER ERROR,
     *                 onFailure if exception
     */
    public void loginClassic(String username, String password, final CallbackAuthToken callback) {
        restInterface.loginClassic(username, password).enqueue(new retrofit2.Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                logResponse(response);

                if (response.isSuccessful()) {
                    AuthToken authToken = response.body();
                    callback.onAuthTokenLoad(authToken);

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
            public void onFailure(Call<AuthToken> call, Throwable t) {
                System.err.println(t.getMessage());
                callback.onFailure();
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
    public void logout(final CallbackSuccess callback) {
        restInterface.logout().enqueue(new retrofit2.Callback<MyMessage>() {
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

    public static interface CallbackEvent extends CallbackMessage {
        /**
         * service replied with an event
         *
         * @param event
         */
        public void onEventLoad(Event event);
    }

    public static interface CallbackEventList extends CallbackMessage {
        /**
         * service replied with a list of events
         *
         * @param events
         */
        public void onEventListLoad(List<Event> events);
    }

    public static interface CallbackUser extends CallbackMessage {
        /**
         * service replied with a user
         *
         * @param user
         */
        public void onUserLoad(User user);
    }

    public static interface CallbackAuthToken extends CallbackMessage {
        /**
         * service replied with an auth token
         *
         * @param authToken
         */
        public void onAuthTokenLoad(AuthToken authToken);
    }

    public static interface CallbackSuccess extends CallbackMessage {
        /**
         * service replied with a success code
         *
         * @param msg
         */
        public void onSuccess(String msg);
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

    public void setToken(AuthToken authToken){
        NSService.authToken = authToken;
    }

    public void removeToken(){
        NSService.authToken = new AuthToken();
        NSService.authToken.setAuthToken("");
        NSService.authToken.setUserId(0);
        NSService.authToken.setUsername("");
        NSService.authToken.setUserUrl("");
    }

    public AuthToken getAuthToken(){
        return authToken;
    }

}
