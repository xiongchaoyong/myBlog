package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateGroupDTO implements Serializable {
    private String groupName;
    private String groupAccount;
    private String groupAvatar;
    private List<Long> memberIds;
    private Long creatorId;
}
