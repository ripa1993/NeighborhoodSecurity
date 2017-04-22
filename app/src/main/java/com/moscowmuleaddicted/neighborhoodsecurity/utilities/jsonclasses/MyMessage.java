package com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Simone Ripamonti on 19/04/2017.
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
}