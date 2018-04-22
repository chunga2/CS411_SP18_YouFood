package com.cs411.droptableuser.youfood_android_app;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.BudgetEndpoints;
import com.cs411.droptableuser.youfood_android_app.responses.GETBudgetStatistics;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by raajesharunachalam on 4/22/18.
 */

public class BudgetStatisticsViewHolder extends BaseViewHolder {
    @BindView(R.id.textview_success_budgets_actual)
    TextView textViewBudgetsSuccesful;

    @BindView(R.id.textview_average_spend_actual)
    TextView textViewAverageSpent;

    public BudgetStatisticsViewHolder(View itemView) {
        super(itemView);
    }

    void bind() {
        Call<GETBudgetStatistics> call = BudgetEndpoints.budgetEndpoints.getBudgetStatistics(UtilsCache.getEmail());

        call.enqueue(new Callback<GETBudgetStatistics>() {
            @Override
            public void onResponse(Call<GETBudgetStatistics> call, Response<GETBudgetStatistics> response) {
                if(response.code() == ResponseCodes.HTTP_OK) {
                    GETBudgetStatistics budgetStatistics = response.body();

                    StringBuilder builder = new StringBuilder();
                    builder.append(budgetStatistics.getNumWeeksSuccessful());
                    builder.append("/");
                    builder.append(budgetStatistics.getNumWeeks());
                    textViewBudgetsSuccesful.setText(builder.toString());

                    textViewAverageSpent.setText(budgetStatistics.getAverageWeekly());

                } else {
                    Toast.makeText(itemView.getContext(), "No Budgets Or No Transactions Within Budgets!", Toast.LENGTH_LONG).show();
                    // Only other response code is server error meaning that user didn't have any budgets, or didn't have any trasactions corresponding to those budgets
                    textViewBudgetsSuccesful.setText("0/0");
                    textViewAverageSpent.setText("$0.00");
                }
            }

            @Override
            public void onFailure(Call<GETBudgetStatistics> call, Throwable t) {
                Toast.makeText(itemView.getContext(), "No Budgets Or No Transactions Within Budgets!", Toast.LENGTH_LONG).show();
                // Only other response code is server error meaning that user didn't have any budgets, or didn't have any trasactions corresponding to those budgets
                textViewBudgetsSuccesful.setText("0/0");
                textViewAverageSpent.setText("$0.00");
            }
        });
    }
}
