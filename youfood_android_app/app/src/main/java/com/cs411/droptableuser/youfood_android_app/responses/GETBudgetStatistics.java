package com.cs411.droptableuser.youfood_android_app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 4/21/18.
 */

public class GETBudgetStatistics {
    @SerializedName("average_weekly")
    private String averageWeekly;

    @SerializedName("num_weeks_successful")
    private int numWeeksSuccessful;

    @SerializedName("num_weeks_total")
    private int numWeeks;

    public String getAverageWeekly() {
        return averageWeekly;
    }

    public int getNumWeeksSuccessful() {
        return numWeeksSuccessful;
    }

    public int getNumWeeks() {
        return numWeeks;
    }
}
