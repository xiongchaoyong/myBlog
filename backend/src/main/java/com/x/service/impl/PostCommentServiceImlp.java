package com.x.service.impl;

import com.x.mapper.PostCommentMapper;
import com.x.pojo.dto.AddCommentDTO;
import com.x.service.PostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostCommentServiceImlp implements PostCommentService {
    @Autowired
    private PostCommentMapper postCommentMapper;

    @Override
    public void addComment(AddCommentDTO addCommentDTO) {
        postCommentMapper.addComment(addCommentDTO);
    }
}
