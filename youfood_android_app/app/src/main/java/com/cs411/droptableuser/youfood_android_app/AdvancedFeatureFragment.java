package com.cs411.droptableuser.youfood_android_app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AdvancedFeatureFragment extends Fragment {
    Unbinder unbinder;

    public static AdvancedFeatureFragment newInstance() {
        AdvancedFeatureFragment fragment = new AdvancedFeatureFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_advanced_feature, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(false);

        return inflater.inflate(R.layout.fragment_advanced_feature, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
