package com.cs411.droptableuser.youfood_android_app;

import android.content.ClipData;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by JunYoung on 2018. 4. 13..
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
