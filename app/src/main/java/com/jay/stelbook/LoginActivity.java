package com.jay.stelbook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jay.util.CipherUtils;

import cc.cloudist.acplibrary.ACProgressBaseDialog;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 登录界面，负责用户登录，以及跳转到注册界面
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText mUser;
    private EditText mPsw;
    private Button mLogin;
    private TextView mRegister;

    private ACProgressBaseDialog mProDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initView();
    }

    /**
     * 初始化所有控件、等待对话框并添加点击事件
     */
    private void initView() {
        mUser = (EditText) findViewById(R.id.user);
        mPsw = (EditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.login);
        mRegister = (TextView) findViewById(R.id.register);
        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        //设置注册文本
        SpannableString spannableString = new SpannableString("没有账号?点我注册");
        spannableString.setSpan(new ForegroundColorSpan(0xffa2a2a2), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(0xff5856d6), 5, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mRegister.setText(spannableString);
        //初始化等待对话框
        mProDialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY).build();
        mProDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                login();
                break;
            case R.id.register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 登录验证
     */
    private void login() {
        String userName = String.valueOf(mUser.getText());
        String password = String.valueOf(mPsw.getText());
        if ("".equals(userName.trim())) {
            mUser.setError(Html.fromHtml("<font color='red'>用户名不能为空</font>"));
            return;
        }
        if ("".equals(password.trim())) {
            mPsw.setError(Html.fromHtml("<font color='red'>密码不能为空</font>"));
            return;
        }
        BmobUser user = new BmobUser();
        user.setUsername(userName);
        //密码使用两次MD5加密
        user.setPassword(CipherUtils.md5(CipherUtils.md5(password)));
        mProDialog.show();
        user.login(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e == null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    showToast("用户名或密码错误，登录失败");
                }
                mProDialog.cancel();
            }
        });
    }

    private void showToast(String toastStr) {
        Toast.makeText(this, toastStr, Toast.LENGTH_SHORT).show();
    }
}
