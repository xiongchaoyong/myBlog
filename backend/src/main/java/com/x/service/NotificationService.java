package com.x.service;

import com.x.pojo.dto.DeleteNotificationDTO;
import com.x.pojo.dto.NotificationDTO;
import com.x.pojo.dto.UpdateNotificationDTO;
import com.x.pojo.vo.NotifictionVO;

import java.util.List;

public interface NotificationService {
    void sendRequest(NotificationDTO notificationDTO);

    List<NotifictionVO> getSendNotificationList(Long id);

    List<NotifictionVO> getReceiveNotificationList(Long id);

    void updateStatus(UpdateNotificationDTO updateNotificationDTO);

    void deleteNotification(DeleteNotificationDTO deleteNotificationDTO);
}
