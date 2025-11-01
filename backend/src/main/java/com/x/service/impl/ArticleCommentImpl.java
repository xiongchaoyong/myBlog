package com.x.service.impl;

import com.x.common.constant.RedisKeyConstants;
import com.x.common.enmu.LikeType;
import com.x.context.BaseContext;
import com.x.exception.BaseException;
import com.x.mapper.ArticleCommentMapper;
import com.x.mapper.LikeMapper;
import com.x.pojo.dto.ArticleCommentDTO;
import com.x.pojo.entity.ArticleComment;
import com.x.pojo.entity.Like;
import com.x.pojo.entity.LikeMessage;
import com.x.pojo.vo.ArticleCommentVO;
import com.x.service.ArticleCommentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleCommentImpl implements ArticleCommentService {
    @Autowired
    private ArticleCommentMapper articleCommentMapper;

    private  final StringRedisTemplate stringRedisTemplate;

    private final LikeMapper likeMapper;

    private final RedissonClient redissonClient;

    @Override
    public List<ArticleCommentVO> getTopComments(Long articleId) {
        List<ArticleCommentVO> list = articleCommentMapper.getTopComments(articleId);
        Long userId = BaseContext.getCurUserInfo().getId();
        for(ArticleCommentVO vo:list){
            String key = RedisKeyConstants.COMMENT_LIKEIDS + vo.getArticleComment().getId();
            if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
                vo.setIsLike(Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(key, userId.toString())));
            }else{
                //查询数据库
                List<Long> userIds =likeMapper.getLikeIdsById(LikeType.ARTICLE_COMMENT,vo.getArticleComment().getId());
                if(userIds!=null && !userIds.isEmpty()){
                    vo.setIsLike(userIds.contains(userId));
                    stringRedisTemplate.opsForSet().add(key, userIds.stream().map(String::valueOf).toArray(String[]::new));
                    stringRedisTemplate.expire(key, RedisKeyConstants.LIKE_TTL, TimeUnit.DAYS);
                }else{
                    vo.setIsLike(false);
                    stringRedisTemplate.opsForSet().add(key, "empty");
                }
            }
        }
        return list;
    }

    @Override
    public List<ArticleCommentVO> getKidComments(Long parentId) {
        List<ArticleCommentVO> list = articleCommentMapper.getKidComments(parentId);
        Long userId = BaseContext.getCurUserInfo().getId();
        for(ArticleCommentVO vo:list){
            String key = RedisKeyConstants.COMMENT_LIKEIDS + vo.getArticleComment().getId();
            if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
                vo.setIsLike(Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(key, userId.toString())));
            }else{
                //查询数据库
                List<Long> userIds =likeMapper.getLikeIdsById(LikeType.ARTICLE_COMMENT,vo.getArticleComment().getId());
                if(userIds!=null && !userIds.isEmpty()){
                    vo.setIsLike(userIds.contains(userId));
                    stringRedisTemplate.opsForSet().add(key, userIds.stream().map(String::valueOf).toArray(String[]::new));
                    stringRedisTemplate.expire(key, RedisKeyConstants.LIKE_TTL, TimeUnit.DAYS);
                }else{
                    vo.setIsLike(false);
                    stringRedisTemplate.opsForSet().add(key, "empty");
                }
            }
        }
        return  list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addComment(ArticleCommentDTO articleComment) {
            // 参数校验（可能抛出IllegalArgumentException - 运行时异常）
            if (articleComment.getContent() == null) {
                throw new BaseException("评论内容不能为空");
            }
            articleCommentMapper.addComment(articleComment);
            if(articleComment.getParentId()!=null){
                articleCommentMapper.upReplyCount(articleComment.getParentId());
            }
            return articleComment.getId();
    }

    @Override
    @Transactional
    public void deleteKidComment(Long id) {
        //参数校验
        if(id==null){
            throw new BaseException("评论id不能为空");
        }
        //查询评论验证
        ArticleComment articleComment = articleCommentMapper.getById(id);
        if(articleComment==null){
            throw new BaseException("评论不存在");
        }
        //权限校验
        if(!BaseContext.getCurUserInfo().getId().equals(articleComment.getUserId())){
            throw new BaseException("没有权限删除该评论");
        }
        //确保是子评论
        if(articleComment.getParentId()==null){
            throw new BaseException("该评论不是子评论");
        }

        //业务逻辑
        //先删除子评论得到删除的行数
        int count = articleCommentMapper.deleteKidComment(id);
        //再更新父评论的计数
            if (count == 0){
                throw new BaseException("删除子评论失败");
            }
            articleCommentMapper.downReplyCount(articleComment.getParentId(),count);
    }

    @Override
    public void deleteParentComment(Long id) {
        //参数校验
        if(id==null){
            throw new BaseException("评论id不能为空");
        }
        int res = articleCommentMapper.deleteParentComment(id);
        if(res==0){
            throw new BaseException("删除评论失败");
        }
    }

    // 初始化消费者组
    @PostConstruct
    public void initConsumerGroup() {
        try {
            stringRedisTemplate.opsForStream().createGroup(
                    RedisKeyConstants.LIKE_STREAM,
                    RedisKeyConstants.LIKE_CONSUMER_GROUP
            );
        } catch (Exception e) {
            log.info("消费者组已存在或创建失败: {}", e.getMessage());
        }
    }
    @Override
    public void likeComment(Long commentId) throws InterruptedException {
        Long userId = BaseContext.getCurUserInfo().getId();
        String key = RedisKeyConstants.COMMENT_LIKEIDS + commentId;

        RLock lock = redissonClient.getLock(RedisKeyConstants.LOCK_LIKE + commentId);
        boolean acquired = lock.tryLock(1, 3, TimeUnit.SECONDS);

        if (!acquired) {
            throw new BaseException("操作频繁，请稍后重试");
        }

        try {
            //缓存不存在
            if(Boolean.FALSE.equals(stringRedisTemplate.hasKey(key))){
                //重构缓存
                //数据库查询当前用户点赞信息
                Like commentLike = likeMapper.getCommentLikeById(LikeType.ARTICLE_COMMENT,commentId, userId);
                if(commentLike==null){
                    //为空,点赞操作，创建新的点赞信息
                    Like like = new Like(null, LikeType.ARTICLE_COMMENT, commentId, userId, null);
//                    sendLikeMessage(commentId, userId, LikeMessage.LikeAction.LIKE);
                    likeMapper.insert(like);
                    articleCommentMapper.likeComment(commentId);
                }else{
                    //存在,取消点赞操作，删除相关信息
//                    sendLikeMessage(commentId, userId, LikeMessage.LikeAction.UNLIKE);
                    likeMapper.deleteById(commentLike.getId());
                    articleCommentMapper.unlikeComment(commentId);
                }
                //重构缓存
                List<Long> userIds =likeMapper.getLikeIdsById(LikeType.ARTICLE_COMMENT,commentId);
                if(userIds==null && userIds.isEmpty()){
                    //添加空占位，防止频繁查询数据库
                    stringRedisTemplate.opsForSet().add(key,"empty");
                }else{
                    stringRedisTemplate.opsForSet().add(key,userIds.stream().map(String::valueOf).toArray(String[]::new));
                }
                stringRedisTemplate.expire(key, RedisKeyConstants.LIKE_TTL, TimeUnit.DAYS);
            }else{
                // 判断当前操作类型
                LikeMessage.LikeAction action = determineLikeAction(key, userId,commentId);

                // 发送消息到 Stream
                sendLikeMessage(commentId, userId, action);

                // 立即更新 Redis 缓存（最终一致性）
                updateRedisCache(key, userId, action);
            }
        } catch (Exception e) {
            log.error("点赞操作失败: ", e);
            throw new BaseException("操作失败");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private LikeMessage.LikeAction determineLikeAction(String key, Long userId, Long commentId) {
            Boolean member = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
            return Boolean.TRUE.equals(member) ? LikeMessage.LikeAction.UNLIKE : LikeMessage.LikeAction.LIKE;
    }

    private void sendLikeMessage(Long commentId, Long userId, LikeMessage.LikeAction action) {
        LikeMessage message = new LikeMessage(commentId, userId, LikeType.ARTICLE_COMMENT, action);

        ObjectRecord<String, LikeMessage> record = StreamRecords.newRecord()
                .ofObject(message)
                .withStreamKey(RedisKeyConstants.LIKE_STREAM);

        stringRedisTemplate.opsForStream().add(record);
        log.debug("发送点赞消息: commentId={}, userId={}, action={}",
                commentId, userId, action);
    }

    private void updateRedisCache(String key, Long userId, LikeMessage.LikeAction action) {
        if (action == LikeMessage.LikeAction.LIKE) {
            stringRedisTemplate.opsForSet().add(key, userId.toString());
        } else {
            stringRedisTemplate.opsForSet().remove(key, userId.toString());
        }

        // 如果集合为空，添加空占位符防止缓存穿透
        Long size = stringRedisTemplate.opsForSet().size(key);
        if (size != null && size == 0) {
            stringRedisTemplate.opsForSet().add(key, "empty");
        }

        stringRedisTemplate.expire(key, RedisKeyConstants.LIKE_TTL, TimeUnit.DAYS);
    }

    @Override
    public void unlikeComment(Long commentId) {
        articleCommentMapper.unlikeComment(commentId);
    }
}
