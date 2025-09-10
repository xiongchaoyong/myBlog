package com.x.service;

import com.x.pojo.dto.TagDTO;
import com.x.pojo.dto.UserUpdateTagDTO;
import com.x.pojo.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getAllTags();

    void addTag(TagDTO tagDTO);

    void deleteTag(Long id);

    void updateTag(Long id);

    List<Tag> getTagById(Long id);

    void updateUserTags(UserUpdateTagDTO updateUserTags);

    List<Tag> getAllTags2();

}
