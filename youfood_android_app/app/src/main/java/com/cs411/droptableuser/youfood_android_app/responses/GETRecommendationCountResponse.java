package com.cs411.droptableuser.youfood_android_app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 4/22/18.
 */

public class GETRecommendationCountResponse {
    @SerializedName("count")
    private int recommendationCount;

    public int getRecommendationCount() {
        return recommendationCount;
    }
}
