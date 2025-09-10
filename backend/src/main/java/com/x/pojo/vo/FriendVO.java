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
public class FriendVO implements Serializable {
    private Long friendId;
    private String username;
    private String avatar;
    private String signature;
    private String region;
    private String cover;
    private Integer gender;
    private Date createdAt;
    private String account;
}
