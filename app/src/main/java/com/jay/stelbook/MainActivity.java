package com.jay.stelbook;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText mEmil;
    private EditText mPsw;
    private Button mLogin;
    private TextView mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化所有控件并添加点击事件
     */
    private void initView() {
        mEmil = (EditText) findViewById(R.id.emil);
        mPsw = (EditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.login);
        mRegister = (TextView) findViewById(R.id.register);
        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        //设置注册文本
        SpannableString spannableString = new SpannableString("没有账号?点我注册");
        spannableString.setSpan(new ForegroundColorSpan(0xffa2a2a2),0,5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(0xff5856d6),5,9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mRegister.setText(spannableString);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                break;
            case R.id.register:
                break;
        }
    }
}
