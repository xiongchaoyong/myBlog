package com.x.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupSearchVO implements Serializable {
    private Long id;
    private String groupAvatar;
    private String groupName;
    private String groupAccount;
    private Integer memberCount;
    private String creatorName;
    private Long creatorId;
    private String  status;
}
