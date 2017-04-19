package com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Simone Ripamonti on 19/04/2017.
 */

public class User extends MyMessage {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("username")
    @Expose
    String username;
    @SerializedName("email")
    @Expose
    String email;
    @SerializedName("created")
    @Expose
    Date created;
    @SerializedName("userUrl")
    @Expose
    String userUrl;

    public User(){
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }
}
