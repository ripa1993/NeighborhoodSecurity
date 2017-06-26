package com.moscowmuleaddicted.neighborhoodsecurity.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Representation of a Event returned by Neighborhood Security Rest webservice
 * Annotations are used to provide Object instantiation give a json file
 *
 * @author Simone Ripamonti
 * @version 1
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
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
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

    /**
     * Generates a dummy event, for testing purpose
     * @return
     */
    public static Event makeDummy(){
        Event event = new Event();
        event.setCity("asd");
        event.setCountry("it");
        event.setStreet("dsa");
        event.setDate(new Date());
        event.setDescription("desc");
        event.setEventType(EventType.BURGLARY);
        event.setLatitude(45.7238097d);
        event.setLongitude(9.0098383d);
        event.setId(19);
        event.setSubmitterId("sub");
        return event;
    }

    public Event(int id, Date date, EventType eventType, String description, String country, String city, String street, Double latitude, Double longitude, int votes, String submitterId) {
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

    // assume unique event id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;

        Event event = (Event) o;

        return id == event.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", date=" + date +
                ", eventType=" + eventType +
                ", description='" + description + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", votes=" + votes +
                ", submitterId='" + submitterId + '\'' +
                '}';
    }
}
