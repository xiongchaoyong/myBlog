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
public class User implements Serializable {

    private Long id;            // 用户ID

    private String account;     // 账号（唯一标识）

    private  String email;      //邮箱

    private String password;    // 登录密码（加密存储）

    private String username;    // 昵称

    private String avatar;      // 头像URL

    private Integer gender;     // 性别 (0未知 1男 2女)

    private String region;      // 地区

    private String signature;   // 个性签名

    private Date createdAt;     // 注册时间  (数据库中默认当前时间)

    private String cover;       // 封面url

    private Integer admin;      //1 管理员 0普通用户

    private Integer status;    //1启用 0禁用

}
