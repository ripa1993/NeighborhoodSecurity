package com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Simone Ripamonti on 24/04/2017.
 */

public class Subscription {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("minLat")
    @Expose
    private float minLat;
    @SerializedName("maxLat")
    @Expose
    private float maxLat;
    @SerializedName("minLon")
    @Expose
    private float minLon;
    @SerializedName("maxLon")
    @Expose
    private float maxLon;
    @SerializedName("radius")
    @Expose
    private int radius;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("street")
    @Expose
    private String street;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getMinLat() {
        return minLat;
    }

    public void setMinLat(float minLat) {
        this.minLat = minLat;
    }

    public float getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(float maxLat) {
        this.maxLat = maxLat;
    }

    public float getMinLon() {
        return minLon;
    }

    public void setMinLon(float minLon) {
        this.minLon = minLon;
    }

    public float getMaxLon() {
        return maxLon;
    }

    public void setMaxLon(float maxLon) {
        this.maxLon = maxLon;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Subscription() {


    }
}
