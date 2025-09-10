package com.x.pojo.dto;

import lombok.Data;

/**
 * 时间线事件数据传输对象
 */
@Data
public class TimelineEventDTO {
    /**
     * 事件标题
     */
    private String title;
    
    /**
     * 事件描述
     */
    private String desc;

}