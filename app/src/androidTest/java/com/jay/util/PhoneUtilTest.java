package com.jay.util;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.jay.javaBean.Contacts;
import com.jay.javaBean.Version;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 电话簿工具类测试用例
 * Created by Jay on 2016/7/12.
 */
public class PhoneUtilTest extends ApplicationTestCase<Application> {

    public PhoneUtilTest() {
        super(Application.class);
    }

    public void testGetAllContacts() throws Exception {
        List<Contacts> allContacts = PhoneUtil.getAllContacts(getContext());
        Log.i("ttt","联系人个数为"+allContacts.size());
        Version version = new Version();
        version.setUser_id((String) BmobUser.getObjectByKey("objectId"));
        version.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                Log.i("ttt",s);
            }
        });
    }
}