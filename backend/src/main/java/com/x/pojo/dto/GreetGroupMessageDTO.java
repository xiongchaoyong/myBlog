package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class GreetGroupMessageDTO implements Serializable {
    private Long groupId;
    private String content;
    private Long senderId;
}
