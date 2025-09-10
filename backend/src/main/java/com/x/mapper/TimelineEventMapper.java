package com.x.mapper;

import com.x.pojo.entity.TimelineEvent;
import com.x.pojo.vo.TimelineEventVO;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 时间线事件Mapper接口
 */
@Mapper
public interface TimelineEventMapper {
    
    /**
     * 获取所有时间线事件
     */
    @Select("SELECT id,create_time,title,`desc` FROM timeline_event ORDER BY create_time DESC")
    List<TimelineEventVO> selectAll();

    /**
     * 插入时间线事件
     */
    @Insert("INSERT INTO timeline_event( title, `desc`, create_time, update_time) " +
           "VALUES( #{title}, #{desc},  #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(TimelineEvent event);
    

    

}