package com.cs411.droptableuser.youfood_android_app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class GETBudgetResponse {
    @SerializedName("date")
    private String date;

    @SerializedName("total")
    private String total;

    @SerializedName("useremail")
    private String userEmail;

    public String getDate() {
        return date;
    }

    public String getTotal() {
        return total;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
