package com.cs411.droptableuser.youfood_android_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.cs411.droptableuser.youfood_android_app.endpoints.RestaurantEndpoints;
import com.cs411.droptableuser.youfood_android_app.responses.GETRestaurantResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JunYoung on 2018. 4. 14..
 */

public class AutoCompleteRestaurantAdapter extends ArrayAdapter<GETRestaurantResponse> {
    private static final String TAG = "AutoCompleteRestaurantAdapter";

    private List<GETRestaurantResponse> filteredRestaurants = new ArrayList<>();

    public AutoCompleteRestaurantAdapter(Context context, List<GETRestaurantResponse> restaurants) {
        super(context, 0, restaurants);
        filteredRestaurants = restaurants;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.auto_complete_row_restaurant, parent, false);

        GETRestaurantResponse restaurant = filteredRestaurants.get(position);
        TextView restaurantName = convertView.findViewById(R.id.textview_row_restaurant_name);
        TextView restaurantAddress = convertView.findViewById(R.id.textview_row_restaurant_address);
        restaurantName.setText(restaurant.getName());
        restaurantAddress.setText(restaurant.getAddress());

        return convertView;
    }

    @Nullable
    @Override
    public GETRestaurantResponse getItem(int position) {
        return filteredRestaurants.get(position);
    }

    @Override
    public int getCount() {
        // Log.d(TAG, String.valueOf(filteredRestaurants.size()));
        return filteredRestaurants.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults filterResults = new FilterResults();
                filterResults.values = filteredRestaurants;
                filterResults.count = filteredRestaurants.size();

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null && filterResults.count > 0) {
                    filteredRestaurants = (ArrayList<GETRestaurantResponse>) filterResults.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }
}
