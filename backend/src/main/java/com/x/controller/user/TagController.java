package com.x.controller.user;

import com.x.pojo.dto.UserUpdateTagDTO;
import com.x.pojo.entity.Tag;
import com.x.service.TagService;
import com.x.utils.AuthJudjeUtil;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("/user/tag")
@RestController
public class TagController {
    @Autowired
    private TagService tagService;
    @GetMapping("/get")
    public Result<List<Tag>>    getAllTags() {
        List<Tag> tags = tagService.getAllTags2();
        return Result.success(tags);
    }

    @GetMapping("/get/{id}")
    public Result<List<Tag>> getTagById(@PathVariable Long id) {
        List<Tag> tags = tagService.getTagById(id);
        return Result.success(tags);
    }

    @PostMapping("/update")
    public Result<String> updateTag(@RequestBody UserUpdateTagDTO tagDTO) {
        AuthJudjeUtil.isAdmin();
        tagService.updateUserTags(tagDTO);
        return Result.success("更新成功");
    }


}
