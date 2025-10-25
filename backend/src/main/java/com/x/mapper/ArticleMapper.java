package com.x.mapper;

import com.x.pojo.dto.ArticleStatsDTO;
import com.x.pojo.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章Mapper接口
 * @author Your Name
 * @date 2024-01-01
 */
@Mapper
public interface ArticleMapper {

    int insert(Article article);

    /**
     * 分页查询文章列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @param categoryId 分类ID（可选）
     * @return 文章列表
     */
    List<Article> selectArticleList( int offset,
                                    int limit,
                                    Long categoryId);

    /**
     * 查询文章总数
     * @param categoryId 分类ID（可选）
     * @return 文章总数
     */
    int selectArticleCount( Long categoryId);

    /**
     * 根据ID查询文章详情
     * @param id 文章ID
     * @return 文章详情
     */
    Article selectById( Long id);

    /**
     * 获取热门文章
     * @param limit 限制数量
     * @return 热门文章列表
     */
    List<Article> selectTopArticles( Integer limit);

    /**
     * 增加文章点赞数
     * @param articleId 文章ID
     */
    void incrementLikeCount(Long articleId);

    /**
     * 增加文章浏览量
     * @param articleId 文章ID
     */
    void incrementViewCount(Long articleId);

    /**
     * 根据ID删除文章
     * @param id 文章ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 更新文章
     * @param article 文章信息
     * @return 影响行数
     */
    int updateArticle(Article article);

    /**
     * 查询近三天发表文章数统计
     * @return 文章统计数据
     */
    @Select("SELECT DATE_FORMAT(d.date, '%c-%e') AS date, COUNT(a.id) AS count " +
            "FROM ( " +
            "    SELECT CURDATE() AS date " +
            "    UNION ALL " +
            "    SELECT CURDATE() - INTERVAL 1 DAY " +
            "    UNION ALL " +
            "    SELECT CURDATE() - INTERVAL 2 DAY " +
            ") d " +
            "LEFT JOIN article a  " +
            "  ON DATE(a.publish_time) = d.date " +
            "GROUP BY d.date " +
            "ORDER BY d.date")

    List<ArticleStatsDTO> selectLast3DaysArticleStats();

    List<Long> getIds(Integer categoryId);

    @Select("select count(*) from article where user_id = 12")
    Long getMyArticleCount();

    List<Article> getArticlesByIds(List<Integer> ids);

}