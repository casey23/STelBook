package com.jay.javaBean;

import cn.bmob.v3.BmobObject;

/**
 * 备份版本映射类
 * Created by Jay on 2016/7/10.
 */
public class version extends BmobObject {
    //创建版本用户的id
    private String user_id;

    public version() {
    }

    public version(String user_id) {

        this.user_id = user_id;
    }

    public void setUser_id(String user_id) {

        this.user_id = user_id;
    }

    public String getUser_id() {

        return user_id;
    }
}
