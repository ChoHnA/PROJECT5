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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

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
    private SQLiteDatabase itemdatabase;
    private String databasename = "MyPiggyBank";
    private String tablename = "MoneyTable";

    ArrayList<HashMap<String, String>> selecteditem = new ArrayList<>();


    String photopath;
    private String dailyMoney = "10000";
    private int dailyintMoney = 10000;
    private String title, type, money, date;
    //private int intmoney, icon;

    private TextView textView, textView3, textView4;
    private ListView listView;
    private ImageView imageView;
    private ImageView button;
    private int hour,minute;
    private CalendarDay today;
    private String date2, todayNew;
    private int small, big;
    private int totalMoney, restMoney;

    ListViewAdapter adapter;
    //PieChartView pieChartView;

    public int budget = 100000;

    public void setBudget(int budget)
    {
        this.budget = budget;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestForPermission();
        //setTitle(username+"'s Photo Diary");
        //SQLiteDatabase database1 = new SQLiteDatabase();

        openDatabase(databasename);


        textView = (TextView) findViewById(R.id.textView);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        imageView = (ImageView) findViewById(R.id.imageView);
        listView = (ListView) findViewById(R.id.listView);
        button = (ImageView) findViewById(R.id.button);
        //pieChartView = findViewById(R.id.chart);

        //앱 재실행했을 때 유지할 데이터
        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        dailyintMoney = prefs.getInt("dailyintmoney", 10000);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭 시 편집/삭제
                show(position);
            }
        });

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/MyPiggyBank");
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
            setTitle(username+"'s Piggy Bank");
        } else {
            setTitle("My Piggy Bank");
        }
        hour = prefs.getInt("hour",20);
        minute = prefs.getInt("minute",0);

        alarm_on();

        //시작하자마자 오늘 데이터 띄우기
        today = CalendarDay.today();
        String ttoday = String.valueOf(today);
        Log.d(ttoday, "오늘 생성됨");

        String real_today = ttoday.substring(12,ttoday.length()-1);

        String datearray[] = real_today.split("-");
        int year = Integer.parseInt(datearray[0]);
        int month = Integer.parseInt(datearray[1]);
        int day = Integer.parseInt(datearray[2]);
        todayNew = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day);
        big = Integer.parseInt(String.valueOf(year) + String.valueOf(month+10) + String.valueOf(day+10));
        Log.d(todayNew, "오늘 생성됨");

        //viewOrinsert(todayNew);

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
                date2 = String.valueOf(Year) + "/" + String.valueOf(Month) + "/" + String.valueOf(Day);

                materialCalendarView.clearSelection();

                viewOrinsert(date2);
            }
        });
        //selectData(tablename);
        //viewOrinsert(todayNew);
    }

    @Override
    protected void onStart() {
        super.onStart();

        selectData(tablename);
        viewOrinsert(todayNew);
    }

    class ListViewAdapter extends BaseAdapter {
        ArrayList<ListItem> items = new ArrayList<ListItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(ListItem item){
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) { //데이터 관리하는 어댑터가 화면에 보여질 각각의 아이템을 위한 뷰를 만듦 ->레이아웃으로 구성되어야
            ListItemView view = null;
            if (convertView == null) {
                view = new ListItemView(getApplicationContext());
            } else {
                view = (ListItemView) convertView;
            }

            ListItem item = items.get(position);
            view.setMoney(item.getMoney());
            view.setTitle(item.getTitle());
            view.setImage(item.getIcon());

            return view;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            // Make sure the request was successful
            switch (requestCode){
                case 1:
                    Log.i("??","????");
                    try {
                        //Intent resultIntent = getIntent();

                        title = data.getExtras().getString("title");
                        type = data.getExtras().getString("type");
                        money = data.getExtras().getString("money");
                        date = data.getExtras().getString("date");
                        //intmoney = Integer.parseInt(money);
                        Log.d(title + "/" + type + "/" + money, "생성됨");

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    insertData(title, type, money, date);
                    viewOrinsert(date);

                case 2:
                    break;
                case 3:
                    break;
            }
        }

    }

    private void show(final int position)
    {
        ListItem item = (ListItem) adapter.getItem(position);

        final List<String> ListItems = new ArrayList<>();
        ListItems.add("항목 편집하기");
        ListItems.add("항목 삭제하기");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item.getTitle() + " " + item.getMoney() + "원");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = items[pos].toString();
                if (selectedText == "항목 편집하기"){
                    reviseList(position);
                }else{
                    deleteList(position);
                }
            }
        });
        builder.show();
    }

    private void deleteList(int position) {
        ListItem item = (ListItem) adapter.getItem(position);

        String str = item.getDate();

        String sql = "DELETE FROM MoneyTable WHERE money = '"+item.getMoney()+"' and title = '"+item.getTitle()+"' and date = '"+item.getDate()+"'";
        database.execSQL(sql);
        Log.d(item.getDate(), "삭제 생성됨.");

        materialCalendarView.removeDecorators();
        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);
        selectData(tablename);

        viewOrinsert(str);
    }

    private void reviseList(int position) {
        ListItem item = (ListItem) adapter.getItem(position);

        String Rdate = item.getDate();
        String Rtitle = item.getTitle();
        String Rmoney = item.getMoney();
        int Ricon = item.getIcon();

        String ttype = "";
        if (item.getIcon()==R.drawable.bus) {
            ttype = "교통";
        } else if (item.getIcon()==R.drawable.fork) {
            ttype = "식비";
        } else if (item.getIcon()==R.drawable.ticket) {
            ttype = "문화";
        }else if (item.getIcon()==R.drawable.supermarket) {
            ttype = "쇼핑";
        }else if (item.getIcon()==R.drawable.question) {
            ttype = "기타";
        }

        deleteList(position);

        Intent intent = new Intent(getApplicationContext(), ReviseActivity.class);
        intent.putExtra("date", Rdate);
        intent.putExtra("money", Rmoney);
        intent.putExtra("title", Rtitle);
        intent.putExtra("ttype", ttype);
        startActivityForResult(intent,1);
    }

    private void getDailyMoney(){
        final EditText edittext = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("평소 하루에 쓰는 금액을 입력해주세요. (원)");
        //builder.setMessage("AlertDialog Content");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //정수만 입력하도록
                        dailyMoney = edittext.getText().toString();
                        dailyintMoney = Integer.parseInt(dailyMoney);
                        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("dailyintmoney",dailyintMoney);
                        editor.commit();

                        selectData(tablename);
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.show();
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


                Intent intent = new Intent(getApplicationContext(), SelectedList.class);

                intent.putExtra("money",budget);
                startActivity(intent);
                startActivityForResult(intent, 1);
                //getUserName();
                return true;

            case R.id.set_end_word:
                getDailyMoney();
                return true;
            case R.id.set_time:
                Intent gintent = new Intent(getApplicationContext(), GraphActivity.class);
                startActivityForResult(gintent,3);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    private void viewOrinsert(final String string){
        String sql = "SELECT * FROM MoneyTable WHERE date = '"+string+"'";
        Cursor cursor = database.rawQuery(sql, null);
        Log.d("조회 생성됨", string +"//"+ String.valueOf(cursor.getCount()));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("date", string);
                startActivityForResult(intent,1);
            }
        });

        String datearray[] = string.split("/");
        int year = Integer.parseInt(datearray[0]);
        int month = Integer.parseInt(datearray[1]);
        int day = Integer.parseInt(datearray[2]);
        String dateNew = String.valueOf(year) + "/" + String.valueOf(month+1) + "/" + String.valueOf(day);

        textView.setText(dateNew);

        countMoney();

        int spendMoney = 0;

        if (cursor.getCount()==0){
            adapter = new ListViewAdapter();
            listView.setAdapter(adapter);

            textView3.setText("0");
            textView4.setText(String.valueOf(dailyintMoney));
            imageView.setImageResource(R.drawable.greenlight);

            adapter.addItem(new ListItem(R.drawable.white, "", "", string));
            adapter.notifyDataSetChanged();
        } else {
            adapter = new ListViewAdapter();
            listView.setAdapter(adapter);

            for (int i=0; i<cursor.getCount(); i++) {
                cursor.moveToNext();

                final String title = cursor.getString(1);
                final String type = cursor.getString(2);
                final String money = cursor.getString(3);
                int intmoney = 0;
                final String date = cursor.getString(4);
                int icon=R.drawable.question;
                Log.d(date, "타입 생성됨.");

                if(money.contains("+")) {
                    intmoney = Integer.parseInt(money)*(-1);
                } else {
                    intmoney = Integer.parseInt(money);
                }
                spendMoney = spendMoney + intmoney;

                if (type.equals("교통")) {
                    icon = R.drawable.bus;
                } else if (type.equals("식비")) {
                    icon = R.drawable.fork;
                } else if (type.equals("문화")) {
                    icon = R.drawable.ticket;
                }else if (type.equals("쇼핑")) {
                    icon = R.drawable.supermarket;
                }else if (type.equals("기타")) {
                    icon = R.drawable.question;
                }

                //리스트뷰에 데이터 넣기
                adapter.addItem(new ListItem(icon, money, title, date));
                adapter.notifyDataSetChanged();
            }
            Drawable drawable;
            textView3.setText(String.valueOf(spendMoney));
            textView4.setText(String.valueOf(dailyintMoney-spendMoney));

            if(((float)spendMoney/dailyintMoney)*100 < 70) {
                Log.d("파란불 생성됨.", String.valueOf(((float)spendMoney/dailyintMoney)*100));
                imageView.setImageResource(R.drawable.greenlight);
                drawable = getResources().getDrawable(R.drawable.green4);
            } else if(((float)spendMoney/dailyintMoney)*100 > 100) {
                imageView.setImageResource(R.drawable.redlight);
                drawable = getResources().getDrawable(R.drawable.red4);
            } else {
                imageView.setImageResource(R.drawable.yellowlight);
                drawable = getResources().getDrawable(R.drawable.yellow4);
            }

            ArrayList<CalendarDay> dates = new ArrayList<>();
            dates.add(new CalendarDay(year,month,day));

            materialCalendarView.addDecorator(new EventDecorator(drawable, dates,MainActivity.this));
        }
        restMoney = (big-small+1)*dailyintMoney - totalMoney;
        Log.d(String.valueOf(restMoney), "남은 돈 생성됨.");
    }


    private void selectData(String tableName) {
        if (database != null){
            String sql = "select title, type, money, date from " + tableName;
            Cursor cursor = database.rawQuery(sql, null);
            Log.d("뿌리기 생성됨", String.valueOf(cursor.getCount()));

            totalMoney = 0;

            for (int i=0; i<cursor.getCount(); i++){
                cursor.moveToNext();
                int intmoney = Integer.parseInt(cursor.getString(2));
                String date = cursor.getString(3);

                if((cursor.getString(2)).contains("+")) {
                    intmoney = intmoney*(-1);
                }
                totalMoney = totalMoney + intmoney;

                viewOrinsert(date);
            }
            cursor.close();
        }
    }

    private void insertData(String title, String type, String money, String date) {

        if (database != null){
            if (title != null && type != null) {
                String sql = "INSERT OR REPLACE INTO " + tablename + " (title, type, money, date) Values (?, ?, ?, ?);";
                Object[] params = {title, type, money, date};
                database.execSQL(sql, params);

                Log.d("데이터 추가(생성됨)", title + "/" + type + "/" + money + "/" + date);
            } else {
                //
            }
        } else {

        }
    }

    private void createTable(String tableName) {

        if (database != null) {
            database.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
                    + " (_id integer PRIMARY KEY autoincrement, title text, type text, money text, date text );");
            Log.d(tableName, "테이블 생성됨.");
        } else {}
    }

    private void openDatabase(String databaseName) {
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        if (database != null){
            Log.d(databaseName, "데이터베이스 생성됨.");
            createTable(tablename);
        }
    }

    private void countMoney(){
        String sql = "SELECT date FROM MoneyTable";
        Cursor cursor = database.rawQuery(sql, null);

        int[] array_ymd = new int[cursor.getCount()];

        if (cursor.getCount()==0){

        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();

                final String date = cursor.getString(0);

                String datearray[] = date.split("/");
                int year = Integer.parseInt(datearray[0]);
                int month = Integer.parseInt(datearray[1]);
                int day = Integer.parseInt(datearray[2]);
                String ymdd = String.valueOf(year)+ String.valueOf(month+10) + String.valueOf(day+10); //연월일 8자로 맞추기 위해
                int ymd = Integer.parseInt(ymdd);

                array_ymd[i] = ymd;
            }
            small = array_ymd[0];
            for(int j=1; j<array_ymd.length; j++){
                small = (small < array_ymd[j]) ? small : array_ymd[j];
            }
            Log.d(String.valueOf(small), "첫날 생성됨.");
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
