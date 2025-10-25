package com.x.controller.user;

import com.x.pojo.dto.PostDTO;
import com.x.pojo.dto.SendPostDTO;
import com.x.pojo.vo.ScrollPageVO;
import com.x.service.PostService;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/post")
@CrossOrigin
public class PostController {
    @Autowired
    private PostService postService;
    @GetMapping("/getPosts")
    public Result<List<PostDTO> > getPosts(){
        List<PostDTO> posts = postService.getAllPosts();
        return Result.success(posts);
    }
    @PostMapping("/sendPost")
    public Result<Long> sendPost(@RequestBody SendPostDTO sendPostDTO){
       Long id=  postService.sendPost(sendPostDTO);
        return Result.success(id);
    }
    @DeleteMapping("/deletePost/{id}")
    public Result deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return Result.success("删除成功");
    }
    @GetMapping("/getFivePosts")
    public Result<List<PostDTO>> getFivePosts(){
        List<PostDTO> posts = postService.getFivePosts();
        return Result.success(posts);
    }

    @GetMapping("/getMyPostsCount")
    public Result<Long> getMyPostsCount(){
        Long count = postService.getMyPostsCount();
        return Result.success(count);
    }

    @GetMapping("/getPostsCount")
    public Result<Long> getPostsCount(){
        Long count = postService.getPostsCount();
        return Result.success(count);
    }


    @GetMapping("/scroll")
    public Result<ScrollPageVO<PostDTO>> getScrollPosts(@RequestParam Long max, @RequestParam Integer offset){
        return Result.success(postService.getScrollPosts(max, offset));
    }
}
