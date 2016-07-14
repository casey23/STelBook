package com.jay.javaBean;

import java.io.Serializable;
import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

/**
 * 联系人列表对应类
 * Created by Jay on 2016/7/10.
 */
public class Contacts extends BmobObject implements Serializable {
    //联系人姓名
    private String name;
    //联系人电话
    private ArrayList<String> tel;
    //此联系人所属版本号
    private String versionid;

    public Contacts() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getTel() {
        return tel;
    }

    public void setTel(ArrayList<String> tel) {
        this.tel = tel;
    }

    public String getVersionid() {
        return versionid;
    }

    public void setVersionid(String versionid) {
        this.versionid = versionid;
    }
}
