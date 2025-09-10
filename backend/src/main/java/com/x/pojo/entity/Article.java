package com.x.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章实体类
 * @author Your Name
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String cover;
    private Long categoryId;
//    private String tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime publishTime;
    private LocalDateTime updateTime;
//    private List<String> tagList;
    private Long userId;
}