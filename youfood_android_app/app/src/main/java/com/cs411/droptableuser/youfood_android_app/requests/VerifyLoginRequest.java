package com.cs411.droptableuser.youfood_android_app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class VerifyLoginRequest {
    public VerifyLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;
}
