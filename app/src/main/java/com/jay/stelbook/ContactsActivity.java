package com.jay.stelbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jay.javaBean.Contacts;

import java.util.List;

/**
 * 显示响应版本联系人的Activity
 */
public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        List<Contacts> list = (List<Contacts>) getIntent().getSerializableExtra("Contacts");
        Log.i("ttt",list.size()+"");
    }
}
