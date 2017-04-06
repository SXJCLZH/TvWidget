package com.owen.tvwidget.demo;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by owen on 2017/3/14.
 */

public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

//        ToastUtil.show(getApplicationContext(), "测试 ToastUtil");
        ToastUtil2.getInstance(getApplicationContext()).Short("测试 ToastUtil").show();
        finish();
    }
}
