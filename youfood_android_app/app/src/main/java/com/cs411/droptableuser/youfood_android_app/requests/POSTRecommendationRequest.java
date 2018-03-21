package com.cs411.droptableuser.youfood_android_app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class POSTRecommendationRequest {
    @SerializedName("date")
    private String date;

    @SerializedName("useremail")
    private String userEmail;

    @SerializedName("restaurant_name")
    private String restaurantName;

    @SerializedName("restaurant_address")
    private String restaurantAddress;

    public POSTRecommendationRequest(String date, String userEmail, String restaurantName, String restaurantAddress) {
        this.date = date;
        this.userEmail = userEmail;
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
    }
}
