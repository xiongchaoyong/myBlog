package com.x.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 时间线事件实体类
 */
@Data
public class TimelineEvent {
    /**
     * 事件ID
     */
    private Long id;
    
    /**
     * 事件时间
     */
    private LocalDateTime time;
    
    /**
     * 事件标题
     */
    private String title;
    
    /**
     * 事件描述
     */
    private String desc;

    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}