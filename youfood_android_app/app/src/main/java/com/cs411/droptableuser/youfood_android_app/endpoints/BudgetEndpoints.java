package com.cs411.droptableuser.youfood_android_app.endpoints;

import com.cs411.droptableuser.youfood_android_app.requests.GETBudgetResponse;
import com.cs411.droptableuser.youfood_android_app.requests.POSTBudgetRequest;
import com.cs411.droptableuser.youfood_android_app.requests.PUTBudgetRequest;
import com.cs411.droptableuser.youfood_android_app.responses.GETBudgetStatistics;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public interface BudgetEndpoints {
    @GET("/budgets")
    Call<ArrayList<GETBudgetResponse>> getBudgets(@Query("start") String start,
                                                  @Query("end") String end,
                                                  @Query("user") String user,
                                                  @Query("total") String total);
    @GET("get_user_budget_statistics")
    Call<GETBudgetStatistics> getBudgetStatistics(@Query("useremail") String email);

    @POST("/budgets")
    Call<Void> createBudget(@Body POSTBudgetRequest postBudgetRequest);

    @PUT("/budgets")
    Call<Void> updateBudget(@Body PUTBudgetRequest putBudgetRequest);

    @DELETE("/budgets")
    Call<Void> deleteBudget(@Query("useremail") String email, @Query("date") String date);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://youfood.ddns.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    BudgetEndpoints budgetEndpoints = retrofit.create(BudgetEndpoints.class);
}
