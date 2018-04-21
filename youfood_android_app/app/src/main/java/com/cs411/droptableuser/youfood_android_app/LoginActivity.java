package com.cs411.droptableuser.youfood_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.UserEndpoints;
import com.cs411.droptableuser.youfood_android_app.requests.VerifyLoginRequest;
import com.cs411.droptableuser.youfood_android_app.responses.GETUserResponse;
import com.cs411.droptableuser.youfood_android_app.responses.VerifyLoginResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.textview_login_signup)
    TextView textViewSignUp;
    @BindView(R.id.button_login_signin)
    Button buttonSignIn;
    @BindView(R.id.edittext_login_username)
    EditText editTextUsername;
    @BindView(R.id.edittext_login_password)
    EditText editTextPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check whether the user is logged in or not.
        //
        // If the user is logged in, start MainActivity and remove LoginActivity from the stack.
        UtilsCache.initialize(getApplicationContext());
        if (UtilsCache.getHasLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            finish();
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        this.getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));

        textViewSignUp.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textview_login_signup:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.button_login_signin:
                verifyUser();
                break;
        }
    }

    private void verifyUser() {
        final String email = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();
        Call<VerifyLoginResponse> call
                = UserEndpoints.userEndpoints.verifyLogin(new VerifyLoginRequest(email, password));

        call.enqueue(new Callback<VerifyLoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<VerifyLoginResponse> call, @NonNull Response<VerifyLoginResponse> response) {
                if(response.code() == ResponseCodes.HTTP_OK) {
                    // Store user details in cache
                    VerifyLoginResponse loginResponse = response.body();
                    UtilsCache.storeName(loginResponse.getName());
                    UtilsCache.storeEmail(loginResponse.getEmail());
                    UtilsCache.storeIsOwner(loginResponse.isOwner());
                    UtilsCache.storeHasLoggedIn(true);
                    UtilsCache.storePassword(password);

                    // Move on to next activity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    Toast.makeText(
                            LoginActivity.this,
                            "Your Email or password were incorrect.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<VerifyLoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, getString(R.string.network_failed_message), Toast.LENGTH_LONG).show();
            }
        });
    }
}
