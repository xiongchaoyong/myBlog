package com.x.mapper;

import com.x.pojo.dto.TagDTO;
import com.x.pojo.dto.UserUpdateTagDTO;
import com.x.pojo.entity.Tag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TagMapper {
    @Select("select * from interest_tag")
    List<Tag> getAllTags();

    @Select("insert into interest_tag(tag_name,color,status) " +
            "values(#{tagName},#{color},#{status})")
    void addTag(TagDTO tagDTO);

    @Select("delete from interest_tag where id=#{id}")
    void deleteTag(Long id);

    @Select("select status from interest_tag where id=#{id}")
    Integer getStatusById(Long id);


    @Update("update interest_tag set status=#{status} where id=#{id}")
    void updateTag(Long id,Integer  status);


    List<Tag> getTagById(Long userId);

    void updateUserTags(UserUpdateTagDTO userUpdateTagDTO);

    @Delete("delete from user_interest where user_id=#{userId}")
    void deleteUserTag(Long userId);

    @Select("select * from interest_tag where status=1")
    List<Tag> getAllTags2();

    List<Long> getUserIds(Long id);
}
