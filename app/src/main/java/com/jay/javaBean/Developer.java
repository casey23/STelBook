package com.jay.javaBean;

import cn.bmob.v3.BmobObject;

/**
 * 开发者描述类，描述开发者信息
 * Created by Jay on 2016/7/12.
 */
public class Developer extends BmobObject {
    private String name;
    private String tel;
    private String Email;
    private String description;

    public Developer() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
