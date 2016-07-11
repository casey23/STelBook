package com.jay.stelbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.jay.util.DoubleClickExitHelper;

/**
 * 登录成功以后的主界面，主要功能，备份/恢复
 */
public class MainActivity extends AppCompatActivity {

    private DoubleClickExitHelper doubleClickExitHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        doubleClickExitHelper = new DoubleClickExitHelper(this);
    }

    /**
     * 双击返回键退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return doubleClickExitHelper.onKeyDown(keyCode, event);
    }
}
