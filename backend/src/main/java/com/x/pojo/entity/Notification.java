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
public class Notification implements Serializable {
    private Long id;
    private Long userId;
    private Long senderId;
    private String content;
    private Date createdAt;
    private Integer status;
    private String type;
    private Long groupId;

}
