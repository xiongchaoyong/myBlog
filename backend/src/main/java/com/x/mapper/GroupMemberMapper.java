package com.x.mapper;

import com.x.pojo.dto.AddGroupMemberDTO;
import com.x.pojo.vo.GroupChatMemberVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupMemberMapper {

    @Select("select count(*) from group_member where group_id=#{groupId}")
    Integer getGroupMembersCount(Long groupId);
    @Select("select u.id,u.username,u.account,u.avatar,gm.joined_at from user u " +
            " join group_member gm on u.id=gm.user_id" +
            " where gm.group_id=#{groupId}")
    List<GroupChatMemberVO> getGroupMembers(Long groupId);

    void insertGroupMember(Long id, Long creatorId);

    @Delete("delete from group_member where group_id=#{id}")
    void deleteGroupMember(Long id);

    @Insert("insert into group_member(group_id,user_id) values(#{groupId},#{userId})")
    void addMember(AddGroupMemberDTO addGroupMemberDTO);

    @Delete("delete from group_member where group_id=#{groupId} and user_id=#{userId}")
    void deleteOneGroupMember(Long groupId, Long userId);

    @Select("select count(*) from group_member " +
            "where group_id=#{id} and user_id=#{currentId}")
    int getCountByGroupIdAndUserId(Long id, Long currentId);

    @Select("select user_id from group_member where group_id=#{id}")
    List<Integer> getIdsByGroupId(Integer groupId);
}
