package com.cs411.droptableuser.youfood_android_app;

import org.junit.Test;

import static org.junit.Assert.*;

import java.text.DecimalFormat;

/**
 * Created by JunYoung on 2018. 4. 15..
 */

public class DecimalFormatTest {
    @Test
    public void conversion_isCorrect() throws Exception {
        double amount = 0.0000;
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String amountAfterConversion = decimalFormat.format(amount);

        // System.out.println(amountAfterConversion);

        assertEquals(0, Double.valueOf(amountAfterConversion), 0.001);
    }

    @Test
    public void conversion_second_case_isCorrect() throws Exception {
        double amount = 0.444;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String amountAfterConversion = decimalFormat.format(amount);

        System.out.println(amountAfterConversion);
        System.out.println(Double.valueOf(amountAfterConversion));

        assertEquals(0, Double.valueOf(amountAfterConversion), 0.001);
    }

    @Test
    public void round_off_conversion_first_case_isCorrect() throws Exception {
        double amount = 1.235;
        DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00");
        String amountAfterConversion = decimalFormat.format(amount);

        System.out.println(amountAfterConversion);

        assertEquals(1.24, Double.valueOf(amountAfterConversion), 0.0001);
    }

    @Test
    public void round_off_conversion_second_case_isCorrect() throws Exception {
        double amount = 1.234;
        DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00");
        String amountAfterConversion = decimalFormat.format(amount);

        System.out.println(amountAfterConversion);

        assertEquals(1.23, Double.valueOf(amountAfterConversion), 0.0001);
    }

    @Test
    public void with_and_without_decimal_places_isEqual() throws Exception {
        double withDecimalPlaces = .00;
        double withoutDecimalPlaces = 0;

        assertEquals(withDecimalPlaces, withoutDecimalPlaces, 0.001);
    }
}
