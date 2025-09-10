package com.x.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session implements Serializable {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime createdTime;
}
