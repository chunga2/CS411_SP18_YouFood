package com.cs411.droptableuser.youfood_android_app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class CreatePromotionRequest {
    @SerializedName("restaurant_name")
    private String name;

    @SerializedName("restaurant_address")
    private String address;

    @SerializedName("date")
    private String date;

    @SerializedName("description")
    private String description;

    public CreatePromotionRequest(String name, String address, String date, String description) {
        this.name = name;
        this.address = address;
        this.date = date;
        this.description = description;
    }
}
