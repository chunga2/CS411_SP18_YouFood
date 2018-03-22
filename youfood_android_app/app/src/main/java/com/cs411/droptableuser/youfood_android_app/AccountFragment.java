package com.cs411.droptableuser.youfood_android_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cs411.droptableuser.youfood_android_app.endpoints.UserEndpoints;

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
    @BindView(R.id.button_account_logout)
    Button buttonLogout;
    @BindView(R.id.button_account_edit_account)
    Button buttonEditAccount;
    @BindView(R.id.button_account_edit_username)
    Button buttonEditUserName;
    @BindView(R.id.button_account_delete_account)
    Button buttonDeleteAccount;
    @BindView(R.id.recycler_view_account_myreviews)
    RecyclerView recyclerViewUserReviews;

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        textViewUserName.setText(UtilsCache.getName());
        textViewEmail.setText(UtilsCache.getEmail());
        if(UtilsCache.getIsOwner()) {
            textViewAccountType.setText(R.string.owner);
        } else {
            textViewAccountType.setText(R.string.user);
        }

        buttonLogout.setOnClickListener(this);
        buttonEditAccount.setOnClickListener(this);
        buttonEditUserName.setOnClickListener(this);
        buttonDeleteAccount.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_account_delete_account:
                deleteAccount();
                break;
            case R.id.button_account_logout:
                SharedPreferences.Editor editor = UtilsCache.prefs.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(AccountFragment.this.getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.button_account_edit_account:
                //TODO implement this one
                break;
            case R.id.button_account_edit_username:
                intent = new Intent(getActivity(), EditNameActivity.class);
                intent.putExtra("username", UtilsCache.getName());
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
            String userName = data.getExtras().getString("username");

            textViewUserName.setText(userName);
        }
    }

    private void deleteAccount() {
        Call<Void> call = UserEndpoints.userEndpoints.deleteUser(UtilsCache.getEmail());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.code() == ResponseCodes.HTTP_NO_RESPONSE) {
                    SharedPreferences.Editor editor = UtilsCache.prefs.edit();
                    editor.clear();
                    editor.apply();

                    Toast.makeText(
                            AccountFragment.this.getContext(),
                            "Deleted Account!",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(AccountFragment.this.getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(
                            AccountFragment.this.getContext(),
                            "Failed To Delete Account!",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(
                        AccountFragment.this.getContext(),
                        getString(R.string.network_failed_message),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
