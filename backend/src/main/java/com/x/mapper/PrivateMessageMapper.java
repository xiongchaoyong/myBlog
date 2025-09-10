package com.x.mapper;

import com.x.pojo.dto.GreetMessageDTO;
import com.x.pojo.entity.PrivateMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PrivateMessageMapper {
    @Select( "select * from private_message where sender_id=#{id} or receiver_id=#{id}")
    List<PrivateMessage> getMesListAll(Long id);

    List<PrivateMessage> getMesList(Long id);

    // 插入一条新的私信消息
    @Insert("INSERT INTO private_message(content, sender_id, receiver_id, created_at) VALUES(#{content}, #{senderId}, #{receiverId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id") // 用于获取插入后生成的自增ID
    int insertMessage(PrivateMessage message);

    @Insert("INSERT INTO private_message(content, sender_id, receiver_id) " +
            "VALUES(#{content}, #{senderId}, #{receiverId})")
    void sendGreetMessage(GreetMessageDTO message);

    @Delete("DELETE FROM private_message WHERE sender_id in (#{curId},#{friendId}) AND receiver_id in (#{curId},#{friendId})")
    void deleteMesList(Long curId, Long friendId);
}
