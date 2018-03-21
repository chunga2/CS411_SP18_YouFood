package com.cs411.droptableuser.youfood_android_app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class GETUserResponse {
    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("is_owner")
    private boolean isOwner;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public boolean isOwner() {
        return isOwner;
    }
}