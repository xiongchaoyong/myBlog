package com.x.mapper;

import com.x.pojo.dto.ArticleCommentDTO;
import com.x.pojo.entity.ArticleComment;
import com.x.pojo.vo.ArticleCommentVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleCommentMapper {

    @Select("select * from article_comment where id=#{id}")
    ArticleComment getById(Long id);
    List<ArticleCommentVO> getTopComments(Long articleId);

    List<ArticleCommentVO> getKidComments(Long parentId);

    void addComment(ArticleCommentDTO articleComment);

    // ❌ MyBatis的delete返回的是int
    @Delete("delete from article_comment where id=#{id} or reply_id=#{id}")
    int deleteKidComment(Long id);

    @Delete("delete from article_comment where id= #{id} or parent_id=#{id}")
    int deleteParentComment(Long id);

    @Update("update article_comment set like_count=like_count+1 where id=#{commentId}")
    void likeComment(Long commentId);
    @Update("update article_comment set like_count=like_count-1 where id= #{commentId}")
    void unlikeComment(Long commentId);

    @Update("update article_comment set reply_count=reply_count+1 where id= #{parentId}")
    void upReplyCount(Long parentId);
    @Update("update article_comment set reply_count=reply_count - #{count} where id= #{parentId}")
    void downReplyCount(Long parentId,Integer count);
}
