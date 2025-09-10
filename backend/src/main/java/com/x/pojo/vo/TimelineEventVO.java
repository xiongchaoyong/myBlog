package com.x.pojo.vo;

import lombok.Data;

@Data
public class TimelineEventVO {
    /**
     * 事件标题
     */
    private Long id;
    private String title;

    /**
     * 事件描述
     */
    private String desc;

    private String createTime;
}
