package com.cs411.droptableuser.youfood_android_app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class PutUserRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("password")
    private String password;

    public PutUserRequest(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
}
