package com.x.controller.user;

import com.x.pojo.dto.TimelineEventDTO;
import com.x.pojo.vo.TimelineEventVO;
import com.x.service.TimelineEventService;
import com.x.utils.AuthJudjeUtil;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 时间线事件控制器
 */
@RestController
@RequestMapping("/user/timeline")
@CrossOrigin
public class TimelineEventController {

    @Autowired
    private TimelineEventService timelineEventService;

    /**
     * 获取所有时间线事件
     */
    @GetMapping
    public Result<List<TimelineEventVO>> getAllEvents() {
       List list=timelineEventService.getAllEvents();
       return Result.success(list);
    }


    /**
     * 创建时间线事件
     */
    @PostMapping("/create")
    public Result createEvent(@RequestBody TimelineEventDTO eventDTO) {
        AuthJudjeUtil.isAdmin();
        timelineEventService.createEvent(eventDTO);
        return Result.success();
    }

}