package com.x.controller.user;

import com.x.pojo.dto.CategoryForm;
import com.x.pojo.entity.Category;
import com.x.service.CategoryService;
import com.x.utils.AuthJudjeUtil;
import com.x.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/user/categories")
@Slf4j
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类列表
     * @return Result<List<Category>>
     */
    @GetMapping
    public Result<List<Category>> getCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return Result.error("获取分类列表失败");
        }
    }

    /**
     * 创建分类
     * @param categoryForm 分类表单
     * @return Result<Category>
     */
    @PostMapping("/create")
    public Result<Category> createCategory(@RequestBody CategoryForm categoryForm) {
        AuthJudjeUtil.isAdmin();
        try {
            Category category = categoryService.createCategory(categoryForm);
            return Result.success(category);
        } catch (Exception e) {
            log.error("创建分类失败", e);
            return Result.error("创建分类失败");
        }
    }

    /**
     * 获取分类列表及其文章数量
     * @return Result<List<Category>>
     */
    @GetMapping("/with-count")
    public Result<List<Category>> getCategoriesWithCount() {
        try {
            List<Category> categories = categoryService.getAllCategoriesWithCount();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return Result.error("获取分类列表失败");
        }
    }
}