package com.x.service;

import com.x.pojo.dto.GreetMessageDTO;
import com.x.pojo.entity.PrivateMessage;

import java.util.List;

public interface PrivateMessageService {
    List<PrivateMessage> getMesListAll(Long id);

    List<PrivateMessage> getMesList(Long id);
    boolean sendMessage(PrivateMessage message);

    void sendGreetMessage(GreetMessageDTO message);

    void deleteMesList(Long friendId);
}
