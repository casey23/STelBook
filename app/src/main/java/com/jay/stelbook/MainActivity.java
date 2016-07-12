package com.jay.stelbook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jay.javaBean.Contacts;
import com.jay.javaBean.Developer;
import com.jay.javaBean.Version;
import com.jay.util.DoubleClickExitHelper;
import com.jay.util.PhoneUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

import java.util.List;

import cc.cloudist.acplibrary.ACProgressBaseDialog;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 登录成功以后的主界面，主要功能，备份/恢复
 */
public class MainActivity extends SlidingActivity implements View.OnClickListener {

    //主界面控件
    private ImageView mSlidingMenu;
    private ImageView mUpload;
    private ImageView mDownload;
    //侧滑菜单控件
    private TextView mPerson;
    private TextView mCancel;
    private TextView mQuit;
    private TextView mAboutMe;
    private TextView mFeedBack;
    //用户头像
    private CircleImageView mUserIcon;
    private DoubleClickExitHelper doubleClickExitHelper;
    private SlidingMenu mMenu;
    private ACProgressBaseDialog mProDialog;

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
        mPerson = (TextView) findViewById(R.id.person);
        mCancel = (TextView) findViewById(R.id.cancel);
        mQuit = (TextView) findViewById(R.id.quit);
        mAboutMe = (TextView) findViewById(R.id.about);
        mFeedBack = (TextView) findViewById(R.id.feedback);
        mUserIcon = (CircleImageView) findViewById(R.id.userIcon);

        //控件点击事件监听
        mSlidingMenu.setOnClickListener(this);
        mUpload.setOnClickListener(this);
        mDownload.setOnClickListener(this);
        mPerson.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mQuit.setOnClickListener(this);
        mAboutMe.setOnClickListener(this);
        mFeedBack.setOnClickListener(this);
        mUserIcon.setOnClickListener(this);

        //初始化等待对话框
        mProDialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("正在处理...")
                .fadeColor(Color.DKGRAY).build();
        mProDialog.setCanceledOnTouchOutside(false);

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

    /**
     * 控件点击事件处理
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu:
                mMenu.toggle(true);
                break;
            //上传通讯录到云端
            case R.id.upload:
                uploadTelBook();
                break;
            //同步通讯录到本地
            case R.id.download:

                break;
            //修改用户头像
            case R.id.userIcon:
                break;
            //个人信息
            case R.id.person:
                break;
            //退出登录
            case R.id.cancel:
                cancel();
                break;
            //退出应用
            case R.id.quit:
                quitApp();
                break;
            //关于我
            case R.id.about:
                showMe();
                break;
            //反馈
            case R.id.feedback:
                feedBack();
                break;
        }
    }

    /**
     * 显示关于我的信息
     */
    private void showMe() {

    }

    /**
     * 退出应用
     */
    private void quitApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("提示");
        builder.setMessage("是否退出应用？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 上传通讯录到云端
     */
    private void uploadTelBook() {
        mProDialog.show();
        //不准连续点击
        mUpload.setClickable(false);
        Version version = new Version();
        version.setUser_id((String) BmobUser.getObjectByKey("objectId"));
        version.save(new SaveListener<String>() {
            //版本号存储成功
            @Override
            public void done(String objectId, BmobException e) {
                //获取所有联系人数据
                List<BmobObject> contactsList = PhoneUtil.getAllContacts(MainActivity.this);
                for (BmobObject contacts : contactsList) {
                    ((Contacts) contacts).setVersionid(objectId);
                }
                BmobBatch bmobBatch = new BmobBatch();
                bmobBatch.insertBatch(contactsList).doBatch(new QueryListListener<BatchResult>() {
                    @Override
                    public void done(List<BatchResult> list, BmobException e) {
                        if (e == null) {
                            for (BatchResult batchResult : list) {
                                BmobException ex = batchResult.getError();
                                if (ex == null) {
                                    Log.i("ttt", "数据批量添加成功：" + batchResult.getCreatedAt() + "," + batchResult.getObjectId() + "," + batchResult.getUpdatedAt());
                                } else {
                                    Log.i("ttt", "数据批量添加失败：" + ex.getMessage() + "," + ex.getErrorCode());
                                }
                            }
                            showToast("数据备份成功!");
                        } else {
                            showToast("数据备份失败!");
                        }
                        mProDialog.cancel();
                        //上传完毕，允许点击
                        mUpload.setClickable(true);
                    }
                });
            }
        });
    }


    /**
     * 退出登录
     */
    private void cancel() {
        //清楚本地缓存对象
        BmobUser.logOut();
        //跳转到登录界面
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 信息反馈
     * 获取开发者信息(手机号)，发送短信的形式反馈
     */
    private void feedBack() {
        BmobQuery<Developer> query = new BmobQuery<Developer>();
        query.findObjects(new FindListener<Developer>() {
            @Override
            public void done(List<Developer> list, BmobException e) {
                //查询成功且存在数据
                if (e == null && list != null && list.size() > 0) {
                    Developer developer = list.get(0);
                    PhoneUtil.sendMessage(MainActivity.this, developer.getTel(), "感谢您的反馈！");
                } else {
                    showToast("反馈失败！");
                }
            }
        });
    }

    private void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
