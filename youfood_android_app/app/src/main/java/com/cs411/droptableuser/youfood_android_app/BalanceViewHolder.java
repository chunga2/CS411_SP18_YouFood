package com.cs411.droptableuser.youfood_android_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.BudgetEndpoints;
import com.cs411.droptableuser.youfood_android_app.requests.GETBudgetResponse;
import com.cs411.droptableuser.youfood_android_app.requests.POSTBudgetRequest;
import com.cs411.droptableuser.youfood_android_app.requests.PUTBudgetRequest;
import com.cs411.droptableuser.youfood_android_app.responses.GETTransactionResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JunYoung on 2018. 4. 13..
 */

public class BalanceViewHolder extends BaseViewHolder implements View.OnClickListener {
    private static final String NO_BUDGET = "No Budget Set!";

    @BindView(R.id.textview_balance)
    TextView textViewBalance;

    @BindView(R.id.text_total_spent)
    TextView textViewTotalSpent;

    @BindView(R.id.text_balance_date_range)
    TextView textViewDateRange;

    @BindView(R.id.imageview_edit_balance)
    ImageView imageViewEditBalance;

    private GETBudgetResponse budget;
    private ArrayList<GETTransactionResponse> transactionsList;

    public BalanceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        imageViewEditBalance.setOnClickListener(this);
        budget = null;
    }

    void bind(ArrayList<GETTransactionResponse> transactions) {
        transactionsList = transactions;
        String currentDate = DateTime.getCurrentDateTime();
        String firstDayMonth = DateTime.getFirstDayMonthOfWeek(currentDate);
        String lastDayMonth = DateTime.getLastDayMonthOfWeek(currentDate);

        StringBuilder builder = new StringBuilder(firstDayMonth);
        builder.append(" - ");
        builder.append(lastDayMonth);
        textViewDateRange.setText(builder.toString());

        String firstDayFormatted = DateTime.getFirstDateOfWeek(currentDate);
        String lastDayFormatted = DateTime.getLastDateOfWeek(currentDate);

        Call<ArrayList<GETBudgetResponse>> call = BudgetEndpoints.budgetEndpoints.getBudgets(firstDayFormatted,
                lastDayFormatted, UtilsCache.getEmail(), null);

        call.enqueue(new Callback<ArrayList<GETBudgetResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<GETBudgetResponse>> call, Response<ArrayList<GETBudgetResponse>> response) {
                if(response.code() == ResponseCodes.HTTP_OK) {
                    if (response.body().size() == 0) {
                        textViewBalance.setText(NO_BUDGET);
                    } else {
                        ArrayList<GETBudgetResponse> body = response.body();
                        budget = body.get(0);
                        String total = budget.getTotal();
                        textViewBalance.setText(total);
                    }

                    if(transactionsList.size() > 0) {
                        double totalSpent = 0.0;
                        for(GETTransactionResponse transaction : transactionsList) {
                            totalSpent += Double.parseDouble(transaction.getAmount().substring(1));
                        }

                        textViewTotalSpent.setText(String.format("$%.2f", totalSpent));
                        if((budget != null) && (totalSpent > Double.parseDouble(budget.getTotal().substring(1))))  {
                            textViewTotalSpent.setTextColor(Color.parseColor("#E1374F"));
                        } else {
                            textViewTotalSpent.setTextColor(Color.parseColor("#00C853"));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GETBudgetResponse>> call, Throwable t) {}
        });
    }

    @Override
    public void onClick(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
        final EditText input = new EditText(itemView.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(params);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        if(budget == null) {
            builder.setTitle("Create Budget");
            StringBuilder stringBuilder = new StringBuilder("Create Budget For Week: ");
            stringBuilder.append(textViewDateRange.getText().toString());
            builder.setMessage(stringBuilder.toString());

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    double total = Double.parseDouble(input.getText().toString());
                    POSTBudgetRequest budgetRequest = new POSTBudgetRequest(DateTime.getCurrentDateTime(), UtilsCache.getEmail(), total);
                    Call<Void> call = BudgetEndpoints.budgetEndpoints.createBudget(budgetRequest);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.code() == ResponseCodes.HTTP_CREATED) {
                                // Bind will allow us to HTTP GET request for the budgets again and set the budget variable to the newly created one
                                bind(transactionsList);
                                Toast.makeText(itemView.getContext(), "Budget Created!", Toast.LENGTH_LONG).show();
                            } else {
                                Log.e("CreateBudget", String.valueOf(response.code()));
                                Toast.makeText(itemView.getContext(), "Budget Failed To Be Created", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {}
                    });
                }
            });
        } else {
            builder.setTitle("Update Budget Total");
            StringBuilder stringBuilder = new StringBuilder("Update Budget Total For Week: ");
            stringBuilder.append(textViewDateRange.getText().toString());
            builder.setMessage(stringBuilder.toString());

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    double total = Double.parseDouble(input.getText().toString());
                    PUTBudgetRequest budgetRequest = new PUTBudgetRequest(budget.getDate(), UtilsCache.getEmail(), total);
                    Call<Void> call = BudgetEndpoints.budgetEndpoints.updateBudget(budgetRequest);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.code() == ResponseCodes.HTTP_NO_RESPONSE) {
                                bind(transactionsList);
                                Toast.makeText(itemView.getContext(), "Budget Updated!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(itemView.getContext(), "Budget Failed To Be Updated", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {}
                    });
                }
            });
        }

        builder.show();
    }
}
