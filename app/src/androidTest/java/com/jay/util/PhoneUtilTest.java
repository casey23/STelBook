package com.jay.util;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.jay.javaBean.Contacts;

/**
 * 电话簿工具类测试用例
 * Created by Jay on 2016/7/12.
 */
public class PhoneUtilTest extends ApplicationTestCase<Application> {

    public PhoneUtilTest() {
        super(Application.class);
    }

    public void testGetAllContacts() throws Exception {
//        List<Contacts> allContacts = PhoneUtil.getAllContacts(getContext());
//        Log.i("ttt","联系人个数为"+allContacts.size());
//        Version version = new Version();
//        version.setUser_id((String) BmobUser.getObjectByKey("objectId"));
//        version.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                Log.i("ttt",s);
//            }
//        });
    }

    public void testGetAllVersion() throws Exception {
//        Version version = new Version();
//        BmobQuery<Version> query = new BmobQuery<>();
//        query.findObjects(new FindListener<Version>() {
//            @Override
//            public void done(List<Version> list, BmobException e) {
//                Log.i("ttt", list.size() + "返回总数");
//                //assertNotNull(list);
//                assertSame(list.size(),0);
//            }
//        });
    }

    public void testInsertContact() {
        Contacts contacts = new Contacts();
        contacts.setName("AAA");
//        contacts.setTel();
    }
}