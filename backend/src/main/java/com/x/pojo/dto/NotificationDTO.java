package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class NotificationDTO implements Serializable {
    private Long senderId;
    private Integer status;
    private Integer type;
    private Long userId;
    private String content;
    private Long groupId;
}
