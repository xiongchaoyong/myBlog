package com.x.controller.user;

import com.x.pojo.dto.AddLikeDTO;
import com.x.pojo.dto.DeleteLikeDTO;
import com.x.service.PostLikeService;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/postLike")
@CrossOrigin
public class PostLikeController {
    @Autowired
    private PostLikeService postLikeService;

    @PostMapping("/addLike")
    public Result addLike(@RequestBody AddLikeDTO addLikeDTO){
         postLikeService.addLike(addLikeDTO);
         return Result.success();
    }
    @PostMapping("/deleteLike")
    public Result deleteLike(@RequestBody DeleteLikeDTO deleteLikeDTO){
        postLikeService.deleteLike(deleteLikeDTO);
        return Result.success();
    }
}
