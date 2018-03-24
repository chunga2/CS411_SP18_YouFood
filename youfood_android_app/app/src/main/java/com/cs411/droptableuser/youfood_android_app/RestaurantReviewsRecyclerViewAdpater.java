package com.cs411.droptableuser.youfood_android_app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cs411.droptableuser.youfood_android_app.responses.GETReviewResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JunYoung on 2018. 3. 23..
 */

public class RestaurantReviewsRecyclerViewAdpater
        extends RecyclerView.Adapter<RestaurantReviewsRecyclerViewAdpater.ViewHolder> {
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

        holder.textViewDate.setText(review.getDate());
        holder.textViewUserName.setText(review.getName());
        holder.textViewDescription.setText(review.getDescription());
        holder.ratingBar.setRating(review.getRating());

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

            context.startActivity(detailedIntent);
        }
    }
}
