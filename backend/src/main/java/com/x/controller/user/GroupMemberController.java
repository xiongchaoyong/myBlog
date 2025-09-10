package com.x.controller.user;

import com.x.pojo.dto.AddGroupMemberDTO;
import com.x.pojo.dto.DeleteGroupMemberDTO;
import com.x.service.GroupMemberService;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/groupMember")
@CrossOrigin
public class GroupMemberController {
    @Autowired
    private GroupMemberService groupMemberService;

    @PostMapping("/addMember")
    public Result addMember(@RequestBody AddGroupMemberDTO addGroupMemberDTO){
            groupMemberService.addMember(addGroupMemberDTO);
            return Result.success();
    }
    @DeleteMapping("/deleteMember")
    public Result deleteMember(@RequestBody DeleteGroupMemberDTO deleteGroupMemberDTO){
            groupMemberService.deleteMember(deleteGroupMemberDTO);
            return Result.success();
    }
}
