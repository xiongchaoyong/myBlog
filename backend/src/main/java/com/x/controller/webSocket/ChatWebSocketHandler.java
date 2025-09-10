package com.x.controller.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.x.mapper.GroupMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            String userId = session.getUri().getQuery().split("=")[1]; // 解析 ?userId=1
            sessions.put(userId, session);
            System.out.println("用户 " + userId + " 已连接");
        } catch (Exception e) {
            System.out.println("连接失败：" + e.getMessage());
            try {
                session.close();
            } catch (Exception ignored) {}
        }
    }

@Override
protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String payload = message.getPayload();//获得消息体
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> msgMap = mapper.readValue(payload, Map.class);
    Map<String, Object> toMap = (Map<String, Object>) msgMap.get("to");//解析
    String type = (String) toMap.get("type");
    if ("private".equals(type)) {
        Integer receiverId = (Integer) toMap.get("receiverId");
        WebSocketSession toSession = sessions.get(receiverId.toString());
        if (toSession != null && toSession.isOpen()) {
            toSession.sendMessage(new TextMessage(payload));//主动发送消息
        }
    } else if ("group".equals(type)) {
        Integer groupId = (Integer) toMap.get("groupId");
        List<Integer> memberIds = groupMemberMapper.getIdsByGroupId(groupId);
        for (Integer memberId : memberIds) {
            WebSocketSession toSession = sessions.get(memberId.toString());
            if (toSession != null && toSession.isOpen()) {
                toSession.sendMessage(new TextMessage(payload));
            }
        }
    }
}



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
        System.out.println("连接关闭");
    }
}
