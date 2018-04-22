package com.cs411.droptableuser.youfood_android_app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.RestaurantCategoriesEndpoints;
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
    private static final String RESTAURANT_NAME_SEARCH_KEY = "RestaurantName";
    private static final String RESTAURANT_ADDRESS_SEARCH_KEY = "RestaurantAddress";

    private ArrayList<GETRestaurantResponse> restaurants = new ArrayList<>();

    public static boolean isCheckButtonSelected = false;

    Unbinder unbinder;
    RestaurantsRecyclerViewAdpater adapter;

    @BindView(R.id.text_restaurants_result_number)
    TextView resultNumber;
    @BindView(R.id.drawer_layout_restaurants)
    DrawerLayout drawerLayout;
    @BindView(R.id.recycler_view_restaurants)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar_restaurants_loading)
    ProgressBar progressBar;

    @BindView(R.id.checkbox_restaurants_name)
    CheckBox nameCheckBox;
    @BindView(R.id.edittext_restaurants_name)
    EditText nameEditText;

    @BindView(R.id.checkbox_restaurants_address)
    CheckBox addressCheckBox;
    @BindView(R.id.edittext_restaurants_address)
    EditText addressEditText;

    @BindView(R.id.checkbox_restaurants_city)
    CheckBox cityCheckBox;
    @BindView(R.id.edittext_restaurants_city)
    EditText cityEditText;

    @BindView(R.id.checkbox_restaurants_price_range)
    CheckBox priceRangeCheckBox;
    @BindView(R.id.spinner_restaurants_price_range_operator)
    Spinner priceRangeOperatorSpinner;
    @BindView(R.id.spinner_restaurants_price_range_value)
    Spinner priceRangeValueSpinner;

    @BindView(R.id.checkbox_restaurants_category)
    CheckBox categoryCheckBox;
    @BindView(R.id.edittext_restaurants_category)
    EditText categoryEditText;

    @BindView(R.id.imageview_restaurants_check)
    ImageView restaurantsFilterCheckButton;
    @BindView(R.id.imageview_restaurants_close)
    ImageView restaurantsFilterCloseButton;


    public static RestaurantsFragment newInstance() {
        RestaurantsFragment fragment = new RestaurantsFragment();
        return fragment;
    }

    public static RestaurantsFragment newInstance(String restaurantName, String restaurantAddress) {
        RestaurantsFragment fragment = new RestaurantsFragment();

        Bundle args = new Bundle();
        args.putString(RESTAURANT_NAME_SEARCH_KEY, restaurantName);
        args.putString(RESTAURANT_ADDRESS_SEARCH_KEY, restaurantAddress);
        fragment.setArguments(args);

        isCheckButtonSelected = true;

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurants, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);

        // Fragment was created with specific restaurant to display, so let's set the filters to that
        Bundle args = getArguments();
        if((args != null) && (args.containsKey(RESTAURANT_NAME_SEARCH_KEY)) && (args.containsKey(RESTAURANT_ADDRESS_SEARCH_KEY))) {
            nameCheckBox.setChecked(true);
            String restaurant = args.getString(RESTAURANT_NAME_SEARCH_KEY);
            nameEditText.setText(restaurant);

            addressCheckBox.setChecked(true);
            String address = args.getString(RESTAURANT_ADDRESS_SEARCH_KEY);
            addressEditText.setText(address);
        }

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

        adapter = new RestaurantsRecyclerViewAdpater(restaurants);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);


        restaurantsFilterCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadRestaurants();
                drawerLayout.closeDrawer(GravityCompat.END);
                isCheckButtonSelected = true;
            }
        });

        restaurantsFilterCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        });

        // When first created, nothing in filters should be checked, so the request will be all Null query arguments, so
        // we will get all the results back. Unless this was created by clicking on the restaurant name of a Recommendation,
        // then we will filter on name and address to return the exact restaurant.
        loadRestaurants();

        return rootView;
    }

    public void loadRestaurants() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        resultNumber.setText("");

        // Fill with all restaurants in San Francisco Bay Area initially
        String priceEquals = null;
        String priceLte = null;
        String priceGte = null;
        if(priceRangeCheckBox.isChecked()) {
            if(priceRangeOperatorSpinner.getSelectedItem().toString().equals("=")) {
                priceEquals = String.valueOf(priceRangeValueSpinner.getSelectedItem().toString().length());
            } else if (priceRangeOperatorSpinner.getSelectedItem().toString().equals(">=")) {
                priceGte = String.valueOf(priceRangeValueSpinner.getSelectedItem().toString().length());
            } else {
                priceLte = String.valueOf(priceRangeValueSpinner.getSelectedItem().toString().length());
            }
        }

        String city = null;
        if(cityCheckBox.isChecked()) {
            city = cityEditText.getText().toString();
        }

        String name = null;
        if(nameCheckBox.isChecked()) {
            name = nameEditText.getText().toString();
        }

        String category = null;
        if(categoryCheckBox.isChecked()) {
            category = categoryEditText.getText().toString();
        }

        String address = null;
        if(addressCheckBox.isChecked()) {
            address = addressEditText.getText().toString();
        }

        Call<ArrayList<GETRestaurantResponse>> call;
        if(category == null) {
            call = RestaurantEndpoints.restaurantEndpoints.getRestaurants(priceEquals, priceLte, priceGte, city, name, address);
        } else {
            call = RestaurantCategoriesEndpoints.restaurantCategoriesEndpoints.getRestaurantsByCategory(category, priceEquals, priceLte, priceGte, city, name, address);
        }

        call.enqueue(new Callback<ArrayList<GETRestaurantResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<GETRestaurantResponse>> call, Response<ArrayList<GETRestaurantResponse>> response) {
                if (response.code() == ResponseCodes.HTTP_OK) {
                    if (restaurants != null) {
                        restaurants.clear();
                    }
                    restaurants.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(RestaurantsFragment.this.getContext(), "Failed to load restaurants", Toast.LENGTH_LONG).show();
                }

                resultNumber.setText("Results: " + adapter.getItemCount());
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ArrayList<GETRestaurantResponse>> call, Throwable t) {
                Toast.makeText(RestaurantsFragment.this.getContext(), getString(R.string.network_failed_message), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                resultNumber.setText("Results: Failed!");
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_restaurant_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item_filter) {
            drawerLayout.openDrawer(GravityCompat.END);
            return true;
        }

        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
