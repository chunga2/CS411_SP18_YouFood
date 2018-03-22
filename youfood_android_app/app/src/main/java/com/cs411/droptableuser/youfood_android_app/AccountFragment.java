package com.cs411.droptableuser.youfood_android_app;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AccountFragment extends Fragment {
    Unbinder unbinder;

    @BindView(R.id.text_username)
    TextView name;

    @BindView(R.id.text_email)
    TextView email;

    @BindView(R.id.text_account_value)
    TextView accountType;

    @BindView(R.id.reviews_list)
    RecyclerView userReviewsList;

    @BindView(R.id.edit_account_button)
    Button editAccount;

    @BindView(R.id.delete_account_button)
    Button deleteAccount;

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        unbinder = ButterKnife.bind(this, rootView);



        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
