package com.x.pojo.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryForm {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
