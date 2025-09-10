package com.x.controller.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.x.ai.api.ArticleAssistant;
import com.x.context.BaseContext;
import com.x.mapper.ArticleMapper;
import com.x.pojo.dto.ArticleForm;
import com.x.pojo.dto.ArticleStatsDTO;
import com.x.pojo.dto.ArticleUpdateDTO;
import com.x.pojo.entity.Article;
import com.x.service.ArticleService;
import com.x.utils.AuthJudjeUtil;
import com.x.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Validated
@RestController
@RequestMapping("/user/articles")
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleAssistant articleAssistant;
    /**
     * 发表文章
     * @param articleForm 文章表单数据
     * @return 发表结果
     */
    @PostMapping("/publish/publish")
    public Result<Article> publishArticle( @RequestBody ArticleForm articleForm) {
        try {
            Article article = articleService.publishArticle(articleForm);
            return Result.success(article);
        } catch (Exception e) {
            log.error("发表文章失败", e);
            return Result.error("发表文章失败：" + e.getMessage());
        }
    }
    @PostMapping("/aiJudge")
    public Result aiJudge(@RequestBody ArticleForm articleForm) {
        String aiResponse = articleAssistant.chat(articleForm.getContent());
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(aiResponse);
            boolean approved = node.get("approved").asBoolean();
            String reason = node.get("reason").asText();
            result.put("approved", approved);
            result.put("reason", reason);
        }catch (Exception e){
            throw new RuntimeException("AI 审核解析失败: " + aiResponse, e);
        }
        return Result.success(result);
    }

    /**
     * 分页查询文章列表
     * @param page 页码
     * @param size 每页大小
     * @param categoryId 分类ID（可选）
     * @return 文章分页数据
     */
    @GetMapping
    public Result<Map<String, Object>> getArticles(
            @RequestParam(defaultValue = "1")  Integer page,
            @RequestParam(defaultValue = "5")  Integer size,
            @RequestParam(required = false) Long categoryId) {
        try {
            List<Article> articles = articleService.getArticles(page, size, categoryId);
            int total = articleService.getArticleCount(categoryId);

            Map<String, Object> result = new HashMap<>();
            result.put("articles", articles);
            result.put("count", total);
            result.put("currentPage",page);

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取文章列表失败", e);
            return Result.error("获取文章列表失败");
        }
    }
    /**
     * 根据ID获取文章详情
     * @param articleId 文章ID
     * @return 文章详情
     */
    @GetMapping("/{articleId}")
    public Result<Article> getArticleById(@PathVariable  Long articleId) {
        try {
            Article article = articleService.getArticleById(articleId);
            if (article == null) {
                return Result.error("文章不存在");
            }
            return Result.success(article);
        } catch (Exception e) {
            log.error("获取文章详情失败，文章ID: {}", articleId, e);
            return Result.error("获取文章详情失败");
        }
    }


    /**
     * 获取热门文章
     * @param limit 限制数量
     * @return 热门文章列表
     */
    @GetMapping("/top")
    public Result<List<Article>> getTopArticles(
            @RequestParam(defaultValue = "5") Integer limit) {
        try {
            List<Article> articles = articleService.getTopArticles(limit);
            return Result.success(articles);
        } catch (Exception e) {
            log.error("获取热门文章失败", e);
            return Result.error("获取热门文章失败");
        }
    }

    /**
     * 点赞文章
     * @param articleId 文章ID
     * @return Result<Void>
     */
    @PostMapping("/{articleId}/like")
    public Result likeArticle(@PathVariable Long articleId) {
        try {
            articleService.likeArticle(articleId);
            return Result.success();
        } catch (Exception e) {
            log.error("点赞文章失败, articleId: {}", articleId, e);
            return Result.error("点赞失败");
        }
    }

    /**
     * 增加文章浏览量
     * @param articleId 文章ID
     * @return Result<Void>
     */
    @PostMapping("/{articleId}/view")
    public Result<Void> incrementViewCount(@PathVariable Long articleId) {
        try {
            articleService.incrementViewCount(articleId);
            return Result.success();
        } catch (Exception e) {
            log.error("增加浏览量失败, articleId: {}", articleId, e);
            return Result.error("操作失败");
        }
    }
    /**
     * 删除文章
     * @param id 文章ID
     * @return Result<Void>
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        AuthJudjeUtil.isAdmin();
        try {
            articleService.deleteArticle(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除文章失败，文章ID: {}", id, e);
            return Result.error("删除文章失败");
        }
    }

    /**
     * 更新文章
     * @param id 文章ID
     * @param updateDTO 更新DTO
     * @return 统一返回结果
     */
    @Autowired
    private ArticleMapper articleMapper;
    @PutMapping("/updateById/{id}")
    public Result<Article> updateArticle(
            @PathVariable Long id,
            @RequestBody ArticleUpdateDTO updateDTO) {
        //根据id获得文章
        Article article = articleMapper.selectById(id);

        if(article.getUserId()!= BaseContext.getCurUserInfo().getId()
        && BaseContext.getCurUserInfo().getAdmin()!=1)
            return Result.error("你想干啥呢?");

        try {
            Article updatedArticle = articleService.updateArticle(id, updateDTO);
            return Result.success(updatedArticle);
        } catch (Exception e) {
            return Result.error("更新文章失败: " + e.getMessage());
        }
    }


    /**
     * 获取近三天发表文章数统计
     * @return 近三天的文章统计
     */
    @GetMapping("/stats/last3days")
    public Result getLast3DaysArticleStats() {
        try {
            List<ArticleStatsDTO> stats = articleService.getLast3DaysArticleStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取近三天文章统计失败", e);
            return Result.error("获取文章统计失败");
        }
    }

    @GetMapping("/ids")
    public  Result<List<Long>> getArticleIds(@RequestParam(required = false) Integer categoryId) {
        try {
            List<Long> articleIds = articleService.getArticleIds(categoryId);
            return Result.success(articleIds);
        } catch (Exception e) {
            log.error("获取文章ID列表失败", e);
            return Result.error("获取文章ID列表失败");
        }
    }

    @GetMapping("/getMyArticleCount")
    public Result<Long> getMyArticleCount() {
        try {
            Long count = articleService.getMyArticleCount();
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取文章数量失败", e);
            return Result.error("获取文章数量失败");
        }
    }
}