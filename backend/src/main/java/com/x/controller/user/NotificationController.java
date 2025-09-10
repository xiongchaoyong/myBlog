package com.x.controller.user;

import com.x.pojo.dto.DeleteNotificationDTO;
import com.x.pojo.dto.NotificationDTO;
import com.x.pojo.dto.UpdateNotificationDTO;
import com.x.pojo.vo.NotifictionVO;
import com.x.service.NotificationService;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/notification")
@CrossOrigin
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/sendRequest")
    public Result sendRequest(@RequestBody NotificationDTO notificationDTO){
         notificationService.sendRequest(notificationDTO);
         return Result.success();
    }
    @GetMapping("/getSendNotificationList/{id}")
    public Result<List<NotifictionVO>> getSendNotificationList(@PathVariable Long id){
        List<NotifictionVO> notificationList = notificationService.getSendNotificationList(id);
        return Result.success(notificationList) ;
    }
    @GetMapping("/getReceiveNotificationList/{id}")
    public Result<List<NotifictionVO>> getReceiveNotificationList(@PathVariable Long id){
        List<NotifictionVO> notificationList = notificationService.getReceiveNotificationList(id);
        return Result.success(notificationList) ;
    }
    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestBody UpdateNotificationDTO updateNotificationDTO){
        System.out.println(updateNotificationDTO+"askjdhlfalsiufaip");
        notificationService.updateStatus(updateNotificationDTO);
        return Result.success();
    }
    @PostMapping("deleteNotification")
    public Result deleteNotification(@RequestBody DeleteNotificationDTO deleteNotificationDTO){
        notificationService.deleteNotification(deleteNotificationDTO);
        return Result.success();
    }
}
