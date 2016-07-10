package com.jay.stelbook;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cc.cloudist.acplibrary.ACProgressBaseDialog;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText mUser;
    private EditText mMail;
    private EditText mPsw;
    private EditText mConfirmPsw;
    private Button mRegister;
    private ACProgressBaseDialog mProDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        InitView();
    }

    /**
     * 初始化所有控件并添加点击事件处理
     */
    private void InitView() {
        mUser = (EditText) findViewById(R.id.user);
        mMail = (EditText) findViewById(R.id.mail);
        mPsw = (EditText) findViewById(R.id.password);
        mConfirmPsw = (EditText) findViewById(R.id.surepassword);
        mRegister = (Button) findViewById(R.id.register);
        mRegister.setOnClickListener(this);
        //初始化等待对话框
        mProDialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY).build();
        mProDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            register();
        }
    }

    /**
     * 注册逻辑处理
     */
    private void register() {
        //输入检测
        String userName = mUser.getText().toString();
        String mail = mMail.getText().toString();
        String psw = mPsw.getText().toString();
        String comfirmPsw = mConfirmPsw.getText().toString();
        //检测空数据
        if ("".equals(userName.trim())) {

            mUser.setError(Html.fromHtml("<font color='red'>用户名不能为空</font>"));
            return;
        }
        if ("".equals(mail.trim())) {
            mMail.setError(Html.fromHtml("<font color='red'>邮箱地址不能为空</font>"));
            return;
        }
        if ("".equals(psw.trim())) {
            mPsw.setError(Html.fromHtml("<font color='red'>密码不能为空</font>"));
            return;
        }
        if ("".equals(comfirmPsw.trim())) {
            mConfirmPsw.setError(Html.fromHtml("<font color='red'>请确认密码</font>"));
            return;
        }
        //检查数据正确性
        if (!psw.equals(comfirmPsw)) {
            mConfirmPsw.setError(Html.fromHtml("<font color='red'>两次输入密码不一致</font>"));
            return;
        }
        if (!mail.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {
            mMail.setError(Html.fromHtml("<font color='red'>请输入有效邮箱地址</font>"));
            return;
        }
        BmobUser user = new BmobUser();
        user.setUsername(userName);
        user.setEmail(mail);
        user.setPassword(psw);
        mProDialog.show();
        user.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e == null) {
                    showToast("注册成功");
                } else {
                    StringBuffer sb = new StringBuffer("注册失败。" + e.getMessage() + e.getErrorCode());
                    //用户名已经存在
                    if (e.getErrorCode() == 202) {
                        mUser.setError(Html.fromHtml("<font color='red'>用户名被占用！</font>"));
                    }
                    //邮箱已经被占用
                    if (e.getErrorCode() == 203) {
                        mMail.setError(Html.fromHtml("<font color='red'>邮箱已经被占用！</font>"));
                    }
                    //邮箱格式不正确
                    if (e.getErrorCode() == 301) {
                        mMail.setError(Html.fromHtml("<font color='red'>请输入有效邮箱地址</font>"));
                    }
                    showToast(sb.toString());
                }
                mProDialog.cancel();
            }
        });
    }

    private void showToast(String toastStr) {
        Toast.makeText(this, toastStr, Toast.LENGTH_SHORT).show();
    }
}
