package com.cs411.droptableuser.youfood_android_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.RestaurantEndpoints;
import com.cs411.droptableuser.youfood_android_app.endpoints.TransactionEndpoints;
import com.cs411.droptableuser.youfood_android_app.requests.POSTTransactionRequest;
import com.cs411.droptableuser.youfood_android_app.responses.GETRestaurantResponse;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JunYoung on 2018. 4. 14..
 */

public class AddTransactionActivity extends AppCompatActivity implements TextWatcher,
        View.OnClickListener {
    private static final String TAG = "AddTransactionActivity";
    private static final String DEFAULT_VALUE = "0.00";
    private static final String EQUAL_KEY = "Equal";
    private static final String ADDITION_KEY = "Addition";
    private static final String SUBTRACTION_KEY = "Subtraction";
    private static final String DIVISION_KEY = "Division";
    private static final String MULTIPLICATION_KEY = "Multiplication";

    private String date;
    private String restaurantName;
    private String restaurantAddress;
    private int userSelectedYear = 0;
    private int userSelectedMonth = 0;
    private int userSelectedDayOfMonth = 0;
    private float secondNum = 0.0f;
    private float totalSumOfFoodCost = 0.0f;
    private boolean hasAdded = false;
    private boolean hasDivided = false;
    private boolean hasMultiplied = false;
    private boolean hasSubtracted = false;
    private boolean isDefaultValue = true;
    private boolean isSecondFoodCost = false;
    private boolean isOperationClicked = false;

    AutoCompleteRestaurantAdapter adapter;
    List<GETRestaurantResponse> restaurants = new ArrayList<>();

    @BindView(R.id.toolbar_add_transaction)
    Toolbar toolbar;

    @BindView(R.id.auto_textview_add_transaction)
    AutoCompleteTextView autoCompleteTextViewRestaurant;
    @BindView(R.id.textview_add_transaction_expense)
    TextView textViewFoodCost;

    @BindView(R.id.button_add_transaction_date_picker)
    Button buttonDatePicker;

    @BindView(R.id.button_add_transaction_num_0)
    Button buttonNumZero;
    @BindView(R.id.button_add_transaction_num_1)
    Button buttonNumOne;
    @BindView(R.id.button_add_transaction_num_2)
    Button buttonNumTwo;
    @BindView(R.id.button_add_transaction_num_3)
    Button buttonNumThree;
    @BindView(R.id.button_add_transaction_num_4)
    Button buttonNumFour;
    @BindView(R.id.button_add_transaction_num_5)
    Button buttonNumFive;
    @BindView(R.id.button_add_transaction_num_6)
    Button buttonNumSix;
    @BindView(R.id.button_add_transaction_num_7)
    Button buttonNumSeven;
    @BindView(R.id.button_add_transaction_num_8)
    Button buttonNumEight;
    @BindView(R.id.button_add_transaction_num_9)
    Button buttonNumNine;

    @BindView(R.id.button_add_transaction_dot)
    Button buttonDot;
    @BindView(R.id.button_add_transaction_erase)
    Button buttonErase;
    @BindView(R.id.button_add_transaction_equal)
    Button buttonEqual;
    @BindView(R.id.button_add_transaction_division)
    Button buttonDivision;
    @BindView(R.id.button_add_transaction_addition)
    Button buttonAddition;
    @BindView(R.id.button_add_transaction_subtraction)
    Button buttonSubtraction;
    @BindView(R.id.button_add_transaction_multiplication)
    Button buttonMultiplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        ButterKnife.bind(this);

        Log.d(TAG, "rest addres: **************" + restaurantAddress);

        toolbar.setTitle("Add Food Cost");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        adapter = new AutoCompleteRestaurantAdapter(this, restaurants);

        autoCompleteTextViewRestaurant.addTextChangedListener(this);
        autoCompleteTextViewRestaurant.setAdapter(adapter);
        autoCompleteTextViewRestaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GETRestaurantResponse restaurant =
                        (GETRestaurantResponse) adapterView.getItemAtPosition(i);

                restaurantName = restaurant.getName();
                restaurantAddress = restaurant.getAddress();

                autoCompleteTextViewRestaurant.setText(restaurant.getName());
            }
        });

        buttonDatePicker.setOnClickListener(this);

        buttonNumZero.setOnClickListener(this);
        buttonNumOne.setOnClickListener(this);
        buttonNumTwo.setOnClickListener(this);
        buttonNumThree.setOnClickListener(this);
        buttonNumFour.setOnClickListener(this);
        buttonNumFive.setOnClickListener(this);
        buttonNumSix.setOnClickListener(this);
        buttonNumSeven.setOnClickListener(this);
        buttonNumEight.setOnClickListener(this);
        buttonNumNine.setOnClickListener(this);

        buttonDot.setOnClickListener(this);
        buttonErase.setOnClickListener(this);
        buttonEqual.setOnClickListener(this);
        buttonDivision.setOnClickListener(this);
        buttonAddition.setOnClickListener(this);
        buttonSubtraction.setOnClickListener(this);
        buttonMultiplication.setOnClickListener(this);

        date = DateTime.getCurrentDateTime();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add_transaction_date_picker:
                createDatePicker();
                break;

            case R.id.button_add_transaction_num_0:
                addNumber("0");
                break;

            case R.id.button_add_transaction_num_1:
                addNumber("1");
                break;

            case R.id.button_add_transaction_num_2:
                addNumber("2");
                break;

            case R.id.button_add_transaction_num_3:
                addNumber("3");
                break;

            case R.id.button_add_transaction_num_4:
                addNumber("4");
                break;

            case R.id.button_add_transaction_num_5:
                addNumber("5");
                break;

            case R.id.button_add_transaction_num_6:
                addNumber("6");
                break;

            case R.id.button_add_transaction_num_7:
                addNumber("7");
                break;

            case R.id.button_add_transaction_num_8:
                addNumber("8");
                break;

            case R.id.button_add_transaction_num_9:
                addNumber("9");
                break;

            case R.id.button_add_transaction_dot:
                if (!textViewFoodCost.getText().toString().contains(".")) {
                    String result = textViewFoodCost.getText() + ".";
                    textViewFoodCost.setText(result);
                }

                break;

            case R.id.button_add_transaction_erase:
                String currentExpense = textViewFoodCost.getText().toString();
                if (currentExpense.length() > 1 && !currentExpense.equals(DEFAULT_VALUE)) {
                    textViewFoodCost.setText(currentExpense.substring(0, currentExpense.length() - 1));
                } else if (currentExpense.length() == 1 && totalSumOfFoodCost != 0) {
                    textViewFoodCost.setText(String.valueOf(totalSumOfFoodCost));
                    totalSumOfFoodCost = 0.0f;
                } else {
                    isDefaultValue = true;
                    textViewFoodCost.setText(DEFAULT_VALUE);
                    totalSumOfFoodCost = 0.0f;
                }

                break;

            case R.id.button_add_transaction_addition:
                float currentFoodCost = Float.valueOf(textViewFoodCost.getText().toString());
                if (totalSumOfFoodCost == 0) {
                    totalSumOfFoodCost = currentFoodCost;
                } else if (isSecondFoodCost) {
                    performPreCalculation(currentFoodCost);
                    textViewFoodCost.setText(String.valueOf(totalSumOfFoodCost));
                }
                controlOperations(ADDITION_KEY);

                break;

            case R.id.button_add_transaction_subtraction:
                currentFoodCost = Float.valueOf(textViewFoodCost.getText().toString());
                if (totalSumOfFoodCost == 0) {
                    totalSumOfFoodCost = currentFoodCost;
                } else if (isSecondFoodCost) {
                    performPreCalculation(currentFoodCost);
                    textViewFoodCost.setText(String.valueOf(totalSumOfFoodCost));
                }
                controlOperations(SUBTRACTION_KEY);

                break;

            case R.id.button_add_transaction_division:
                currentFoodCost = Float.valueOf(textViewFoodCost.getText().toString());
                if (totalSumOfFoodCost == 0) {
                    totalSumOfFoodCost = currentFoodCost;
                } else if (isSecondFoodCost) {
                    performPreCalculation(currentFoodCost);
                    textViewFoodCost.setText(String.valueOf(totalSumOfFoodCost));
                }
                controlOperations(DIVISION_KEY);

                break;

            case R.id.button_add_transaction_multiplication:
                currentFoodCost = Float.valueOf(textViewFoodCost.getText().toString());
                if (totalSumOfFoodCost == 0) {
                    totalSumOfFoodCost = currentFoodCost;
                } else if (isSecondFoodCost) {
                    performPreCalculation(currentFoodCost);
                    textViewFoodCost.setText(String.valueOf(totalSumOfFoodCost));
                }
                controlOperations(MULTIPLICATION_KEY);

                break;

            case R.id.button_add_transaction_equal:
                currentFoodCost = Float.valueOf(textViewFoodCost.getText().toString());
                if (isSecondFoodCost) {
                    performPreCalculation(currentFoodCost);
                    textViewFoodCost.setText(String.valueOf(totalSumOfFoodCost));
                }

                controlOperations(EQUAL_KEY);
        }
    }

    private void controlOperations(String operation) {
        if ("Addition".equals(operation)) {
            hasAdded = true;
            hasDivided = false;
            hasSubtracted = false;
            hasMultiplied = false;
            isOperationClicked = true;
        } else if ("Subtraction".equals(operation)) {
            hasSubtracted = true;
            hasAdded = false;
            hasDivided = false;
            hasMultiplied = false;
            isOperationClicked = true;
        } else if ("Multiplication".equals(operation)) {
            hasMultiplied = true;
            hasAdded = false;
            hasDivided = false;
            hasSubtracted = false;
            isOperationClicked = true;
        } else if ("Division".equals(operation)) {
            hasDivided = true;
            hasAdded = false;
            hasMultiplied = false;
            hasSubtracted = false;
            isOperationClicked = true;
        } else if ("Equal".equals(operation)) {
            hasDivided = false;
            hasAdded = false;
            hasMultiplied = false;
            hasSubtracted = false;
            isOperationClicked = true;
        }
    }

    private void performPreCalculation(float currentFoodCost) {
        if (hasAdded) {
            hasAdded = false;
            totalSumOfFoodCost += currentFoodCost;
        } else if (hasSubtracted) {
            hasSubtracted = false;
            totalSumOfFoodCost -= currentFoodCost;
        } else if (hasDivided) {
            hasDivided = false;
            totalSumOfFoodCost /= currentFoodCost;
        } else if (hasMultiplied) {
            hasMultiplied = false;
            totalSumOfFoodCost *= currentFoodCost;
        }

        isSecondFoodCost = false;
    }

    private void addNumber(String num) {
        String currentExpense = textViewFoodCost.getText().toString();
        if (isDefaultValue || isOperationClicked) {
            if (isOperationClicked) {
                isSecondFoodCost = true;
            }
            isDefaultValue = false;
            isOperationClicked = false;
            textViewFoodCost.setText(num);
        } else {
            String result = currentExpense + num;
            textViewFoodCost.setText(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_transaction_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_add_transaction:
                if (restaurantName == null || restaurantAddress == null) {
                    Toast.makeText(this, "Please search the restaurant", Toast.LENGTH_SHORT).show();
                } else if (Double.valueOf(textViewFoodCost.getText().toString()) == 0
                        && totalSumOfFoodCost == 0) {
                    Toast.makeText(this, "Please fill in the amount", Toast.LENGTH_SHORT).show();
                } else {
                    addTransaction();
                }
                break;
        }

        return true;
    }

    private void addTransaction() {
        final double foodCost = formatDecimalPlaces(Double.valueOf(textViewFoodCost.getText().toString()));

        Call<Void> call = TransactionEndpoints.transactionEndpoints.createTransaction(
                new POSTTransactionRequest(
                        restaurantName,
                        restaurantAddress,
                        date,
                        foodCost,
                        UtilsCache.getEmail()
                ));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == ResponseCodes.HTTP_CREATED) {
                    Log.d(TAG, "Transaction is added successfully: " + restaurantName + " " + restaurantAddress + " " + date
                    + " " + foodCost + " " + UtilsCache.getEmail());
                    Intent intent = new Intent();
                    setResult(response.code(), intent);
                    finish();
                } else {
                    Toast.makeText(
                            AddTransactionActivity.this,
                            "Can't add the transaction.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(
                        AddTransactionActivity.this,
                        getString(R.string.network_failed_message),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        getFilteredRestaurants(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void getFilteredRestaurants(String restaurantName) {
        Call<ArrayList<GETRestaurantResponse>> call
                = RestaurantEndpoints.restaurantEndpoints.getRestaurants(
                null, null, null, null, restaurantName, null
        );

        call.enqueue(new Callback<ArrayList<GETRestaurantResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<GETRestaurantResponse>> call, Response<ArrayList<GETRestaurantResponse>> response) {
                if (response.code() == ResponseCodes.HTTP_OK) {
                    Log.d(TAG, String.valueOf(response.body().size()));
                    ArrayList<GETRestaurantResponse> searchedRestaurants = response.body();

                    restaurants.clear();
                    restaurants.addAll(searchedRestaurants);

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GETRestaurantResponse>> call, Throwable t) {

            }
        });
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
        date.show(getSupportFragmentManager(), "Date Picker");
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
        }
    };

    private Double formatDecimalPlaces(Double foodCost) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        return Double.valueOf(decimalFormat.format(foodCost));
    }
}
