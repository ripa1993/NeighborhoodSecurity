package com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest;

import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.AuthToken;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventType;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Subscription;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Simone Ripamonti on 12/04/2017.
 */

public interface NSRestService {

    // events

    @GET("events")
    public Call<List<Event>> getEventsByArea(@Query("latMin") float latitutdeMin, @Query("latMax") float latitudeMax,
                                             @Query("lonMin") float longitudeMin, @Query("lonMax") float longitudeMax);

    @GET("events")
    public Call<List<Event>> getEventsByRadius(@Query("lat") float latitude, @Query("lon") float longitude,
                                               @Query("rad") float radius);

    @GET("events/{id}")
    public Call<Event> getEventById(@Path("id") int eventId);

    @FormUrlEncoded
    @POST("events")
    public Call<MyMessage>  postEventWithAddress(@Field("eventType") EventType eventType, @Field("description") String description,
                                                 @Field("country") String country, @Field("city") String city, @Field("street") String street);

    @FormUrlEncoded
    @POST("events")
    public Call<MyMessage> postEventWithCoordinates(@Field("eventType") EventType eventType, @Field("description") String description,
                                                    @Field("latitude") float latitude, @Field("longitude") float longitude);

    @DELETE("events/{id}")
    public Call<MyMessage> deleteEvent(@Path("id") int eventId);

    // votes

    @POST("events/{id}/vote")
    public Call<MyMessage> voteEvent(@Path("id") int eventId);

    @DELETE("events/{id}/vote")
    public Call<MyMessage> unvoteEvent(@Path("id") int eventId);

    // users

    @GET("users/{id}")
    public Call<User> getUserById(@Path("id") String userId);

    @GET("users/{id}/events")
    public Call<List<Event>> getEventByUser(@Path("id") String userId);

    @FormUrlEncoded
    @POST("users")
    public Call<MyMessage> postUser(@Field("id") String id, @Field("name") String name, @Field("email") String email);

    @GET("users/{id}/subscriptions")
    public Call<List<Subscription>> getSubscriptionsByUser(@Path("id") String userId);

    @PUT("users/fcm")
    @FormUrlEncoded
    public Call<MyMessage> updateFcm(@Field("fcm") String fcm);

    // subscriptions

    @GET("subscriptions/{id}")
    public Call<Subscription> getSubscriptionById(@Path("id") int id);

    @DELETE("subscriptions/{id}")
    public Call<MyMessage> deleteSubscriptionById(@Path("id") int id);

    @POST("subscriptions")
    @FormUrlEncoded
    public Call<Subscription> postSubscriptionArea(@Field("minLat") float minLat,
                                                   @Field("maxLat") float maxLat,
                                                   @Field("minLon") float minLon,
                                                   @Field("maxLon") float maxLon);

    @POST("subscriptions")
    @FormUrlEncoded
    public Call<Subscription> postSubscriptionCenterAndRadius(@Field("lat") float lat,
                                                                @Field("lon") float lon,
                                                                @Field("radius") int radius);

    @POST("subscriptions")
    @FormUrlEncoded
    public Call<Subscription> postSubscriptionAddress(@Field("country") String country,
                                                      @Field("city") String city,
                                                      @Field("street") String street,
                                                      @Field("radius") int radius);


}
