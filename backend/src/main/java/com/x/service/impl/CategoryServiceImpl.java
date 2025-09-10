package com.x.service.impl;

import com.x.mapper.CategoryMapper;
import com.x.pojo.dto.CategoryForm;
import com.x.pojo.entity.Category;
import com.x.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 分类服务实现类
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAll();
    }

    @Override
    public Category createCategory(CategoryForm categoryForm) {
        // 检查分类名称是否已存在
        Category existingCategory = categoryMapper.selectByName(categoryForm.getName());
        if (existingCategory != null) {
            throw new RuntimeException("分类名称已存在");
        }
        
        // 创建分类
        Category category = new Category();
        category.setName(categoryForm.getName());
        category.setColor(categoryForm.getColor());
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public List<Category> getAllCategoriesWithCount() {
        return categoryMapper.selectAllWithArticleCount();
    }
}