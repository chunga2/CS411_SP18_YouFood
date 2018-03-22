package com.example.alex.graphtesting;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

        // What if the format changes, like 03 vs 3 for march?
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar c = Calendar.getInstance();
        Double monies[] = new Double[7];
        for (int i =0; i < monies.length; i++) {
            monies[i] = 0.0;
        }
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

        GraphView graph = (GraphView) findViewById(R.id.graph);
        BarGraphSeries<DataPoint> sunday = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(1,monies[0])
        });
        sunday.setColor(Color.RED);
        sunday.setTitle("Sunday");
        sunday.setSpacing(0);
        sunday.setDrawValuesOnTop(true);
        graph.addSeries(sunday);


        BarGraphSeries<DataPoint> monday = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(2,monies[1])
        });
        monday.setColor(Color.YELLOW);
        monday.setTitle("Monday");
        monday.setSpacing(0);
        monday.setDrawValuesOnTop(true);
        graph.addSeries(monday);

        BarGraphSeries<DataPoint> tuesday = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(3,monies[2])
        });
        tuesday.setColor(Color.GREEN);
        tuesday.setTitle("Tuesday");
        tuesday.setSpacing(0);
        tuesday.setDrawValuesOnTop(true);
        graph.addSeries(tuesday);

        BarGraphSeries<DataPoint> wednesday = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(4,monies[3])
        });
        wednesday.setColor(Color.BLUE);
        wednesday.setTitle("Wednesday");
        wednesday.setSpacing(0);
        wednesday.setDrawValuesOnTop(true);
        graph.addSeries(wednesday);

        BarGraphSeries<DataPoint> thursday = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(5,monies[4])
        });
        thursday.setColor(Color.CYAN);
        thursday.setTitle("Thursday");
        thursday.setSpacing(0);
        thursday.setDrawValuesOnTop(true);
        graph.addSeries(thursday);

        BarGraphSeries<DataPoint> friday = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(6,monies[5])
        });
        friday.setColor(Color.BLACK);
        friday.setTitle("Friday");
        friday.setSpacing(0);
        friday.setDrawValuesOnTop(true);
        graph.addSeries(friday);

        BarGraphSeries<DataPoint> saturday = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(7,monies[6])
        });
        saturday.setColor(Color.GRAY);
        saturday.setTitle("Saturday");
        saturday.setSpacing(0);
        saturday.setDrawValuesOnTop(true);
        graph.addSeries(saturday);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(8);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

}
