package com.x.mapper;

import com.x.pojo.dto.PostCommentDTO;
import com.x.pojo.dto.PostDTO;
import com.x.pojo.dto.PostLikeDTO;
import com.x.pojo.dto.SendPostDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper {
//    List<PostDTO> getFriendPosts(@Param("friendIds") List<Long> friendIds);

    List<PostCommentDTO> getCommentsByPostIds(@Param("postIds") List<Long> postIds);

    List<PostLikeDTO> getLikesByPostIds(@Param("postIds") List<Long> postIds);

    @Insert("insert into post (user_id,content,image_urls) values (#{userId}" +
            ",#{content},#{imageUrls,typeHandler=com.x.utils.JsonStringListTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void sendPost(SendPostDTO sendPostDTO);

    @Delete("delete from post where id=#{id}")
    void deletePost(Long id);

    List<PostDTO> getAllPosts();

    List<PostCommentDTO> getAllComments();

    List<PostLikeDTO> getAllLikes();

    List<Long> getPostGrowthData();

    @Select("select count(*) from post")
    Long getPostTotal();

    @Select("select * from post order by created_at desc limit 5")
    List<PostDTO> getFivePosts();

    @Select("select count(*) from post where user_id = 12")
    Long getMyPostsCount();

    @Select("select count(*) from post")
    Long getPostsCount();

//    List<PostDTO> getPostsById(Long friendId);
}