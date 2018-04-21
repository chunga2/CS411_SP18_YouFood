package com.cs411.droptableuser.youfood_android_app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cs411.droptableuser.youfood_android_app.responses.GETRestaurantResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantsRecyclerViewAdpater extends RecyclerView.Adapter<RestaurantsRecyclerViewAdpater.ViewHolder> {
    private List<GETRestaurantResponse> restaurants;

    public RestaurantsRecyclerViewAdpater(List<GETRestaurantResponse> restaurants) {
        this.restaurants = restaurants;
    }

    public void setData(List<GETRestaurantResponse> restaurants) {
        this.restaurants = restaurants;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GETRestaurantResponse restaurantObj = this.restaurants.get(position);
        holder.restaurantObj = restaurantObj;

        if ("".equals(restaurantObj.getImageUrl())) {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.default_restaurant_image)
                    .into(holder.imageViewRestaurant);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(restaurantObj.getImageUrl())
                    .apply(new RequestOptions().override(110, 110).centerCrop())
                    .into(holder.imageViewRestaurant);
        }
        holder.textViewRestaurantName.setText(restaurantObj.getName());

        String[] categories = restaurantObj.getCategories();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < (categories.length - 1); i++) {
            builder.append(categories[i]);
            builder.append(", ");
        }
        builder.append(categories[categories.length-1]);
        holder.textViewRestaurantCuisine.setText(builder.toString());

        holder.textViewRestaurantLocation.setText(restaurantObj.getAddress());

        builder = new StringBuilder();
        if(restaurantObj.getPriceRange() != null) {
            int priceRange = Integer.parseInt(restaurantObj.getPriceRange());
            for (int i = 0; i < priceRange; i++) {
                builder.append("$");
            }

            holder.textViewRestaurantPriceRange.setText(builder.toString());
        } else {
            holder.textViewRestaurantPriceRange.setText("None");
        }
    }

    // TODO: Change the total number of items after implementing data set.
    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_restaurant)
        ImageView imageViewRestaurant;
        @BindView(R.id.text_restaurant_name)
        TextView textViewRestaurantName;
        @BindView(R.id.text_restaurant_cuisine)
        TextView textViewRestaurantCuisine;
        @BindView(R.id.text_restaurant_location)
        TextView textViewRestaurantLocation;
        @BindView(R.id.text_restaurant_price_range)
        TextView textViewRestaurantPriceRange;
        GETRestaurantResponse restaurantObj;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final Context context = view.getContext();
            Intent detailedIntent = new Intent(context, RestaurantDetailActivity.class);
            detailedIntent.putExtra(RestaurantDetailActivity.RESTAURANT_KEY, restaurantObj);
            detailedIntent.putExtra(RestaurantDetailActivity.CATEGORIES_KEY, textViewRestaurantCuisine.getText().toString());
            detailedIntent.putExtra(RestaurantDetailActivity.PRICE_RANGE_KEY, textViewRestaurantPriceRange.getText().toString());
            context.startActivity(detailedIntent);
        }
    }
}
