package com.x.mapper;

import com.x.pojo.dto.AddFriendDTO;
import com.x.pojo.dto.DeleteFriendDTO;
import com.x.pojo.entity.Friend;
import com.x.pojo.vo.FriendVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FriendMapper {
    List<FriendVO> getFriendList(Long id);

    @Select("select * from friend where user_id= #{currentId} and friend_id= #{receiverId}")
    Friend getByIds(Long currentId, Long receiverId);

    //同时插入两条
    @Insert("insert into friend (user_id,friend_id) " +
            "values (#{userId},#{friendId}),(#{friendId},#{userId})")
    void addFriend(AddFriendDTO addFriendDTO);

    @Delete("delete from friend where user_id in (#{userId},#{friendId}) and friend_id in (#{userId},#{friendId})")
    void deleteFriend(DeleteFriendDTO deleteFriendDTO);

    @Select("select user_id from friend where friend_id=#{userId}")
    List<Long> getFriendIds(Long userId);
}
