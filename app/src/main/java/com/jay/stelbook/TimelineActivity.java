package com.jay.stelbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jay.javaBean.Contacts;
import com.jay.javaBean.Version;
import com.jay.util.PhoneUtil;
import com.jay.util.ViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressBaseDialog;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class TimelineActivity extends Activity {
    //备份总数
    private TextView mBackupCount;
    //联系人总数
    private TextView mContactCount;
    //备份列表
    private ListView mBackupList;
    private ACProgressBaseDialog mProDialog;
    //版本库信息
    private List<Version> mVersionList;
    //联系人总数
    private int mCount;
    //返回按钮
    private ImageView mBack;
    //适配器
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        initView();
        loadData();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mBackupCount = (TextView) findViewById(R.id.backupCount);
        mContactCount = (TextView) findViewById(R.id.contactCount);
        mBackupList = (ListView) findViewById(R.id.backupList);
        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //ListView设置空视图
        mBackupList.setEmptyView(findViewById(R.id.emptyView));
        //初始化等待对话框
        mProDialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("努力加载中...")
                .fadeColor(Color.DKGRAY)
                .build();
        mProDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 加载版本库信息，加载联系人总数
     */
    private void loadData() {
        mProDialog.show();
        BmobQuery<Version> query = new BmobQuery<>();
        //按照时间顺序降序排列
        query.order("-createdAt");
        query.addWhereEndsWith("user_id", (String) BmobUser.getObjectByKey("objectId"));
        query.setLimit(50);
        query.findObjects(new FindListener<Version>() {
            @Override
            public void done(List<Version> list, BmobException e) {
                Log.i("ttt", list.size() + "返回总数");
                if (e == null) {
                    mVersionList = list;
                    mCount = PhoneUtil.getContactsCount(TimelineActivity.this);
                    showLoadData();
                    mProDialog.cancel();
                }
            }
        });
    }

    /**
     * 传递信息到通讯录（自定义通讯录）
     *
     * @param list    联系人列表
     * @param version
     */
    private void showContactsData(List<Contacts> list, Version version) {
        Intent intent = new Intent();
        intent.setClass(this, ContactsActivity.class);
        intent.putExtra("Contacts", (ArrayList<Contacts>) list);
        intent.putExtra("Version", version);
        startActivity(intent);
        finish();
    }

    /**
     * 显示加载的信息到界面上
     */
    private void showLoadData() {
        //显示版本库总数
        if (mVersionList == null || mVersionList.size() <= 0) {
            mBackupCount.setText(String.format(mBackupCount.getText().toString(), 0));
        } else {
            mBackupCount.setText(String.format(mBackupCount.getText().toString(), mVersionList.size()));
        }
        //显示本地联系人总数
        mContactCount.setText(String.format(mContactCount.getText().toString(), mCount));
        //设置适配器
        mAdapter = new MyAdapter(this, mBackupList, mVersionList, R.layout.timeline_item_layout);
        mBackupList.setAdapter(mAdapter);
    }


    /**
     * ListView的适配器
     */
    private class MyAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener {
        private ListView mListView;
        private List<Version> mVersionList;
        private int mLayoutId;
        private Context mContext;
        //底部弹出框的控件，点击对应联系人弹出
        private PopupWindow mPopupView;
        //底部弹出框 删除
        private TextView mDel;
        //底部弹出框 还原
        private TextView mRestore;
        //当前选中项的序列
        private int mIndex;

        public MyAdapter(Context context, ListView listView, List<Version> versionList, int layoutId) {
            this.mContext = context;
            mListView = listView;
            this.mVersionList = versionList;
            this.mLayoutId = layoutId;
            mListView.setOnItemClickListener(this);
            mListView.setOnItemLongClickListener(this);
            initPopupWindow();

        }

        @Override
        public int getCount() {
            return mVersionList == null ? 0 : mVersionList.size();
        }

        @Override
        public Object getItem(int position) {
            return mVersionList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //获取对应位置的版本库信息
            Version version = mVersionList.get(position);
            if (convertView == null) {
                convertView = View.inflate(mContext, mLayoutId, null);
            }
            TextView year = ViewHolder.get(convertView, R.id.year);
            TextView mouthAndDay = ViewHolder.get(convertView, R.id.mouthAndDay);
            TextView hour = ViewHolder.get(convertView, R.id.hour);
            TextView amOrPm = ViewHolder.get(convertView, R.id.pmOrAm);
            TextView backupContactsNo = ViewHolder.get(convertView, R.id.backupContactsNo);

            //将版本库的创建时间转为date对象然后转为Calendar
            String at = version.getCreatedAt();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = simpleDateFormat.parse(at);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            //设置时间信息
            year.setText(calendar.get(Calendar.YEAR) + "");
            mouthAndDay.setText((calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.DAY_OF_MONTH));
            hour.setText(calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
            amOrPm.setText(calendar.get(Calendar.AM_PM) == 1 ? "pm" : "am");
            backupContactsNo.setText(String.format("可还原联系人:%d", version.getCount()));
            return convertView;
        }

        //listview的Item被点击相应事件，点击加载对应版本的联系人信息，然后跳转到显示联系人信息界面
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Version version = mVersionList.get(position);
            //显示进度框
            mProDialog.show();
            loadContactsByVersion(version, new FindListener<Contacts>() {
                @Override
                public void done(List<Contacts> list, BmobException e) {
                    //获取成功
                    if (e == null && list != null) {
                        showContactsData(list, version);
                    } else {
                        Toast.makeText(TimelineActivity.this, "获取联系人信息失败！", Toast.LENGTH_SHORT).show();
                    }
                    mProDialog.cancel();
                }
            });
        }

        /**
         * 加载对应版本的联系人
         *
         * @param version  待加载的联系人版本
         * @param listener 加载结果的监听器
         */
        private void loadContactsByVersion(final Version version, FindListener<Contacts> listener) {
            BmobQuery<Contacts> query = new BmobQuery<>();
            query.addWhereEqualTo("versionid", version.getObjectId());
            query.setLimit(1000);
            query.findObjects(listener);
        }

        /**
         * 初始化PopupWindow
         */
        private void initPopupWindow() {
            // 如果没有加载过，那么加载PopupWindow的布局
            if (mPopupView == null) {
                View view = View.inflate(mContext, R.layout.version_sheet_layout, null);
                mPopupView = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
                mPopupView.setBackgroundDrawable(new ColorDrawable(0));
                //设置动画
                mPopupView.setAnimationStyle(R.style.popwin_anim_style);
                mRestore = (TextView) view.findViewById(R.id.restore);
                mDel = (TextView) view.findViewById(R.id.del);
                mDel.setOnClickListener(this);
                mRestore.setOnClickListener(this);
            }
        }

        /**
         * 显示底部弹出对话框
         *
         * @param v
         */
        private void showBottomMenu(View v) {
            //设置popupwindow的位置
            mPopupView.showAtLocation(v, Gravity.BOTTOM, 0, 0);
            //设置背景半透明
            backgroundAlpha(0.6f);
            //点击空白位置，popupwindow消失的事件监听，这时候让背景恢复正常
            mPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1.0f);
                }
            });
        }

        /**
         * 设置屏幕的背景透明度
         *
         * @param bgAlpha
         */
        public void backgroundAlpha(float bgAlpha) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = bgAlpha; // 0.0-1.0
            getWindow().setAttributes(lp);
        }

        /**
         * 底部弹出框的点击相应
         *
         * @param v 被点击的控件
         */
        @Override
        public void onClick(View v) {
            final Version version = mVersionList.get(mIndex);
            switch (v.getId()) {
                //删除版本
                case R.id.del:
                    deleteVersion(version);
                    break;
                //还原此版本
                case R.id.restore:
                    restoreVersion(version);
                    break;
            }
            mPopupView.dismiss();
        }

        /**
         * 还原一个版本
         *
         * @param version 待还原的版本
         */
        private void restoreVersion(Version version) {
            mProDialog.show();
            loadContactsByVersion(version, new FindListener<Contacts>() {
                @Override
                public void done(List<Contacts> list, BmobException e) {
                    if (e == null) {
                        try {
                            PhoneUtil.insertContacts(mContext, list);
                            Toast.makeText(mContext, "成功还原！", Toast.LENGTH_SHORT).show();
                        } catch (Exception e1) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "还原联系人信息失败！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "获取联系人信息失败！", Toast.LENGTH_SHORT).show();
                    }
                    mProDialog.cancel();
                }
            });
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            mIndex = position;
            showBottomMenu(mListView);
            return true;
        }

        /**
         * 删除指定版本
         *
         * @param version 待删除的版本
         */
        private void deleteVersion(final Version version) {
            AlertDialog.Builder bulider = new AlertDialog.Builder(TimelineActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            bulider.setTitle("提示").setMessage("是否删除此版本，不可还原！");
            bulider.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    version.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(mContext, "删除一个版本！", Toast.LENGTH_SHORT).show();
                                mVersionList.remove(version);
                                MyAdapter.this.notifyDataSetChanged();
                            } else {

                                Toast.makeText(mContext, "删除一个版本失败！", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            });
            bulider.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            bulider.show();
        }
    }
}