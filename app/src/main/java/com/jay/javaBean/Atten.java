package com.jay.javaBean;

import cn.bmob.v3.BmobObject;

/**
 * 联系人列表对应类
 * Created by Jay on 2016/7/10.
 */
public class Atten extends BmobObject {
    //联系人姓名
    private String name;
    //联系人电话
    private String tel;
    //此联系人所属版本号
    private String versionid;

    public Atten() {
    }

    public Atten(String name, String tel, String versionid) {
        this.name = name;
        this.tel = tel;
        this.versionid = versionid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setVersionid(String versionid) {
        this.versionid = versionid;
    }

    public String getName() {
        return name;
    }

    public String getTel() {
        return tel;
    }

    public String getVersionid() {
        return versionid;
    }
}
