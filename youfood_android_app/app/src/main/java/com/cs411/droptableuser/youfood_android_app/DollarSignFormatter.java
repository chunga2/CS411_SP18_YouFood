package com.cs411.droptableuser.youfood_android_app;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by JunYoung on 2018. 4. 14..
 */

public class DollarSignFormatter implements IValueFormatter {
    private DecimalFormat dollarSignFormat;

    public DollarSignFormatter() {
        dollarSignFormat = new DecimalFormat("###,###,###.00");
    }
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return "$" + dollarSignFormat.format(value);
    }
}

