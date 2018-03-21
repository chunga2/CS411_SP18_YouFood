package com.cs411.droptableuser.youfood_android_app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class PUTReviewRequest {
    @SerializedName("date")
    private String date;

    @SerializedName("useremail")
    private String email;

    @SerializedName("restaurant_name")
    private String restaurantName;

    @SerializedName("restaurant_address")
    private String restaurantAddress;

    @SerializedName("description")
    private String description;

    @SerializedName("rating")
    private int rating;

    public PUTReviewRequest(String date, String email, String restaurantName, String restaurantAddress, String description, int rating) {
        this.date = date;
        this.email = email;
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.description = description;
        this.rating = rating;
    }
}
