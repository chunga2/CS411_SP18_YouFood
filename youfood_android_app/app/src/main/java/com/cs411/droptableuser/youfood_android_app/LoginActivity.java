package com.cs411.droptableuser.youfood_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.UserEndpoints;
import com.cs411.droptableuser.youfood_android_app.requests.VerifyLoginRequest;
import com.cs411.droptableuser.youfood_android_app.responses.VerifyLoginResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.textview_login_signup)
    TextView textViewSignUp;

    @BindView(R.id.edittext_login_username)
    EditText usernameEditText;

    @BindView(R.id.edittext_login_password)
    EditText passwordEditText;

    @BindView(R.id.button_login_signin)
    Button loginButton;

    @BindView(R.id.button_login_guest)
    Button buttonContinueAsGuest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        this.getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));

        textViewSignUp.setOnClickListener(this);
        buttonContinueAsGuest.setOnClickListener(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                Call<VerifyLoginResponse> call = UserEndpoints.userEndpoints.verifyLogin(new VerifyLoginRequest(email, password));

                call.enqueue(new Callback<VerifyLoginResponse>() {
                    @Override
                    public void onResponse(Call<VerifyLoginResponse> call, Response<VerifyLoginResponse> response) {
                        if(response.code() == 200) {
                            Toast.makeText(LoginActivity.this, "Login email/password successful!", Toast.LENGTH_LONG).show();

                            // TODO: This means login was successful so make intent to next activity
                        } else {
                            Toast.makeText(LoginActivity.this, "Login email/password combination failed!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<VerifyLoginResponse> call, Throwable t) {

                    }
                });
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textview_login_signup:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.button_login_guest:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
