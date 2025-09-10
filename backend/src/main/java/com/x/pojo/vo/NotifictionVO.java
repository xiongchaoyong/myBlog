package com.x.pojo.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifictionVO implements Serializable {
    private String avatar;
    private String username;
    private Integer type;
    private  Integer status;
    private Date createdAt;
    private Long userId;
    private Long senderId;
    private Long groupId;
    private String content;
}
