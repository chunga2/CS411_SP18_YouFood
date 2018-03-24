package com.cs411.droptableuser.youfood_android_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cs411.droptableuser.youfood_android_app.responses.GETReviewResponse;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JunYoung on 2018. 3. 23..
 */

public class ReviewActivity extends AppCompatActivity {
    public static final int EDIT_REVIEW_REQUEST = 42;
    public static final String REVIEW_KEY = "Review";
    public static final String RATING_KEY = "Rating";
    public static final String DESCRIPTION_KEY = "Description";

    private GETReviewResponse review;

    @BindView(R.id.toolbar_review)
    Toolbar toolbar;
    @BindView(R.id.text_review_date)
    TextView textViewDate;
    @BindView(R.id.text_review_usernname)
    TextView textViewUserName;
    @BindView(R.id.text_review_description)
    TextView textViewDescription;
    @BindView(R.id.ratingbar_review)
    RatingBar ratingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                review = extras.getParcelable(REVIEW_KEY);
            }
        }

        this.getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(review.getRestaurantName());
        setSupportActionBar(toolbar);

        textViewDate.setText(review.getDate());
        textViewUserName.setText(review.getName());
        textViewDescription.setText(review.getDescription());
        ratingBar.setRating(review.getRating());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (review.getEmail().equals(UtilsCache.getEmail())) {
            getMenuInflater().inflate(R.menu.menu_review_toolbar, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_edit) {
            Intent intent = new Intent(this, EditReviewActivity.class);
            intent.putExtra(EditReviewActivity.REVIEW_KEY, review);
            startActivityForResult(intent, EDIT_REVIEW_REQUEST);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_REVIEW_REQUEST && resultCode == 204) {
            int numStars = data.getExtras().getInt(RATING_KEY);
            String description = data.getExtras().getString(DESCRIPTION_KEY);

            ratingBar.setRating(numStars);
            textViewDescription.setText(description);
        }
    }
}
