package com.cs411.droptableuser.youfood_android_app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class PUTTransactionRequest {
    @SerializedName("date")
    private String date;

    @SerializedName("amount")
    private double amount;

    @SerializedName("useremail")
    private String userEmail;

    public PUTTransactionRequest(String date, double amount, String userEmail) {
        this.date = date;
        this.amount = amount;
        this.userEmail = userEmail;
    }
}
