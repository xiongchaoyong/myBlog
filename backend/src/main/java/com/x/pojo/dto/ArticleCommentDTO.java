package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentDTO implements Serializable {
    private Long id;
    private Long parentId;
    private Long articleId;
    private Long userId;
    private Long replyId;
    private String content;
}
