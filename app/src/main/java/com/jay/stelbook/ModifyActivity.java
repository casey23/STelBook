package com.jay.stelbook;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jay.util.CipherUtils;

import cc.cloudist.acplibrary.ACProgressBaseDialog;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 修改密码
 * Created by Jay on 2016/7/12.
 */
public class ModifyActivity extends Activity implements View.OnClickListener {
    private ImageView mBack;
    private EditText mUser;
    private EditText mNewPsw;
    private EditText mOldPsw;
    private Button mCommit;
    private BmobUser mBmobUser;
    private ACProgressBaseDialog mProDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_info);
        initView();
    }

    private void initView() {
        //初始化控件
        mBack = (ImageView) findViewById(R.id.back);
        mUser = (EditText) findViewById(R.id.user);
        mNewPsw = (EditText) findViewById(R.id.newPassword);
        mOldPsw = (EditText) findViewById(R.id.oldPassword);
        mCommit = (Button) findViewById(R.id.commit);
        //获取当前登录用户
        mBmobUser = BmobUser.getCurrentUser();
        mUser.setText(mBmobUser.getUsername());

        //初始化等待对话框
        mProDialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY).build();
        mProDialog.setCanceledOnTouchOutside(false);


        mBack.setOnClickListener(this);
        mCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        }
        if (v.getId() == R.id.commit) {
            modifyInformation();
        }
    }

    /**
     * 修改密码
     */
    private void modifyInformation() {
        String newPsw = mNewPsw.getText().toString();
        String oldPsw = mOldPsw.getText().toString();
        mProDialog.show();
        mCommit.setEnabled(false);
        //密码两次MD5加密
        BmobUser.updateCurrentUserPassword(CipherUtils.md5(CipherUtils.md5(oldPsw)),
                CipherUtils.md5(CipherUtils.md5(newPsw)), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            showToast("密码修改成功!");
                        } else {
                            showToast("失败:" + e.getMessage());
                        }
                        mCommit.setEnabled(true);
                        mProDialog.cancel();
                    }
                });
    }

    private void showToast(String toastStr) {
        Toast.makeText(this, toastStr, Toast.LENGTH_SHORT).show();
    }
}
