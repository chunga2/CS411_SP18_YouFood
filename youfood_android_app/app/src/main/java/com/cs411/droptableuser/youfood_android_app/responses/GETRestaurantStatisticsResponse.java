package com.cs411.droptableuser.youfood_android_app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/23/18.
 */

public class GETRestaurantStatisticsResponse {
    @SerializedName("review_average")
    float reviewAverage;

    @SerializedName("review_count")
    int reviewCount;

    @SerializedName("transaction_count")
    int transactionCount;

    public float getReviewAverage() {
        return reviewAverage;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public int getTransactionCount() {
        return transactionCount;
    }
}
