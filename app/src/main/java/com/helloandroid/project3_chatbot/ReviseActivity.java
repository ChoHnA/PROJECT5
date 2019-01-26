package com.helloandroid.project3_chatbot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ReviseActivity extends AppCompatActivity {

    EditText userInput_title;
    EditText userInput_money;
    TextView button_save;
    ImageView image1, image2, image3, image4, image5;
    ImageView back1, back2, back3, back4, back5;
    ImageView button_plus, button_minus;
    TextView textView;

    public static Context context;
    private String money = "";
    private String title = "";
    private String type = "";
    private String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = this;

        init();

        Intent intent = getIntent();
        date = intent.getExtras().getString("date");
        money = intent.getExtras().getString("money");
        title = intent.getExtras().getString("title");
        type = intent.getExtras().getString("ttype");
        Log.d(date, "수정봇 생성됨.");

        //초기 설정
        userInput_money.setText(money);
        userInput_title.setText(title);
        if(type.equals("교통")) {
            back1.setVisibility(View.VISIBLE);
        } else if(type.equals("식비")) {
            back2.setVisibility(View.VISIBLE);
        } else if(type.equals("문화")) {
            back3.setVisibility(View.VISIBLE);
        } else if(type.equals("쇼핑")) {
            back4.setVisibility(View.VISIBLE);
        } else if(type.equals("기타")) {
            back5.setVisibility(View.VISIBLE);
        }

        sortType();

        button_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("수입");
                button_plus.setVisibility(View.INVISIBLE);
                button_minus.setVisibility(View.VISIBLE);
            }
        });
        button_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("지출");
                button_plus.setVisibility(View.VISIBLE);
                button_minus.setVisibility(View.INVISIBLE);
            }
        });

        userInput_money.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    responseAction();
                }
                return true;
            }
        });
    }

    private void init() {
        userInput_title = (EditText) findViewById(R.id.userInput_title);
        userInput_money = (EditText) findViewById(R.id.userInput_money);
        button_save = (TextView) findViewById(R.id.textView_save);
        image1 = (ImageView) findViewById(R.id.image_1);
        image2 = (ImageView) findViewById(R.id.image_2);
        image3 = (ImageView) findViewById(R.id.image_3);
        image4 = (ImageView) findViewById(R.id.image_4);
        image5 = (ImageView) findViewById(R.id.image_5);
        back1 = (ImageView) findViewById(R.id.back_1);
        back2 = (ImageView) findViewById(R.id.back_2);
        back3 = (ImageView) findViewById(R.id.back_3);
        back4 = (ImageView) findViewById(R.id.back_4);
        back5 = (ImageView) findViewById(R.id.back_5);
        button_plus = (ImageView) findViewById(R.id.button_plus);
        button_minus = (ImageView) findViewById(R.id.button_minus);
        textView = (TextView) findViewById(R.id.textView);

        buttonAction();
    }

    private void buttonAction() {
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseAction();
            }
        });
    }

    //돈, 항목명, 종류 저장해야 함
    private void responseAction() {
        title = userInput_title.getText().toString();
        try{
            money = userInput_money.getText().toString().trim();
            int mn = Integer.parseInt(money);

            if(money.equals("") || title.equals("") || type=="") {
                Toast.makeText(this, "입력되지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show();
            } else {
                if((textView.getText().toString()).equals("수입")){
                    money = "+" + money;
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("title",title);
                resultIntent.putExtra("money", money);
                resultIntent.putExtra("type", type);
                resultIntent.putExtra("date", date);
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "금액에는 숫자만 입력하세요", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("title",title);
        resultIntent.putExtra("money", money);
        resultIntent.putExtra("type", type);
        resultIntent.putExtra("date", date);
        setResult(RESULT_OK,resultIntent);
        finish();
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    try {
                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                        resultIntent.putExtra("title",title);
                        resultIntent.putExtra("money", money);
                        resultIntent.putExtra("type", type);
                        startActivity(resultIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
    */

    private void sortType() {
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "교통";
                if(back1.isShown()==true) {
                    back1.setVisibility(View.INVISIBLE);
                } else {
                    back1.setVisibility(View.VISIBLE);
                }
                back2.setVisibility(View.INVISIBLE);
                back3.setVisibility(View.INVISIBLE);
                back4.setVisibility(View.INVISIBLE);
                back5.setVisibility(View.INVISIBLE);
            }
        });
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "식비";
                if(back2.isShown()==true) {
                    back2.setVisibility(View.INVISIBLE);
                } else {
                    back2.setVisibility(View.VISIBLE);
                }
                back1.setVisibility(View.INVISIBLE);
                back3.setVisibility(View.INVISIBLE);
                back4.setVisibility(View.INVISIBLE);
                back5.setVisibility(View.INVISIBLE);
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "문화";
                if(back3.isShown()==true) {
                    back3.setVisibility(View.INVISIBLE);
                } else {
                    back3.setVisibility(View.VISIBLE);
                }
                back2.setVisibility(View.INVISIBLE);
                back1.setVisibility(View.INVISIBLE);
                back4.setVisibility(View.INVISIBLE);
                back5.setVisibility(View.INVISIBLE);
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "쇼핑";
                if(back4.isShown()==true) {
                    back4.setVisibility(View.INVISIBLE);
                } else {
                    back4.setVisibility(View.VISIBLE);
                }
                back2.setVisibility(View.INVISIBLE);
                back3.setVisibility(View.INVISIBLE);
                back1.setVisibility(View.INVISIBLE);
                back5.setVisibility(View.INVISIBLE);
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "기타";
                if(back5.isShown()==true) {
                    back5.setVisibility(View.INVISIBLE);
                } else {
                    back5.setVisibility(View.VISIBLE);
                }
                back2.setVisibility(View.INVISIBLE);
                back3.setVisibility(View.INVISIBLE);
                back4.setVisibility(View.INVISIBLE);
                back1.setVisibility(View.INVISIBLE);
            }
        });
    }
}
