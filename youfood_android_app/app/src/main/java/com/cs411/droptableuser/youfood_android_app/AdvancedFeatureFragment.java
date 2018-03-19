package com.cs411.droptableuser.youfood_android_app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AdvancedFeatureFragment extends Fragment {
    public static AdvancedFeatureFragment newInstance() {
        AdvancedFeatureFragment fragment = new AdvancedFeatureFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advanced_feature, container, false);
    }
}
