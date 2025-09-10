package com.x.service;

import com.x.pojo.dto.CategoryForm;
import com.x.pojo.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> getAllCategories();
    
    /**
     * 创建分类
     * @param categoryForm 分类表单
     * @return 创建的分类
     */
    Category createCategory(CategoryForm categoryForm);
    /**
     * 获取所有分类及其文章数量
     * @return 分类列表
     */
    List<Category> getAllCategoriesWithCount();

}