package com.x.service;

import com.x.pojo.dto.AddLikeDTO;
import com.x.pojo.dto.DeleteLikeDTO;

public interface PostLikeService {
    void addLike(AddLikeDTO addLikeDTO);

    void deleteLike(DeleteLikeDTO deleteLikeDTO);
}
