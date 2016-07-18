package com.jay.stelbook;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.jay.CustomView.IndexView;
import com.jay.CustomView.SuperEditText;
import com.jay.javaBean.Contacts;
import com.jay.javaBean.Version;
import com.jay.util.PhoneUtil;
import com.jay.util.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 显示响应版本联系人的Activity
 */
public class ContactsActivity extends Activity {
    //联系人列表
    private List<Contacts> mContactsList;
    //对应联系人的版本
    private Version mVersion;
    //返回按钮
    private ImageView mBack;
    //搜索框
    private SuperEditText mSuperEdit;
    //联系人列表
    private ListView mListView;
    //索引菜单
    private IndexView mIndexView;
    //适配器
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        //加载来自版本库的数据
        mContactsList = (List<Contacts>) getIntent().getSerializableExtra("Contacts");
        mVersion = (Version) getIntent().getSerializableExtra("Version");
        initView();
    }

    /**
     * 初始化所有视图控件
     */
    private void initView() {
        //初始化视图控件
        mBack = (ImageView) findViewById(R.id.back);
        mSuperEdit = (SuperEditText) findViewById(R.id.super_edit);
        mListView = (ListView) findViewById(R.id.contactsList);
        mIndexView = (IndexView) findViewById(R.id.contactsIndex);
        //点击返回按钮返回
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //初始化适配器
        mAdapter = new MyAdapter(this, mListView, mContactsList, R.layout.contacts_item_layout);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 自定义ListView的适配器
     */
    private class MyAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, View.OnClickListener {
        private Context mContext;
        private ListView mListView;
        private List<Contacts> mContactsList;
        private PopupWindow mPopupView;
        private int mLayoutId;
        //底部弹出框的控件，点击对应联系人弹出
        private TextView mName;
        private TextView mCall;
        private TextView mSendMsg;
        private TextView mDel;
        private TextView mRestore;

        public MyAdapter(Context mContext, ListView mListView, List<Contacts> mContactsList, int mLayoutId) {
            this.mContext = mContext;
            this.mListView = mListView;
            this.mContactsList = mContactsList;
            this.mLayoutId = mLayoutId;
            //为ListView添加点击事件响应
            mListView.setOnItemClickListener(this);
            //初始化弹出框
            initPopupWindow();
        }

        @Override
        public int getCount() {
            return mContactsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mContactsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Contacts contacts = mContactsList.get(position);
            if (convertView == null) {
                convertView = View.inflate(mContext, mLayoutId, null);
            }
            TextView contactName = ViewHolder.get(convertView, R.id.contactName);
            TextView contactTel = ViewHolder.get(convertView, R.id.contactTel);
            contactName.setText(contacts.getName());
            contactTel.setText(convertList2String(contacts.getTel()));
            return convertView;
        }

        /**
         * 一个联系人的电话号码可能不止一个，使用此函数将一个电话号码的
         * list集合转化为String对象显示，使用 , 隔开每个号码
         *
         * @param list 待转换的联系人集合
         * @return 转化后的联系人字符串
         */
        private String convertList2String(ArrayList<String> list) {
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                result.append(list.get(i));
                if (i != list.size() - 1) {
                    result.append(",");
                }
            }
            return result.toString();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Contacts contacts = mContactsList.get(position);
            showBottomMenu(mListView, contacts);
        }

        /**
         * 显示底部弹出对话框
         *
         * @param v
         * @param contacts
         */
        private void showBottomMenu(View v, Contacts contacts) {
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
            mName.setText(contacts.getName());
            mName.setTag(contacts);
        }

        /**
         * 初始化PopupWindow
         */
        private void initPopupWindow() {
            // 如果没有加载过，那么加载PopupWindow的布局
            if (mPopupView == null) {
                View view = View.inflate(mContext, R.layout.action_sheet_layout, null);
                mPopupView = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
                mPopupView.setBackgroundDrawable(new ColorDrawable(0));
                //设置动画
                mPopupView.setAnimationStyle(R.style.popwin_anim_style);
                mName = (TextView) view.findViewById(R.id.name);
                mCall = (TextView) view.findViewById(R.id.call);
                mSendMsg = (TextView) view.findViewById(R.id.sendMsg);
                mRestore = (TextView) view.findViewById(R.id.restore);
                mDel = (TextView) view.findViewById(R.id.del);
                mCall.setOnClickListener(this);
                mSendMsg.setOnClickListener(this);
                mDel.setOnClickListener(this);
                mRestore.setOnClickListener(this);
            }
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

        @Override
        public void onClick(View v) {
            Contacts contacts = (Contacts) mName.getTag();
            switch (v.getId()) {
                //拨打电话
                case R.id.call:
                    PhoneUtil.callPhones(mContext, contacts.getTel().get(0));
                    break;
                //发送短信
                case R.id.sendMsg:
                    PhoneUtil.sendMessage(mContext, contacts.getTel().get(0), "");
                    break;
                //还原联系人
                case R.id.restore:
                    break;
                //删除联系人
                case R.id.del:
                    delContacts(contacts);
                    break;
            }
            mPopupView.dismiss();
        }

        /**
         * 删除联系人
         *
         * @param contacts
         */
        private void delContacts(Contacts contacts) {
            //删除成功，对应版本里面的备份数量-1
            contacts.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        mVersion.increment("count", -1);
                        //不能使用无参数的update，不然无效
                        mVersion.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {

                            }
                        });
                    }
                }
            });
        }
    }
}
