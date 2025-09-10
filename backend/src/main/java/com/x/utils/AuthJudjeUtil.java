package com.x.utils;

import com.x.context.BaseContext;

public class AuthJudjeUtil {
    public static void isAdmin() {
        if(BaseContext.getCurUserInfo().getAdmin()==0){
            throw new RuntimeException("你想干啥呢?");
        }
    }
}
