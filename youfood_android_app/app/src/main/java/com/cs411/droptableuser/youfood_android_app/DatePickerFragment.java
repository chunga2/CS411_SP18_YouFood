package com.cs411.droptableuser.youfood_android_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by JunYoung on 2018. 4. 15..
 */

public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener onDateSetListener;
    private int year;
    private int month;
    private int day;

    public void setCallBack(DatePickerDialog.OnDateSetListener onDate) {
        onDateSetListener = onDate;
    }

    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), R.style.DatePickerDialogStyle, onDateSetListener,
                year, month, day);
    }
}
