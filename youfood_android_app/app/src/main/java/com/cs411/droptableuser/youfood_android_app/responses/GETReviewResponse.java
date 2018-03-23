package com.cs411.droptableuser.youfood_android_app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class GETReviewResponse {
    @SerializedName("date")
    private String date;

    @SerializedName("description")
    private String description;

    @SerializedName("name")
    private String name;

    @SerializedName("rating")
    private int rating;

    @SerializedName("restaurant_name")
    private String restaurantName;

    @SerializedName("restaurant_address")
    private String restaurantAddress;

    @SerializedName("useremail")
    private String email;

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
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
