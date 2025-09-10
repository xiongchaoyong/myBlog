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
public class UserSearchVO implements Serializable {
    private String username;
    private String avatar;
    private String signature;
    private String status;
    private String account;
    private Long id;
}
