package com.cs411.droptableuser.youfood_android_app.endpoints;

import com.cs411.droptableuser.youfood_android_app.requests.POSTReviewRequest;
import com.cs411.droptableuser.youfood_android_app.requests.PUTReviewRequest;
import com.cs411.droptableuser.youfood_android_app.responses.GETReviewResponse;

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

public interface ReviewEndpoints {

    // search_user MUST be a capital String of a boolean. Either "False" or "True".
    @GET("reviews")
    Call<ArrayList<GETReviewResponse>> getReviews(@Query("search_user") String bool,
                                                  @Query("restaurant_name") String restaurantName,
                                                  @Query("restaurant_address") String restaurantAddress,
                                                  @Query("useremail") String email);

    @POST("/reviews")
    Call<Void> createReview(@Body POSTReviewRequest postReviewRequest);

    @PUT("/reviews")
    Call<Void> updateReview(@Body PUTReviewRequest putReviewRequest);

    @DELETE("/reviews")
    Call<Void> deleteReview(@Query("useremail") String email,
                            @Query("restaurant_address") String address,
                            @Query("restaurant_name") String name,
                            @Query("date") String date);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://youfood.ddns.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    ReviewEndpoints reviewEndpoints = retrofit.create(ReviewEndpoints.class);
}
