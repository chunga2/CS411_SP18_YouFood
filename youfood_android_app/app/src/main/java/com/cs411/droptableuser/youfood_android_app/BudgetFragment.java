package com.cs411.droptableuser.youfood_android_app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.TransactionEndpoints;
import com.cs411.droptableuser.youfood_android_app.requests.GETBudgetResponse;
import com.cs411.droptableuser.youfood_android_app.responses.GETTransactionResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "BudgetFragment";

    private int userSelectedYear = 0;
    private int userSelectedMonth = 0;
    private int userSelectedDayOfMonth = 0;
    private String date;
    private ArrayList<String> dateList = new ArrayList<>();
    private ArrayList<GETTransactionResponse> weeklyTransactions = new ArrayList<>();

    private Unbinder unbinder;
    private BudgetCardViewAdapter adapter;

    @BindView(R.id.recycler_view_budget)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_budget)
    SwipeRefreshLayout swipeLayout;

    public static BudgetFragment newInstance() {
        BudgetFragment fragment = new BudgetFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = DateTime.getCurrentDateTime();
        dateList.add(date);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budget, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);

        Context context = getActivity();

        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(
                ContextCompat.getColor(context, R.color.Red1),
                ContextCompat.getColor(context, R.color.Red2),
                ContextCompat.getColor(context, R.color.Red3)
        );

        adapter = new BudgetCardViewAdapter(weeklyTransactions, dateList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        getWeeklyTransactions();

        return rootView;
    }

    protected void getWeeklyTransactions() {
        Call<ArrayList<GETTransactionResponse>> call =
                TransactionEndpoints.transactionEndpoints.getWeeklyTransactions(
                        UtilsCache.getEmail(), date
                );

        call.enqueue(new Callback<ArrayList<GETTransactionResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<GETTransactionResponse>> call, Response<ArrayList<GETTransactionResponse>> response) {
                Log.d(TAG, String.valueOf(response.code()));
                if (weeklyTransactions != null) {
                    weeklyTransactions.clear();
                }
                if (response.code() == ResponseCodes.HTTP_OK) {
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
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ArrayList<GETTransactionResponse>> call, Throwable t) {
                Toast.makeText(
                        getActivity(),
                        getString(R.string.network_failed_message),
                        Toast.LENGTH_LONG).show();
                swipeLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_budget_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_pick_date:
                createDatePicker();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createDatePicker() {
        Calendar calendar = Calendar.getInstance();
        Bundle args = new Bundle();

        if (userSelectedYear == 0) {
            args.putInt("year", calendar.get(Calendar.YEAR));
            args.putInt("month", calendar.get(Calendar.MONTH));
            args.putInt("day", calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            args.putInt("year", userSelectedYear);
            args.putInt("month", userSelectedMonth);
            args.putInt("day", userSelectedDayOfMonth);
        }

        DatePickerFragment date = new DatePickerFragment();

        date.setArguments(args);
        date.setCallBack(onDateSetListener);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            userSelectedYear = year;
            userSelectedMonth = month;
            userSelectedDayOfMonth = dayOfMonth;

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",
                    Locale.getDefault());

            date = dateFormat.format(c.getTime());
            dateList.clear();
            dateList.add(date);
            getWeeklyTransactions();
        }
    };

    @Override
    public void onRefresh() {
        getWeeklyTransactions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
