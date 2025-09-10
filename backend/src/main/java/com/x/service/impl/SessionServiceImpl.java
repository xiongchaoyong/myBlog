package com.x.service.impl;

import com.x.mapper.SessionMapper;
import com.x.pojo.dto.UpdateSessionTitleDTO;
import com.x.pojo.entity.Session;
import com.x.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service

public class SessionServiceImpl implements SessionService {
    @Autowired
    private SessionMapper sessionMapper;

    @Override
    public List<Session> getSessionList(Long userId) {
        return sessionMapper.getSessionList(userId);
    }

    @Override
    public Long addSession(Session session) {
        sessionMapper.addSession(session);
        return session.getId();
    }

    @Override
    public void updateSessionTitle(UpdateSessionTitleDTO updateSessionTitleDTO) {
        sessionMapper.updateSessionTitle(updateSessionTitleDTO);
    }

    @Override
    public void deleteSession(Long sessionId) {
        sessionMapper.deleteSession(sessionId);
    }
}
