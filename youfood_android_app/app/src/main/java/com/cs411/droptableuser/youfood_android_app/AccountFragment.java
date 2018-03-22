package com.cs411.droptableuser.youfood_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    Unbinder unbinder;

    @BindView(R.id.textview_account_username)
    TextView name;

    @BindView(R.id.textview_account_email)
    TextView email;

    @BindView(R.id.textview_account_accvalue)
    TextView accountType;

    @BindView(R.id.recycler_view_account_myreviews)
    RecyclerView userReviewsList;

    @BindView(R.id.button_account_edit_account)
    Button editAccount;

    @BindView(R.id.button_account_delete_account)
    Button deleteAccount;

    @BindView(R.id.button_account_logout)
    Button logout;

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        name.setText(UtilsCache.getName());
        email.setText(UtilsCache.getEmail());

        if(UtilsCache.getIsOwner() == true) {
            accountType.setText("Owner");
        } else {
            accountType.setText("User");
        }

        editAccount.setOnClickListener(this);
        deleteAccount.setOnClickListener(this);
        logout.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_account_delete_account:
                Call<Void> call = UserEndpoints.userEndpoints.deleteUser(UtilsCache.getEmail());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == ResponseCodes.HTTP_NO_RESPONSE) {
                            Toast.makeText(AccountFragment.this.getContext(), "Deleted Account!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AccountFragment.this.getContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(AccountFragment.this.getContext(), "Failed To Delete Account!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AccountFragment.this.getContext(), getString(R.string.network_failed_message), Toast.LENGTH_LONG).show();
                    }
                });
                break;

            case R.id.button_account_logout:
                Intent intent = new Intent(AccountFragment.this.getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;

            case R.id.button_account_edit_account:
                //TODO implement this one
                break;
        }
    }


}
