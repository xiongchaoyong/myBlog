package com.x.service.impl;

import com.x.context.BaseContext;
import com.x.exception.BaseException;
import com.x.mapper.GroupChatMapper;
import com.x.mapper.GroupMemberMapper;
import com.x.mapper.NotificationMapper;
import com.x.mapper.UserMapper;
import com.x.pojo.dto.CreateGroupDTO;
import com.x.pojo.dto.GroutChatDTO;
import com.x.pojo.dto.NotificationDTO;
import com.x.pojo.entity.GroupChat;
import com.x.pojo.vo.GroupChatMemberVO;
import com.x.pojo.vo.GroupSearchVO;
import com.x.pojo.vo.GroupVO;
import com.x.service.GroupChatSerivice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupChatSeriviceImpl implements GroupChatSerivice {
    @Autowired
    private GroupChatMapper groupChatMapper;
    @Autowired
    private GroupMemberMapper groupMemberMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public List<GroupChat> getMyCreatedGroups(Long id) {
        return groupChatMapper.getMyCreatedGroups(id);
    }

    @Override
    public Integer getGroupMembersCount(Long groupId) {
        return groupMemberMapper.getGroupMembersCount(groupId);
    }

    @Override
    public List<GroupChat> getMyJoinedGroups(Long id) {
        return groupChatMapper.getMyJoinedGroups(id);
    }

    @Override
    public List<GroupChatMemberVO> getGroupMembers(Long groupId) {
        return  groupMemberMapper.getGroupMembers(groupId);
    }

    @Override
    public void updateGroup(GroutChatDTO groutChatDTO) {
        groupChatMapper.updateGroup(groutChatDTO);
    }

    @Transactional
    @Override
    public void createGroup(CreateGroupDTO createGroupDTO) {
        GroupChat groupChat = GroupChat.builder()
                .creatorId(BaseContext.getCurUserInfo().getId())
                .groupName(createGroupDTO.getGroupName())
                .groupAvatar(createGroupDTO.getGroupAvatar())
                .groupAccount(createGroupDTO.getGroupAccount())
                .build();
        try {
            //插入一条群聊表数据
            groupChatMapper.createGroup(groupChat);
        }
        catch (Exception e) {
            throw new BaseException("群号已存在,换个群号试试吧！");
        }

        List<Long> selectedMembers = createGroupDTO.getMemberIds();
        //插入一条群聊成员表数据
        groupMemberMapper.insertGroupMember(groupChat.getId(),groupChat.getCreatorId());
        //插入多条通知数据
        for(Long memberId:selectedMembers) {
            notificationMapper.sendRequest(NotificationDTO.builder()
                    .userId(memberId)
                    .senderId(BaseContext.getCurUserInfo().getId())
                    .type(2)
                    .content(groupChat.getGroupName())
                    .status(0)
                    .groupId(groupChat.getId())
                    .build());
        }
    }

    @Transactional
    @Override
    public void dissolveGroup(Long id) {
        groupChatMapper.dissolveGroup(id);
        groupMemberMapper.deleteGroupMember(id);
    }

    @Transactional
    @Override
    public GroupSearchVO searchGroup(String account) {
        //判断群组存不存在
        GroupChat groupChat = groupChatMapper.getGroupByAccount(account);
        if(groupChat == null){
            return null;
        }
        else {
            //群存在
            //判断有没有申请过
            Long receiverId = groupChat.getCreatorId();
            Long senderId = BaseContext.getCurUserInfo().getId();
            Integer type = 2;
            Integer  notificationCount = notificationMapper.getCountByIds(senderId,receiverId,type,groupChat.getId());

            //判断是否是群主
            int countt=groupChatMapper.getCountByGroupIdAndUserId(groupChat.getId(),BaseContext.getCurUserInfo().getId());
            if(countt>0){
                return GroupSearchVO.builder()
                        .id(groupChat.getId())
                        .groupAvatar(groupChat.getGroupAvatar())
                        .groupName(groupChat.getGroupName())
                        .groupAccount(groupChat.getGroupAccount())
                        .memberCount(groupMemberMapper.getGroupMembersCount(groupChat.getId()))
                        .creatorName(userMapper.getById(groupChat.getCreatorId()).getUsername())
                        .status("created")
                        .creatorId(groupChat.getCreatorId())
                        .build()
                        ;
            }
            //判断是否已经加入
            int count=groupMemberMapper.getCountByGroupIdAndUserId(groupChat.getId(), BaseContext.getCurUserInfo().getId());
            if(count>0){
                return GroupSearchVO.builder()
                        .id(groupChat.getId())
                        .groupAvatar(groupChat.getGroupAvatar())
                        .groupName(groupChat.getGroupName())
                        .groupAccount(groupChat.getGroupAccount())
                        .memberCount(groupMemberMapper.getGroupMembersCount(groupChat.getId()))
                        .creatorName(userMapper.getById(groupChat.getCreatorId()).getUsername())
                        .status("joined")
                        .creatorId(groupChat.getCreatorId())
                        .build();
            }

            if(notificationCount != 0) {
                //申请过
                return GroupSearchVO.builder()
                        .id(groupChat.getId())
                        .groupAvatar(groupChat.getGroupAvatar())
                        .groupName(groupChat.getGroupName())
                        .groupAccount(groupChat.getGroupAccount())
                        .memberCount(groupMemberMapper.getGroupMembersCount(groupChat.getId()))
                        .creatorName(userMapper.getById(groupChat.getCreatorId()).getUsername())
                        .creatorId(groupChat.getCreatorId())
                        .status("pending").build();
            }
            //获取群
            return GroupSearchVO.builder()
                    .id(groupChat.getId())
                    .groupAvatar(groupChat.getGroupAvatar())
                    .groupName(groupChat.getGroupName())
                    .groupAccount(groupChat.getGroupAccount())
                    .memberCount(groupMemberMapper.getGroupMembersCount(groupChat.getId()))
                    .creatorName(userMapper.getById(groupChat.getCreatorId()).getUsername())
                    .creatorId(groupChat.getCreatorId())
                    .status("notJoined").build();
        }
    }

    @Override
    public List<GroupVO> getAll() {
        return groupChatMapper.getAll();
    }
}
