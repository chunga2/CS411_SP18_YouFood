package com.cs411.droptableuser.youfood_android_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.UserEndpoints;
import com.cs411.droptableuser.youfood_android_app.requests.PUTUserRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JunYoung on 2018. 3. 22..
 */

public class EditAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String EMPTY_STRING = "";

    @BindView(R.id.toolbar_editname)
    Toolbar toolbar;
    @BindView(R.id.edittext_editname)
    EditText editTextUserName;
    @BindView(R.id.text_editname_layout_editname)
    TextInputLayout textInputLayoutUserName;
    @BindView(R.id.edittext_edit_account_current_password)
    EditText editTextCurrentPassword;
    @BindView(R.id.text_layout_edit_account_current_password)
    TextInputLayout textInputLayoutCurrentPassword;
    @BindView(R.id.edittext_edit_account_new_password)
    EditText editTextNewPassword;
    @BindView(R.id.text_layout_edit_account_new_password)
    TextInputLayout textInputLayoutNewPassword;
    @BindView(R.id.edittext_edit_account_confirm_password)
    EditText editTextConfirmPassword;
    @BindView(R.id.text_layout_edit_account_confirm_password)
    TextInputLayout textInputLayoutConfirmPassword;
    @BindView(R.id.button_edit_account_delete)
    Button buttonDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(EMPTY_STRING);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextUserName.setText(UtilsCache.getName());

        buttonDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_edit_account_delete) {
            deleteAccount();
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return TRUE for the menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_name_toolbar, menu);

        return true;
    }

    /**
     * This method is called whenever the send item is selected.
     *
     * If the username is empty, the text input layout sets an error message that will be
     * displayed below the EditText.
     *
     * If the username is valid, the method uses updateUserName() to modify the username.
     *
     * @see EditAccountActivity#updateAccount(String, String)
     * @param item The menu item that was selected
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_send) {
            String userName = editTextUserName.getText().toString();
            String currentPassword = editTextCurrentPassword.getText().toString();
            String newPassword = editTextNewPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            textInputLayoutUserName.setErrorEnabled(false);
            textInputLayoutNewPassword.setErrorEnabled(false);
            textInputLayoutCurrentPassword.setErrorEnabled(false);
            textInputLayoutConfirmPassword.setErrorEnabled(false);


            if (userName.length() == 0) {
                textInputLayoutUserName.setError("Name cannot be updated to empty");
            }
            if (currentPassword.length() > 0 && !UtilsCache.getPassword().equals(currentPassword)) {
                textInputLayoutCurrentPassword.setError("Your old password was entered " +
                        "incorrectly");
            }
            if ((newPassword.length() != 0 || confirmPassword.length() != 0)
                    && currentPassword.length() == 0) {
                textInputLayoutCurrentPassword.setError("Current password should be entered in " +
                        "order to update");
            }
            if((currentPassword.length() > 0 || confirmPassword.length() > 0)
                    && newPassword.length() == 0) {
                textInputLayoutNewPassword.setError("New password cannot be updated to empty");
            }
            if ((newPassword.length() != 0 || confirmPassword.length() != 0) &&
                    !newPassword.equals(confirmPassword)) {
                textInputLayoutConfirmPassword.setError("Passwords must be same");
            }

            if((userName.length() != 0)
                    && (currentPassword.length() == 0)
                    && (newPassword.length() == 0)
                    && (confirmPassword.length() == 0)) {
                updateAccount(userName, UtilsCache.getPassword());
            } else if ((userName.length() != 0)
                    && (currentPassword.equals(UtilsCache.getPassword()))
                    && ((newPassword.length() != 0) || (confirmPassword.length() != 0))
                    && newPassword.equals(confirmPassword)) {
                updateAccount(userName, newPassword);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Update the user's textViewUserName.
     *
     * After updating the textViewUserName successfully, this method calls finish() to close the current activity.
     *
     * @param userName The new user textViewUserName.
     */
    private void updateAccount(final String userName, final String password) {
        Call<Void> call
                = UserEndpoints.userEndpoints.updateUser(
                new PUTUserRequest(UtilsCache.getEmail(), userName, password));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.code() == ResponseCodes.HTTP_NO_RESPONSE) {
                    UtilsCache.storeName(userName);
                    UtilsCache.storePassword(password);
                    Intent intent = new Intent();

                    setResult(response.code(), intent);
                    finish();
                } else {
                    Toast.makeText(EditAccountActivity.this, "Server error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(EditAccountActivity.this, getString(R.string.network_failed_message), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete this account?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Call<Void> call = UserEndpoints.userEndpoints.deleteUser(UtilsCache.getEmail());
                Log.e("AccountFragment", call.request().url().toString());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if(response.code() == ResponseCodes.HTTP_NO_RESPONSE) {
                            UtilsCache.clear();

                            Toast.makeText(
                                    EditAccountActivity.this,
                                    "Deleted Account!",
                                    Toast.LENGTH_LONG
                            ).show();

                            Intent intent = new Intent(EditAccountActivity.this,
                                    LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(
                                    EditAccountActivity.this,
                                    "Failed To Delete Account!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(
                                EditAccountActivity.this,
                                getString(R.string.network_failed_message),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    /**
     * This method is called whenever the user chooses to navigate Up within this activity from
     * the action bar.
     *
     * @return True, UP navigation completed successfully.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }
}
