package com.x.pojo.entity;

import com.x.common.enmu.LikeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like implements Serializable {
    private Long id;
    private LikeType type;
    private Long forId;
    private Long userId;
    private LocalDate createdAt;
}
