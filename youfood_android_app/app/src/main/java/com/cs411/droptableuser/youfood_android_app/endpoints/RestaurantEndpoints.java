package com.cs411.droptableuser.youfood_android_app.endpoints;

import com.cs411.droptableuser.youfood_android_app.requests.PUTRestaurantRequest;
import com.cs411.droptableuser.youfood_android_app.responses.GETRestaurantResponse;

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
                                                          @Query("pricelt") String priceLessThan,
                                                          @Query("city") String address,
                                                          @Query("name") String name);

    @PUT("/restaurants")
    Call<Void> updateRestaurantOwner(@Body PUTRestaurantRequest putRestaurantRequest);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://youfood.ddns.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RestaurantEndpoints restaurantEndpoints = retrofit.create(RestaurantEndpoints.class);
}
