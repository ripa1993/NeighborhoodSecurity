package com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Simone Ripamonti on 19/04/2017.
 */

public class AuthToken extends MyMessage {
    @SerializedName("authToken")
    @Expose
    private String authToken;
    @SerializedName("userId")
    @Expose
    private int userId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("userUrl")
    @Expose
    private String userUrl;

    public AuthToken(){
        super();
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }
}
