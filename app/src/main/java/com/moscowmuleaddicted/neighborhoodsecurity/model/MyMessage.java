package com.moscowmuleaddicted.neighborhoodsecurity.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Representation of a message returned by Neighborhood Security Rest webservice
 * Annotations are used to provide Object instantiation give a json file
 *
 * @author Simone Ripamonti
 * @version 1
 */

public class MyMessage {
    @SerializedName("argument")
    @Expose
    private String argument;
    @SerializedName("message")
    @Expose
    private String message;

    public MyMessage(){

    }

    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString(){
        return "["+argument+"] "+message;
    }
}
