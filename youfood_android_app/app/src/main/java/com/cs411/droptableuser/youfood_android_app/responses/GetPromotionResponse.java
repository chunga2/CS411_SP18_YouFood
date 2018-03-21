package com.cs411.droptableuser.youfood_android_app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class GetPromotionResponse {
    @SerializedName("date")
    private String date;

    @SerializedName("description")
    private String description;

    @SerializedName("restaurant_name")
    private String name;

    @SerializedName("restaurant_address")
    private String address;

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
