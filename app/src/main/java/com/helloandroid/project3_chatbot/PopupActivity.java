package com.helloandroid.project3_chatbot;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class PopupActivity extends Activity {

    TextView txtText;
    int index;
    String link;
    String title;
    String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        title = "";
        price = "";
        link = "";
        //UI 객체생성
        txtText = (TextView)findViewById(R.id.txtText);

        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        index = intent.getIntExtra("index", -1);

        if(data.equals("add"))
        {
            setContentView(R.layout.activity_popup);
            txtText = (TextView)findViewById(R.id.txtText);
            index = intent.getIntExtra("index", -1);
            txtText.setText("추가하겠습니까?");

        }

        else if(data.equals("delete"))
        {
            setContentView(R.layout.activity_popup2);
            txtText = (TextView)findViewById(R.id.txtText2);
            index = intent.getIntExtra("index", -1);

            title = intent.getStringExtra("title");
            price = intent.getStringExtra("price");
            link = intent.getStringExtra("link");

            txtText.setText(title + "\n" + price);
            //txtText.setText();
        }

        else if(data.equals("buy"))
        {

            setContentView(R.layout.activity_popup2);
            txtText = (TextView)findViewById(R.id.txtText2);
            index = intent.getIntExtra("index", -1);

            title = intent.getStringExtra("title");
            price = intent.getStringExtra("price");
            link = intent.getStringExtra("link");

            txtText.setText(title + "\n" + price);
            //txtText.setText();

        }


    }

    //확인 버튼 클릭


    public void mCancel(View v)
    {
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();

    }

    public void mAdd(View v){
        Intent intent = new Intent();
        intent.putExtra("result", "add");
        intent.putExtra("index", index);
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    public void mDelete(View v){


        Intent intent = new Intent();
        intent.putExtra("result", "delete");
        intent.putExtra("title", title);
        intent.putExtra("index", index);
        setResult(RESULT_OK, intent);


        //액티비티(팝업) 닫기
        finish();

    }

    public void mBuy(View v){
        Intent intent = new Intent();

        intent.putExtra("result", "buy");
        intent.putExtra("title", title);
        intent.putExtra("price", price );
        intent.putExtra("index", index);
        setResult(RESULT_OK, intent);

        finish();

    }



    public void mLink(View v) {

        String site = link;

        Uri uri = Uri.parse(site);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        finish();
    }


    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

*/


}
