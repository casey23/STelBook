package com.jay.stelbook;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.jay.util.DoubleClickExitHelper;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

/**
 * 登录成功以后的主界面，主要功能，备份/恢复
 */
public class MainActivity extends SlidingActivity implements View.OnClickListener {

    private ImageView mSlidingMenu;
    private ImageView mUpload;
    private ImageView mDownload;
    private DoubleClickExitHelper doubleClickExitHelper;
    private SlidingMenu mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBehindContentView(R.layout.slidingmenu_layout);
        initView();
    }

    private void initView() {
        //初始化控件
        mSlidingMenu = (ImageView) findViewById(R.id.menu);
        mUpload = (ImageView) findViewById(R.id.upload);
        mDownload = (ImageView) findViewById(R.id.download);
        mSlidingMenu.setOnClickListener(this);
        mUpload.setOnClickListener(this);
        mDownload.setOnClickListener(this);
        //初始化侧滑菜单 SlidingMenu
        mMenu = getSlidingMenu();
        mMenu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //设置菜单阴影
        mMenu.setShadowWidthRes(R.dimen.shadow_width);
        // 设置滑动菜单视图剩余宽度
        mMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        mMenu.setFadeDegree(0.5f);
        // 设置淡入淡出效果
        mMenu.setFadeEnabled(true);
        //初始化双击退出对象
        doubleClickExitHelper = new DoubleClickExitHelper(this);
    }

    /**
     * 双击返回键退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return doubleClickExitHelper.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu:
                mMenu.toggle(true);
                break;
            case R.id.upload:
                break;
            case R.id.download:
                break;
        }
    }
}
