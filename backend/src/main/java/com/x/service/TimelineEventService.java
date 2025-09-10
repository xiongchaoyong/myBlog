package com.x.service;

import com.x.pojo.dto.TimelineEventDTO;
import com.x.pojo.vo.TimelineEventVO;

import java.util.List;

/**
 * 时间线事件服务接口
 */
public interface TimelineEventService {
    
    /**
     * 获取所有时间线事件
     */
    List<TimelineEventVO> getAllEvents();
    

    /**
     * 创建时间线事件
     */
    void createEvent(TimelineEventDTO eventDTO);

}