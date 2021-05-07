package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchActivity extends AppCompatActivity {

    private ArrayList<SearchItemData> arrayList, totalList, resultList;
    private ArrayList<String> keyBattery, keyClothes, keyGlass, keyMetal, keyPaper, keyPlastic, keyTrash;
    private SearchAdapter searchAdapter, resultAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private EditText et_searchBar;
    private Button btn_searchBar;
    private String str;
    private TextView tv_title, tv_title_result;
    private static final int REQUEST_CODE = 26;  // detailActivity와 연결을 위한 임의의 상수 값을 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        et_searchBar = findViewById(R.id.et_searchbar);
        btn_searchBar = findViewById(R.id.btn_searchbar);
        tv_title = findViewById(R.id.tv_searh);
        tv_title_result = findViewById(R.id.tv_searh_result);

        // 검색 키워드를 정의
        keyBattery = new ArrayList<>();
        keyBattery.add("건전지");

        keyClothes = new ArrayList<>();
        keyClothes.add("가방");
        keyClothes.add("커튼");
        keyClothes.add("면티");

        keyGlass = new ArrayList<>();
        keyGlass.add("주스병");
        keyGlass.add("콜라병");

        keyMetal = new ArrayList<>();
        keyMetal.add("부탄가스");
        keyMetal.add("음료수캔");
        keyMetal.add("철사");
        keyMetal.add("못");

        keyPaper = new ArrayList<>();
        keyPaper.add("우유팩");
        keyPaper.add("신문");
        keyPaper.add("공책");
        keyPaper.add("종이컵");
        keyPaper.add("상자");

        keyPlastic = new ArrayList<>();
        keyPlastic.add("페트병");
        keyPlastic.add("플라스틱용기");

        keyTrash = new ArrayList<>();
        keyTrash.add("가위");
        keyTrash.add("거울");
        keyTrash.add("깨진유리");

        // totalList 만들기 + 초기화
        totalList = new ArrayList<>();
        for (int i = 0; i < keyTrash.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_trash, keyTrash.get(i), 0));
        }
        for (int i = 0; i < keyPlastic.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_plastic, keyPlastic.get(i), 0));
        }
        for (int i = 0; i < keyPaper.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_paper, keyPaper.get(i), 0));
        }
        for (int i = 0; i < keyMetal.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_can, keyMetal.get(i), 0));
        }
        for (int i = 0; i < keyGlass.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_glass, keyGlass.get(i), 0));
        }
        for (int i = 0; i < keyClothes.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_tshirt, keyClothes.get(i), 0));
        }
        for (int i = 0; i < keyBattery.size(); i++) {
            totalList.add(new SearchItemData(R.drawable.icon_battery, keyBattery.get(i), 0));
        }

        // 버튼을 눌렀을 때 + 엔터키를 눌렀을 때 intent가 실행
        btn_searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = et_searchBar.getText().toString().replace(" ","");
                // 빈 문자열인지 체크
                if (str.length() > 0) {
                    str = classifier(str);
                    Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                    intent.putExtra("title", str);
                    //startActivity(intent);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    // editText를 초기화
                    et_searchBar.getText().clear();
                }
            }
        });

        et_searchBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    str = et_searchBar.getText().toString().replace(" ","");
                    if (str.length() > 0) {
                        str = classifier(str);
                        Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                        intent.putExtra("title", str);
                        //startActivity(intent);
                        startActivityForResult(intent, REQUEST_CODE);
                    } else {
                        // editText를 초기화
                        et_searchBar.getText().clear();
                    }
                }
                return true;
            }
        });

        // RecyclerView
        recyclerView = (RecyclerView)findViewById(R.id.rv_search);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        arrayList = readSharedPreferences();

//        arrayList.add(new SearchItemData(R.drawable.icon_plastic, "플라스틱류", 0));
//        arrayList.add(new SearchItemData(R.drawable.icon_glass, "유리류", 0));
//        arrayList.add(new SearchItemData(R.drawable.icon_paper, "종이류", 0));

        searchAdapter = new SearchAdapter(arrayList);
        recyclerView.setAdapter(searchAdapter);

        SearchAdapter searchAdapter = new SearchAdapter(arrayList);
        RecyclerView recyclerView = findViewById(R.id.rv_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchAdapter);

        // editText가 활성화 되었을 때의 이벤트
        et_searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // 1. 검색 버튼 생성
                btn_searchBar.setVisibility(View.VISIBLE);

                // 2. 출력될 정보의 제목 변경
                tv_title.setVisibility(TextView.GONE);
                tv_title_result.setVisibility(TextView.VISIBLE);

                // 3. resultList 만들기
                resultList = new ArrayList<>();
                resultList.addAll(totalList);

                // resultList 연동될 어뎁터 생성
                resultAdapter = new SearchAdapter(resultList);
                recyclerView.setAdapter(resultAdapter);
            }
        });

        et_searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {  // text에 변화가 있을 때마다
                str = et_searchBar.getText().toString();
                search(str);
            }
        });
    }

    public void search(String keyword) {
        resultList.clear();

        if (keyword.length() == 0) {
            resultList.addAll(totalList);
        } else {
            for (int i = 0; i < totalList.size(); i++) {
                if (totalList.get(i).getName().contains(str)) {
                    resultList.add(totalList.get(i));
                }
            }
        }
        // result 내용을 갱신
        resultAdapter.notifyDataSetChanged();
    }

    // 검색 키워드를 특정 종류로 일반화하는 함수
    private String classifier(String keyword) {
        if (keyBattery.contains(keyword)) {
            return "폐건전지";
        } else if (keyClothes.contains(keyword)) {
            return "의류";
        } else if (keyGlass.contains(keyword)) {
            return "유리류";
        } else if (keyMetal.contains(keyword)) {
            return "캔류";
        } else if (keyPaper.contains(keyword)) {
            return "종이류";
        } else if (keyPlastic.contains(keyword)) {
            return "플라스틱류";
        } else {
            return "일반쓰레기";
        }
     }

     // detailAcitivy에서 돌아왔을 때 실행되는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            et_searchBar.getText().clear();
            et_searchBar.clearFocus();

            // 되돌아 왔을 때, activity 상태를 초기화해준다.
            btn_searchBar.setVisibility(View.GONE);
            tv_title.setVisibility(TextView.VISIBLE);
            tv_title_result.setVisibility(TextView.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        saveSharedPreferences(arrayList);
    }

    private void saveSharedPreferences(ArrayList<SearchItemData> list) {
        // SharedPreferences로 데이터 save
        // JSON 파싱해서 다시 저장
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("SearchObjectList", json);
        editor.commit();
    }

    private ArrayList<SearchItemData> readSharedPreferences() {
        // SharedPreferences로 데이터 read
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("SearchObjectList", "");
        // JSON to object list
        Type type = new TypeToken<ArrayList<SearchItemData>>(){}.getType();
        ArrayList<SearchItemData> searchItemList = gson.fromJson(json, type);
        try {
            return sortSearchItemData(searchItemList);
        } catch(NullPointerException npe) {
            return new ArrayList<>();
        }
    }

    //
    private ArrayList<SearchItemData> sortSearchItemData(ArrayList<SearchItemData> list) {
        // sort
        Collections.sort(list, new Comparator<SearchItemData>() {
            @Override
            public int compare(SearchItemData o1, SearchItemData o2) {
                return o2.getCount() - o1.getCount();
            }
        });

        // 5개만 뽑아서 저장하기
        ArrayList<SearchItemData> result = new ArrayList<>();
        int resultCount = 0;
        if (list.size() > 5) {
            for (int i=0; i<5; i++) {
                result.add(list.get(i));
            }
        } else {
            for (SearchItemData item : list) {
                result.add(item);
            }
        }

        return result;
    }
}

