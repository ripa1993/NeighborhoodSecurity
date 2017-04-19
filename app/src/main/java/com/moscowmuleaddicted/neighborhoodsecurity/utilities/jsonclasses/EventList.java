package com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Simone Ripamonti on 12/04/2017.
 */

public class EventList extends MyMessage {
    @SerializedName("events")
    private List<Event> events;

    public EventList(){
        super();
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
