package com.x.mapper;

import com.x.common.enmu.LikeType;
import com.x.pojo.entity.Like;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LikeMapper {

    @Select("select * from blog_like where type=#{likeType} and for_id=#{commentId} and user_id=#{userId}")
    Like getCommentLikeById(LikeType likeType, Long commentId, Long userId);

    @Insert("insert into blog_like (type,for_id,user_id) values(#{type},#{forId},#{userId})")
    void insert(Like like);


    @Delete("delete from blog_like where id=#{id}")
    void deleteById(Long id);

    @Select("select user_id from blog_like where type=#{likeType} and for_id=#{commentId}")
    List<Long> getLikeIdsById(LikeType likeType, Long commentId);

    @Delete("delete from blog_like where user_id=#{userId} and type=#{likeType} and for_id=#{commentId}")
    void deleteByUserId(Long userId, LikeType likeType, Long commentId);

}
