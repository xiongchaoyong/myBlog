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
public class UpdateNotificationDTO implements Serializable {
    private Long senderId;
    private Long userId;
    private Integer status;
    private Integer type;
}
