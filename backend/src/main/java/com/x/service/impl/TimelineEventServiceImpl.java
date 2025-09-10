package com.x.service.impl;

import com.x.pojo.dto.TimelineEventDTO;
import com.x.pojo.entity.TimelineEvent;
import com.x.mapper.TimelineEventMapper;
import com.x.pojo.vo.TimelineEventVO;
import com.x.service.TimelineEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 时间线事件服务实现类
 */
@Service
public class TimelineEventServiceImpl implements TimelineEventService {

    @Autowired
    private TimelineEventMapper timelineEventMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取所有时间线事件
     */
    @Override
    public List<TimelineEventVO> getAllEvents() {
        List<TimelineEventVO> events = timelineEventMapper.selectAll();
        return events;
    }



    /**
     * 创建时间线事件
     */
    @Override
    public void createEvent(TimelineEventDTO eventDTO) {
        TimelineEvent event =new TimelineEvent();
        LocalDateTime now = LocalDateTime.now();
        event.setTitle(eventDTO.getTitle());
        event.setDesc(eventDTO.getDesc());
        event.setCreateTime(now);
        event.setUpdateTime(now);
        
        timelineEventMapper.insert(event);
    }

}

