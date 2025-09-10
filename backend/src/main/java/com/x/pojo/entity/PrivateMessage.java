package com.x.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrivateMessage implements Serializable {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Date createdAt;
}
