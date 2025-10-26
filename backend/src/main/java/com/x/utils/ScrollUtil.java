package com.x.utils;

import com.x.exception.BaseException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

public class ScrollUtil {
    public static Map<String, Object> ScllorGetIds(String key, Integer min, Long max, Integer offset, int count, StringRedisTemplate redisTemplate){
        //从redis中查询缓存数据  id :  timestamp
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(
                key, min, max, offset, count);
        // 3.非空判断
        if (typedTuples == null || typedTuples.isEmpty()) {
            throw new BaseException("没有更多数据了");
        }
        //解析数据
        List<Integer> ids = new ArrayList<>(typedTuples.size());
        int os=1;
        long minTime= max;
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            String id = typedTuple.getValue();
            ids.add(Integer.parseInt(id));
            long time=typedTuple.getScore().longValue();
            if(time == minTime){
                os++;
            }else{
                os=1;
                minTime=time;
            }
        }
        Map<String,Object> res=new HashMap();
        res.put("minTime",minTime);
        res.put("os",os);
        res.put("ids",ids);
        return res;

    }
}
