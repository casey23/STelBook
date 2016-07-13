package com.jay.javaBean;

import cn.bmob.v3.BmobObject;

/**
 * 备份版本映射类
 * Created by Jay on 2016/7/10.
 */
public class Version extends BmobObject {
    //创建版本用户的id
    private String user_id;
    //备份总数
    private Integer count;

    public Version() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
