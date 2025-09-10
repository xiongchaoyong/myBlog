package com.x.mapper;

import com.x.pojo.entity.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMessageMapper {
    @Select("SELECT * FROM chat_message WHERE session_id = #{sessionId}")
    List<ChatMessage> getCurMsgList(Long sessionId);

    @Insert("INSERT INTO chat_message (session_id, user_id, content, role) VALUES (#{sessionId}, #{userId}, #{content}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addMsg(ChatMessage chatMessage);

    @Delete("DELETE FROM chat_message WHERE id = #{msgId}")
    void deleteMsg(Long msgId);
}
