package com.x.pojo.vo;

import com.x.pojo.entity.ArticleComment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentVO implements Serializable {
    private Long id;
    private ArticleComment articleComment;
    private String username;
    private String avatar;
    private Boolean isLike;
}
