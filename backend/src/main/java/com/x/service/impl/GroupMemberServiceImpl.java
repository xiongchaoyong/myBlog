package com.x.service.impl;

import com.x.exception.BaseException;
import com.x.mapper.GroupMemberMapper;
import com.x.pojo.dto.AddGroupMemberDTO;
import com.x.pojo.dto.DeleteGroupMemberDTO;
import com.x.service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {
    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Override
    public void addMember(AddGroupMemberDTO addGroupMemberDTO) {
        try {
            groupMemberMapper.addMember(addGroupMemberDTO);

        }
        catch (Exception e){
                throw  new BaseException("已加入群聊了哦!");
        }
    }

    @Override
    public void deleteMember(DeleteGroupMemberDTO deleteGroupMemberDTO) {
        groupMemberMapper.deleteOneGroupMember( deleteGroupMemberDTO.getGroupId(),deleteGroupMemberDTO.getUserId());
    }
}
