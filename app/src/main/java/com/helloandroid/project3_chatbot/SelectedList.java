package com.helloandroid.project3_chatbot;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectedList extends AppCompatActivity {

    ListView listView;

    public int money;
    FloatingActionButton button;
    private SQLiteDatabase database;
    private String databasename = "ItemDB";
    private String tablename = "ItemTable";

    TextView budget;

    ArrayList<HashMap<String, String>> selectedItem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_list);



        budget = findViewById(R.id.money);

        button = findViewById(R.id.addBtn);
        openDatabase(databasename);
        selectItemData(tablename);
        listView = findViewById(R.id.selectedlist);

        Intent intent= getIntent();
        money = intent.getIntExtra("money", 0);

    }


    @Override
    protected void onStart() {
        super.onStart();

        String mon = "잔고: " + String.valueOf(money)+ " 원";

        budget.setText(mon);

        selectItemData(tablename);
        ListAdapter adapter = new ExtendedSimpleAdapter(
                this, selectedItem,
                R.layout.selected_item, new String[]{"title", "price", "photo"},
                new int[]{R.id.s_item, R.id.s_price, R.id.s_photo});

        ((ExtendedSimpleAdapter) adapter).getBudget(money);

        listView.setAdapter(adapter);

        ResultListListener listListener = new ResultListListener();
        listView.setOnItemClickListener(listListener);

    }

    class ResultListListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            // 검색 결과의 position 번째 링크 주소를 가져온다.

            Intent intent = new Intent(getApplicationContext(), PopupActivity.class);

            intent.putExtra("data","delete");
            intent.putExtra("title", selectedItem.get(i).get("title"));
            //Log.i("title", arrayList.get(i).get("title"));

            intent.putExtra("price",selectedItem.get(i).get("price"));
            //Log.i("price",arrayList.get(i).get("price"));

            intent.putExtra("link",selectedItem.get(i).get("link"));
            intent.putExtra("index", i);
            startActivity(intent);

            startActivityForResult(intent, 2);
            /*
            String site = arrayList.get(i).get("link");

            Uri uri = Uri.parse(site);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
*/

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                String result = data.getStringExtra("result");
                Log.i("result", result);

                if(result.equals("delete") || result.equals("buy")) {

                    String title = data.getStringExtra("title");
                    int index = data.getIntExtra("index", -1);

                    selectedItem.remove(index);
                    deleteData(title);


                    if (result.equals("buy")) {
                        String price = data.getStringExtra("price");

                        int item_price = Integer.parseInt(price);

                        money -= item_price;


                        CalendarDay today = CalendarDay.today();
                        String ttoday = String.valueOf(today);
                        Log.d(ttoday, "오늘 생성됨");

                        String real_today = ttoday.substring(12,ttoday.length()-1);

                        String datearray[] = real_today.split("-");
                        int year = Integer.parseInt(datearray[0]);
                        int month = Integer.parseInt(datearray[1]);
                        int day = Integer.parseInt(datearray[2]);
                        String todayNew = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day);


                        String date = todayNew;

                        Intent intent = new Intent();
                        intent.putExtra("title", title);
                        intent.putExtra("type", "기타");
                        intent.putExtra("money", price);
                        intent.putExtra("date", date);

                        setResult(RESULT_OK, intent);


                        String mon = "잔고: " + String.valueOf(money) + " 원";

                        MainActivity mainActivity = new MainActivity();
                        mainActivity.setBudget(money);

                        budget.setText(mon);

                    }

                    ListAdapter adapter = new ExtendedSimpleAdapter(
                            this, selectedItem,
                            R.layout.selected_item, new String[]{"title", "price", "photo"},
                            new int[]{R.id.s_item, R.id.s_price, R.id.s_photo});

                    ((ExtendedSimpleAdapter) adapter).getBudget(money);
                    listView.setAdapter(adapter);
                }


            }
        }
    }


    public void addList(View v)
    {
        Intent intent = new Intent(getApplicationContext(), ItemSearch.class);
        startActivity(intent);
        startActivityForResult(intent, 2);

    }


    private void selectItemData(String tableName)
    {
        if (database != null) {
            String sql = "select title, price, photo, date, link from " + tableName;
            Cursor cursor = database.rawQuery(sql, null);
            Log.d("뿌리기 생성됨", String.valueOf(cursor.getCount()));

            selectedItem.clear();

            for (int i = 0; i < cursor.getCount(); i++) {

                HashMap<String, String> item = new HashMap<>();

                cursor.moveToNext();
                String title = cursor.getString(0);
                String price = cursor.getString(1);
                String photo = cursor.getString(2);
                String date = cursor.getString(3);
                String link = cursor.getString(4);
                //date 연월일로 분해

                item.put("title", title);
                item.put("price",price);
                item.put("photo",photo);
                item.put("link",link);

                selectedItem.add(item);

                Log.i("data", title + " / " + price + " / " + photo + " / " + link);

            }

            cursor.close();
        }


    }

    private void deleteData(String title)
    {
        if(database != null)
        {
            String sql = "delete from ItemTable where title = '" + title +"'";
            database.execSQL(sql);

        }

    }

    private void createTable(String tableName) {

        if (database != null) {
            database.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
                    + " (_id integer PRIMARY KEY autoincrement, title text, price text, photo text, date text, link text );");
            Log.d("테이블", "생성됨.");
        } else {
        }
    }

    private void openDatabase(String databaseName) {
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        if (database != null) {
            Log.d("데이터베이스", "생성됨.");
            createTable(tablename);
        }
    }

}
