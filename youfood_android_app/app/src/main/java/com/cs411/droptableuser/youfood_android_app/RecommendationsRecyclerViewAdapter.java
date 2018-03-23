package com.cs411.droptableuser.youfood_android_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.RecommendationEndpoints;
import com.cs411.droptableuser.youfood_android_app.responses.GETRecommendationResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by raajesharunachalam on 3/23/18.
 */

public class RecommendationsRecyclerViewAdapter extends RecyclerView.Adapter<RecommendationsRecyclerViewAdapter.ViewHolder> {
    ArrayList<GETRecommendationResponse> recommendations;
    Context context;
    AccountFragment.RestaurantSelectedListener listener;

    public RecommendationsRecyclerViewAdapter(ArrayList<GETRecommendationResponse> recommendations, Context context,
                                              AccountFragment.RestaurantSelectedListener listener) {
        this.recommendations = recommendations;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recommendation_item, parent, false);

        return new ViewHolder(view);
    }

    public void setData(ArrayList<GETRecommendationResponse> recommendations) {
        this.recommendations = recommendations;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GETRecommendationResponse recommendationObj = recommendations.get(position);
        holder.recommendationObj = recommendationObj;
        holder.index = position;

        int length = recommendationObj.getRestaurantName().length();
        final SpannableString span = new SpannableString(recommendationObj.getRestaurantName());
        span.setSpan(new URLSpan(""), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.restaurantName.setText(span, TextView.BufferType.SPANNABLE);

        holder.restaurantAddress.setText(recommendationObj.getRestaurantAddress());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(recommendationObj.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String firstHalfDate = String.format("%d/%d/%d", calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
            holder.date.setText(firstHalfDate);

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
            holder.time.setText(secondHalfDate);

        } catch (ParseException e) {}
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.text_recommendation_name)
        TextView restaurantName;

        @BindView(R.id.text_recommendation_address)
        TextView restaurantAddress;

        @BindView(R.id.text_recommendation_date)
        TextView date;

        @BindView(R.id.text_recommendation_time)
        TextView time;

        @BindView(R.id.image_recommendation_delete)
        ImageView deleteButton;

        GETRecommendationResponse recommendationObj;
        int index;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.deleteButton.setOnClickListener(this);
            this.restaurantName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.image_recommendation_delete) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Recommendation");
                builder.setMessage("Are you sure you want to delete this recommendation?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Call<Void> call = RecommendationEndpoints.recommendationEndpoints.deleteRecommendation(UtilsCache.getEmail(),
                                recommendationObj.getRestaurantAddress(), recommendationObj.getRestaurantName(), recommendationObj.getDate());
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == ResponseCodes.HTTP_NO_RESPONSE) {
                                    recommendations.remove(index);
                                    RecommendationsRecyclerViewAdapter.this.notifyItemRemoved(index);
                                    Toast.makeText(context, "Succesfully deleted", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "Could not delete", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(context, R.string.network_failed_message, Toast.LENGTH_LONG).show();
                            }
                        });
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            } else if (view.getId() == R.id.text_recommendation_name) {
                if(listener != null) {
                    listener.onRestaurantSelected(this.restaurantName.getText().toString());
                }
            }
        }
    }
}
