package com.x.pojo.vo;

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
public class GroupChatMemberVO implements Serializable {
    private Long id;
    private String account;
    private String username;
    private String avatar;
    private Date joinedAt;
}
