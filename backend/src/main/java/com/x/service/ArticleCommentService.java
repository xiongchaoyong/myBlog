package com.x.service;

import com.x.pojo.dto.ArticleCommentDTO;
import com.x.pojo.vo.ArticleCommentVO;

import java.util.List;

public interface ArticleCommentService {
    List<ArticleCommentVO> getTopComments(Long articleId);

    List<ArticleCommentVO> getKidComments(Long parentId);

    Long addComment(ArticleCommentDTO articleComment);

    void deleteKidComment(Long id);

    void deleteParentComment(Long id);

    void likeComment(Long commentId) throws InterruptedException;

    void unlikeComment(Long commentId);
}
