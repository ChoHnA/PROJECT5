package com.helloandroid.project3_chatbot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListItemView extends LinearLayout {
    TextView textView;
    TextView textView2;
    ImageView imageView;

    public ListItemView(Context context) {
        super(context);

        init(context);
    }

    public ListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init (Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item, this, true);

        textView = (TextView) findViewById(R.id.textVeiw);
        textView2 = (TextView) findViewById(R.id.textVeiw2);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void setMoney(String money){
        textView.setText(money);
    }

    public void setTitle(String title){
        textView2.setText(title);
    }

    public void setImage(int icon) {
        imageView.setImageResource(icon);
    }
}
