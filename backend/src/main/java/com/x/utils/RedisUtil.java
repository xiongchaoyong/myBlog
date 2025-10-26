package com.x.utils;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RedisUtil {

    public static Long getCount(String key, long ttl, TimeUnit unit, Supplier<Long> dbFallback, StringRedisTemplate stringRedisTemplate) {
        // 1. 先查缓存
        String count = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(count)) {
            return Long.parseLong(count);
        }

        // 2. 缓存未命中，查询数据库
        Long countRes = dbFallback.get();
        if (countRes == null) {
            countRes = 0L;
        }
        int randomTtl = (int) (ttl * 0.8) + new Random().nextInt((int)(ttl * 0.4)); // 随机 TTL
        // 3. 写入缓存
        stringRedisTemplate.opsForValue().set(key, countRes.toString(), randomTtl, unit);
        return countRes;
    }

}
