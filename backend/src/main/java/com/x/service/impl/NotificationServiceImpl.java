package com.x.service.impl;

import com.x.exception.BaseException;
import com.x.mapper.NotificationMapper;
import com.x.pojo.dto.DeleteNotificationDTO;
import com.x.pojo.dto.NotificationDTO;
import com.x.pojo.dto.UpdateNotificationDTO;
import com.x.pojo.vo.NotifictionVO;
import com.x.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public void sendRequest(NotificationDTO notificationDTO) {
        notificationMapper.sendRequest(notificationDTO);
    }

    @Override
    public List<NotifictionVO> getSendNotificationList(Long id) {
        return notificationMapper.getSendNotificationList(id);
    }

    @Override
    public List<NotifictionVO> getReceiveNotificationList(Long id) {
        return notificationMapper.getReceiveNotificationList(id);
    }

    @Override
    public void updateStatus(UpdateNotificationDTO updateNotificationDTO) {
        System.out.println(updateNotificationDTO);
        try {
            notificationMapper.updateStatus(updateNotificationDTO);
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

    }

    @Override
    public void deleteNotification(DeleteNotificationDTO deleteNotificationDTO) {
        notificationMapper.deleteNotification(deleteNotificationDTO);
    }
}
