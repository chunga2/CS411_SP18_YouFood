package com.cs411.droptableuser.youfood_android_app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.TransactionEndpoints;
import com.cs411.droptableuser.youfood_android_app.requests.GETBudgetResponse;
import com.cs411.droptableuser.youfood_android_app.responses.GETTransactionResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetFragment extends Fragment {
    private static final String TAG = "BudgetFragment";

    private ArrayList<GETBudgetResponse> budget;
    private ArrayList<GETTransactionResponse> weeklyTransactions = new ArrayList<>();

    private Unbinder unbinder;
    private BudgetCardViewAdapter adapter;

    @BindView(R.id.recycler_view_budget)
    RecyclerView recyclerView;

    public static BudgetFragment newInstance() {
        BudgetFragment fragment = new BudgetFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWeeklyTransactions();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budget, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(false);

        adapter = new BudgetCardViewAdapter(weeklyTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        getWeeklyTransactions();
    }*/

    private void getWeeklyTransactions() {
        Call<ArrayList<GETTransactionResponse>> call =
                TransactionEndpoints.transactionEndpoints.getWeeklyTransactions(
                        UtilsCache.getEmail(),
                        DateTime.getCurrentDateTime()
                );

        call.enqueue(new Callback<ArrayList<GETTransactionResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<GETTransactionResponse>> call, Response<ArrayList<GETTransactionResponse>> response) {
                Log.d(TAG, String.valueOf(response.code()));
                if (weeklyTransactions != null) {
                    weeklyTransactions.clear();
                }
                if (response.code() == ResponseCodes.HTTP_OK) {
                    //Log.d(TAG, String.valueOf(response.body().size()));
                    if (response.body() != null) {
                        weeklyTransactions.addAll(response.body());
                    }
                } else {
                    Toast.makeText(
                            getActivity(),
                            "Can't get the weekly transactions.",
                            Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<GETTransactionResponse>> call, Throwable t) {
                Toast.makeText(
                        getActivity(),
                        getString(R.string.network_failed_message),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
