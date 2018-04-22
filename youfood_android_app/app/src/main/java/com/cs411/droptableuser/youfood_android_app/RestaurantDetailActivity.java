package com.cs411.droptableuser.youfood_android_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.RestaurantEndpoints;
import com.cs411.droptableuser.youfood_android_app.endpoints.ReviewEndpoints;
import com.cs411.droptableuser.youfood_android_app.responses.GETRestaurantResponse;
import com.cs411.droptableuser.youfood_android_app.responses.GETRestaurantStatisticsResponse;
import com.cs411.droptableuser.youfood_android_app.responses.GETReviewResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, View.OnClickListener {
    public static final String RESTAURANT_KEY = "Restaurant";
    public static final String CATEGORIES_KEY = "Categories";
    public static final String PRICE_RANGE_KEY = "PriceRange";
    public static final int POST_REVIEW_REQUEST = 44;

    public RestaurantReviewsRecyclerViewAdpater adapter;

    @BindView(R.id.rating_bar_detail_aggregate_rating)
    RatingBar ratingBarAggregateRating;
    @BindView(R.id.text_detail_restaurant_name)
    TextView textViewName;
    @BindView(R.id.text_detail_average_cost)
    TextView textViewAverageCost;
    @BindView(R.id.text_detail_price_range)
    TextView textViewPriceRange;
    @BindView(R.id.text_detail_num_votes)
    TextView textViewNumVotes;
    @BindView(R.id.text_detail_cuisines)
    TextView textViewCuisines;
    @BindView(R.id.text_detail_location)
    TextView textViewLocation;
    @BindView(R.id.relative_layout_detail_clickable_review)
    RelativeLayout relativeLayoutWriteReview;
    @BindView(R.id.recycler_view_restaurant_detail_reviews)
    RecyclerView recyclerView;
    @BindView(R.id.textview_detail_username)
    TextView textViewUserName;

    GETRestaurantResponse restaurantObj;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent.hasExtra(RESTAURANT_KEY) && intent.hasExtra(CATEGORIES_KEY) && intent.hasExtra(PRICE_RANGE_KEY)) {
            restaurantObj = intent.getParcelableExtra("Restaurant");
            String cuisines = intent.getStringExtra(CATEGORIES_KEY);
            String priceRange = intent.getStringExtra(PRICE_RANGE_KEY);

            textViewName.setText(restaurantObj.getName());
            textViewCuisines.setText(cuisines);
            textViewLocation.setText(restaurantObj.getAddress());
            textViewPriceRange.setText(priceRange);
            textViewUserName.setText(UtilsCache.getName());

            String intPriceRange = restaurantObj.getPriceRange();
            if(intPriceRange != null) {
                switch(Integer.parseInt(intPriceRange)){
                    case 1:
                        textViewAverageCost.setText(R.string.one_dollar_sign_description);
                        break;
                    case 2:
                        textViewAverageCost.setText(R.string.two_dollar_signs_description);
                        break;
                    case 3:
                        textViewAverageCost.setText(R.string.three_dollar_signs_description);
                        break;
                    case 4:
                        textViewAverageCost.setText(R.string.four_dollar_signs_description);
                        break;
                    default:
                        textViewAverageCost.setText(R.string.one_dollar_sign_description);
                }
            }

            adapter = new RestaurantReviewsRecyclerViewAdpater(new ArrayList<GETReviewResponse>());

            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.google_map);
            mapFragment.getMapAsync(this);

            relativeLayoutWriteReview.setOnClickListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getReviews();
        setStatistics(restaurantObj.getName(), restaurantObj.getAddress());
    }

    private void setStatistics(String name, String address) {
        Call<GETRestaurantStatisticsResponse> call = RestaurantEndpoints.restaurantEndpoints.getRestaurantStatistics(name, address);
        call.enqueue(new Callback<GETRestaurantStatisticsResponse>() {
            @Override
            public void onResponse(Call<GETRestaurantStatisticsResponse> call, Response<GETRestaurantStatisticsResponse> response) {
                if(response.code() == ResponseCodes.HTTP_OK) {
                    textViewNumVotes.setText("(" + String.valueOf(response.body().getReviewCount()) + ")");
                    ratingBarAggregateRating.setRating(response.body().getReviewAverage()/2);
                } else {
                    textViewNumVotes.setText("(0)");
                    ratingBarAggregateRating.setRating(0);
                }
            }

            @Override
            public void onFailure(Call<GETRestaurantStatisticsResponse> call, Throwable t) {
                Toast.makeText(RestaurantDetailActivity.this, getString(R.string.network_failed_message), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getReviews() {
        Call<ArrayList<GETReviewResponse>> call
                = ReviewEndpoints.reviewEndpoints.getReviews(
                        "False",
                        restaurantObj.getName(),
                        restaurantObj.getAddress(),
                        null);

        call.enqueue(new Callback<ArrayList<GETReviewResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<GETReviewResponse>> call,
                                   @NonNull Response<ArrayList<GETReviewResponse>> response) {
                if(response.code() == ResponseCodes.HTTP_OK) {
                    Log.d("RestaurantDetailActivity", String.valueOf(response.code()));
                    adapter.setData(response.body());
                } else {
                    Log.d("RestaurantDetailActivity", String.valueOf(response.code()));
                    Toast.makeText(
                            RestaurantDetailActivity.this,
                            "Can't load reviews successfully.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GETReviewResponse>> call, Throwable t) {
                Toast.makeText(
                        RestaurantDetailActivity.this,
                        getString(R.string.network_failed_message),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng restaurantLatLng = new LatLng(restaurantObj.getLatitude(), restaurantObj.getLongitude());
        map.addMarker(new MarkerOptions().position(restaurantLatLng)
                .title(restaurantObj.getName()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLatLng, 15.5f));
        map.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        final String location = restaurantObj.getAddress();

        try {
            final String encodedLocation = URLEncoder.encode(location, "UTF-8");
            final Context context = this;

            Uri locationUri = Uri.parse("geo:0,0?q=" + encodedLocation);

            Intent intent = new Intent(Intent.ACTION_VIEW, locationUri);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Sorry, we can't open the capable map application.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.relative_layout_detail_clickable_review) {
            Intent intent = new Intent(this, PostReviewActivity.class);
            intent.putExtra(PostReviewActivity.RESTAURANT_NAME_KEY, restaurantObj.getName());
            intent.putExtra(PostReviewActivity.RESTAURANT_ADDRESS_KEY, restaurantObj.getAddress());
            startActivityForResult(intent, POST_REVIEW_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getReviews();
    }
}
