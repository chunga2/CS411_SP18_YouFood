package com.cs411.droptableuser.youfood_android_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.cs411.droptableuser.youfood_android_app.endpoints.RecommendationEndpoints;
import com.cs411.droptableuser.youfood_android_app.responses.GETRecommendationCountResponse;
import com.cs411.droptableuser.youfood_android_app.responses.GETRecommendationResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AccountFragment.RestaurantSelectedListener {
    private static final class Constants {
        static final int ADD_TRANSACTION_REQUEST = 102;
        static final String BUDGET_TAG = "Budget";
        static final String ACCOUNT_TAG = "Account";
        static final String RESTAURANTS_TAG = "Restaurants";
    }

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;
    @BindView(R.id.bottom_naviagation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.floating_action_button_main)
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setActionBar();
        this.getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivityForResult(intent, Constants.ADD_TRANSACTION_REQUEST);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_up);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        setFragmentTransaction(item);

                        return true;
                    }
                });

        bottomNavigationView.setSelectedItemId(R.id.menu_restaurants);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Call<GETRecommendationCountResponse> call = RecommendationEndpoints.recommendationEndpoints.getRecommendationCount(UtilsCache.getEmail());
        call.enqueue(new Callback<GETRecommendationCountResponse>() {
            @Override
            public void onResponse(Call<GETRecommendationCountResponse> call, Response<GETRecommendationCountResponse> response) {
                Log.e("RecommendationCount", String.valueOf(response.code()));
                if(response.code() == ResponseCodes.HTTP_OK) {
                    GETRecommendationCountResponse countResponse = response.body();
                    Log.e("ResponseCount", String.valueOf(countResponse.getRecommendationCount()));
                    Log.e("CacheCount", String.valueOf(UtilsCache.getRecommenationCount()));
                    if(countResponse.getRecommendationCount() > UtilsCache.getRecommenationCount()) {
                        int difference = countResponse.getRecommendationCount() - UtilsCache.getRecommenationCount();

                        // Alert dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("New Recommendations!");
                        StringBuilder message = new StringBuilder("You have ");
                        message.append(difference);
                        message.append(" new Recommendation(s). Check the Account tab to view them!");
                        builder.setMessage(message.toString());

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        builder.show();
                    }
                    // Store the updated recommendation count in the shared preference cache
                    UtilsCache.storeRecommendationCount(countResponse.getRecommendationCount());
                }
            }

            @Override
            public void onFailure(Call<GETRecommendationCountResponse> call, Throwable t) {}
        });
    }

    private void setFragmentTransaction(MenuItem item) {
        Fragment selectedFragment = RestaurantsFragment.newInstance();
        Fragment currentFragment = getSupportFragmentManager().getPrimaryNavigationFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (item.getItemId()) {
            case R.id.menu_restaurants:
                selectedFragment = getSupportFragmentManager().findFragmentByTag(Constants.RESTAURANTS_TAG);
                if (selectedFragment == null || RestaurantsFragment.isCheckButtonSelected) {
                    RestaurantsFragment.isCheckButtonSelected = false;
                    selectedFragment = RestaurantsFragment.newInstance();
                    transaction.add(R.id.frame_layout, selectedFragment, Constants.RESTAURANTS_TAG);
                    if (currentFragment != null) {
                        transaction.hide(currentFragment);
                    }
                } else if (currentFragment != selectedFragment) {
                    transaction.hide(currentFragment).show(selectedFragment);
                } else {
                    RestaurantsFragment fragment = (RestaurantsFragment) selectedFragment;
                    fragment.recyclerView.scrollToPosition(0);
                }
                floatingActionButton.setVisibility(View.GONE);
                getSupportActionBar().setTitle(Constants.RESTAURANTS_TAG);
                break;
            case R.id.menu_budget_temp:
                selectedFragment = getSupportFragmentManager().findFragmentByTag(Constants.BUDGET_TAG);
                if (selectedFragment == null) {
                    selectedFragment = BudgetFragment.newInstance();
                    transaction.add(R.id.frame_layout, selectedFragment, Constants.BUDGET_TAG);
                    if (currentFragment != null) {
                        transaction.hide(currentFragment);
                    }
                } else if(currentFragment != selectedFragment) {
                    transaction.hide(currentFragment).show(selectedFragment);
                }
                floatingActionButton.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle(Constants.BUDGET_TAG);
                break;
            case R.id.menu_account:
                selectedFragment = getSupportFragmentManager().findFragmentByTag(Constants.ACCOUNT_TAG);
                if (selectedFragment == null) {
                    selectedFragment = AccountFragment.newInstance();
                    transaction.add(R.id.frame_layout, selectedFragment, Constants.ACCOUNT_TAG);
                    if (currentFragment != null) {
                        transaction.hide(currentFragment);
                    }
                } else if (currentFragment != selectedFragment){
                    transaction.hide(currentFragment).show(selectedFragment);
                }
                floatingActionButton.setVisibility(View.GONE);
                getSupportActionBar().setTitle(Constants.ACCOUNT_TAG);
                break;
        }

        transaction.setPrimaryNavigationFragment(selectedFragment);
        transaction.commit();
    }

    private void setActionBar() {
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADD_TRANSACTION_REQUEST && resultCode == ResponseCodes.HTTP_CREATED) {
            bottomNavigationView.setSelectedItemId(R.id.menu_budget_temp);
            BudgetFragment fragment = (BudgetFragment) getSupportFragmentManager().findFragmentByTag(Constants.BUDGET_TAG);
            fragment.getWeeklyTransactions();
        }
    }

    @Override
    public void onRestaurantSelected(String restaurant, String address) {
        MenuItem item = bottomNavigationView.getMenu().findItem(R.id.menu_restaurants);
        item.setChecked(true);
        getSupportActionBar().setTitle("Restaurants");

        Fragment fragment = RestaurantsFragment.newInstance(restaurant, address);
        Fragment currentFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_layout,
                fragment,
                Constants.RESTAURANTS_TAG);
        transaction.hide(currentFragment);
        transaction.setPrimaryNavigationFragment(fragment);
        transaction.commit();
    }
}
