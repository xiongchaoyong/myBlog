package com.x.controller.user;


import com.x.pojo.entity.Tool;
import com.x.service.ToolService;
import com.x.utils.AuthJudjeUtil;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/tool")
@CrossOrigin
public class ToolController {
    @Autowired
    private ToolService toolService;

    @GetMapping("/getAll")
    public Result<List<Tool>> getAll(){
        return Result.success(toolService.getAll());
    }

    @PostMapping("/add")
    public Result<Long> add(@RequestBody Tool tool){
        AuthJudjeUtil.isAdmin();
       Long id= toolService.add(tool);
        return Result.success(id);
    }
}
