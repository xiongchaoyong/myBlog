package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateDTO implements Serializable {
    private String account;
    private String avatar;
    private String cover;
    private Integer gender;     // 性别 (0未知 1男 2女)
    private String newPassword;
    private String oldPassword;
    private String region;
    private String signature;
    private String username;
}
