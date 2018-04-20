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
        String firstDateOfWeek = DateTime.getFirstDateOfWeek("20-04-2018 17:28:38");

        System.out.println(firstDateOfWeek);
        assertEquals("15-04-2018 00:00:00", firstDateOfWeek);
    }

    @Test
    public void substring_isCorrect() {
        String date = "15-3-2018 12:12:12";

        assertEquals("15", date.substring(0,2));
    }

    @Test
    public void get_first_day_month_of_week_isCoreect() {
        String firstDayMonthOfWeek = DateTime.getFirstDayMonthOfWeek("20-04-2018 17:28:38");

        assertEquals("04/15/2018", firstDayMonthOfWeek);
    }

    @Test
    public void get_last_day_month_of_week_isCoreect() {
        String lastDayMonthOfWeek = DateTime.getLastDayMonthOfWeek("20-04-2018 17:28:38");

        assertEquals("04/21/2018", lastDayMonthOfWeek);
    }

    @Test
    public void get_last_date_of_week_isCoreect() {
        String lastDatefWeek = DateTime.getLastDateOfWeek("20-04-2018 17:28:38");

        assertEquals("21-04-2018 23:59:59", lastDatefWeek);
    }
}
