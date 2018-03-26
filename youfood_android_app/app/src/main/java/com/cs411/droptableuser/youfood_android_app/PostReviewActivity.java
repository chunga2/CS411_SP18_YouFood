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
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.ReviewEndpoints;
import com.cs411.droptableuser.youfood_android_app.requests.POSTReviewRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JunYoung on 2018. 3. 23..
 */

public class PostReviewActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String RESTAURANT_NAME_KEY = "RestaurantName";
    public static final String RESTAURANT_ADDRESS_KEY = "RestaurantAddress";

    private float numStars;
    private String restaurantName;
    private String restaurantAddress;

    @BindView(R.id.ratingbar_post_review)
    RatingBar ratingBar;
    @BindView(R.id.button_post_review_post)
    Button buttonPost;
    @BindView(R.id.button_post_review_close)
    ImageButton buttonClose;
    @BindView(R.id.text_post_review_restaurant_name)
    TextView textViewRestaurantName;
    @BindView(R.id.edittext_post_review_description)
    EditText editTextDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_review);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                restaurantName = extras.getString(RESTAURANT_NAME_KEY);
                restaurantAddress = extras.getString(RESTAURANT_ADDRESS_KEY);
            }
        }

        textViewRestaurantName.setText(restaurantName);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                numStars = v;
            }
        });

        buttonPost.setOnClickListener(this);
        buttonClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_post_review_close:
                finish();
                break;
            case R.id.button_post_review_post:
                postReview();
                break;
        }
    }



    private void postReview() {
        String dateTimeNow = getCurrentDateTime();
        int rating = (int) (numStars * 2);

        Call<Void> call
                = ReviewEndpoints.reviewEndpoints.createReview(new POSTReviewRequest(
                dateTimeNow,
                UtilsCache.getEmail(),
                restaurantName,
                restaurantAddress,
                editTextDescription.getText().toString(),
                rating
        ));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.code() == ResponseCodes.HTTP_CREATED) {
                    Intent intent = new Intent();

                    setResult(response.code(), intent);
                    finish();
                } else {
                    Log.d("PostReviewActivity", String.valueOf(response.code()));
                    Toast.makeText(
                            PostReviewActivity.this,
                            "Can't post the review.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(
                        PostReviewActivity.this,
                        getString(R.string.network_failed_message),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        Date date = new Date();

        return dateFormat.format(date);
    }
}
