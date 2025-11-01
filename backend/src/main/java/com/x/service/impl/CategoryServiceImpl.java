package com.x.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.x.common.constant.RedisKeyConstants;
import com.x.mapper.CategoryMapper;
import com.x.pojo.dto.CategoryForm;
import com.x.pojo.entity.Category;
import com.x.service.CategoryService;
import com.x.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<Category> getAllCategories() {
        String key= RedisKeyConstants.CATEGORY_INFO;
        String  res=stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank( res)){
            return JSONUtil.toList(res,Category.class);
        }

        List< Category>  categories=categoryMapper.selectAll();
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(categories));
        return categories;
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
//        String key= RedisKeyConstants.CATEGORY_INFO;
//        String  res=stringRedisTemplate.opsForValue().get(key);
//        if(StrUtil.isNotBlank( res)){
//            return JSONUtil.toList(res,Category.class);
//        }
//
//        List< Category>  categories=categoryMapper.selectAllWithArticleCount();
//        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(categories));
//        System.out.println("从数据库中获取分类信息");
//        return categories;
        return  null;
    }

    @Override
    public Long getCategoryCount() {
        String key= RedisKeyConstants.CATEGORY_COUNT;
        return RedisUtil.getCount(key,RedisKeyConstants.COUNT_TTL, TimeUnit.HOURS, categoryMapper::getCount,stringRedisTemplate);
    }
}