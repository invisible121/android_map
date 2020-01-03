package com.luoma.map;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amap.map.R;
import com.luoma.map.adapter.MyAdapter;
import com.luoma.map.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddCourse extends AppCompatActivity {

    int date = 1;
    int time = 1;

    String token;
    EditText nameText, placeText;
    Spinner spinner, spinner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = this.getIntent().getExtras();

        token = bundle.getString("token");

        /**Spinner主要用于下来菜单的选项，数据加载和绑定很类似Listview，但是应用场景不同*/
        spinner = (Spinner) findViewById(R.id.spinner);
        final List<String> datas = new ArrayList<>();
        datas.addAll(generateData());
//        for (int i = 1; i < 8; i++) {
//            datas.add("星期" + i);
//        }

        MyAdapter adapter = new MyAdapter(this);
        spinner.setAdapter(adapter);

        adapter.setDatas(datas);

        /**选项选择监听*/
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, "点击了" + datas.get(position), Toast.LENGTH_SHORT).show();

                date = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner2 = (Spinner) findViewById(R.id.spinner2);
        final List<String> datas2 = new ArrayList<>();
        datas2.addAll(generateDate());
//        for (int i = 1; i < 8; i++) {
//            datas.add("星期" + i);
//        }

        MyAdapter adapter2 = new MyAdapter(this);
        spinner2.setAdapter(adapter2);

        adapter2.setDatas(datas2);

        /**选项选择监听*/
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, "点击了" + datas.get(position), Toast.LENGTH_SHORT).show();

                time = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nameText = (EditText) findViewById(R.id.name);
        placeText = (EditText) findViewById(R.id.place);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();

            addBo(bundle.getString("state"));
        }
    };

    void addBo(String state) {
        if (Integer.parseInt(state) == 0) {
            nameText.setText("");
            placeText.setText("");
            Toast.makeText(this, "添加成功！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "添加失败！", Toast.LENGTH_SHORT).show();
        }
    }


    public List<String> generateData() {
        List<String> date = new ArrayList<>();
        date.add("星期一");
        date.add("星期二");
        date.add("星期三");
        date.add("星期四");
        date.add("星期五");
        date.add("星期六");
        date.add("星期日");
        return date;
    }

    public List<String> generateDate() {
        List<String> date = new ArrayList<>();
        date.add("第一节");
        date.add("第二节");
        date.add("第三节");
        date.add("第四节");
        date.add("第五节");
        date.add("第六节");
        return date;
    }

    public void clickBtn(View view) {
        if (nameText.getText().toString().isEmpty() ||
                placeText.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入课程或教室", Toast.LENGTH_SHORT).show();
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    NetworkUtil network = new NetworkUtil();

                    try {
                        JSONObject json = new JSONObject();

                        json.put("token", token);

                        json.put("name", nameText.getText().toString());

                        json.put("place", placeText.getText().toString());

                        json.put("date", date);

                        json.put("time", time);

                        String data = network.get("/class/addClass", json);

                        JSONObject response = new JSONObject(data);

                        Message msg = new Message();

                        Bundle bundle = new Bundle();

                        bundle.putString("state", response.getString("state"));

                        msg.setData(bundle);

                        handler.sendMessage(msg);


                        System.out.println(data);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            new Thread(runnable).start();
        }

    }
}
