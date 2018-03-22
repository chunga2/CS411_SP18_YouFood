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
import android.widget.RadioButton;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.UserEndpoints;
import com.cs411.droptableuser.youfood_android_app.requests.POSTUserRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JunYoung on 2018. 3. 21..
 */

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "SignUpActivity";

    private static boolean isOwner;

    @BindView(R.id.edittext_signup_username)
    EditText editTextUserName;
    @BindView(R.id.edittext_signup_email)
    EditText editTextEmail;
    @BindView(R.id.edittext_signup_password)
    EditText editTextPassword;
    @BindView(R.id.button_signup)
    Button buttonSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        this.getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                final String userName = editTextUserName.getText().toString();
                Call<Void> call
                        = UserEndpoints.userEndpoints.createUser(
                                new POSTUserRequest(email, userName, password, isOwner));

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if(response.code() == ResponseCodes.HTTP_CREATED) {
                            UtilsCache.storeName(userName);
                            UtilsCache.storeEmail(email);
                            UtilsCache.storeIsOwner(isOwner);
                            UtilsCache.storeHasLoggedIn(true);


                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, String.valueOf(response.code()));
                            Toast.makeText(
                                    SignUpActivity.this,
                                    "Your textViewEmail or password were incorrect.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radio_button_signup_user:
                if (checked) {
                    isOwner = false;
                }
                break;
            case R.id.radio_button_signup_owner:
                if (checked) {
                    isOwner = true;
                }
        }
    }
}
