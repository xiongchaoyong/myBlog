package com.x.service;

import com.x.pojo.dto.CreateGroupDTO;
import com.x.pojo.dto.GroutChatDTO;
import com.x.pojo.entity.GroupChat;
import com.x.pojo.vo.GroupChatMemberVO;
import com.x.pojo.vo.GroupSearchVO;
import com.x.pojo.vo.GroupVO;

import java.util.List;

public interface GroupChatSerivice {
    List<GroupChat> getMyCreatedGroups(Long id);

    Integer getGroupMembersCount(Long groupId);

    List<GroupChat> getMyJoinedGroups(Long id);

    List<GroupChatMemberVO> getGroupMembers(Long groupId);

    void updateGroup(GroutChatDTO groutChatDTO);

    void createGroup(CreateGroupDTO createGroupDTO);

    void dissolveGroup(Long id);

    GroupSearchVO searchGroup(String account);

    List<GroupVO> getAll();
}
