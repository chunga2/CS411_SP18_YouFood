package com.cs411.droptableuser.youfood_android_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantDetailActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        ButterKnife.bind(this);

        textViewName.setText(R.string.temp_restaurant_name);
        textViewCuisines.setText(R.string.temp_restaurant_cuisines);
        textViewLocation.setText(R.string.temp_restaurant_location);
        textViewNumVotes.setText(R.string.temp_restaurant_num_votes);
        textViewPriceRange.setText(R.string.temp_restaurant_price_range);
        textViewAverageCost.setText(R.string.temp_restaurant_average_cost);
        ratingBarAggregateRating.setRating(5);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Temporary Siebel center coordinate.
        LatLng restaurantLatLng = new LatLng(40.113819, -88.225036);
        map.addMarker(new MarkerOptions().position(restaurantLatLng)
                .title(getString(R.string.temp_restaurant_name)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(restaurantLatLng, 16);
        //map.moveCamera(CameraUpdateFactory.newLatLng(restaurantLatLng));
        map.animateCamera(cameraUpdate);
        map.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        final String location = getString(R.string.temp_restaurant_location);

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
}
