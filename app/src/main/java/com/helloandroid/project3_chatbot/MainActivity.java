package com.helloandroid.project3_chatbot;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.helloandroid.project3_chatbot.decorators.EventDecorator;
import com.helloandroid.project3_chatbot.decorators.OneDayDecorator;
import com.helloandroid.project3_chatbot.decorators.SaturdayDecorator;
import com.helloandroid.project3_chatbot.decorators.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    String time,menu;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    Cursor cursor;
    private MaterialCalendarView materialCalendarView;
    private String username;
    private android.support.v7.widget.Toolbar myToolBar;
    private String selected_day;
    private int Year,Month,Day;
    public SharedPreferences prefs;
    private SQLiteDatabase database;
    private String databasename = "MoneyDB";
    private String tablename = "MoneyTable";

    String photopath;
    private String endword;
    private String title, type;
    private int money;

    private TextView textView;
    private ListView listView;
    private ImageView imageView;
    private TextView button, button2;
    private int hour,minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestForPermission();
        //setTitle(username+"'s Photo Diary");

        openDatabase(databasename);

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        listView = (ListView) findViewById(R.id.listView);
        button = (TextView) findViewById(R.id.button);
        button2 = (TextView) findViewById(R.id.button2);

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/MyPhotoDiary");
        photopath = path.getPath();
        Log.e("file",photopath);

        if(path.mkdirs()){
            Log.e("FILE", "Directory not created");
        }else{
            Toast.makeText(this, "카메라까지 50,000원", Toast.LENGTH_SHORT).show();
        }

        prefs = getSharedPreferences("data",Activity.MODE_PRIVATE);
        username = prefs.getString("username",null);
        if (username != null){
            setTitle(username+"'s Photo Diary");
        }
        hour = prefs.getInt("hour",20);
        minute = prefs.getInt("minute",0);

        alarm_on();

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2019, 0, 1))
                .setMaximumDate(CalendarDay.from(2040, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Year = date.getYear();
                Month = date.getMonth();
                Day = date.getDay();
                String date2 = String.valueOf(Year) + "/" + String.valueOf(Month) + "/" + String.valueOf(Day);

                materialCalendarView.clearSelection();

                //viewOrinsert(date2);
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivityForResult(intent,1);
            }
        });
        selectData(tablename);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            // Make sure the request was successful
            switch (requestCode){
                case 1:
                    try {
                        Intent resultIntent = getIntent();

                        title = resultIntent.getExtras().getString("title");
                        type = resultIntent.getExtras().getString("type");
                        money = resultIntent.getExtras().getInt("money");

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
    }



    private void getUserName(){
        final EditText edittext = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("당신은 누구인가요?");
        //builder.setMessage("AlertDialog Content");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        username = edittext.getText().toString();
                        setTitle(username+"'s Photo Diary");
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("username",username);
                        editor.commit();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void getDailyMoney(){
        final EditText edittext = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("평소 하루에 쓰는 금액을 입력해주세요");
        //builder.setMessage("AlertDialog Content");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        endword = edittext.getText().toString();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("endword",endword);
                        editor.commit();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void getTime(){
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int aaminute) {
                // 설정버튼 눌렀을 때
                hour = hourOfDay;
                minute = aaminute;

                Intent intent = new Intent();
                PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                manager.cancel(sender);
                alarm_on();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("hour",hour);
                editor.putInt("minute",minute);
                editor.commit();
            }
        };
        TimePickerDialog dialog = new TimePickerDialog(this,listener,12,0,false);
        dialog.show();
    }

    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public final int EXTERNAL_REQUEST = 138;

    public boolean requestForPermission() {

        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!canAccessExternalSd()) {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        return isPermissionOn;
    }

    public boolean canAccessExternalSd() {
        return (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    //추가된 소스, ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.set_user_name:
                // User chose the "Settings" item, show the app settings UI...
                getUserName();
                return true;

            case R.id.set_end_word:
                getDailyMoney();
                return true;
            case R.id.set_time:
                getTime();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    private void viewOrinsert(final String string){
        //Log.d("생성됨", string);
        //String sql = "SELECT path, contents FROM photoDiary WHERE date = "+string;
        String sql = "SELECT * FROM photoDiary WHERE date = '"+string+"'";
        //String sql = "SELECT * FROM photoDiary WHERE date=" + string + ";";
        Cursor cursor = database.rawQuery(sql, null);
        Log.d("조회 생성됨", String.valueOf(cursor.getCount()));

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        startActivityForResult(intent,1);

        cursor.moveToNext();

        final String title = cursor.getString(1);
        final String type = cursor.getString(2);
        final int money = Integer.parseInt(cursor.getString(3));
        final String date = cursor.getString(4);

        //textView.setText(string);

        String datearray[] = date.split("/");
        int year = Integer.parseInt(datearray[0]);
        int month = Integer.parseInt(datearray[1]);
        int day = Integer.parseInt(datearray[2]);
        String dateNew = String.valueOf(year) + "/" + String.valueOf(month+1) + "/" + String.valueOf(day);

        textView.setText(dateNew);
        //리스트뷰에 데이터 넣기

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                //intent.putExtra("endword",endword);
                startActivityForResult(intent,1);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() { //삭제 후 해당 날짜 다시 들어갔다가 나오면 삭제됐던 데이터가 복구되는 문제점
            @Override
            public void onClick(View v) {
                String sql = "DELETE FROM photoDiary WHERE date = '"+string+"'";
                database.execSQL(sql);
                Log.d("삭제", "생성됨.");

                materialCalendarView.removeDecorators();
                materialCalendarView.addDecorators(
                        new SundayDecorator(),
                        new SaturdayDecorator(),
                        oneDayDecorator);
                selectData(tablename);
            }
        });
    }

    private void selectData(String tableName) {
        if (database != null){
            String sql = "select title, type, money, date from " + tableName;
            Cursor cursor = database.rawQuery(sql, null);
            Log.d("뿌리기 생성됨", String.valueOf(cursor.getCount()));

            for (int i=0; i<cursor.getCount(); i++){
                cursor.moveToNext();
                String title = cursor.getString(0);
                String type = cursor.getString(1);
                int money = Integer.parseInt(cursor.getString(2));
                String date = cursor.getString(3);

                //date 연월일로 분해
                String datearray[] = date.split("/");
                int year = Integer.parseInt(datearray[0]);
                int month = Integer.parseInt(datearray[1]);
                int day = Integer.parseInt(datearray[2]);
            }
            cursor.close();
        }
    }

    private void insertData(String title, String type, int money, String date) {

        if (database != null){
            if (title != null && type != null) {
                String sql = "INSERT OR REPLACE INTO " + tablename + " (title, type, money, date) Values (?, ?, ?, ?);";
                Object[] params = {title, type, money, date};
                database.execSQL(sql, params);

                Log.d("데이터 추가(생성됨)", title + "/" + type + "/" + money);
            } else {
                //데이터 추가 실패
            }
        } else {

        }
    }

    private void createTable(String tableName) {

        if (database != null) {
            database.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
                    + " (_id integer PRIMARY KEY autoincrement, title text, type text, money Integer, date text );");
            Log.d("테이블", "생성됨.");
        } else {}
    }

    private void openDatabase(String databaseName) {
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        if (database != null){
            Log.d("데이터베이스", "생성됨.");
            createTable(tablename);
        }
    }

    public void alarm_on(){
        // 알람 등록하기
        Log.i("alarm", "setAlarm");
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), Notification.class);   //AlarmReceive.class이클레스는 따로 만들꺼임 알람이 발동될때 동작하는 클레이스임

        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        //알람시간 calendar에 set해주기

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hour,minute);//시간을 10시 01분으로 일단 set했음
        calendar.set(Calendar.SECOND, 0);

        //알람 예약
        //am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);//이건 한번 알람
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, sender);//이건 여러번 알람 24*60*60*1000 이건 하루에한번 계속 알람한다는 뜻.
        Toast.makeText(this,"시간설정:"+ Integer.toString(calendar.get(calendar.HOUR_OF_DAY))+":"+Integer.toString(calendar.get(calendar.MINUTE)),Toast.LENGTH_LONG).show();
    }

}
