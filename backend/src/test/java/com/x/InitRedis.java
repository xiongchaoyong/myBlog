package com.x;

import com.x.common.constant.RedisKeyConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.concurrent.TimeUnit;

@SpringBootTest

public class InitRedis {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    public void initArticleIds() throws InterruptedException {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        for(int i=1;i<=21;i++){
            //时间戳为秒
            zSetOperations.add(RedisKeyConstants.STORE_ARTICLEIDS,String.valueOf(i),System.currentTimeMillis());

            //停顿
            long sleep =50+ (long) (Math.random() * 150);
            TimeUnit.MILLISECONDS.sleep(sleep);
        }
    }
    @Test
    public void initPostIds() throws InterruptedException {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        for(int i=5;i<=27;i++){
            //时间戳为秒
            zSetOperations.add(RedisKeyConstants.STORE_POSTIDS,String.valueOf( i),System.currentTimeMillis());

            //停顿
            long sleep =50+ (long) (Math.random() * 150);
            TimeUnit.MILLISECONDS.sleep(sleep);
        }
    }
}
