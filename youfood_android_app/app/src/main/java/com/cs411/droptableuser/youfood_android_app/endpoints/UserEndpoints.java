package com.cs411.droptableuser.youfood_android_app.endpoints;

import com.cs411.droptableuser.youfood_android_app.requests.PostUserRequest;
import com.cs411.droptableuser.youfood_android_app.requests.PutUserRequest;
import com.cs411.droptableuser.youfood_android_app.requests.VerifyLoginRequest;
import com.cs411.droptableuser.youfood_android_app.responses.GetUserResponse;
import com.cs411.droptableuser.youfood_android_app.responses.VerifyLoginResponse;

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

public interface UserEndpoints {
    @POST("/verify_login")
    Call<VerifyLoginResponse> verifyLogin(@Body VerifyLoginRequest verifyLoginRequest);

    @GET("/users")
    Call<GetUserResponse> getUser(@Query("email") String email);

    @POST("/users")
    Call<Void> createUser(@Body PostUserRequest postUserRequest);

    @PUT("/users")
    Call<Void> updateUser(@Body PutUserRequest putUserRequest);

    @DELETE("/users")
    Call<Void> deleteUser(@Query("email") String email);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://youfood.ddns.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    UserEndpoints userEndpoints = retrofit.create(UserEndpoints.class);
}
