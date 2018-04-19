package com.cs411.droptableuser.youfood_android_app;

import org.junit.Test;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by JunYoung on 2018. 4. 18..
 */

public class DateFormatTest {
    @Test
    public void get_first_date_of_week_isCorrect() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar c = Calendar.getInstance();
        Calendar sunday = Calendar.getInstance();
        try {
            sunday.setTime(dateFormat.parse("15-3-2018 12:12:12"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sunday.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        sunday.set(Calendar.HOUR_OF_DAY,0);
        sunday.set(Calendar.MINUTE,0);
        sunday.set(Calendar.SECOND,0);
        Date sunday_date = sunday.getTime();

        System.out.println(dateFormat.format(sunday_date));
    }

    @Test
    public void substring_isCorrect() {
        String date = "15-3-2018 12:12:12";

        assertEquals("15", date.substring(0,2));
    }
}
