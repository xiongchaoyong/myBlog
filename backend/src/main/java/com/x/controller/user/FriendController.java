package com.x.controller.user;

import com.x.pojo.dto.AddFriendDTO;
import com.x.pojo.dto.DeleteFriendDTO;
import com.x.pojo.vo.FriendVO;
import com.x.service.FriendService;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/friend")
@CrossOrigin
public class FriendController {
    @Autowired
    private FriendService friendService;
    @GetMapping("/getFriendList/{id}")
    public Result<List<FriendVO>> getFriendList(@PathVariable Long id){
        return friendService.getFriendList(id);
    }
    @PostMapping("/addFriend")
    public Result addFriend(@RequestBody AddFriendDTO addFriendDTO){
        friendService.addFriend(addFriendDTO);
        return Result.success();
    }
    @PostMapping("/deleteFriend")
    public Result deleteFriend(@RequestBody DeleteFriendDTO deleteFriendDTO){
        friendService.deleteFriend(deleteFriendDTO);
        return Result.success();
    }

}
