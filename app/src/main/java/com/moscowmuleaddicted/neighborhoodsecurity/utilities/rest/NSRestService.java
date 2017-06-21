package com.moscowmuleaddicted.neighborhoodsecurity.utilities.rest;

import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Event;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.MyMessage;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Subscription;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.User;

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
 * Interface used by Retrofit to interact with Neighborhood Security Rest webservice
 *
 * @author Simone Ripamonti
 * @version 1
 */

public interface NSRestService {

    /**
     * Gets list of {@link Event} in the specified rectangle area
     * @param latitutdeMin
     * @param latitudeMax
     * @param longitudeMin
     * @param longitudeMax
     * @return
     */
    @GET("events")
    public Call<List<Event>> getEventsByArea(@Query("latMin") Double latitutdeMin, @Query("latMax") Double latitudeMax,
                                             @Query("lonMin") Double longitudeMin, @Query("lonMax") Double longitudeMax);

    /**
     * Get list of {@link Event} in the specied circular area
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     */
    @GET("events")
    public Call<List<Event>> getEventsByRadius(@Query("lat") Double latitude, @Query("lon") Double longitude,
                                               @Query("rad") int radius);

    /**
     * Get the {@link Event} with the specified id
     * @param eventId
     * @return
     */
    @GET("events/{id}")
    public Call<Event> getEventById(@Path("id") int eventId);

    /**
     * Creates a new {@link Event} with the specified address
     * @param eventType
     * @param description
     * @param country
     * @param city
     * @param street
     * @return
     */
    @FormUrlEncoded
    @POST("events")
    public Call<MyMessage>  postEventWithAddress(@Field("eventType") String eventType, @Field("description") String description,
                                                 @Field("country") String country, @Field("city") String city, @Field("street") String street);

    /**
     * Creates a new {@link Event} with the specified coordinates
     * @param eventType
     * @param description
     * @param latitude
     * @param longitude
     * @return
     */
    @FormUrlEncoded
    @POST("events")
    public Call<MyMessage> postEventWithCoordinates(@Field("eventType") String eventType, @Field("description") String description,
                                                    @Field("latitude") Double latitude, @Field("longitude") Double longitude);

    /**
     * Deletes the {@link Event} with the specified di
     * @param eventId
     * @return
     */
    @DELETE("events/{id}")
    public Call<MyMessage> deleteEvent(@Path("id") int eventId);

    /**
     * Adds a vote to the {@link Event} with the specified id
     * @param eventId
     * @return
     */
    @POST("events/{id}/vote")
    public Call<MyMessage> voteEvent(@Path("id") int eventId);

    /**
     * Deletes a vote from the {@link Event} with the specified id
     * @param eventId
     * @return
     */
    @DELETE("events/{id}/vote")
    public Call<MyMessage> unvoteEvent(@Path("id") int eventId);

    /**
     * Gets the {@link User} with the specified id
     * @param userId
     * @return
     */
    @GET("users/{id}")
    public Call<User> getUserById(@Path("id") String userId);

    /**
     * Gets a list of {@link Event} created by the specified user
     * @param userId
     * @return
     */
    @GET("users/{id}/events")
    public Call<List<Event>> getEventByUser(@Path("id") String userId);

    /**
     * Creates a new {@link User}
     * @param id
     * @param name
     * @param email
     * @return
     */
    @FormUrlEncoded
    @POST("users")
    public Call<MyMessage> postUser(@Field("id") String id, @Field("name") String name, @Field("email") String email);

    /**
     * Get a list of {@link Subscription} created by the specified user id
     * @param userId
     * @return
     */
    @GET("users/{id}/subscriptions")
    public Call<List<Subscription>> getSubscriptionsByUser(@Path("id") String userId);

    /**
     * Updates the FCM of the submitting user
     * @param fcm
     * @return
     */
    @PUT("users/fcm")
    @FormUrlEncoded
    public Call<MyMessage> updateFcm(@Field("fcm") String fcm);

    /**
     * Get a {@link Subscription} given the id
     * @param id
     * @return
     */
    @GET("subscriptions/{id}")
    public Call<Subscription> getSubscriptionById(@Path("id") int id);

    /**
     * Deletes a {@link Subscription} given the id
     * @param id
     * @return
     */
    @DELETE("subscriptions/{id}")
    public Call<MyMessage> deleteSubscriptionById(@Path("id") int id);

    /**
     * Creates a new {@link Subscription} given a rectangular are
     * @param minLat
     * @param maxLat
     * @param minLon
     * @param maxLon
     * @return
     */
    @POST("subscriptions")
    @FormUrlEncoded
    public Call<MyMessage> postSubscriptionArea(@Field("minLat") Double minLat,
                                                   @Field("maxLat") Double maxLat,
                                                   @Field("minLon") Double minLon,
                                                   @Field("maxLon") Double maxLon);

    /**
     * Creates a new {@link Subscription} given a circular area
     * @param lat
     * @param lon
     * @param radius
     * @return
     */
    @POST("subscriptions")
    @FormUrlEncoded
    public Call<MyMessage> postSubscriptionCenterAndRadius(@Field("lat") Double lat,
                                                                @Field("lon") Double lon,
                                                                @Field("radius") int radius);

    /**
     * Creates a {@link Subscription} given an address and radius
     * @param country
     * @param city
     * @param street
     * @param radius
     * @return
     */
    @POST("subscriptions")
    @FormUrlEncoded
    public Call<MyMessage> postSubscriptionAddress(@Field("country") String country,
                                                      @Field("city") String city,
                                                      @Field("street") String street,
                                                      @Field("radius") int radius);


}
