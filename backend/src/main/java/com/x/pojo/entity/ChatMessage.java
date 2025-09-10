package com.x.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage implements Serializable {
    private Long id;
    private Long sessionId;
    private Long userId;
    private String content;
    private Role role;  // user / ai

    public enum Role {
        user,
        ai
    }
}
