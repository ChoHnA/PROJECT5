package com.helloandroid.project5;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.helloandroid.project5.decorators.EventDecorator;
import com.helloandroid.project5.decorators.OneDayDecorator;
import com.helloandroid.project5.decorators.SaturdayDecorator;
import com.helloandroid.project5.decorators.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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


    String photopath2;
    private String dailyMoney = "20000";
    private int dailyintMoney = 20000;
    private String title, type, money, date, back;
    //private int intmoney, icon;

    private TextView textView, textView3, textView4;
    private ListView listView;
    private ImageView imageView;
    private ImageView button;
    private int hour,minute;
    private CalendarDay today;
    private String date2, todayNew, thisMonth, thisYear;
    private int small, big;
    private int totalMoney, restMoney;
    private int money1, money2, money3, money4, money5;

    ListViewAdapter adapter;
    //PieChartView pieChartView;

    public int budget = 10000;

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

        //flushData();
        textView = (TextView) findViewById(R.id.textView);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        imageView = (ImageView) findViewById(R.id.imageView);
        listView = (ListView) findViewById(R.id.listView);
        button = (ImageView) findViewById(R.id.button);
        //pieChartView = findViewById(R.id.chart);

        //앱 재실행했을 때 유지할 데이터
        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        dailyintMoney = prefs.getInt("dailyintmoney", 20000);
        SharedPreferences prefs2 = getSharedPreferences("pref2", MODE_PRIVATE);
        restMoney = prefs2.getInt("restmoney", 20000);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭 시 편집/삭제
                show(position);
            }
        });

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/MyPiggyBank");
        photopath2 = path.getPath();
        Log.e("file",photopath2);

        if(path.mkdirs()){
            Log.e("FILE", "Directory not created");
        }else{
            Toast.makeText(this, "지금까지 " + restMoney + "원 모음.", Toast.LENGTH_SHORT).show();
        }

        prefs = getSharedPreferences("data",Activity.MODE_PRIVATE);

        hour = prefs.getInt("hour",20);
        minute = prefs.getInt("minute",0);

        //시작하자마자 오늘 데이터 띄우기
        today = CalendarDay.today();
        String ttoday = String.valueOf(today);
        Log.d(ttoday, "오늘 생성됨");

        String real_today = ttoday.substring(12,ttoday.length()-1);

        String datearray[] = real_today.split("-");
        int year = Integer.parseInt(datearray[0]);
        int month = Integer.parseInt(datearray[1]);
        int day = Integer.parseInt(datearray[2]);
        thisMonth = String.valueOf(Month);
        thisYear = String.valueOf(Year + 2019);
        todayNew = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day);
        big = Integer.parseInt(String.valueOf(year) + String.valueOf(month+10) + String.valueOf(day+10));
        Log.d(todayNew, "오늘 생성됨");

        countMoney();

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

                thisMonth = String.valueOf(Month);
                thisYear = String.valueOf(Year);

                materialCalendarView.clearSelection();

                viewOrinsert(date2);
            }
        });
        selectData(tablename);
        viewOrinsert(todayNew);
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();

        selectData(tablename);
    }
    */

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
                    selectData(tablename);
                    viewOrinsert(date);

                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    try {
                        dailyMoney = data.getExtras().getString("smoney");
                        if (dailyMoney.equals("none")){

                        } else {
                            dailyintMoney = Integer.parseInt(dailyMoney);
                        }
                        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("dailyintmoney",dailyintMoney);
                        editor.commit();

                        selectData(tablename);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
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
        Intent intent = new Intent(getApplicationContext(), PopupActivity.class);
        intent.putExtra("data", "money");
        startActivity(intent);
        startActivityForResult(intent, 4);
        /*
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
        */
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
                statistics();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }


    private void flushData()
    {
        if(database != null)
        {
            String sql = "delete from MoneyTable";
            database.execSQL(sql);

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
            imageView.setImageResource(R.drawable.greenlightt);

            //adapter.addItem(new ListItem(R.drawable.white, "", "", string));
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
                imageView.setImageResource(R.drawable.greenlightt);
                drawable = getResources().getDrawable(R.drawable.bank);
            } else if(((float)spendMoney/dailyintMoney)*100 > 100) {
                imageView.setImageResource(R.drawable.redlight);
                drawable = getResources().getDrawable(R.drawable.arrest);
            } else {
                imageView.setImageResource(R.drawable.yellowlight);
                drawable = getResources().getDrawable(R.drawable.coinss);
            }

            ArrayList<CalendarDay> dates = new ArrayList<>();
            dates.add(new CalendarDay(year,month,day));

            materialCalendarView.addDecorator(new EventDecorator(drawable, dates,MainActivity.this));
        }
        restMoney = (big-small+1)*dailyintMoney - totalMoney;
        budget = restMoney;
        Log.d(String.valueOf(restMoney), "남은 돈 생성됨.");

        SharedPreferences prefs2 = getSharedPreferences("pref2", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = prefs2.edit();
        editor2.putInt("restmoney",restMoney);
        editor2.commit();
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

    private void statistics() {
        if (database != null){

            money1 = 0;
            money2 = 0;
            money3 = 0;
            money4 = 0;
            money5 = 0;

            for(int j=1; j<32 ; j++){
                String string = thisYear + "/" + thisMonth + "/" + String.valueOf(j);
                String sql = "SELECT title, type, money, date FROM MoneyTable WHERE date = '"+string+"'";
                Cursor cursor = database.rawQuery(sql, null);

                if(cursor.getCount() == 0) {

                } else {
                    for (int i=0; i<cursor.getCount(); i++){
                        cursor.moveToNext();
                        int intmoney = Integer.parseInt(cursor.getString(2));
                        if((cursor.getString(2)).contains("+")) {
                            intmoney = 0;
                        }

                        if((cursor.getString(1)).equals("교통")){
                            money1 = money1 + intmoney;
                        } else if((cursor.getString(1)).equals("식비")){
                            money2 = money2 + intmoney;
                        } else if((cursor.getString(1)).equals("문화")){
                            money3 = money3 + intmoney;
                        } else if((cursor.getString(1)).equals("쇼핑")){
                            money4 = money4 + intmoney;
                        } else if((cursor.getString(1)).equals("기타")){
                            money5 = money5 + intmoney;
                        }
                    }
                }
                cursor.close();
            }
        }
        Intent gintent = new Intent(getApplicationContext(), GraphActivity.class);
        gintent.putExtra("month", thisMonth);
        gintent.putExtra("year", thisYear);
        gintent.putExtra("교통", money1);
        gintent.putExtra("식비", money2);
        gintent.putExtra("문화", money3);
        gintent.putExtra("쇼핑", money4);
        gintent.putExtra("기타", money5);
        startActivityForResult(gintent,3);
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

            String datearray[] = todayNew.split("/");
            int year = Integer.parseInt(datearray[0]);
            int month = Integer.parseInt(datearray[1]);
            int day = Integer.parseInt(datearray[2]);
            String ymdd = String.valueOf(year)+ String.valueOf(month+10) + String.valueOf(day+10);
            int ymd = Integer.parseInt(ymdd);
            small = ymd;


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
}
