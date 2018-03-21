package com.cs411.droptableuser.youfood_android_app.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class GETRestaurantResponse {
    @SerializedName("address")
    private String address;

    @SerializedName("categories")
    private String[] categories;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("pricerange")
    private String priceRange;

    public String getAddress() {
        return address;
    }

    public String[] getCategories() {
        return categories;
    }

    public String getImageURL() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPriceRange() {
        return priceRange;
    }
}
