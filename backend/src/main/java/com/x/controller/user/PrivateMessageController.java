package com.x.controller.user;

import com.x.pojo.dto.GreetMessageDTO;
import com.x.pojo.entity.PrivateMessage;
import com.x.service.PrivateMessageService;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/privateMessage")
@CrossOrigin
public class PrivateMessageController {
    @Autowired
    private PrivateMessageService privateMessageService;
    @GetMapping("/getMesListAll/{id}")
    public Result<List<PrivateMessage>> getMesListAll(@PathVariable Long id) {
        List<PrivateMessage> list = privateMessageService.getMesListAll(id);
        return Result.success(list);
    }
    @GetMapping("/getMesList/{id}")
    public Result<List<PrivateMessage>> getMesList(@PathVariable Long id) {
        List<PrivateMessage> list = privateMessageService.getMesList(id);
        return Result.success(list);
    }

    @PostMapping("/sendMessage")
    public Result sendMessage(@RequestBody PrivateMessage message) {
        try {
            boolean success = privateMessageService.sendMessage(message);
            if (success) {
                // 消息成功保存到数据库后，这里通常会通过WebSocket发送给接收者
                // 这部分逻辑将由 WebSocketController 处理
                return Result.success("消息发送成功");
            } else {
                return Result.error("消息保存失败");
            }
        } catch (Exception e) {
            return Result.error("消息发送异常: " + e.getMessage());
        }
    }
    @PostMapping("/sendGreetMessage")
    public Result sendGreetMessage(@RequestBody GreetMessageDTO message) {
        privateMessageService.sendGreetMessage(message);
        return Result.success();
    }
    @DeleteMapping("/deleteMesList/{friendId}")
    public Result deleteMesList(@PathVariable Long friendId) {
        privateMessageService.deleteMesList(friendId);
        return Result.success();
    }
}
