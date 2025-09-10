package com.x.service.impl;

import com.x.context.BaseContext;
import com.x.mapper.ArticleMapper;
import com.x.pojo.dto.ArticleForm;
import com.x.pojo.dto.ArticleStatsDTO;
import com.x.pojo.dto.ArticleUpdateDTO;
import com.x.pojo.entity.Article;
import com.x.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章服务实现类
 * @author Your Name
 * @date 2024-01-01
 */
@Slf4j
@Service

public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Article publishArticle(ArticleForm articleForm) {

        // 创建文章实体
        Article article = new Article();
        article.setTitle(articleForm.getTitle());
        article.setContent(articleForm.getContent());
        article.setSummary(articleForm.getSummary());
        article.setCover(articleForm.getCover());
        article.setCategoryId(articleForm.getCategoryId());
        article.setUserId(BaseContext.getCurUserInfo().getId());

        // 处理标签 - 简单转换为字符串
        if (articleForm.getTags() != null && !articleForm.getTags().isEmpty()) {
            String tagsStr = String.join(",", articleForm.getTags());
        }
        // 设置初始值
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setPublishTime(java.time.LocalDateTime.now());
        article.setUpdateTime(java.time.LocalDateTime.now());
        // 保存文章
        articleMapper.insert(article);
        return article;
    }

    @Override
    public List<Article> getArticles(int page, int size, Long categoryId) {
        // 计算偏移量
        int offset = (page - 1) * size;

        // 查询文章列表
        List<Article> articles = articleMapper.selectArticleList(offset, size, categoryId);



        return articles;
    }

    @Override
    public int getArticleCount(Long categoryId) {
        return articleMapper.selectArticleCount(categoryId);
    }


    @Override
    public Article getArticleById(Long articleId) {
        // 查询文章详情
        Article article = articleMapper.selectById(articleId);

        return article;
    }
    @Override
    public List<Article> getTopArticles(Integer limit) {
        // 查询热门文章
        List<Article> articles = articleMapper.selectTopArticles(limit);

        // 处理标签列表

        log.info("获取热门文章成功，数量: {}", articles.size());
        return articles;
    }

    /**
     * 点赞文章
     * @param articleId 文章ID
     */
    public void likeArticle(Long articleId) {
        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        // 增加点赞数
        articleMapper.incrementLikeCount(articleId);
    }

    /**
     * 增加文章浏览量
     * @param articleId 文章ID
     */
    public void incrementViewCount(Long articleId) {
        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        // 增加浏览量
        articleMapper.incrementViewCount(articleId);
    }

    @Override
    public void deleteArticle(Long id) {
        // 检查文章是否存在
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        // 删除文章
        int result = articleMapper.deleteById(id);
        if (result == 0) {
            throw new RuntimeException("删除文章失败");
        }
    }

    /**
     * 更新文章
     * @param id 文章ID
     * @param updateDTO 更新DTO
     * @return 更新后的文章
     */
    public Article updateArticle(Long id, ArticleUpdateDTO updateDTO) {
        // 使用已有的方法查询原文章
        Article article = getArticleById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        // 更新文章信息
        article.setTitle(updateDTO.getTitle());
        article.setContent(updateDTO.getContent());
        article.setSummary(updateDTO.getSummary());
        article.setCover(updateDTO.getCover());
        article.setCategoryId(updateDTO.getCategoryId());


        // 更新修改时间
        article.setUpdateTime(LocalDateTime.now());

        // 执行更新
        int result = articleMapper.updateArticle(article);
        if (result <= 0) {
            throw new RuntimeException("更新文章失败");
        }

        return article;
    }
    /**
     * 获取近三天发表文章数统计
     * @return 文章统计数据
     */
    public List<ArticleStatsDTO> getLast3DaysArticleStats() {
        return articleMapper.selectLast3DaysArticleStats();
    }

    @Override
    public List<Long> getArticleIds(Integer categoryId) {
        return articleMapper.getIds(categoryId);
    }

    @Override
    public Long getMyArticleCount() {
        return articleMapper.getMyArticleCount();
    }
}