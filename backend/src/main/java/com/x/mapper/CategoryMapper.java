package com.x.mapper;

import com.x.pojo.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类Mapper接口
 */
@Mapper
public interface CategoryMapper {
    
    /**
     * 查询所有分类
     * @return 分类列表
     */
    List<Category> selectAll();
    
    /**
     * 根据名称查询分类
     * @param name 分类名称
     * @return 分类
     */
    Category selectByName(String name);
    
    /**
     * 插入分类
     * @param category 分类
     */
    void insert(Category category);

    /**
     * 获取所有分类及其文章数量
     * @return 分类列表
     */
    List<Category> selectAllWithArticleCount();

    @Select("select count(*) from category")
    Long getCount();
}