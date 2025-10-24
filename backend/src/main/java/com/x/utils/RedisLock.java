package com.x.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class RedisLock implements ILock{
    private static final String KEY_PREFIX = "lock:";
    private StringRedisTemplate stringRedisTemplate;
    private String name;
    public RedisLock(StringRedisTemplate stringRedisTemplate, String name) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.name = name;
    }
    @Override
    public boolean tryLock(Long timeoutSec) {
        //请求线程id
        long threadId = Thread.currentThread().getId();
        Boolean b = stringRedisTemplate
                .opsForValue()
                .setIfAbsent(KEY_PREFIX + name, threadId + "", timeoutSec, TimeUnit.SECONDS);
        return b != null && b;
    }



    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    @Override
    public void unlock() {
        stringRedisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(KEY_PREFIX + name),
                Thread.currentThread().getId()+"");
    }

    //        //获取线程标识
//        String threadId = Thread.currentThread().getId()+"";
//        //获取锁中的标识（value）
//        String id = stringRedisTemplate.opsForValue().get(KEY_PREFIX + name);
//        //判断标识是否一致
//        if (threadId.equals(id)) {
//            //释放锁
//            stringRedisTemplate.delete(KEY_PREFIX + name);
//        }
//        stringRedisTemplate.delete(KEY_PREFIX + name);

}
