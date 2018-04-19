package com.cs411.droptableuser.youfood_android_app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class POSTBudgetRequest {
    @SerializedName("date")
    private String date;

    @SerializedName("useremail")
    private String userEmail;

    @SerializedName("total")
    private double total;

    public POSTBudgetRequest(String date, String userEmail, double total) {
        this.date = date;
        this.userEmail = userEmail;
        this.total = total;
    }
}
