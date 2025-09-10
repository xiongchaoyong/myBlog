package com.x.controller.user;

import com.x.pojo.dto.AddCommentDTO;
import com.x.service.PostCommentService;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/postComment")
@CrossOrigin
public class PostCommentController {
    @Autowired
    private PostCommentService postCommentService;
    @PostMapping("/addComment")
    public Result addComment(@RequestBody AddCommentDTO addCommentDTO) {
        System.out.println(addCommentDTO);
        postCommentService.addComment(addCommentDTO);
        return Result.success();
    }
}
