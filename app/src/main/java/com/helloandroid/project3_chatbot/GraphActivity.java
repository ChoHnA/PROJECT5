package com.helloandroid.project3_chatbot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class GraphActivity extends AppCompatActivity {
    PieChartView pieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();

        pieChartView = findViewById(R.id.chart);

        List<SliceValue> pieData = new ArrayList<>();

        pieData.add(new SliceValue(15, Color.BLUE).setLabel("교통"));
        pieData.add(new SliceValue(25, Color.GRAY).setLabel("식비"));
        pieData.add(new SliceValue(10, Color.RED).setLabel("문화"));
        pieData.add(new SliceValue(40, Color.MAGENTA).setLabel("쇼핑"));
        pieData.add(new SliceValue(20, Color.MAGENTA).setLabel("기타"));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);

        pieChartView.setPieChartData(pieChartData);

    }

    @Override
    public void onBackPressed() {
        Intent graphIntent = new Intent();
        setResult(RESULT_OK,graphIntent);
        finish();
    }
}
