package com.x.service;

import com.x.pojo.dto.UpdateSessionTitleDTO;
import com.x.pojo.entity.Session;

import java.util.List;

public interface SessionService {
    List<Session> getSessionList(Long userId);

    Long addSession(Session session);

    void updateSessionTitle(UpdateSessionTitleDTO updateSessionTitleDTO);

    void deleteSession(Long sessionId);
}
