package com.example.alex.graphtesting;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mock data in a array of GETTransactionResponses
        GETTransactionResponse[] list_res = new GETTransactionResponse[7];
        list_res[0] =
                new GETTransactionResponse("7.50", "11-3-2018 12:12:12", "address", "name", "useremail");
        list_res[1] =
                new GETTransactionResponse("5.94", "12-3-2018 12:12:12", "address", "name", "useremail");
        list_res[2] =
                new GETTransactionResponse("1.23", "13-3-2018 12:12:12", "address", "name", "useremail");
        list_res[3] =
                new GETTransactionResponse("1.10", "13-3-2018 12:12:12", "address", "name", "useremail");
        list_res[4] =
                new GETTransactionResponse("3.30", "15-3-2018 12:12:12", "address", "name", "useremail");
        list_res[5] =
                new GETTransactionResponse("9.99", "16-03-2018 12:12:12", "address", "name", "useremail");
        list_res[6] =
                new GETTransactionResponse("1.00", "17-3-2018 12:12:12", "address", "name", "useremail");

        //Converters and Array to load total per day
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar c = Calendar.getInstance();
        Double monies[] = new Double[7];
        for (int i =0; i < monies.length; i++) {
            monies[i] = 0.0;
        }

        //gets the first sunday in the week
        Calendar sunday=Calendar.getInstance();
        try {
            sunday.setTime(sdf.parse(list_res[0].getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sunday.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        sunday.set(Calendar.HOUR_OF_DAY,0);
        sunday.set(Calendar.MINUTE,0);
        sunday.set(Calendar.SECOND,0);
        Date sunday_date = sunday.getTime();

        //loop thorough and update all datapoints
        for(int i =0; i < list_res.length; i++) {
            String date_string = list_res[i].getDate();
            try {
                c.setTime(sdf.parse(date_string));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            String amount = list_res[i].getAmount();
            Double amo = Double.parseDouble(amount);
            monies[dayOfWeek - 1] += amo;
        }
        //monies = array of amount of cash per day with sunday as 0 and satruday as 6
        //sunday is a calendar object of the first sunday of this week

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> sim_series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(sunday.getTime().getTime() + 0*24*60*60*1000 + -6*60*60*1000, monies[0]),
                new DataPoint(sunday.getTime().getTime() + 1*24*60*60*1000 + -6*60*60*1000, monies[1]),
                new DataPoint(sunday.getTime().getTime() + 2*24*60*60*1000 + -6*60*60*1000, monies[2]),
                new DataPoint(sunday.getTime().getTime() + 3*24*60*60*1000 + -6*60*60*1000, monies[3]),
                new DataPoint(sunday.getTime().getTime() + 4*24*60*60*1000 + -6*60*60*1000, monies[4]),
                new DataPoint(sunday.getTime().getTime() + 5*24*60*60*1000 + -6*60*60*1000, monies[5]),
                new DataPoint(sunday.getTime().getTime() + 6*24*60*60*1000 + -6*60*60*1000, monies[6]),
        });

        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(7);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Dollars");

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(sunday.getTime().getTime());
        graph.getViewport().setMaxX(sunday.getTime().getTime() + 6*24*60*60*1000);

        Double monies_max = monies[0];
        for (int i = 1; i < monies.length; i++) {
            if (monies[i] > monies_max) {
                monies_max = monies[i];
            }
        }
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(monies_max * 1.2);


//        graph.getLegendRenderer().setVisible(true);
//        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph.addSeries(sim_series);
    }

}
