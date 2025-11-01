package com.x.controller.user;

import com.x.pojo.dto.ArticleCommentDTO;
import com.x.pojo.vo.ArticleCommentVO;
import com.x.service.ArticleCommentService;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/articleComment")
@CrossOrigin
public class ArticleCommentController {
    @Autowired
    private ArticleCommentService articleCommentService;

    @GetMapping("/getTopComments/{articleId}")
    public Result<List<ArticleCommentVO>> getTopComments(@PathVariable Long articleId){
        return Result.success(articleCommentService.getTopComments(articleId));
    }

    @GetMapping("/getKidComments/{parentId}")
    public Result<List<ArticleCommentVO>> getKidComments(@PathVariable Long parentId){
        return Result.success(articleCommentService.getKidComments(parentId));
    }

    @PostMapping("/addComment")
    public Result<Long> addComment(@RequestBody ArticleCommentDTO articleComment){
        return Result.success(articleCommentService.addComment(articleComment));
    }
    @DeleteMapping("/deleteKidComment/{id}")
    public Result deleteKidComment(@PathVariable Long id){
        articleCommentService.deleteKidComment(id);
        return Result.success();
    }
    @DeleteMapping("/deleteParentComment/{id}")
    public Result deleteParentComment(@PathVariable Long id){
        articleCommentService.deleteParentComment(id);
        return Result.success();
    }

    @PostMapping("/like/{commentId}")
    public Result likeComment(@PathVariable Long commentId) throws InterruptedException {
        articleCommentService.likeComment(commentId);
        return Result.success();
    }
    @PostMapping("/unlike/{commentId}")
    public Result unlikeComment(@PathVariable Long commentId){
        articleCommentService.unlikeComment(commentId);
        return Result.success();
    }

}
