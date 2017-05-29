package com.moscowmuleaddicted.neighborhoodsecurity.utilities.model;


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

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd MMMM yyyy");

        list.add(new Details(DetailsEnum.DATE.toString(), sdf.format(e.getDate())));
        list.add(new Details(DetailsEnum.EVENT_TYPE.toString(), e.getEventType().toString()));
        if(e.getDescription().length() > 0 && !e.getDescription().equals("null"))
            list.add(new Details(DetailsEnum.DESCRIPTION.toString(), e.getDescription()));

        String address = "";
        if (e.getCity().length() > 0 && !e.getCity().equals("null"))
            address = address + e.getCity();
        else
            address = address + e.getCountry();
        if (e.getStreet().length()>0 && !e.getStreet().equals("null"))
            address = address + ", "+ e.getStreet();

        list.add(new Details(DetailsEnum.ADDRESS.toString(), address));
        list.add(new Details(DetailsEnum.VOTES.toString(), String.valueOf(e.getVotes())));

        return list;
    }

}


