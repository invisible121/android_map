package com.luoma.map;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amap.map.R;
import com.lzf.easyfloat.EasyFloat;

public class Setting extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Switch wlan = null;
    private ToggleButton gps = null;

    String token;

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        TextView login_text=(TextView)findViewById(R.id.Login_text);
        Drawable drawable=getResources().getDrawable(R.drawable.user);
        drawable.setBounds(0,0,120,120);
        login_text.setCompoundDrawables(drawable,null,null,null);



        Bundle bundle = this.getIntent().getExtras();

        token = bundle.getString("token");
        userName = bundle.getString("user");

        login_text.setText(userName);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //获取Swtich对象、ToggleButton对象
        wlan = (Switch) super.findViewById(R.id.wlan);


        /*因为Switch组件继承自CompoundButton，在代码中可以通过实现CompoundButton.OnCheckedChangeListener接口，并
        实现其内部类的onCheckedChanged来监听状态变化。*/
        wlan.setOnCheckedChangeListener(this);
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

    public void click(View view) {
        Intent intent = new Intent(this, TestCourse.class);
        Bundle bundle = new Bundle();
        bundle.putString("token", token);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Toast.makeText(getApplicationContext(), "开", Toast.LENGTH_SHORT).show();
            EasyFloat.with(this).setLayout(R.layout.activity_test_course).
                    setDragEnable(true).setMatchParent(false, false)
                    .setTag("test").show();
        } else {
            EasyFloat.hide(this, "test");
            View view = EasyFloat.getAppFloatView("test");
            Toast.makeText(getApplicationContext(), "关", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickAddCourse(View view) {
        Intent intent = new Intent(this, AddCourse.class);
        Bundle bundle = new Bundle();
        bundle.putString("token", token);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void clickFeedBack(View view) {
        Intent intent = new Intent(this, Feedback.class);

        startActivity(intent);


    }

    public void exit(View view) {

        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }
}
