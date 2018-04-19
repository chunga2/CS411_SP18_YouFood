package com.cs411.droptableuser.youfood_android_app;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AccountFragment.RestaurantSelectedListener {
    public static final int ADD_TRANSACTION_REQUEST = 102;

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

        this.getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));
        setActionBar();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivityForResult(intent, ADD_TRANSACTION_REQUEST);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_up);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.menu_restaurants:
                                selectedFragment = RestaurantsFragment.newInstance();
                                floatingActionButton.setVisibility(View.GONE);
                                getSupportActionBar().setTitle("Restaurants");
                                break;
                            case R.id.menu_budget_temp:
                                selectedFragment = BudgetFragment.newInstance();
                                floatingActionButton.setVisibility(View.VISIBLE);
                                getSupportActionBar().setTitle("Budget");
                                break;
                            case R.id.menu_account:
                                selectedFragment = AccountFragment.newInstance();
                                floatingActionButton.setVisibility(View.GONE);
                                getSupportActionBar().setTitle("Account");
                                break;
                        }

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();

                        return true;
                    }
                });

        getSupportActionBar().setTitle("Restaurants");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, RestaurantsFragment.newInstance());
        transaction.commit();
    }

    private void setActionBar() {
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MenuItem item = bottomNavigationView.getMenu().findItem(R.id.menu_budget_temp);
        item.setChecked(true);
        getSupportActionBar().setTitle("Budget");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, BudgetFragment.newInstance());
        transaction.commit();
    }

    @Override
    public void onRestaurantSelected(String restaurant, String address) {
        MenuItem item = bottomNavigationView.getMenu().findItem(R.id.menu_restaurants);
        item.setChecked(true);
        getSupportActionBar().setTitle("Restaurants");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, RestaurantsFragment.newInstance(restaurant, address));
        transaction.commit();
    }
}
