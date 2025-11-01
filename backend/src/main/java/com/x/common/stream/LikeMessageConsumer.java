package com.x.common.stream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.x.common.constant.RedisKeyConstants;
import com.x.mapper.ArticleCommentMapper;
import com.x.mapper.LikeMapper;
import com.x.pojo.entity.Like;
import com.x.pojo.entity.LikeMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LikeMessageConsumer {
    
    @Autowired
    private LikeMapper likeMapper;
    
    @Autowired
    private ArticleCommentMapper articleCommentMapper;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    // 使用 @PostConstruct 启动消费者，或者使用 Spring Boot 的 CommandLineRunner
    @PostConstruct
    public void startConsumer() {
        new Thread(this::consumeMessages, "like-consumer-thread").start();
    }
    
    private void consumeMessages() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<MapRecord<String, Object, Object>> records = stringRedisTemplate.opsForStream()
                    .read(Consumer.from(RedisKeyConstants.LIKE_CONSUMER_GROUP,
                          RedisKeyConstants.LIKE_CONSUMER_NAME),
                        StreamReadOptions.empty().count(10).block(Duration.ofSeconds(2)),
                        StreamOffset.create(RedisKeyConstants.LIKE_STREAM, ReadOffset.lastConsumed())
                    );
                
                if (records != null && !records.isEmpty()) {
                    for (MapRecord<String, Object, Object> record : records) {
                        processMessage(record);
                    }
                }
            } catch (Exception e) {
                log.error("消费消息异常: ", e);
                try {
                    Thread.sleep(1000); // 异常后暂停1秒
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    private void processMessage(MapRecord<String, Object, Object> record) {
        String messageId = record.getId().getValue();
        
        try {
            // 解析消息
            Map<Object, Object> value = record.getValue();
            LikeMessage message = parseLikeMessage(value);
            
            if (message != null) {
                // 执行数据库操作
                executeDatabaseOperation(message);
                
                // 确认消息
                stringRedisTemplate.opsForStream()
                    .acknowledge(RedisKeyConstants.LIKE_CONSUMER_GROUP, record);
                
                log.info("成功处理点赞消息: messageId={}, commentId={}, userId={}, action={}", 
                    messageId, message.getCommentId(), message.getUserId(), message.getAction());
            }
        } catch (Exception e) {
            log.error("处理点赞消息失败: messageId={}", messageId, e);
            // 这里可以实现重试机制或者将失败消息移到死信队列
        }
    }

    private LikeMessage parseLikeMessage(Map<Object, Object> value) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // 配置 ObjectMapper 忽略未知属性
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            String json = mapper.writeValueAsString(value);
            return mapper.readValue(json, LikeMessage.class);
        } catch (Exception e) {
            log.error("解析点赞消息失败", e);
            return null;
        }
    }
    
    private void executeDatabaseOperation(LikeMessage message) {
        if (message.getAction() == LikeMessage.LikeAction.LIKE) {
            // 点赞操作
            Like like = new Like(null, message.getLikeType(), message.getCommentId(),
                message.getUserId(), null);
            likeMapper.insert(like);
            articleCommentMapper.likeComment(message.getCommentId());
        } else {
            // 取消点赞操作
            likeMapper.deleteByUserId(message.getUserId(), message.getLikeType(), 
                message.getCommentId());
            articleCommentMapper.unlikeComment(message.getCommentId());
        }
    }
}