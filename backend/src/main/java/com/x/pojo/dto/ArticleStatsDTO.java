package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleStatsDTO {
    /**
     * 日期 (格式: yyyy-MM-dd)
     */
    private String date;
    
    /**
     * 文章数量
     */
    private Integer count;
}