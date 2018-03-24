package com.cs411.droptableuser.youfood_android_app.endpoints;

import com.cs411.droptableuser.youfood_android_app.requests.POSTTransactionRequest;
import com.cs411.droptableuser.youfood_android_app.requests.PUTTransactionRequest;
import com.cs411.droptableuser.youfood_android_app.responses.GETTransactionResponse;

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

public interface TransactionEndpoints {
    @GET("/transactions")
    Call<ArrayList<GETTransactionResponse>> getTransactions(@Query("start") String start,
                                                            @Query("end") String end,
                                                            @Query("user") String user,
                                                            @Query("amount") String amount);

    @GET("/transactions_weekly")
    Call<ArrayList<GETTransactionResponse>> getWeeklyTransactionsForUser(@Query("useremail") String email, @Query("date") String date);

    @POST("/transactions")
    Call<Void> createTransaction(@Body POSTTransactionRequest postTransactionRequest);

    @PUT("/transactions")
    Call<Void> updateTransaction(@Body PUTTransactionRequest putTransactionRequest);

    @DELETE("/transactions")
    Call<Void> deleteTransaction(@Query("useremail") String email, @Query("date") String date);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://youfood.ddns.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    TransactionEndpoints transactionEndpoints = retrofit.create(TransactionEndpoints.class);
}
