package com.x.common.constant;

public class RedisKeyConstants {
    private RedisKeyConstants() {} // 私有构造，防止实例化

    public static final String LOCK_PREFIX = "lock:";

    // 具体业务锁
    public static final String LOCK_REGISTER = LOCK_PREFIX + "register";
}
