package com.cs411.droptableuser.youfood_android_app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class VerifyLoginResponse {
    @SerializedName("is_owner")
    private boolean isOwner;

    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    public boolean isOwner() {
        return isOwner;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
