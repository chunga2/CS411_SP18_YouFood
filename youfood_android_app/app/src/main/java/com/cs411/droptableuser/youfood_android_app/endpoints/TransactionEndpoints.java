package com.cs411.droptableuser.youfood_android_app.endpoints;

import com.cs411.droptableuser.youfood_android_app.responses.GETTransactionResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public interface TransactionEndpoints {
    @GET("/transactions")
    Call<ArrayList<GETTransactionResponse>> getTransactions(@Query("start") String start,
                                                            @Query("end") String end,
                                                            @Query("user") String user,
                                                            @Query("amount") String amount);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://youfood.ddns.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    TransactionEndpoints transactionEndpoints = retrofit.create(TransactionEndpoints.class);
}
