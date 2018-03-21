package com.cs411.droptableuser.youfood_android_app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class VerifyLoginResponse {
    @SerializedName("is_owner")
    private boolean isOwner;

    public boolean isOwner() {
        return isOwner;
    }
}
