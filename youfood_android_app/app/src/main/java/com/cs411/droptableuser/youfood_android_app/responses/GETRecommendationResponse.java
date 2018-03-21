package com.cs411.droptableuser.youfood_android_app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class GETRecommendationResponse {
    @SerializedName("date")
    private String date;

    @SerializedName("restaurant_name")
    private String restaurantName;

    @SerializedName("restaurant_address")
    private String restaurantAddress;

    @SerializedName("useremail")
    private String email;

    public String getDate() {
        return date;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public String getEmail() {
        return email;
    }
}
