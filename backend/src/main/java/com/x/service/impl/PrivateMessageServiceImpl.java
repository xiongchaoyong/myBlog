package com.x.service.impl;

import com.x.context.BaseContext;
import com.x.mapper.PrivateMessageMapper;
import com.x.pojo.dto.GreetMessageDTO;
import com.x.pojo.entity.PrivateMessage;
import com.x.service.PrivateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PrivateMessageServiceImpl implements PrivateMessageService {
    @Autowired
    private PrivateMessageMapper privateMessageMapper;

    @Override
    public List<PrivateMessage> getMesListAll(Long id) {
        return privateMessageMapper.getMesListAll(id);
    }

    @Override
    public List<PrivateMessage> getMesList(Long id) {
            return privateMessageMapper.getMesList(id);
    }

    @Override
    public boolean sendMessage(PrivateMessage message) {
        message.setCreatedAt(new Date()); // 设置当前时间
        return privateMessageMapper.insertMessage(message) > 0;
    }

    @Override
    public void sendGreetMessage(GreetMessageDTO message) {
        privateMessageMapper.sendGreetMessage(message);
    }

    @Override
    public void deleteMesList(Long friendId) {
        Long curId= BaseContext.getCurUserInfo().getId();
        privateMessageMapper.deleteMesList(curId,friendId);
    }
}
