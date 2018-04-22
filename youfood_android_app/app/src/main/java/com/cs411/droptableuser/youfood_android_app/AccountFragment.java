package com.cs411.droptableuser.youfood_android_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.RecommendationEndpoints;
import com.cs411.droptableuser.youfood_android_app.endpoints.UserEndpoints;
import com.cs411.droptableuser.youfood_android_app.responses.GETRecommendationResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment implements View.OnClickListener {
    private static final int EDIT_NAME_REQUEST = 2;

    Unbinder unbinder;

    @BindView(R.id.textview_account_username)
    TextView textViewUserName;
    @BindView(R.id.textview_account_email)
    TextView textViewEmail;
    @BindView(R.id.textview_account_accvalue)
    TextView textViewAccountType;
    @BindView(R.id.textview_account_info)
    TextView textViewNoRecommendationsInfo;
    @BindView(R.id.imageview_account_info_sign)
    ImageView imageViewInfo;
    @BindView(R.id.button_account_edit_account)
    RelativeLayout buttonEditAccount;
    @BindView(R.id.button_account_logout)
    RelativeLayout buttonLogout;
    @BindView(R.id.relative_layout_account_viewholder)
    RelativeLayout layoutViewHolder;
    @BindView(R.id.recycler_view_account_myrecommendations)
    RecyclerView myRecommendationsRV;

    RecommendationsRecyclerViewAdapter adapter;
    RestaurantSelectedListener listener;

    public interface RestaurantSelectedListener {
        public void onRestaurantSelected(String restaurant, String address);
    }

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof  MainActivity) {
            MainActivity activity = (MainActivity) context;
            listener = (RestaurantSelectedListener) activity;
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter = null;
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(false);

        textViewUserName.setText(UtilsCache.getName());
        textViewEmail.setText(UtilsCache.getEmail());
        if(UtilsCache.getIsOwner()) {
            textViewAccountType.setText(R.string.owner);
        } else {
            textViewAccountType.setText(R.string.user);
        }

        buttonEditAccount.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        myRecommendationsRV.setHasFixedSize(true);
        myRecommendationsRV.setNestedScrollingEnabled(false);
        myRecommendationsRV.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        Call<ArrayList<GETRecommendationResponse>> call = RecommendationEndpoints.recommendationEndpoints.getRecommendationsForUser(UtilsCache.getEmail());
        call.enqueue(new Callback<ArrayList<GETRecommendationResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<GETRecommendationResponse>> call, Response<ArrayList<GETRecommendationResponse>> response) {
                if(response.code() == ResponseCodes.HTTP_OK) {
                    if (response.body().size() == 0) {
                        myRecommendationsRV.setVisibility(View.INVISIBLE);
                        textViewNoRecommendationsInfo.setVisibility(View.VISIBLE);
                        imageViewInfo.setVisibility(View.VISIBLE);
                        layoutViewHolder.getLayoutParams().height = (int) getResources()
                                .getDimension(R.dimen.viewholder_height);
                    } else {
                        myRecommendationsRV.setVisibility(View.VISIBLE);
                        textViewNoRecommendationsInfo.setVisibility(View.GONE);
                        imageViewInfo.setVisibility(View.GONE);
                    }
                    adapter = new RecommendationsRecyclerViewAdapter(response.body(), AccountFragment.this.getContext(), listener);
                    myRecommendationsRV.setAdapter(adapter);
                } else {
                    Log.e("AccountFrag", String.valueOf(response.code()));
                    Toast.makeText(AccountFragment.this.getContext(), "Failed To Load Recommendations!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GETRecommendationResponse>> call, Throwable t) {
                Toast.makeText(AccountFragment.this.getContext(), R.string.network_failed_message, Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Call<ArrayList<GETRecommendationResponse>> call = RecommendationEndpoints.recommendationEndpoints.getRecommendationsForUser(UtilsCache.getEmail());
        call.enqueue(new Callback<ArrayList<GETRecommendationResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<GETRecommendationResponse>> call, Response<ArrayList<GETRecommendationResponse>> response) {
                if(response.code() == ResponseCodes.HTTP_OK) {
                    if(adapter != null) {
                        if (response.body().size() == 0) {
                            myRecommendationsRV.setVisibility(View.INVISIBLE);
                            textViewNoRecommendationsInfo.setVisibility(View.VISIBLE);
                            imageViewInfo.setVisibility(View.VISIBLE);
                            layoutViewHolder.getLayoutParams().height = (int) getResources()
                                    .getDimension(R.dimen.viewholder_height);
                        } else {
                            myRecommendationsRV.setVisibility(View.VISIBLE);
                            textViewNoRecommendationsInfo.setVisibility(View.GONE);
                            imageViewInfo.setVisibility(View.GONE);
                        }
                        adapter.setData(response.body());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("AccountFrag", String.valueOf(response.code()));
                    Toast.makeText(AccountFragment.this.getContext(), "Failed To Load Recommendations!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GETRecommendationResponse>> call, Throwable t) {
                Toast.makeText(AccountFragment.this.getContext(), R.string.network_failed_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_account_logout:
                UtilsCache.clear();
                Intent intent = new Intent(AccountFragment.this.getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.button_account_edit_account:
                //TODO implement this one
                intent = new Intent(getActivity(), EditAccountActivity.class);
                startActivityForResult(intent, EDIT_NAME_REQUEST);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_NAME_REQUEST && resultCode == 204) {
            textViewUserName.setText(UtilsCache.getName());
        }
    }
}
