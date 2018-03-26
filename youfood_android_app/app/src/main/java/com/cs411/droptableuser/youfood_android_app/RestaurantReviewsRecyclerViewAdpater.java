package com.cs411.droptableuser.youfood_android_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cs411.droptableuser.youfood_android_app.responses.GETReviewResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JunYoung on 2018. 3. 23..
 */

public class RestaurantReviewsRecyclerViewAdpater
        extends RecyclerView.Adapter<RestaurantReviewsRecyclerViewAdpater.ViewHolder> {
    private static final int REVIEW_REQUEST = 4;

    private List<GETReviewResponse> reviews;

    public RestaurantReviewsRecyclerViewAdpater(List<GETReviewResponse> reviews) {
        this.reviews = reviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant_review, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GETReviewResponse review = reviews.get(position);

        holder.review = review;

        holder.textViewUserName.setText(review.getName());
        holder.textViewDescription.setText(review.getDescription());
        holder.ratingBar.setRating(review.getRating()/2.0f);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(review.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String firstHalfDate = String.format("%d/%d/%d", calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));

            String timeofDay;
            int hourOfDay;
            if(calendar.get(Calendar.HOUR_OF_DAY) > 12) {
                hourOfDay = calendar.get(Calendar.HOUR_OF_DAY) - 12;
                timeofDay = "PM";
            } else {
                hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                timeofDay = "AM";
            }

            String secondHalfDate = String.format("%d:%d %s", hourOfDay, calendar.get(Calendar.MINUTE), timeofDay);
            holder.textViewDate.setText(firstHalfDate + " " + secondHalfDate);

        } catch (ParseException e) {}


        holder.userEmail = review.getEmail();
        holder.restaurantName = review.getRestaurantName();
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setData(List<GETReviewResponse> reviews) {
        this.reviews = reviews;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.text_restaurant_review_date)
        TextView textViewDate;
        @BindView(R.id.text_restaurant_review_username)
        TextView textViewUserName;
        @BindView(R.id.text_restaurant_review_description)
        TextView textViewDescription;
        @BindView(R.id.ratingbar_restaurant_review)
        RatingBar ratingBar;

        private GETReviewResponse review;

        private String userEmail;
        private String restaurantName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final Context context = view.getContext();
            Intent detailedIntent = new Intent(context, ReviewActivity.class);

            detailedIntent.putExtra(ReviewActivity.REVIEW_KEY, review);

            ((Activity) context).startActivityForResult(detailedIntent, REVIEW_REQUEST);
        }
    }
}
