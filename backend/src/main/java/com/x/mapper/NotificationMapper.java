package com.x.mapper;

import com.x.pojo.dto.DeleteNotificationDTO;
import com.x.pojo.dto.NotificationDTO;
import com.x.pojo.dto.UpdateNotificationDTO;
import com.x.pojo.entity.Notification;
import com.x.pojo.vo.NotifictionVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NotificationMapper {
    @Select("select * from notification " +
            "where user_id=#{receiverId} and sender_id=#{currentId} and type=#{type}")
    Notification getByIds(Long currentId, Long receiverId,Integer type);

    void sendRequest(NotificationDTO notificationDTO);

    List<NotifictionVO> getSendNotificationList(Long id);
    List<NotifictionVO> getReceiveNotificationList(Long id);

    @Update("update notification set status=#{status} " +
            "where sender_id=#{senderId} and user_id=#{userId} and type=#{type}")
    void updateStatus(UpdateNotificationDTO updateNotificationDTO);
@Delete("delete from notification " +
        "where user_id in (#{userId},#{senderId}) and sender_id in (#{userId},#{senderId})")
    void deleteNotification(DeleteNotificationDTO deleteNotificationDTO);

    @Select("select count(*) from notification " +
            "where user_id=#{receiverId} and sender_id=#{currentId} and type=#{type} and group_id=#{groupId}")
    Integer getCountByIds(Long currentId, Long receiverId,Integer type,Long groupId);
}
