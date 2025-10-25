package com.x.service;

import com.x.pojo.dto.PostDTO;
import com.x.pojo.dto.SendPostDTO;
import com.x.pojo.vo.ScrollPageVO;

import java.util.List;

public interface PostService {
    List<PostDTO> getPosts();

    Long sendPost(SendPostDTO sendPostDTO);

    void deletePost(Long id);

    List<PostDTO> getAllPosts();

    List<PostDTO> getFivePosts();

    Long getMyPostsCount();

    Long getPostsCount();

    ScrollPageVO<PostDTO> getScrollPosts(Long max, Integer offset);

}
