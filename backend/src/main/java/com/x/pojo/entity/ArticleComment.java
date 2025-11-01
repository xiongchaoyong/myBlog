package com.x.pojo.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleComment implements Serializable {
    private Long id;
    private Long parentId;
    private Long articleId;
    private Long userId;
    private Long replyId;
    private String content;
    private Integer status;
    private Integer likeCount;
    private Integer replyCount;
    private String createdAt;
    private String updatedAt;
}
