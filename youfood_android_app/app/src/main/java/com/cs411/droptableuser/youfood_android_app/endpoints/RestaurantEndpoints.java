package com.cs411.droptableuser.youfood_android_app.endpoints;

import com.cs411.droptableuser.youfood_android_app.requests.PUTRestaurantRequest;
import com.cs411.droptableuser.youfood_android_app.responses.GETRestaurantResponse;
import com.cs411.droptableuser.youfood_android_app.responses.GETRestaurantStatisticsResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public interface RestaurantEndpoints {
    @GET("/restaurants")
    Call<ArrayList<GETRestaurantResponse>> getRestaurants(@Query("priceeq") String priceEquals,
                                                          @Query("pricelte") String priceLte,
                                                          @Query("pricegte") String priceGte,
                                                          @Query("city") String city,
                                                          @Query("name") String name);
    @GET("/restaurant_statistics")
    Call<GETRestaurantStatisticsResponse> getRestaurantStatistics(@Query("name") String name, @Query("address") String address);

    @PUT("/restaurants")
    Call<Void> updateRestaurantOwner(@Body PUTRestaurantRequest putRestaurantRequest);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://youfood.ddns.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RestaurantEndpoints restaurantEndpoints = retrofit.create(RestaurantEndpoints.class);
}
