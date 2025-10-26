package com.x.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 分类实体类
 * @author Your Name
 * @date 2024-01-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    private Long id;

    private String name;

    private String color;

    private Date createTime;

    private Date updateTime;

}