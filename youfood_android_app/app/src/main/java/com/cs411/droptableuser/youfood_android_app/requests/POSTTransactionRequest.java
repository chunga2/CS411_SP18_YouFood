package com.cs411.droptableuser.youfood_android_app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class POSTTransactionRequest {
    @SerializedName("restaurant_name")
    private String name;

    @SerializedName("restaurant_address")
    private String address;

    @SerializedName("date")
    private String date;

    @SerializedName("amount")
    private double amount;

    @SerializedName("useremail")
    private String userEmail;

    public POSTTransactionRequest(String name, String address, String date, double amount, String userEmail) {
        this.name = name;
        this.address = address;
        this.date = date;
        this.amount = amount;
        this.userEmail = userEmail;
    }
}
