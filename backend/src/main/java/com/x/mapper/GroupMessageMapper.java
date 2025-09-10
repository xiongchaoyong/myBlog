package com.x.mapper;

import com.x.pojo.dto.GreetGroupMessageDTO;
import com.x.pojo.vo.GroupMessageVO;
import com.x.pojo.vo.GroupSessionVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMessageMapper {
    @Insert("INSERT INTO group_message(content, sender_id, group_id) " +
            "values (#{content},#{senderId},#{groupId})")
    void sendMessage(GreetGroupMessageDTO greetGroupMessageDTO);

    List<GroupMessageVO> getMesListAll(Long id);

    List<GroupSessionVO> getMesList(Long id);
}
