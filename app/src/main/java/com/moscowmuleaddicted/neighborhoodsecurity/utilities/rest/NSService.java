package com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest;

import android.content.Context;

import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.AuthToken;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventList;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.User;

import java.lang.annotation.Annotation;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Simone Ripamonti on 12/04/2017.
 */

public class NSService {

    private static final String baseUrl = "thawing-taiga-87659.herokuapp.com";

    private static NSService instance;
    private static NSRestService restInterface;

    private NSService(Context context){

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HeaderRequestInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.omdbapi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        restInterface = retrofit.create(NSRestService.class);

    }

    public static synchronized NSService getInstance(Context context){
        if (instance == null){
            instance = new NSService(context);
        }
        return instance;
    }

    public void getEventsByArea(float latitudeMin, float latitudeMax, float longitudeMin, float longitudeMax, final CallbackEventList callback){
        restInterface.getEventsByArea(latitudeMin, latitudeMax, longitudeMin, longitudeMax).enqueue(new retrofit2.Callback<EventList>(){
            @Override
            public void onResponse(Call<EventList> call, Response<EventList> response) {
                EventList eventList = response.body();
                if(eventList.getArgument().equals("")) {
                    List<Event> events = eventList.getEvents();
                    callback.onEventListLoad(events);
                } else {
                    callback.onMessageLoad(eventList);
                }
            }

            @Override
            public void onFailure(Call<EventList> call, Throwable t) {
                callback.onFailure();
            }

        });
    }

    public void getEventsByRadius(float latitude, float longitude, float radius, final CallbackEventList callback){
        restInterface.getEventsByRadius(latitude, longitude,radius).enqueue(new retrofit2.Callback<EventList>(){
            @Override
            public void onResponse(Call<EventList> call, Response<EventList> response) {
                EventList eventList = response.body();
                if(eventList.getArgument().equals("")) {
                    List<Event> events = eventList.getEvents();
                    callback.onEventListLoad(events);
                } else {
                    callback.onMessageLoad(eventList);
                }
            }

            @Override
            public void onFailure(Call<EventList> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    public void getEventById(int id, final CallbackEvent callback){
        restInterface.getEventById(id).enqueue(new retrofit2.Callback<Event>(){
            @Override
            public void onResponse(Call<Event> call, Response<Event> response){
                Event event = response.body();
                if (event.getArgument().equals("")){
                    callback.onEventLoad(event);
                } else{
                    callback.onMessageLoad(event);
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t){
                callback.onFailure();
            }
        });
    }

    public void postEventWithAddress(String eventType, String description, String country, String city, String street,final CallbackSuccess callback){
        restInterface.postEventWithAddress(eventType, description, country, city, street).enqueue(new retrofit2.Callback<MyMessage>(){
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response){
                if (response.isSuccessful()){
                    callback.onSuccess(response.headers().get("Content-Location"));
                } else {
                    callback.onMessageLoad(response.body());
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t){
                callback.onFailure();
            }
        });
    }

    public void postEventWithCoordinates(String eventType, String description, float latitude, float longitude,final CallbackSuccess callback){
        restInterface.postEventWithCoordinates(eventType, description, latitude, longitude).enqueue(new retrofit2.Callback<MyMessage>(){
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response){
                if (response.isSuccessful()){
                    callback.onSuccess(response.headers().get("Content-Location"));
                } else {
                    callback.onMessageLoad(response.body());
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t){
                callback.onFailure();
            }
        });
    }

    public void deleteEvent(int id,final CallbackSuccess callback){
        restInterface.deleteEvent(id).enqueue(new retrofit2.Callback<MyMessage>(){
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response){
                if (response.isSuccessful()){
                    callback.onSuccess("ok");
                } else {
                    callback.onMessageLoad(response.body());
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t){
                callback.onFailure();
            }
        });
    }

    public void voteEvent(int id,final CallbackSuccess callback){
        restInterface.voteEvent(id).enqueue(new retrofit2.Callback<MyMessage>(){
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response){
                if (response.isSuccessful()){
                    callback.onSuccess("ok");
                } else {
                    callback.onMessageLoad(response.body());
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t){
                callback.onFailure();
            }
        });
    }

    public void unvoteEvent(int id,final CallbackSuccess callback){
        restInterface.unvoteEvent(id).enqueue(new retrofit2.Callback<MyMessage>(){
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response){
                if (response.isSuccessful()){
                    callback.onSuccess("ok");
                } else {
                    callback.onMessageLoad(response.body());
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t){
                callback.onFailure();
            }
        });
    }

    public void getUserById(int id,final CallbackUser callback){
        restInterface.getUserById(id).enqueue(new retrofit2.Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response){
                User user = response.body();
                if(user.getArgument().equals("")){
                    callback.onUserLoad(user);
                } else {
                    callback.onMessageLoad(user);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t){
                callback.onFailure();
            }
        });
    }

    public void getEventsByUser(int id,final CallbackEventList callback){
        restInterface.getEventByUser(id).enqueue(new retrofit2.Callback<EventList>(){
            @Override
            public void onResponse(Call<EventList> call, Response<EventList> response) {
                EventList eventList = response.body();
                if(eventList.getArgument().equals("")) {
                    List<Event> events = eventList.getEvents();
                    callback.onEventListLoad(events);
                } else {
                    callback.onMessageLoad(eventList);
                }
            }

            @Override
            public void onFailure(Call<EventList> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    public void createUserClassic(String username, String email, String password,final CallbackSuccess callback){
        restInterface.createUserClassic(username, email, password).enqueue(new retrofit2.Callback<MyMessage>(){
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response){
                if (response.isSuccessful()){
                    callback.onSuccess(response.headers().get("Content-Location"));
                } else {
                    callback.onMessageLoad(response.body());
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t){
                callback.onFailure();
            }
        });
    }

    public void loginClassic(String username, String password,final CallbackAuthToken callback){
        restInterface.loginClassic(username, password).enqueue(new retrofit2.Callback<AuthToken>(){
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response){
                AuthToken authToken = response.body();
                if(authToken.getArgument().equals("")){
                    callback.onAuthTokenLoad(authToken);
                } else {
                    callback.onMessageLoad(authToken);
                }
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t){
                callback.onFailure();
            }
        });
    }

    public void logout(final CallbackSuccess callback){
        restInterface.logout().enqueue(new retrofit2.Callback<MyMessage>(){
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response){
                if(response.isSuccessful()){
                    callback.onSuccess("ok");
                } else {
                    callback.onMessageLoad(response.body());
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable t){
                callback.onFailure();
            }
        });
    }




    public static interface CallbackMessage{
        public void onFailure();
        public void onMessageLoad(MyMessage message);

    }

    public static interface CallbackEvent extends CallbackMessage{
        public void onEventLoad(Event event);
    }

    public static interface CallbackEventList extends CallbackMessage{
        public void onEventListLoad(List<Event> events);
    }

    public static interface CallbackUser  extends CallbackMessage{
        public void onUserLoad(User user);
    }

    public static interface CallbackAuthToken  extends CallbackMessage{
        public void onAuthTokenLoad(AuthToken authToken);
    }

    public static interface CallbackSuccess extends CallbackMessage{
        public void onSuccess(String msg);
    }


}
