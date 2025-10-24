package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * UserLoginDTO 类
 * 作者：朝永
 * 日期：2025/10/22
 * 描述：UserLoginDTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginDTO implements Serializable {
    private String password;
    private String account;
    private String email;
}
