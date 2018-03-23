package com.cs411.droptableuser.youfood_android_app.endpoints;

import com.cs411.droptableuser.youfood_android_app.responses.GETRestaurantResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public interface RestaurantCategoriesEndpoints {

    @GET("/restaurant_categories")
    Call<ArrayList<GETRestaurantResponse>> getRestaurantsByCategory(@Query("category") String category,
                                                                    @Query("priceeq") String priceEquals,
                                                                    @Query("pricelte") String priceLte,
                                                                    @Query("pricegte") String priceGte,
                                                                    @Query("city") String city,
                                                                    @Query("name") String name);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://youfood.ddns.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RestaurantCategoriesEndpoints restaurantCategoriesEndpoints = retrofit.create(RestaurantCategoriesEndpoints.class);
}
