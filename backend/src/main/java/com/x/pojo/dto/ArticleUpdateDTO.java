package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleUpdateDTO {
    private String title;
    private String content;
    private String summary;
    private String cover;
    private Long categoryId;
    private List<String> tags;
}