package com.x.service.impl;

import com.x.exception.BaseException;
import com.x.mapper.TagMapper;
import com.x.pojo.dto.TagDTO;
import com.x.pojo.dto.UserUpdateTagDTO;
import com.x.pojo.entity.Tag;
import com.x.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> getAllTags() {
        return tagMapper.getAllTags();
    }

    @Override
    public void addTag(TagDTO tagDTO) {
        try {
            tagMapper.addTag(tagDTO);
        }
        catch (Exception e){
            throw new BaseException("该标签已存在哦!");
        }
    }

    @Override
    public void deleteTag(Long id) {
        tagMapper.deleteTag(id);
    }

    @Override
    public void updateTag(Long id) {
        Integer status = tagMapper.getStatusById(id);
        if (status == 1)
            status = 0;
        else
            status = 1;
        tagMapper.updateTag(id, status);
    }

    @Override
    public List<Tag> getTagById(Long id) {
        return tagMapper.getTagById(id);
    }

    @Override
    public void updateUserTags(UserUpdateTagDTO userUpdateTagDTO) {
        tagMapper.deleteUserTag(userUpdateTagDTO.getUserId());
        tagMapper.updateUserTags(userUpdateTagDTO);
    }

    @Override
    public List<Tag> getAllTags2() {
        return tagMapper.getAllTags2();
    }
}
