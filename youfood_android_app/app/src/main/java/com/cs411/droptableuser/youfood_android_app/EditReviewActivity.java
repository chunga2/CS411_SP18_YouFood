package com.cs411.droptableuser.youfood_android_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.ReviewEndpoints;
import com.cs411.droptableuser.youfood_android_app.endpoints.UserEndpoints;
import com.cs411.droptableuser.youfood_android_app.requests.PUTReviewRequest;
import com.cs411.droptableuser.youfood_android_app.requests.VerifyLoginRequest;
import com.cs411.droptableuser.youfood_android_app.responses.GETReviewResponse;
import com.cs411.droptableuser.youfood_android_app.responses.VerifyLoginResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JunYoung on 2018. 3. 23..
 */

public class EditReviewActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String REVIEW_KEY = "Review";
    public static final String TOOLBAR_TITLE = "Edit Review";

    private float numStars;

    @BindView(R.id.toolbar_edit_review)
    Toolbar toolbar;
    @BindView(R.id.edittext_edit_review)
    EditText editTextReview;
    @BindView(R.id.ratingbar_edit_review)
    RatingBar ratingBar;
    @BindView(R.id.button_edit_review_delete)
    Button buttonDelete;
    @BindView(R.id.button_edit_review_update)
    Button buttonUpdate;

    private GETReviewResponse review;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                review = extras.getParcelable(REVIEW_KEY);
            }
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(TOOLBAR_TITLE);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ratingBar.setRating(review.getRating()/2.0f);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                numStars = v;
            }
        });

        if (review != null) {
            editTextReview.setText(review.getDescription());
        }
        buttonDelete.setOnClickListener(this);
        buttonUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_edit_review_delete:
                deleteReview();
                break;
            case R.id.button_edit_review_update:
                updateReview();
                break;
        }
    }

    private void deleteReview() {
        Call<Void> call
                = ReviewEndpoints.reviewEndpoints.deleteReview(
                UtilsCache.getEmail(),
                review.getRestaurantAddress(),
                review.getRestaurantName(),
                review.getDate());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.code() == ResponseCodes.HTTP_NO_RESPONSE) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(
                            EditReviewActivity.this,
                            "Can't delete the review.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(
                        EditReviewActivity.this,
                        getString(R.string.network_failed_message),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateReview() {
        final int rating = (int) (numStars * 2);
        Call<Void> call
                = ReviewEndpoints.reviewEndpoints.updateReview(new PUTReviewRequest(
                        review.getDate(),
                        UtilsCache.getEmail(),
                        review.getRestaurantName(),
                        review.getRestaurantAddress(),
                        editTextReview.getText().toString(),
                        rating
        ));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.code() == ResponseCodes.HTTP_NO_RESPONSE) {
                    Intent intent = new Intent();
                    intent.putExtra(ReviewActivity.DESCRIPTION_KEY,
                            editTextReview.getText().toString());
                    intent.putExtra(ReviewActivity.RATING_KEY, rating);

                    setResult(response.code(), intent);
                    finish();
                } else {
                    Toast.makeText(
                            EditReviewActivity.this,
                            "Can't update the review.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(
                        EditReviewActivity.this,
                        getString(R.string.network_failed_message),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }
}
