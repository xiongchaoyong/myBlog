package com.x.service.impl;

import com.x.mapper.GroupMessageMapper;
import com.x.pojo.dto.GreetGroupMessageDTO;
import com.x.pojo.vo.GroupMessageVO;
import com.x.pojo.vo.GroupSessionVO;
import com.x.service.GroupMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupMessageServiceImpl implements GroupMessageService {
    @Autowired
    private GroupMessageMapper groupMessageMapper;

    @Override
    public void sendGreetMessage(GreetGroupMessageDTO greetGroupMessageDTO) {
        groupMessageMapper.sendMessage(greetGroupMessageDTO);
    }

    @Override
    public List<GroupMessageVO> getMesListAll(Long id) {
        return groupMessageMapper.getMesListAll(id);
    }

    @Override
    public List<GroupSessionVO> getMesList(Long id) {
        return groupMessageMapper.getMesList(id);
    }
}
