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
public class GroupChat implements Serializable {
    private Long id;
    private  String groupName;
    private String groupAvatar;
    private String groupAccount;
    private Long creatorId;
    private Date createdAt;
}
