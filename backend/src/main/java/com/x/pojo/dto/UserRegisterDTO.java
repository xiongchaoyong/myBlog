package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterDTO implements Serializable {
    private Long id;
    private String email;  //邮箱时需要
    private String password;
    private String  username;
    private String account;  //插入时需要
    private String avatar;  //第三方登录时需要
}
