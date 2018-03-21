package com.cs411.droptableuser.youfood_android_app.endpoints;

import com.cs411.droptableuser.youfood_android_app.requests.POSTPromotionRequest;
import com.cs411.droptableuser.youfood_android_app.responses.GETPromotionResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public interface PromotionEndpoints {

    @GET("/promotions")
    Call<ArrayList<GETPromotionResponse>> getPromotionsForRestaurant(
            @Query("restaurant_name") String name, @Query("restaurant_address") String address);

    @POST("/promotions")
    Call<Void> createPromotion(@Body POSTPromotionRequest createPromotionRequest);

    @DELETE("/promotions")
    Call<Void> deletePromotion(@Query("restaurant_name") String name,
                               @Query("restaurant_address") String address,
                               @Query("date") String date,
                               @Query("description") String description);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://youfood.ddns.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    PromotionEndpoints promotionEndpoints = retrofit.create(PromotionEndpoints.class);
}
