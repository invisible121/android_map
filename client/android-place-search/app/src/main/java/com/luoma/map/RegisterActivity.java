package com.luoma.map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.amap.map.R;
import com.luoma.map.util.NetworkUtil;
import com.luoma.map.util.StringUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {


    Boolean isTeacher = false;

    RegisterActivity registerActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Switch isTeacherSwitch = (Switch) findViewById(R.id.isTeacher);

        isTeacherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isTeacher = buttonView.isChecked();
            }
        });

        registerActivity = this;
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle data = msg.getData();

            switch (data.getString("handleType")) {
                case "registerResponse":
                    int state = data.getInt("state");

                    refreshUI(state);
                    break;
            }
        }
    };


    public void clickRegister(View view) {
        EditText userText = (EditText) findViewById(R.id.regUserName);

        EditText userpasswd = (EditText) findViewById(R.id.regPasswd);

        EditText userEnterPasswd = (EditText) findViewById(R.id.regEnterPasswd);


        final String userName = userText.getText().toString();

        final String passwd = userpasswd.getText().toString();

        String forsure = userEnterPasswd.getText().toString();

        if (userName.isEmpty()) {
            Toast.makeText(this, "请输入用户名!", Toast.LENGTH_SHORT).show();
        } else {

            if (passwd.isEmpty()) {
                Toast.makeText(this, "请输入密码!", Toast.LENGTH_SHORT).show();
            } else {
                if (forsure.isEmpty()) {
                    Toast.makeText(this, "请确认密码!", Toast.LENGTH_SHORT).show();
                }
            }
        }


        if (!userName.isEmpty() && !passwd.isEmpty() && !forsure.isEmpty()) {
            if (passwd.equals(forsure)) {
                //TODO: 注册
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        NetworkUtil network = new NetworkUtil();

                        try {
                            JSONObject requestData = new JSONObject();

                            requestData.put("user", userName);
                            requestData.put("passwd", StringUtil.MD5(passwd + StringUtil.salt));

                            requestData.put("group", isTeacher ? 1 : 0);

                            String data = network.get("/user/register", requestData);

                            JSONObject response = new JSONObject(data);

                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("handleType", "registerResponse");
                            bundle.putInt("state", Integer.parseInt(response.getString("state")));
                            msg.setData(bundle);

                            handler.sendMessage(msg);
                            System.out.println(data);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                new Thread(run).start();


            } else {
                Toast.makeText(this, "确认密码错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void goLogin(View view) {
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }


    public void refreshUI(int state) {
        if (state == 0) {
            Toast.makeText(this, "注册成功!", Toast.LENGTH_SHORT).show();
        }
        if (state == 1) {
            Toast.makeText(this, "用户已存在!", Toast.LENGTH_SHORT).show();
        }
    }
}
