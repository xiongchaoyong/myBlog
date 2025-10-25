package com.x.service;

import com.x.pojo.dto.ArticleForm;
import com.x.pojo.dto.ArticleStatsDTO;
import com.x.pojo.dto.ArticleUpdateDTO;
import com.x.pojo.entity.Article;
import com.x.pojo.vo.ScrollPageVO;

import java.util.List;

/**
 * 文章服务接口
 * @author Your Name
 * @date 2024-01-01
 */
public interface ArticleService {

    /**
     * 发表文章
     * @param articleForm 文章表单数据
     * @return 发表的文章
     */
    Article publishArticle(ArticleForm articleForm);

    /**
     * 分页查询文章列表
     * @param page 页码
     * @param size 每页大小
     * @param categoryId 分类ID（可选）
     * @return 文章列表和总数
     */
    List<Article> getArticles(int page, int size, Long categoryId);

    /**
     * 获取文章总数
     * @param categoryId 分类ID（可选）
     * @return 文章总数
     */
    int getArticleCount(Long categoryId);

    /**
     * 根据ID获取文章详情
     * @param articleId 文章ID
     * @return 文章详情
     */
    Article getArticleById(Long articleId);

    /**
     * 获取热门文章
     * @param limit 限制数量
     * @return 热门文章列表
     */
    List<Article> getTopArticles(Integer limit);

    void likeArticle(Long articleId);

  void incrementViewCount(Long articleId);
    /**
     * 删除文章
     * @param id 文章ID
     */
    void deleteArticle(Long id);

     Article updateArticle(Long id, ArticleUpdateDTO updateDTO);

    List<ArticleStatsDTO> getLast3DaysArticleStats();

    List<Long> getArticleIds(Integer categoryId);

    Long getMyArticleCount();

    ScrollPageVO<Article> getScrollArticles(Long max, Integer offset);
}