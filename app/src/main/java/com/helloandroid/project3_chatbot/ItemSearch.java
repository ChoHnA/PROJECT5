package com.helloandroid.project3_chatbot;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ItemSearch extends AppCompatActivity {


    MenuItem list;
    MenuItem item_search;
    SearchView search1;

    ListView list1;

    ArrayList<HashMap<String, String>> itemlist = new ArrayList<>();
    ArrayList<HashMap<String, String>> selecteditem = new ArrayList<>();



    ArrayList<String> result_title_list;
    ArrayList<String> result_link_list;
    ArrayList<String> result_price_list;
    ArrayList<String> result_photo_list;




    private SQLiteDatabase database;
    private String databasename = "ItemDB";
    private String tablename = "ItemTable";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_search);

        openDatabase(databasename);

        Log.d(databasename, "데이터베이스스");

        int i=0;
        //deleteData(selecteditem, i);


        requestForPermission();
        list1 = findViewById(R.id.listview);

        result_link_list = new ArrayList<>();
        result_title_list = new ArrayList<>();
        result_price_list = new ArrayList<>();
        result_photo_list = new ArrayList<>();
        /*
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                this,android.R.layout.simple_list_item_1,result_title_list
        );
*/

        ListAdapter adapter = new ExtendedSimpleAdapter(
                this, itemlist,
                R.layout.list_item, new String[]{"title", "price", "photo"},
                new int[]{R.id.item, R.id.price, R.id.photo});


        list1.setAdapter(adapter);

        ResultListListener listListener = new ResultListListener();
        list1.setOnItemClickListener(listListener);

    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.item1:
                break;
            case R.id.item2:

                onBackPressed();

                Intent intent = new Intent(getApplicationContext(), SelectedList.class);
                startActivity(intent);

                break;

        }
        return super.onOptionsItemSelected(item);

    }

*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        // 서치뷰를 가지고있는 메뉴 아이템의 주소값을 담는다.
        item_search = menu.findItem(R.id.item1);

        // 서치뷰의 주소값을 가지고온다.
        search1 = (SearchView) item_search.getActionView();
        search1.setQueryHint("검색어 입력");

        SearchViewListener listener = new SearchViewListener();
        search1.setOnQueryTextListener(listener);
        return true;
    }


    class SearchViewListener implements SearchView.OnQueryTextListener {

        @Override
        // 검색 버튼을 누르면 호출되는 메소드
        public boolean onQueryTextSubmit(String query) {
            // 네트워크 스레드 가동
            NetworkThread thread = new NetworkThread(query);
            thread.start();
            return false;
        }

        @Override
        // 입력할떄마다 호출되는 메소드
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }


    class NetworkThread extends Thread {
        String keyword;
        // 네이버 오픈 API 사용을 위한 client ID 와 secret 값
        String client_id = "Mc1T3Ds0ZNOPev0j9eg_";
        String client_secret = "A_ArOzIcE_";

        public NetworkThread(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public void run() {
            try {
                result_title_list.clear();
                result_link_list.clear();
                result_price_list.clear();
                result_photo_list.clear();
                //검색어을 인코딩한다.
                keyword = URLEncoder.encode(keyword, "UTF-8");
                // 접속 주소
                String site = "https://openapi.naver.com/v1/search/shop.xml?query=" + keyword;
                // 접속
                URL url = new URL(site);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //요청 방식과 client_id , client_secret 값을 설정한다.
                conn.setRequestMethod("GET");
                conn.setRequestProperty("X-Naver-Client-Id", client_id);
                conn.setRequestProperty("X-Naver-Client-Secret", client_secret);
                // 데이터를 읽어온다.
                InputStream is = conn.getInputStream();
                // DOM  파서 생성
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(is);

                // 최상위 루트태그를 가져온다.
                Element root = document.getDocumentElement();
                // item 태그 객체들을 가져온다.
                NodeList item_list = root.getElementsByTagName("item");
                // 태그 개수만큼 반복한다.


                itemlist.clear();
                for (int i = 0; i < item_list.getLength(); i++) {
                    // i 번째 태그 객체를 가져온다.
                    Element item_tag = (Element) item_list.item(i);
                    // item 태그 내의 title 과 link 를 가져온다.
                    NodeList title_list = item_tag.getElementsByTagName("title");
                    NodeList link_list = item_tag.getElementsByTagName("link");
                    NodeList price_list = item_tag.getElementsByTagName("lprice");
                    NodeList photo_list = item_tag.getElementsByTagName("image");


                    Element title_tag = (Element) title_list.item(0);
                    Element link_tag = (Element) link_list.item(0);
                    Element price_tag = (Element) price_list.item(0);
                    Element photo_tag = (Element) photo_list.item(0);


                    String title = title_tag.getTextContent();
                    String link = link_tag.getTextContent();
                    String price = price_tag.getTextContent();
                    String photo = photo_tag.getTextContent();
                    title = stripHtml(title);

                    result_title_list.add(title);
                    result_link_list.add(link);
                    result_price_list.add(price);
                    result_photo_list.add(photo);

                    HashMap<String, String> item = new HashMap<>();

                    item.put("title", title);
                    item.put("price", price);
                    item.put("photo", photo);

                    itemlist.add(item);


                }

                //리스트 뷰를 구성한다.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ListAdapter adapter = list1.getAdapter();

                        // ArrayAdapter<String> adapter=(ArrayAdapter<String>)list1.getAdapter();

                        list1.setAdapter(adapter);

                        //adapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }


    class ResultListListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            // 검색 결과의 position 번째 링크 주소를 가져온다.

            Intent intent = new Intent(getApplicationContext(), PopupActivity.class);
            intent.putExtra("data", "add");
            intent.putExtra("index", i);
            startActivity(intent);
            startActivityForResult(intent, 1);

            /*
            Uri uri = Uri.parse(site);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            */

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                String result = data.getStringExtra("result");


                Log.i("result", result);

                if(result.equals("add")) {
                    int i = data.getIntExtra("index",-1);

                    long mNow;
                    Date mDate;
                    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd/hh/mm/ss");
                    mNow = System.currentTimeMillis();
                    mDate = new Date(mNow);

                    String date = mFormat.format(mDate);
                    String title = result_title_list.get(i);
                    String price = result_price_list.get(i);
                    String photo = result_photo_list.get(i);
                    String link = result_link_list.get(i);

                    insertData(title, price, photo, date, link);
                }
            }
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

    private void deleteData(ArrayList<HashMap<String, String>> arraylist, int i)
    {
        if(database != null)
        {
            String sql = "delete from ItemTable";
            database.execSQL(sql);

        }

    }


    private void insertData(String title, String price, String photo, String date, String link) {

        if (database != null) {
            if (title != null && price != null) {
                String sql = "INSERT OR REPLACE INTO " + tablename + " (title, price, photo, date, link) Values (?, ?, ?, ?, ?);";
                Object[] params = {title, price, photo, date, link};
                database.execSQL(sql, params);

                HashMap<String, String> item = new HashMap<>();
                item.put("title", title);
                item.put("price",price);
                item.put("photo",photo);
                item.put("link",link);

                selecteditem.add(item);

                Log.d("데이터 추가(생성됨)", title + " / " + price + " / " + photo + " // " + link);

            } else {
                //데이터 추가 실패
            }
        } else {

        }
    }

    private void selectData(String tableName) {
        if (database != null) {
            String sql = "select title, price, photo, date, link from " + tableName;
            Cursor cursor = database.rawQuery(sql, null);
            Log.d("뿌리기 생성됨", String.valueOf(cursor.getCount()));

            selecteditem.clear();

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

                selecteditem.add(item);

                Log.i("data", title + " / " + price + " / " + photo + " / " + link);

            }

            cursor.close();
        }
    }

    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

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



}

