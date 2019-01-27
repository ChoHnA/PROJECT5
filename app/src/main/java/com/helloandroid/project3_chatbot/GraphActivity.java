package com.helloandroid.project3_chatbot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (TextView) findViewById(R.id.textView6);

        int color1 = getResources().getColor(R.color.color1);
        int color2 = getResources().getColor(R.color.color2);
        int color3 = getResources().getColor(R.color.color3);
        int color4 = getResources().getColor(R.color.color4);
        int color5 = getResources().getColor(R.color.color5);

        Intent intent = getIntent();
        type1 = intent.getExtras().getInt("교통");
        type2 = intent.getExtras().getInt("식비");
        type3 = intent.getExtras().getInt("문화");
        type4 = intent.getExtras().getInt("쇼핑");
        type5 = intent.getExtras().getInt("기타");

        Log.d(String.valueOf(type3), "그래프 문화 생성됨.");

        pieChartView = findViewById(R.id.chart);

        List<SliceValue> pieData = new ArrayList<>();

        pieData.add(new SliceValue(type1, color1));
        pieData.add(new SliceValue(type2, color2));
        pieData.add(new SliceValue(type3, color3));
        pieData.add(new SliceValue(type4, color4));
        pieData.add(new SliceValue(type5, color5));

        PieChartData pieChartData = new PieChartData(pieData);
        //pieChartData.setHasLabels(true).setValueLabelTextSize(14);

        pieChartView.setPieChartData(pieChartData);

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(String.valueOf(type1));
        arrayList.add(String.valueOf(type2));
        arrayList.add(String.valueOf(type3));
        arrayList.add(String.valueOf(type4));
        arrayList.add(String.valueOf(type5));

        Collections.sort(arrayList);
        Collections.reverse(arrayList);

        textView4.setText(arrayList.get(0));
        textView5.setText(arrayList.get(1));
        textView6.setText(arrayList.get(2));

        ArrayList<String> arrList = new ArrayList<String>();

        for (int i=0; i<3; i++) {
            if(type1 == Integer.parseInt(arrayList.get(i))){
                arrList.add("교통");
            } else if(type2 == Integer.parseInt(arrayList.get(i))){
                arrList.add("식비");
            } else if(type3 == Integer.parseInt(arrayList.get(i))){
                arrList.add("문화");
            } else if(type4 == Integer.parseInt(arrayList.get(i))){
                arrList.add("쇼핑");
            } else if(type5 == Integer.parseInt(arrayList.get(i))){
                arrList.add("기타");
            }
        }

        textView1.setText(arrList.get(0));
        textView2.setText(arrList.get(1));
        textView3.setText(arrList.get(2));
    }

    @Override
    public void onBackPressed() {
        Intent graphIntent = new Intent();
        setResult(RESULT_OK,graphIntent);
        finish();
    }
}
