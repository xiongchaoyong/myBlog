package com.x.mapper;

import com.x.pojo.dto.GroutChatDTO;
import com.x.pojo.entity.GroupChat;
import com.x.pojo.vo.GroupVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GroupChatMapper {
    @Select("select * from group_chat where creator_id=#{id}")
    List<GroupChat> getMyCreatedGroups(Long id);

    @Select("select * from group_chat gc join group_member gm " +
            "on gc.id=gm.group_id " +
            "where gm.user_id=#{id}" +
            " and gc.creator_id !=#{id}")
    List<GroupChat> getMyJoinedGroups(Long id);

    @Update("update group_chat " +
            "set group_name=#{name},group_avatar=#{groupAvatar}" +
            "where id=#{id}")
    void updateGroup(GroutChatDTO groutChatDTO);

    void createGroup(GroupChat groupChat);

    @Delete("delete from group_chat where id=#{id}")
    void dissolveGroup(Long id);

    @Select("select * from group_chat where group_account=#{account}")
    GroupChat getGroupByAccount(String account);

    @Select("select count(*) from group_chat where id=#{id} and creator_id=#{currentId}")
    int getCountByGroupIdAndUserId(Long id, Long currentId);


    List<GroupVO> getAll();

    @Select("select count(*) from group_chat")
    Long getGroupTotal();
}
