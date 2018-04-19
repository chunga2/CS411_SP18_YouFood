package com.cs411.droptableuser.youfood_android_app.endpoints;

import com.cs411.droptableuser.youfood_android_app.requests.POSTRecommendationRequest;
import com.cs411.droptableuser.youfood_android_app.responses.GETRecommendationResponse;

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

public interface RecommendationEndpoints {
    @GET("/recommendations")
    Call<ArrayList<GETRecommendationResponse>> getRecommendationsForUser(@Query("useremail") String email);

    @POST("/recommendations")
    Call<Void> createRecommendation(@Body POSTRecommendationRequest postRecommendationRequest);

    @DELETE("/recommendations")
    Call<Void> deleteRecommendation(@Query("useremail") String email,
                                    @Query("restaurant_address") String address,
                                    @Query("restaurant_name") String name);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://youfood.ddns.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RecommendationEndpoints recommendationEndpoints = retrofit.create(RecommendationEndpoints.class);
}
