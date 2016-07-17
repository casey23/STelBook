package com.jay.stelbook;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jay.CustomView.IndexView;
import com.jay.CustomView.SuperEditText;
import com.jay.javaBean.Contacts;
import com.jay.util.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示响应版本联系人的Activity
 */
public class ContactsActivity extends Activity {
    private List<Contacts> mContactsList;
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
    private static class MyAdapter extends BaseAdapter {
        private Context mContext;
        private ListView mListView;
        private List<Contacts> mContactsList;
        private int mLayoutId;

        public MyAdapter(Context mContext, ListView mListView, List<Contacts> mContactsList, int mLayoutId) {
            this.mContext = mContext;
            this.mListView = mListView;
            this.mContactsList = mContactsList;
            this.mLayoutId = mLayoutId;
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
    }
}
