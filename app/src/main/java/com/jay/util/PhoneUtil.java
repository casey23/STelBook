/**
 * Copyright 2014 Zhenguo Jin
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jay.util;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;

import com.jay.javaBean.Contacts;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 手机组件调用工具类
 *
 * @author jingle1267@163.com
 */
public final class PhoneUtil {

    /**
     * Don't let anyone instantiate this class.
     */
    private PhoneUtil() {
        throw new Error("Do not need instantiate!");
    }

    /**
     * 调用系统发短信界面
     *
     * @param activity    Activity
     * @param phoneNumber 手机号码
     * @param smsContent  短信内容
     */
    public static void sendMessage(Context activity, String phoneNumber,
                                   String smsContent) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return;
        }
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", smsContent);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(it);

    }

    /**
     * 调用系统打电话界面
     *
     * @param context     上下文
     * @param phoneNumber 手机号码
     */
    public static void callPhones(Context context, String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 1) {
            return;
        }
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent intent = null;
        //检查是否可以直接拨号，如果没有直接拨号的权限
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            intent = new Intent(Intent.ACTION_DIAL, uri);
        } else {
            intent = new Intent(Intent.ACTION_CALL, uri);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 获取所有联系人信息（姓名+电话）
     */
    public static List<BmobObject> getAllContacts(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        ArrayList<BmobObject> contactses = new ArrayList<BmobObject>();
        while (cursor.moveToNext()) {

            //获得联系人ID
            String id = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
            //获得联系人姓名
            String name = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME));
            //获得联系人手机号码
            Cursor phone = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);

            ArrayList<String> tels = new ArrayList<String>();
            //取得电话号码(可能存在多个号码)
            while (phone.moveToNext()) {
                int phoneFieldColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNumber = phone.getString(phoneFieldColumnIndex);
                tels.add(phoneNumber);
            }
            phone.close();
            Contacts contacts = new Contacts();
            contacts.setName(name);
            contacts.setTel(tels);
            contactses.add(contacts);
        }
        cursor.close();
        return contactses;
    }

    /**
     * 返回联系人总数
     *
     * @param context 上下文对象
     * @return 返回联系人总数
     */
    public static int getContactsCount(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

}
