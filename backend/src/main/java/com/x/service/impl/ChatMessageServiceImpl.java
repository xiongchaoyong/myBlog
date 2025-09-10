package com.x.service.impl;

import com.x.mapper.ChatMessageMapper;
import com.x.pojo.entity.ChatMessage;
import com.x.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    @Override
    public List<ChatMessage> getCurMsgList(Long sessionId) {

        return chatMessageMapper.getCurMsgList(sessionId);
    }

    @Override
    public Long addMsg(ChatMessage chatMessage) {
        chatMessageMapper.addMsg(chatMessage);
      return   chatMessage.getId();
    }

    @Override
    public void deleteMsg(Long msgId) {
        chatMessageMapper.deleteMsg(msgId);
    }

}
