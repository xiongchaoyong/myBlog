package com.x.service.impl;

import com.x.mapper.PostLikeMapper;
import com.x.pojo.dto.AddLikeDTO;
import com.x.pojo.dto.DeleteLikeDTO;
import com.x.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostLikeServiceImpl implements PostLikeService {
    @Autowired
    private PostLikeMapper postLikeMapper;

    @Override
    public void addLike(AddLikeDTO addLikeDTO) {
        postLikeMapper.addLike(addLikeDTO);
    }

    @Override
    public void deleteLike(DeleteLikeDTO deleteLikeDTO) {

        postLikeMapper.deleteLike(deleteLikeDTO);
    }
}
