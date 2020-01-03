package com.luoma.map;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.amap.map.R;
import com.luoma.map.util.NetworkUtil;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestCourse extends AppCompatActivity  {

    TimetableView mTimetableView;
    WeekView mWeekView;
    ArrayList<Schedule> scheduleList;

    List<Integer> week = new ArrayList<Integer>();

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_course);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = this.getIntent().getExtras();

        token = bundle.getString("token");

        mTimetableView = (TimetableView) findViewById(R.id.id_timetableView);

        loadData();

        mTimetableView.data(scheduleList)
                .curWeek(1)
                .showView();
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

            switch (bundle.getString("type")) {
                case "clean":
                    scheduleList.clear();
                    break;

                case "add":
                    Schedule myClass = new Schedule();
                    myClass.setName(bundle.getString("name"));
                    myClass.setDay(bundle.getInt("date"));
                    myClass.setStart(bundle.getInt("time"));
                    myClass.setRoom(bundle.getString("place"));
                    myClass.setWeekList(week);

                    scheduleList.add(myClass);

//                    mWeekView.data(scheduleList).showView();
                    mTimetableView.data(scheduleList).updateView();
//                    mTimetableView.data(scheduleList)
//                            .curWeek(1)
//                            .showView();
                    break;
            }
        }
    };

    void loadData() {
        scheduleList = new ArrayList<>();

        week.add(1);


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();

                try {
                    json.put("token", token);
                    NetworkUtil network = new NetworkUtil();

                    String response = network.get("/class/selectClassById", json);

                    System.out.println(response);

                    JSONObject data = new JSONObject(response);

                    if (data.getInt("state") == 0) {


                        JSONArray array = data.getJSONArray("classInfos");

                        Bundle clean = new Bundle();

                        clean.putString("type", "clean");

                        Message cleanMsg = new Message();

                        cleanMsg.setData(clean);

                        handler.sendMessage(cleanMsg);

                        for (int i = 0; i < array.length(); i++) {
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "add");
                            bundle.putString("name", array.getJSONObject(i).getString("className"));
                            bundle.putString("place", array.getJSONObject(i).getString("place"));
                            bundle.putInt("date", array.getJSONObject(i).getInt("date"));
                            bundle.putInt("time", array.getJSONObject(i).getInt("time"));
                            Message msg = new Message();
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }

                    } else {

                    }




                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();


//        Schedule myClass = new Schedule();
//        myClass.setName("数据结构");
//        myClass.setDay(1);
//        myClass.setStart(4);
//        myClass.setRoom("yf801");
//        myClass.setWeekList(week);
//        Schedule myClass2 = new Schedule();
//        myClass2.setName("数据结构");
//        myClass2.setDay(1);
//        myClass2.setStart(4);
//        myClass2.setRoom("yf801");
//        myClass2.setWeekList(week);
//        scheduleList.add(myClass);
//        scheduleList.add(myClass2);
    }


    /**
     * 初始化课程控件
     */
    private void initTimetableView() {
        //获取控件
        mWeekView = findViewById(R.id.id_weekview);
        mTimetableView = findViewById(R.id.id_timetableView);


    }
}
