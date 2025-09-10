package com.x.mapper;

import com.x.pojo.dto.AddLikeDTO;
import com.x.pojo.dto.DeleteLikeDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostLikeMapper {
    @Insert("insert into post_like (post_id,user_id) values (#{postId},#{userId})")
    void addLike(AddLikeDTO addLikeDTO);

    @Insert("delete from post_like where post_id=#{postId} and user_id=#{userId}")
    void deleteLike(DeleteLikeDTO deleteLikeDTO);
}
