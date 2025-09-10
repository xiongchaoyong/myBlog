package com.x.mapper;

import com.x.pojo.dto.UpdateSessionTitleDTO;
import com.x.pojo.entity.Session;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SessionMapper {
    @Select("SELECT * FROM session WHERE user_id = #{userId}")
    List<Session> getSessionList(Long userId);

    @Insert("INSERT INTO session (user_id, title) VALUES (#{userId}, #{title})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addSession(Session session);

    @Update("UPDATE session SET title = #{title} WHERE id = #{sessionId}")
    void updateSessionTitle(UpdateSessionTitleDTO updateSessionTitleDTO);

    @Delete("DELETE FROM session WHERE id = #{sessionId}")
    void deleteSession(Long sessionId);
}
