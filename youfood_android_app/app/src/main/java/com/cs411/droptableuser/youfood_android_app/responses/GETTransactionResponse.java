package com.cs411.droptableuser.youfood_android_app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class GETTransactionResponse {
    @SerializedName("amount")
    private String amount;

    @SerializedName("date")
    private String date;

    @SerializedName("restaurant_address")
    private String address;

    @SerializedName("restaurant_name")
    private String name;

    @SerializedName("useremail")
    private String userEmail;

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
