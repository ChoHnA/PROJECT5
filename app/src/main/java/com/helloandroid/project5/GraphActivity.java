package com.helloandroid.project5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

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

    private String month = "";
    private String year = "";

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
        month = intent.getExtras().getString("month");
        year = intent.getExtras().getString("year");
        int imonth = Integer.parseInt(month) + 1;
        String smonth = String.valueOf(imonth);

        if(imonth<10){
            smonth = 0 + smonth;
        }

        setTitle(year + "년 " + smonth + "월 지출");

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

        int rank[] = new int[5];
        rank[0] = type1;
        rank[1] = type2;
        rank[2] = type3;
        rank[3] = type4;
        rank[4] = type5;

        for(int i=0; i<5; i++) {
            for(int j=0; j<5; j++) {
                if(rank[i]>rank[j]){
                    int tmp = rank[i];
                    rank[i] = rank[j];
                    rank[j] = tmp;
                }
            }
        }

        textView4.setText(String.valueOf(rank[0]));
        textView5.setText(String.valueOf(rank[1]));
        textView6.setText(String.valueOf(rank[2]));

        String srank[] = new String[3];

        for (int i=0; i<3; i++) {
            if(type1 == rank[i]){
                srank[i] = "교통";
            } else if(type2 == rank[i]){
                srank[i] = "식비";
            } else if(type3 == rank[i]){
                srank[i] = "문화";
            } else if(type4 == rank[i]){
                srank[i] = "쇼핑";
            } else if(type5 == rank[i]){
                srank[i] = "기타";
            }
        }

        textView1.setText(srank[0]);
        textView2.setText(srank[1]);
        textView3.setText(srank[2]);
    }

    @Override
    public void onBackPressed() {
        Intent graphIntent = new Intent();
        setResult(RESULT_OK,graphIntent);
        finish();
    }
}
