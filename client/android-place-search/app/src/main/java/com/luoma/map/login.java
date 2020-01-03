package com.luoma.map;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.amap.map.R;
import com.luoma.map.util.NetworkUtil;
import com.luoma.map.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class login extends AppCompatActivity {


    EditText userNameView, passwdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameView = (EditText) findViewById(R.id.userNameView);
        passwdView = (EditText) findViewById(R.id.passwdView);

        ImageView ivAnim = (ImageView) findViewById(R.id.ivAnim);
        AnimationDrawable animationDrawable1 = (AnimationDrawable) ivAnim.getBackground();
        animationDrawable1.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle data = msg.getData();

            switch (data.getString("handleType")) {
                case "loginResponse":
                    int state = data.getInt("state");
                    int group = data.getInt("group");
                    String token = data.getString("token");
                    loginBO(state, group, token);
                    break;
            }
        }
    };

    public void loginBO(int state, int group, String token) {
        switch (state) {
            case -1:
                //TODO: 密码错误
                Toast.makeText(this, "密码错误！", Toast.LENGTH_SHORT).show();
                passwdView.setText("");
                break;

            case 0:
                //TODO: 登录成功
                Toast.makeText(this, "欢迎" + userNameView.getText().toString(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, MainActivity.class);

                Bundle bundle = new Bundle();

                bundle.putInt("group", group);
                bundle.putString("token", token);

                bundle.putString("user", userNameView.getText().toString());

                intent.putExtras(bundle);

                startActivity(intent);
                break;

            case 1:
                // TODO: 账号不存在
                Toast.makeText(this, "用户不存在！", Toast.LENGTH_SHORT).show();
                userNameView.setText("");
                passwdView.setText("");
                break;

        }
    }


    public void loginBtn(View view) {


        if (userNameView.getText().toString().isEmpty() || passwdView.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入账号名或密码", Toast.LENGTH_SHORT).show();
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    NetworkUtil network = new NetworkUtil();

                    try {
                        JSONObject json = new JSONObject();

                        json.put("user", userNameView.getText().toString());

                        json.put("passwd", StringUtil.MD5(passwdView.getText().toString() + StringUtil.salt));
                        String data = network.get("/user/login", json);

                        JSONObject response = new JSONObject(data);

                        Message msg = new Message();

                        Bundle bundle = new Bundle();

                        bundle.putString("handleType", "loginResponse");

                        bundle.putInt("state", Integer.parseInt(response.getString("state")));

                        bundle.putInt("group", Integer.parseInt(response.getString("group")));

                        bundle.putString("token", response.getString("token"));

                        msg.setData(bundle);

                        handler.sendMessage(msg);

//                    Toast.makeText(this, data, Toast.LENGTH_LONG).show();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            new Thread(runnable).start();
        }


    }

    public void clickRegister(View view) {

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
