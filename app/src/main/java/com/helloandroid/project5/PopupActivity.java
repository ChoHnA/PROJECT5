package com.helloandroid.project5;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PopupActivity extends Activity {

    TextView txtText;
    int index;
    String link;
    String title;
    String price;

    EditText sTitle;
    EditText sPrice;
    EditText sMoney;

    String stitle;
    String sprice;
    String sphoto;
    String smoney;


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

        else if(data.equals("self"))
        {
            setContentView(R.layout.activity_popup3);

            sPrice = findViewById(R.id.sPrice);
            sTitle = findViewById(R.id.sTitle);



        }

        else if(data.equals("money"))
        {
            setContentView(R.layout.activity_popup4);

            sMoney = findViewById(R.id.sMoney);
            Button button = (Button) findViewById(R.id.addself);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMoney(v);
                }
            });

            Button button2 = (Button) findViewById(R.id.cancelself);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("smoney", "none");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
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


    public void mSelf(View v)
    {

        sprice = sPrice.getText().toString();
        stitle = sTitle.getText().toString();
        sphoto = "non";

        Log.i("????","?????");
        Intent intent = new Intent();
        intent.putExtra("title", stitle);
        intent.putExtra("price", sprice);
        intent.putExtra("photo", sphoto);
        intent.putExtra("link", "non");
        intent.putExtra("result", "self");

        Log.i("self", stitle + "/" + sprice + "/" + sphoto);

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

        if(!site.equals("non")) {
            Uri uri = Uri.parse(site);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "링크가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();

        }
        finish();
    }


    public void mMoney(View v) {

        smoney = sMoney.getText().toString();

        Intent intent = new Intent();
        intent.putExtra("smoney", smoney);
        setResult(RESULT_OK, intent);
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
