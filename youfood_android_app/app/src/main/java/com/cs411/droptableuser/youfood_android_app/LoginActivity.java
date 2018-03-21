package com.cs411.droptableuser.youfood_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.textview_login_signup)
    TextView textViewSignUp;
    @BindView(R.id.button_login_guest)
    Button buttonContinueAsGuest;
    @BindView(R.id.button_login_signin)
    Button buttonSignIn;
    @BindView(R.id.edittext_login_username)
    EditText editTextUsername;
    @BindView(R.id.edittext_login_password)
    EditText editTextPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        this.getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));

        textViewSignUp.setOnClickListener(this);
        buttonContinueAsGuest.setOnClickListener(this);
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
