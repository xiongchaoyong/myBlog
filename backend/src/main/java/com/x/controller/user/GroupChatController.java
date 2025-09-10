package com.x.controller.user;

import com.x.pojo.dto.CreateGroupDTO;
import com.x.pojo.dto.GroutChatDTO;
import com.x.pojo.entity.GroupChat;
import com.x.pojo.vo.GroupChatMemberVO;
import com.x.pojo.vo.GroupSearchVO;
import com.x.service.GroupChatSerivice;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/groupChat")
@CrossOrigin
public class GroupChatController {
    @Autowired
    private GroupChatSerivice groupChatService;

    @GetMapping("/getMyCreatedGroups/{id}")
    public Result<List<GroupChat>> getMyCreatedGroups(@PathVariable Long id){
        List<GroupChat> list = groupChatService.getMyCreatedGroups(id);
        return Result.success(list);
    }
    @GetMapping("/getGroupMembersCount/{groupId}")
    public Result<Integer> getGroupMembersCount(@PathVariable Long groupId){
        Integer count = groupChatService.getGroupMembersCount(groupId);
        return Result.success(count);
    }

    @GetMapping("/getMyJoinedGroups/{id}")
    public Result<List<GroupChat>> getMyJoinedGroups(@PathVariable Long id){
        List<GroupChat> list=groupChatService.getMyJoinedGroups(id);
        return Result.success(list);
    }

    @GetMapping("/getGroupMembers/{groupId}")
    public Result<List<GroupChatMemberVO>> getGroupMembers(@PathVariable Long groupId){
        List<GroupChatMemberVO> list=groupChatService.getGroupMembers(groupId);
        return Result.success(list);
    }
    @PostMapping("/updateGroup")
    public Result updateGroup(@RequestBody GroutChatDTO groutChatDTO){
        groupChatService.updateGroup(groutChatDTO);
        return Result.success();
    }
    @PostMapping("/createGroup")
    public Result createGroup(@RequestBody CreateGroupDTO createGroupDTO){
        groupChatService.createGroup(createGroupDTO);
        return Result.success();
    }
    @DeleteMapping("/dissolveGroup/{id}")
    public Result dissolveGroup(@PathVariable Long id){
        groupChatService.dissolveGroup(id);
        return Result.success();
    }
    @GetMapping("/searchGroup/{account}")
    public Result<GroupSearchVO> searchGroup(@PathVariable String account){
        GroupSearchVO groupSearchVO = groupChatService.searchGroup(account);
        return Result.success(groupSearchVO);
    }
}
