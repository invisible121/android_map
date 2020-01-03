package com.luoma.map;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.map.R;
import com.cazaea.sweetalert.SweetAlertDialog;


public class Feedback extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

        EditText problem = (EditText) findViewById(R.id.problem);

        if (problem.getText().toString().length() < 5) {
            Toast.makeText(this, "问题描述小于5个字",Toast.LENGTH_SHORT).show();
        } else {
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("问题反馈提交")
                    .setContentText("问题反馈成功！")
                    .setConfirmText("确定")
                    .show();
        }
    }
}
