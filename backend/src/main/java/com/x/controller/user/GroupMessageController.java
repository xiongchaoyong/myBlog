package com.x.controller.user;

import com.x.pojo.dto.GreetGroupMessageDTO;
import com.x.pojo.vo.GroupMessageVO;
import com.x.pojo.vo.GroupSessionVO;
import com.x.service.GroupMessageService;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/groupMessage")
@CrossOrigin
public class GroupMessageController {
    @Autowired
    private GroupMessageService groupMessageService;

    @PostMapping("/sendGreetMessage")
    public Result sendMessage(@RequestBody GreetGroupMessageDTO greetGroupMessageDTO){
        groupMessageService.sendGreetMessage(greetGroupMessageDTO);
        return Result.success();
    }
    @GetMapping("getMesListAll/{id}")
    public Result<List<GroupMessageVO>> getMesListAll(@PathVariable Long id){
        List<GroupMessageVO> list = groupMessageService.getMesListAll(id);
        return Result.success(list);
    }
    @GetMapping("getMesList/{id}")
    public Result<List<GroupSessionVO>> getMesList(@PathVariable Long id){
        List<GroupSessionVO> list = groupMessageService.getMesList(id);
        return Result.success(list);
    }
}
