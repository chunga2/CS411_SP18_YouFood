package com.cs411.droptableuser.youfood_android_app;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.bottom_naviagation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));
        setActionBar();

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.menu_restaurants:
                                selectedFragment = RestaurantsFragment.newInstance();
                                getSupportActionBar().setTitle("Restaurants");
                                break;
                            case R.id.menu_budget_temp:
                                selectedFragment = AdvancedFeatureFragment.newInstance();
                                getSupportActionBar().setTitle("Budget");
                                break;
                            case R.id.menu_account:
                                selectedFragment = AccountFragment.newInstance();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private void setActionBar() {
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }
}
