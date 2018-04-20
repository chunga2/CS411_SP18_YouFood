package com.cs411.droptableuser.youfood_android_app;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs411.droptableuser.youfood_android_app.responses.GETTransactionResponse;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by JunYoung on 2018. 4. 13..
 */

public class BudgetCardViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    public static final String TAG = "BudgetCardViewAdapter";

    private ArrayList<String> date;
    private ArrayList<GETTransactionResponse> weeklyTransactions;

    public BudgetCardViewAdapter(ArrayList<GETTransactionResponse> weeklyTransactions, ArrayList<String> date) {
        this.date = date;
        this.weeklyTransactions = weeklyTransactions;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(viewType, parent, false);

        BaseViewHolder holder = null;
        switch (viewType) {
            case R.layout.card_balance:
                holder = new BalanceViewHolder(view);
                break;
            case R.layout.card_expenses_chart:
                holder = new ExpensesPieChartViewHolder(view);
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        Log.d(TAG, String.valueOf(holder.getClass()));
        switch (holder.getItemViewType()) {
            case R.layout.card_expenses_chart:
                ExpensesPieChartViewHolder expensesHolder = (ExpensesPieChartViewHolder)holder;
                expensesHolder.bind(weeklyTransactions);
                break;
            case R.layout.card_balance:
                BalanceViewHolder balanceHolder = (BalanceViewHolder) holder;
                balanceHolder.bind(weeklyTransactions, date.get(0));
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return R.layout.card_balance;
            case 1:
                return R.layout.card_expenses_chart;
            default:
                return R.layout.card_balance;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
