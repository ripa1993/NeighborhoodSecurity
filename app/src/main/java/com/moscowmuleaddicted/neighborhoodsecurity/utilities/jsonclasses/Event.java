package com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Simone Ripamonti on 12/04/2017.
 */

public class Event implements Serializable{

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("date")
    @Expose
    private Date date;
    @SerializedName("eventType")
    @Expose
    private EventType eventType;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("street")
    @Expose
    private String street;
    @SerializedName("latitude")
    @Expose
    private Float latitude;
    @SerializedName("longitude")
    @Expose
    private Float longitude;
    @SerializedName("votes")
    @Expose
    private int votes;
    @SerializedName("submitterId")
    @Expose
    private String submitterId;



    public Event(){

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(String submitterId) {
        this.submitterId = submitterId;
    }

    public static Event makeDummy(){
        Event event = new Event();
        event.setCity("asd");
        event.setCountry("it");
        event.setStreet("dsa");
        event.setDate(new Date());
        event.setDescription("desc");
        event.setEventType(EventType.BURGLARY);
        event.setLatitude(2f);
        event.setLongitude(3f);
        event.setId(19);
        event.setSubmitterId("sub");
        return event;
    }

    public Event(int id, Date date, EventType eventType, String description, String country, String city, String street, Float latitude, Float longitude, int votes, String submitterId) {
        this.id = id;
        this.date = date;
        this.eventType = eventType;
        this.description = description;
        this.country = country;
        this.city = city;
        this.street = street;
        this.latitude = latitude;
        this.longitude = longitude;
        this.votes = votes;
        this.submitterId = submitterId;
    }
}
