package com.x.context;

import com.x.pojo.entity.UserInfo;

public class BaseContext {

    public static ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();

    public static void setCurUserInfo(UserInfo userInfo) {
        threadLocal.set(userInfo);
    }

    public static UserInfo getCurUserInfo() {
        return threadLocal.get();
    }


    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
