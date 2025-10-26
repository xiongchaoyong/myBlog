package com.x.service.impl;

import com.x.common.constant.RedisKeyConstants;
import com.x.exception.BaseException;
import com.x.mapper.PostCommentMapper;
import com.x.mapper.PostLikeMapper;
import com.x.mapper.PostMapper;
import com.x.mapper.UserMapper;
import com.x.pojo.dto.PostCommentDTO;
import com.x.pojo.dto.PostDTO;
import com.x.pojo.dto.PostLikeDTO;
import com.x.pojo.dto.SendPostDTO;
import com.x.pojo.vo.ScrollPageVO;
import com.x.service.PostService;
import com.x.utils.RedisUtil;
import com.x.utils.ScrollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PostLikeMapper postLikeMapper;
    @Autowired
    private PostCommentMapper postCommentMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<PostDTO> getPosts() {
        List<PostDTO> posts= postMapper.getAllPosts();

        //获取所有动态的id
        List<Long> postIds = posts.stream().map(PostDTO::getId).toList();
        if(postIds.isEmpty()) {
            return null;
        }
        List<PostCommentDTO> comments = postMapper.getCommentsByPostIds(postIds);
        List<PostLikeDTO> likes = postMapper.getLikesByPostIds(postIds);
        for (PostDTO post : posts) {
            post.setComments(comments.stream().filter(comment -> comment.getPostId().equals(post.getId())).toList());
            post.setLikes(likes.stream().filter(like -> like.getPostId().equals(post.getId())).toList());
        }
        return posts;
    }

    @Override
    public Long sendPost(SendPostDTO sendPostDTO) {
        postMapper.sendPost(sendPostDTO);
        //添加到redis
        stringRedisTemplate.opsForZSet().add(RedisKeyConstants.STORE_POSTIDS,String.valueOf(sendPostDTO.getId()),System.currentTimeMillis());
        return sendPostDTO.getId();
    }

    @Override
    public void deletePost(Long id) {
        postMapper.deletePost(id);
    }

    @Override
    public List<PostDTO> getAllPosts() {
        List<PostDTO> posts= postMapper.getAllPosts();

        //获取所有动态的id
        List<PostCommentDTO> comments = postMapper.getAllComments();
        List<PostLikeDTO> likes = postMapper.getAllLikes();
        for (PostDTO post : posts) {
            post.setComments(comments.stream().filter(comment -> comment.getPostId().equals(post.getId())).toList());
            post.setLikes(likes.stream().filter(like -> like.getPostId().equals(post.getId())).toList());
        }
        return posts;
    }

    @Override
    public List<PostDTO> getFivePosts() {
        return postMapper.getFivePosts();
    }

    @Override
    public Long getMyPostsCount() {
        return RedisUtil.getCount(RedisKeyConstants.MY_POST_COUNT,
                RedisKeyConstants.COUNT_TTL, TimeUnit.HOURS, postMapper::getMyPostsCount,stringRedisTemplate);
    }

    @Override
    public Long getPostsCount() {
        return RedisUtil.getCount(RedisKeyConstants.POST_COUNT,
                RedisKeyConstants.COUNT_TTL, TimeUnit.HOURS, postMapper::getPostsCount,stringRedisTemplate);    }

    @Override
    public ScrollPageVO<PostDTO> getScrollPosts(Long max, Integer offset) {
        Map<String, Object> map = ScrollUtil.ScllorGetIds(RedisKeyConstants.STORE_POSTIDS, 0, max, offset, 5,stringRedisTemplate);
        Long minTime = (Long) map.get("minTime");
        Integer os = (Integer) map.get("os");
        System.out.println(map.get("ids"));
        System.out.println(map);
        List<Integer> ids = (List<Integer>) map.get("ids");
        if(ids == null || ids.isEmpty()){
            throw new BaseException("没有更多数据了");
        }
        List<PostDTO> posts= postMapper.getPostsByIds(ids);
        //获取所有动态的id
        List<PostCommentDTO> comments = postMapper.getCommentsByIds(ids);
        List<PostLikeDTO> likes = postMapper.getLikesByIds(ids);
        for (PostDTO post : posts) {
            post.setComments(comments.stream().filter(comment -> comment.getPostId().equals(post.getId())).toList());
            post.setLikes(likes.stream().filter(like -> like.getPostId().equals(post.getId())).toList());
        }
        return new ScrollPageVO<>(minTime,os,posts);
    }

}
