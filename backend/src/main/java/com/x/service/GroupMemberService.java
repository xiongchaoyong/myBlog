package com.x.service;

import com.x.pojo.dto.AddGroupMemberDTO;
import com.x.pojo.dto.DeleteGroupMemberDTO;

public interface GroupMemberService {
    void addMember(AddGroupMemberDTO addGroupMemberDTO);

    void deleteMember(DeleteGroupMemberDTO deleteGroupMemberDTO);
}
