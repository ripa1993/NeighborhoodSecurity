package com.moscowmuleaddicted.neighborhoodsecurity.utilities.details;


import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Simone Ripamonti on 25/04/2017.
 */

public class Details {
    private String name;
    private String content;

    public Details(){

    }

    public Details(String name, String content){
        this.name=name;
        this.content=content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static List<Details> listFromEvent(Event e){
        List<Details> list = new LinkedList<Details>();
//        list.add(new Details("id", String.valueOf(e.getId())));
//        list.add(new Details("submitterId", String.valueOf(e.getSubmitterId())));
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd MMMM yyyy");

        list.add(new Details(DetailsEnum.DATE.toString(), sdf.format(e.getDate())));
        list.add(new Details(DetailsEnum.EVENT_TYPE.toString(), e.getEventType().toString()));
        list.add(new Details(DetailsEnum.DESCRIPTION.toString(), e.getDescription()));
        list.add(new Details(DetailsEnum.ADDRESS.toString(), e.getCountry() +", "+e.getCity()+", "+e.getStreet()));
        list.add(new Details(DetailsEnum.COORDINATES.toString(), "("+e.getLatitude()+", "+e.getLongitude()+")"));
        list.add(new Details(DetailsEnum.VOTES.toString(), String.valueOf(e.getVotes())));

//        list.add(new Details("dummy", "dummy"));
//        list.add(new Details("dummy", "dummy"));
//        list.add(new Details("dummy", "dummy"));
//        list.add(new Details("dummy", "dummy"));
        return list;
    }

}


