package com.helloandroid.project3_chatbot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class GraphActivity extends AppCompatActivity {
    PieChartView pieChartView;

    private int type1 = 0;
    private int type2 = 0;
    private int type3 = 0;
    private int type4 = 0;
    private int type5 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();
        type1 = intent.getExtras().getInt("교통");
        type2 = intent.getExtras().getInt("식비");
        type3 = intent.getExtras().getInt("문화");
        type4 = intent.getExtras().getInt("쇼핑");
        type5 = intent.getExtras().getInt("기타");

        Log.d(String.valueOf(type3), "그래프 문화 생성됨.");

        pieChartView = findViewById(R.id.chart);

        List<SliceValue> pieData = new ArrayList<>();

        pieData.add(new SliceValue(type1, Color.BLUE).setLabel("교통"));
        pieData.add(new SliceValue(type2, Color.GRAY).setLabel("식비"));
        pieData.add(new SliceValue(type3, Color.RED).setLabel("문화"));
        pieData.add(new SliceValue(type4, Color.MAGENTA).setLabel("쇼핑"));
        pieData.add(new SliceValue(type5, Color.GREEN).setLabel("기타"));

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
