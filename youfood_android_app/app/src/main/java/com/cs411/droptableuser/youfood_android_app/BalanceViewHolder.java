package com.cs411.droptableuser.youfood_android_app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs411.droptableuser.youfood_android_app.requests.GETBudgetResponse;

import butterknife.BindView;

/**
 * Created by JunYoung on 2018. 4. 13..
 */

public class BalanceViewHolder extends BaseViewHolder {
    @BindView(R.id.textview_balance)
    TextView textViewBalance;
    @BindView(R.id.imageview_edit_balance)
    ImageView imageViewEditBalance;

    public BalanceViewHolder(View itemView) {
        super(itemView);
    }

    void bind(GETBudgetResponse item) {
    }
}
