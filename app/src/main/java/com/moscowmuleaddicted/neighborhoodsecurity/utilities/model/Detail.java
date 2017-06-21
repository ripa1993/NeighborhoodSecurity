package com.moscowmuleaddicted.neighborhoodsecurity.utilities.model;


import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a couple name - content, used to display Event information
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class Detail {
    /**
     * Name of the detail
     */
    private String name;
    /**
     * Content of the detail
     */
    private String content;

    /**
     * Empty constructor
     */
    public Detail(){
    }

    /**
     * Constructor
     * @param name
     * @param content
     */
    public Detail(String name, String content){
        this.name=name;
        this.content=content;
    }

    /**
     * Getter name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Setter name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter content
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter content
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Obtain a list of {@link Detail} given a {@link Event}
     * @param e
     * @return
     */
    public static List<Detail> listFromEvent(Event e){
        List<Detail> list = new LinkedList<Detail>();

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd MMMM yyyy");

        list.add(new Detail(DetailEventEnum.DATE.toString(), sdf.format(e.getDate())));
        list.add(new Detail(DetailEventEnum.EVENT_TYPE.toString(), e.getEventType().toString()));
        if(e.getDescription().length() > 0 && !e.getDescription().equals("null"))
            list.add(new Detail(DetailEventEnum.DESCRIPTION.toString(), e.getDescription()));

        String address = "";
        if (e.getCity().length() > 0 && !e.getCity().equals("null"))
            address = address + e.getCity();
        else
            address = address + e.getCountry();
        if (e.getStreet().length()>0 && !e.getStreet().equals("null"))
            address = address + ", "+ e.getStreet();

        list.add(new Detail(DetailEventEnum.ADDRESS.toString(), address));
        list.add(new Detail(DetailEventEnum.VOTES.toString(), String.valueOf(e.getVotes())));

        return list;
    }

}


