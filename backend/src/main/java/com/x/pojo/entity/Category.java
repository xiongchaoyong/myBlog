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
    
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类颜色
     */
    private String color;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    private Integer articleCount; // 文章数量（通过查询计算得出）

}