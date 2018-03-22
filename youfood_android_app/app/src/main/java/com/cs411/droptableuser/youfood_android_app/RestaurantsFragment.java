package com.cs411.droptableuser.youfood_android_app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.RestaurantEndpoints;
import com.cs411.droptableuser.youfood_android_app.responses.GETRestaurantResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantsFragment extends Fragment {
    Unbinder unbinder;

    @BindView(R.id.recycler_view_restaurants)
    RecyclerView recyclerView;

    public static RestaurantsFragment newInstance() {
        RestaurantsFragment fragment = new RestaurantsFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurants, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        final RestaurantsRecyclerViewAdpater adapter = new RestaurantsRecyclerViewAdpater(new ArrayList<GETRestaurantResponse>());
        recyclerView.setAdapter(adapter);

        Call<ArrayList<GETRestaurantResponse>> call = RestaurantEndpoints.restaurantEndpoints.getRestaurants(null, null, "San Francisco", null);
        call.enqueue(new Callback<ArrayList<GETRestaurantResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<GETRestaurantResponse>> call, Response<ArrayList<GETRestaurantResponse>> response) {
                if(response.code() == ResponseCodes.HTTP_OK) {
                    adapter.setData(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GETRestaurantResponse>> call, Throwable t) {
                Toast.makeText(RestaurantsFragment.this.getContext(), getString(R.string.network_failed_message), Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
