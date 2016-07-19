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
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
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
     * Use a simple string represents the long.
     */
    private static final String COLUMN_CONTACT_ID =
            ContactsContract.Data.CONTACT_ID;
    private static final String COLUMN_RAW_CONTACT_ID =
            ContactsContract.Data.RAW_CONTACT_ID;
    private static final String COLUMN_MIMETYPE =
            ContactsContract.Data.MIMETYPE;
    private static final String COLUMN_NAME =
            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME;
    private static final String COLUMN_NUMBER =
            ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String COLUMN_NUMBER_TYPE =
            ContactsContract.CommonDataKinds.Phone.TYPE;
    private static final String COLUMN_EMAIL =
            ContactsContract.CommonDataKinds.Email.DATA;
    private static final String COLUMN_EMAIL_TYPE =
            ContactsContract.CommonDataKinds.Email.TYPE;
    private static final String MIMETYPE_STRING_NAME =
            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;
    private static final String MIMETYPE_STRING_PHONE =
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
    private static final String MIMETYPE_STRING_EMAIL =
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;

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


    /**
     * 批量写入联系人到通讯录
     *
     * @param context      上下文对象
     * @param contactsList 待写入的联系人
     * @throws RemoteException
     * @throws OperationApplicationException
     */
    public static void insertContacts(Context context, List<Contacts> contactsList) throws RemoteException, OperationApplicationException {
        for (Contacts contacts : contactsList) {
            PhoneUtil.addContact(context, contacts);
        }
        //联系人不存在
//        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
//        ContentResolver resolver = context.getContentResolver();
//        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
//        ContentProviderOperation op1 = ContentProviderOperation.newInsert(uri)
//                .withValue("account_name", null)
//                .build();
//        operations.add(op1);
//
//        uri = Uri.parse("content://com.android.contacts/data");
//        for (Contacts contacts : contactsList) {
//            //添加姓名
//            ContentProviderOperation op2 = ContentProviderOperation.newInsert(uri)
//                    .withValueBackReference("raw_contact_id", 0)
//                    .withValue("mimetype", "vnd.android.cursor.item/name")
//                    .withValue("data2", contacts.getName())
//                    .build();
//            operations.add(op2);
//            //添加电话号码
//            ArrayList<String> stringArrayList = contacts.getTel();
//            for (String tel : stringArrayList) {
//                ContentProviderOperation op3 = ContentProviderOperation.newInsert(uri)
//                        .withValueBackReference("raw_contact_id", 0)
//                        .withValue("mimetype", "vnd.android.cursor.item/phone_v2")
//                        .withValue("data1", tel)
//                        .withValue("data2", "2")
//                        .build();
//                operations.add(op3);
//            }
//        }
//        resolver.applyBatch("com.android.contacts", operations);
    }

    /**
     * 获取联系人Id（根据姓名）
     *
     * @param context  上下文对象
     * @param contacts 待搜索的联系人
     * @return 联系人id，如果联系人不存在返回“0”
     */
    public static String getContactID(Context context, Contacts contacts) {
        ContentResolver resolver = context.getContentResolver();
        String id = "0";
        Cursor cursor = resolver.query(
                android.provider.ContactsContract.Contacts.CONTENT_URI,
                new String[]{android.provider.ContactsContract.Contacts._ID},
                android.provider.ContactsContract.Contacts.DISPLAY_NAME +
                        "='" + contacts.getName() + "'", null, null);
        if (cursor != null && cursor.moveToNext()) {
            id = cursor.getString(cursor.getColumnIndex(
                    android.provider.ContactsContract.Contacts._ID));
        }
        if (cursor != null)
            cursor.close();
        return id;
    }

    /**
     * 添加联系人，如果联系人不存在则插入联系人,若果联系人存在则删除联系人再插入
     *
     * @param context
     * @param contacts
     * @throws RemoteException
     * @throws OperationApplicationException
     */
    public static void addContact(Context context, Contacts contacts) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        String id = getContactID(context, contacts);
        if (!id.equals("0")) {
            //联系人已经存在
            PhoneUtil.deleteContact(context, contacts);
        }
        //联系人不存在
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = context.getContentResolver();
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation op1 = ContentProviderOperation.newInsert(uri)
                .withValue("account_name", null)
                .build();
        operations.add(op1);

        uri = Uri.parse("content://com.android.contacts/data");
        //添加姓名
        ContentProviderOperation op2 = ContentProviderOperation.newInsert(uri)
                .withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", "vnd.android.cursor.item/name")
                .withValue("data2", contacts.getName())
                .build();
        operations.add(op2);
        //添加电话号码
        ArrayList<String> stringArrayList = contacts.getTel();
        for (String tel : stringArrayList) {
            ContentProviderOperation op3 = ContentProviderOperation.newInsert(uri)
                    .withValueBackReference("raw_contact_id", 0)
                    .withValue("mimetype", "vnd.android.cursor.item/phone_v2")
                    .withValue("data1", tel)
                    .withValue("data2", "2")
                    .build();
            operations.add(op3);
        }
        resolver.applyBatch("com.android.contacts", operations);
    }


    /**
     * 删除联系人
     *
     * @param context  上下文对象
     * @param contacts 待删除的联系人
     * @throws RemoteException
     * @throws OperationApplicationException
     */
    public static void deleteContact(Context context, Contacts contacts) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String id = PhoneUtil.getContactID(context, contacts);
        //delete contact
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=" + id, null)
                .build());
        //delete contact information such as phone number,email
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(COLUMN_CONTACT_ID + "=" + id, null)
                .build());
        ContentResolver resolver = context.getContentResolver();
        resolver.applyBatch(ContactsContract.AUTHORITY, ops);
    }
}
