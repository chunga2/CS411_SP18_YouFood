package com.cs411.droptableuser.youfood_android_app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class PUTRestaurantRequest {
    @SerializedName("restaurant_name")
    private String name;

    @SerializedName("restaurant_address")
    private String address;

    @SerializedName("owner_email")
    private String ownerEmail;

    public PUTRestaurantRequest(String name, String address, String ownerEmail) {
        this.name = name;
        this.address = address;
        this.ownerEmail = ownerEmail;
    }
}
