package com.x.mapper;

import com.x.pojo.dto.AddCommentDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostCommentMapper {
    @Insert("insert into post_comment (post_id,user_id,content) values (#{postId},#{userId},#{content})")
    void addComment(AddCommentDTO addCommentDTO);
}
