package com.x.service;

import com.x.pojo.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    List<ChatMessage> getCurMsgList(Long sessionId);

    Long addMsg(ChatMessage chatMessage);

    void deleteMsg(Long msgId);
}
