package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

/**
 * 文章表单DTO
 * @author Your Name
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleForm {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 封面图片URL
     */
    private String cover;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签列表
     */
    private List<String> tags;
}