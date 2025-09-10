package com.x.service;

import com.x.pojo.dto.GreetGroupMessageDTO;
import com.x.pojo.vo.GroupMessageVO;
import com.x.pojo.vo.GroupSessionVO;

import java.util.List;

public interface GroupMessageService {
    void sendGreetMessage(GreetGroupMessageDTO greetGroupMessageDTO);

    List<GroupMessageVO> getMesListAll(Long id);

    List<GroupSessionVO> getMesList(Long id);
}
