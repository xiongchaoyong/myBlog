<template>
  <div class="article-page">
    <SiderBar></SiderBar>

    <div class="article-container">
      <!-- 彻底删除顶部主题切换按钮区域 -->
      <div v-if="showToc || isReadingMode" class="article-bg-color" @click="toggleReadingMode"></div>
      <!-- Header部分：封面图片和文章信息 -->
      <div class="article-header">
        <div class="cover-image">
          <img :src="articleData?.cover" :alt="articleData?.title" />
          <div class="cover-overlay">
            <div class="article-info">
              <h1 class="article-title">{{ articleData?.title }}</h1>
              <div class="article-meta">
                <div class="meta-info-row">
                  <span>发表于 {{ formatFullDate(articleData?.publishTime) }}</span>
                  <span class="divider">|</span>
                  <span>更新于 {{ formatFullDate(articleData?.updateTime) }}</span>
                  <span class="divider">|</span>
                  <span v-if="getCategoryName(articleData?.categoryId)" class="category-name">
                    {{ getCategoryName(articleData?.categoryId) }}
                  </span>
                  <span class="divider">|</span>
                  <span class="stats-group">
                    <span class="stat-item">
                      <svg style="width:0.9rem;height:0.9rem;">
                        <use xlink:href="#icon-chakan" fill="#fff"></use>
                      </svg>
                      {{ articleData?.viewCount }} 观看
                    </span>
                    <span class="stat-item" @click="handleLikeArticle" style="cursor: pointer;">
                      <svg style="width:0.9rem;height:0.9rem;">
                        <use xlink:href="#icon-icon" fill="#fff"></use>
                      </svg>
                      {{ articleData?.likeCount }} 点赞
                    </span>
                    <span class="stat-item">
                      <svg style="width:0.9rem;height:0.9rem;">
                        <use xlink:href="#icon-pinglun" fill="#fff"></use>
                      </svg>
                      {{ articleData?.commentCount }} 评论
                    </span>
                  </span>
                </div>
              </div>
              <!-- 主题切换按钮，放在时间展示下方 -->
              <div class="theme-switch-bar theme-switch-bar-in-cover">
                <div class="theme-switch-btns">
                  <el-dropdown trigger="click">
                    <div class="theme-btn" style="cursor:pointer;">
                      <span class="theme-label">预览主题</span>
                      <!-- <span class="theme-value">{{ previewTheme }}</span> -->
                    </div>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item v-for="theme in previewThemes" :key="theme" @click="previewTheme = theme">
                          {{ theme }}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                  <el-dropdown trigger="click">
                    <div class="theme-btn" style="cursor:pointer;">
                      <span class="theme-label">代码主题</span>
                      <!-- <span class="theme-value">{{ codeTheme }}</span> -->
                    </div>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item v-for="theme in codeThemes" :key="theme" @click="codeTheme = theme">
                          {{ theme }}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                  <div class="theme-btn dark-mode-btn" @click="toggleDarkMode" style="cursor:pointer;">
                    <span class="theme-label">{{ isDarkMode ? '默认模式' : '暗黑模式' }}</span>
                    <!-- <span class="theme-value">{{ isDarkMode ? '开启' : '关闭' }}</span> -->
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- 删除按钮 -->
          <div class="delete-button-container"
           v-if="articleData?.userId === userStore.user?.id || userStore.user?.admin===1">
            <el-button type="primary" size="small" @click="handleEditArticle" class="edit-button"
              style="margin-right: 8px;">
              <el-icon>
                <Edit />
              </el-icon>
              修改文章
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteArticle" :loading="isDeleting"
              class="delete-button"
              v-if="userStore.user?.admin===1">
              <el-icon>
                <Delete />
              </el-icon>
              删除文章
            </el-button>
          </div>
        </div>
        <!-- 主题切换区域，放在封面图片正下方 -->
        <div class="theme-switch-bar">
          <div class="theme-switch-btns">
            <el-dropdown trigger="click">
              <div class="theme-btn" style="cursor:pointer;">
                <span class="theme-label">预览主题</span>
                <span class="theme-value">{{ previewTheme }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="theme in previewThemes" :key="theme" @click="previewTheme = theme">
                    {{ theme }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-dropdown trigger="click">
              <div class="theme-btn" style="cursor:pointer;">
                <span class="theme-label">代码主题</span>
                <span class="theme-value">{{ codeTheme }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="theme in codeThemes" :key="theme" @click="codeTheme = theme">
                    {{ theme }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <div class="theme-btn dark-mode-btn" @click="toggleDarkMode" style="cursor:pointer;">
              <span class="theme-label">{{ isDarkMode ? '默认模式' : '暗黑模式' }}</span>
              <span class="theme-value">{{ isDarkMode ? '开启' : '关闭' }}</span>
            </div>
          </div>
        </div>
              <waves height="3rem"></waves>

      </div>


      <!-- Content部分：文章内容 -->
      <div class="article-content">
        <div class="content-wrapper">
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="10" animated />
          </div>
          <div v-else class="markdown-content">
            <!-- 文章摘要 -->
            <div v-if="articleData?.summary" class="article-summary">
              <div class="summary-content">
                <h3 class="summary-title">文章摘要</h3>
                <p class="summary-text">{{ articleData?.summary }}</p>
              </div>
            </div>

            <!-- 文章内容 -->
            <div class="content-main">
              <MdPreview v-if="!loading && articleData?.content" :modelValue="articleData?.content" :toc="true"
                :tocVisible="false" ref="mdPreviewRef" @tocChange="handleTocChange" :previewTheme="previewTheme"
                :codeTheme="codeTheme" :mermaid="true" :theme="isDarkMode ? 'dark' : 'light'" />
            </div>

            <!-- 文章时间信息 -->
            <div class="article-time-info">
              <div class="time-item">
                <span class="time-label">发布时间：</span>
                <span class="time-value">{{ formatFullDate(articleData?.publishTime) }}</span>
              </div>
              <div class="time-item">
                <span class="time-label">更新时间：</span>
                <span class="time-value">{{ formatFullDate(articleData?.updateTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="article-comments-section">
        <div class="article-comments">
              <h3 class="comments-title">评论区</h3>

              <!-- 加载状态和错误提示 -->
              <div v-if="loadingComments" class="comments-loading">
                <el-skeleton :rows="3" animated />
              </div>
              <div v-else-if="commentError" class="comments-error">
                {{ commentError }}
              </div>

              <!-- 评论输入框 -->
              <div class="comment-form">
                <Avatar :avatar="userStore.user?.avatar" :username="userStore.user?.username || '游客'" 
                  :style="{ width: '40px', height: '40px', fontSize: '16px' }" />
                <div class="comment-input-wrapper">
                  <el-input
                    v-model="commentContent"
                    type="textarea"
                    :rows="3"
                    placeholder="写下你的评论..."
                    resize="none"
                  />
                  <div class="comment-form-footer">
                    <span class="comment-tips">{{ isLoggedIn ? '善语结善缘，恶言伤人心' : '登录后即可评论' }}</span>
                    <el-button 
                      type="primary" 
                      @click="submitComment"
                      :disabled="!isLoggedIn || !commentContent.trim()"
                    >
                      发表评论
                    </el-button>
                  </div>
                </div>
              </div>

              <!-- 评论列表 -->
              <div class="comments-list">
                <div v-for="comment in comments" :key="comment.articleComment.id" class="comment-item">
                  <Avatar 
                    :avatar="comment.avatar || ''" 
                    :username="comment.username"
                    :style="{ width: '40px', height: '40px', fontSize: '16px' }" 
                  />
                  <div class="comment-content">
                    <div class="comment-header">
                      <div class="comment-info">
                        <span class="comment-author">{{ comment.username }}</span>
                        <span class="comment-time">{{ formatTime(comment.articleComment.createdAt) }}</span>
                      </div>
                    </div>
                    <div class="comment-text">{{ comment.articleComment.content }}</div>
                    <div class="comment-actions">
                      <span 
                        class="action-item" 
                        :class="{ 'liked': comment.isLike }"
                        @click="handleLikeComment(comment)"
                      >
                        <el-icon><Star /></el-icon>
                        <span>{{ comment.articleComment.likeCount || 0 }}</span>
                      </span>
                      <div style="display: flex; align-items: center; gap: 10px;">
                        <span v-if="comment.articleComment.userId != userStore.userId" class="action-item" @click="showReplyInput(comment)">
                          <el-icon ><ChatDotRound /></el-icon>
                          <span>回复</span>
                        </span>
                        <span 
                          class="action-item"
                          @click="toggleReplies(comment.articleComment.id)"
                          style="color: #409EFF;"
                        >
                          <el-icon><ChatDotRound /></el-icon>
                          <span>{{ expandedComments.has(comment.articleComment.id) ? '收起' : `展开${comment.articleComment.replyCount}条回复` }}</span>
                        </span>
                        <span 
                          v-if="comment.articleComment.userId === userStore.user?.id || userStore.user?.admin === 1"
                          class="action-item delete-action"
                          @click="handleDeleteComment(comment)"
                        >
                          <el-icon><Delete /></el-icon>
                          <span>删除</span>
                        </span>
                      </div>
                    </div>

                    <!-- 回复列表 -->
                    <div v-if="  expandedComments.has(comment.articleComment.id)" 
                         class="reply-list">
                      <div v-for="reply in commentReplies.get(comment.articleComment.id)" :key="reply.articleComment.id" class="reply-item">
                        <Avatar 
                          :avatar="reply.avatar || ''" 
                          :username="reply.username"
                          :style="{ width: '30px', height: '30px', fontSize: '14px' }" 
                        />
                        <div class="reply-content">
                          <div class="comment-header">
                            <div class="comment-info">
                              <span class="comment-author">{{ reply.username }}</span>
                              <span class="comment-time">{{ formatTime(reply.articleComment.createdAt) }}</span>
                            </div>
                          </div>
                          <p class="comment-text">
                            <span v-if="comment.username" class="reply-to">回复 @{{getUserNameByReplyId(reply?.articleComment?.parentId,reply?.articleComment?.replyId)}}：</span>
                            {{ reply.articleComment.content }}
                          </p>
                          <div class="comment-actions">
                            <span 
                              class="action-item" 
                              :class="{ 'liked': reply.isLike }"
                              @click="handleLikeComment(reply)"
                            >
                              <el-icon><Star /></el-icon>
                              <span>{{ reply.articleComment.likeCount || 0 }}</span>
                            </span>
                            <div style="display: flex; align-items: center; gap: 10px;">
                              <span v-if="reply.articleComment.userId != userStore.userId" class="action-item" @click="showReplyInput(comment, reply)">
                                <el-icon><ChatDotRound /></el-icon>
                                <span>回复</span>
                              </span>
                              <span 
                                v-if="reply.articleComment.userId === userStore.user?.id"
                                class="action-item delete-action"
                                @click="handleDeleteComment(reply)"
                              >
                                <el-icon><Delete /></el-icon>
                                <span>删除</span>
                              </span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- 回复弹窗 -->
                    <el-dialog
                      v-model="showReplyDialog"
                      title="发表回复"
                      width="500px"
                      :show-close="true"
                      :close-on-click-modal="false"
                    >
                      <div class="reply-dialog-content">
                        <div v-if="currentReplyInfo?.replyToUser" class="reply-to-info">
                          回复 @{{ currentReplyInfo.replyToUser }}
                        </div>
                        <el-input
                          v-model="replyContent"
                          type="textarea"
                          :rows="4"
                          placeholder="写下你的回复..."
                          resize="none"
                        />
                      </div>
                      <template #footer>
                        <div class="dialog-footer">
                          <el-button @click="cancelReply">取消</el-button>
                          <el-button
                            type="primary"
                            @click="submitReply"
                            :disabled="!replyContent.trim()"
                          >
                            发表回复
                          </el-button>
                        </div>
                      </template>
                    </el-dialog>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 目录部分：右侧固定目录 -->
      <div class="article-toc" :class="{ 'toc-visible': showToc || isReadingMode }">
        <div class="toc-wrapper">
          <h3 class="toc-title">目录</h3>
          <div class="toc-content">
            <div v-if="tocItems.length === 0" class="toc-empty">
              <p>暂无目录</p>
            </div>
            <div v-else class="toc-list">
              <div v-for="item in tocItems" :key="item.id" class="toc-item" :class="{
                'toc-item-active': item.active,
                [`toc-level-${item.level}`]: true
              }" @click="scrollToHeading(item.id)">
                {{ item.text }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 工具按钮组件 -->
      <div v-if="isReadingMode" class="tool-buttons">
        <div class="tool-main-btn" @click="toggleToolMenu">
          <el-tooltip effect="light" content="更多功能" placement="left">
            <div class="tool-rotating" :class="{ 'rotating': showToolMenu }">
              <svg class="tool-icon">
                <use xlink:href="#icon-gengduogongju" fill="#fff"></use>
              </svg>
            </div>
          </el-tooltip>
        </div>

        <!-- 工具菜单 -->
        <transition name="tool-menu">
          <div v-if="showToolMenu" class="tool-menu">
            <!-- 预览主题 -->
            <el-dropdown trigger="hover" placement="left">
              <div class="tool-menu-item">
                <svg class="tool-menu-icon">
                  <use xlink:href="#icon-yemianyangshi" fill="#fff"></use>
                </svg>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="theme in previewThemes" :key="theme" @click="previewTheme = theme">
                    {{ theme }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <!-- 代码主题 -->
            <el-dropdown trigger="hover" placement="left">
              <div class="tool-menu-item">
                <svg class="tool-menu-icon">
                  <use xlink:href="#icon-code" fill="#fff"></use>
                </svg>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="theme in codeThemes" :key="theme" @click="codeTheme = theme">
                    {{ theme }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <!-- 暗黑模式 -->
            <div class="tool-menu-item" @click="toggleDarkMode">
              <el-tooltip effect="light" :content="isDarkMode ? '切换到默认模式' : '切换到暗黑模式'" placement="left">
                <svg class="tool-menu-icon">
                  <use :xlink:href="isDarkMode ? '#icon-Sun' : '#icon-moon'" fill="#fff"></use>
                </svg>
              </el-tooltip>
            </div>

            <!-- 回到顶部 -->
            <div class="tool-menu-item" @click="scrollToTop">
              <el-tooltip effect="light" content="回到顶部" placement="left">
                <svg class="tool-menu-icon rocket-icon">
                  <use xlink:href="#icon-huojian" fill="#fff"></use>
                </svg>
              </el-tooltip>
            </div>
          </div>
        </transition>
      </div>
    
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, ChatDotRound, Star, Plus } from '@element-plus/icons-vue'
import SiderBar from '@/components/SiderBar.vue'
import { getArticleById, incrementViewCount, likeArticle, deleteArticle, type Article } from '@/api/article'
import { getTopComments, getKidComments, createComment, likeComment, deleteKidComment, deleteParentComment, type ArticleComment, type ArticleCommentVO, type ArticleCommentDTO } from '@/api/articleComment'
import { formatFullDate } from '@/utils/dateUtils'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { ElSelect, ElOption, ElDropdown, ElDropdownMenu, ElDropdownItem } from 'element-plus'
import { useUserStore } from '@/stores/userStore'
import waves from '@/components/waves.vue'
import Avatar from '@/components/Avatar.vue'

const userStore=useUserStore()
const route = useRoute()
const router = useRouter()

const articleData = ref<Article>({
  id: 0,
  title: '',
  content: '',
  cover: '',
  summary: '',
  publishTime: [],
  updateTime: [],
  viewCount: 0,
  likeCount: 0,
  commentCount: 0,
  categoryId: 0,
  userId: 0
})

const loading = ref(false)
// 分类数据
const categories = ref<any[]>([]) // Changed to any[] as Category type is removed

const mdPreviewRef = ref()

interface TocItem {
  id: string
  text: string
  level: number
  active: boolean
}

const tocItems = ref<TocItem[]>([])
const activeTocId = ref('')
const showToc = ref(false)


/**
 * 删除状态
 */
const isDeleting = ref(false)

/**
 * 内置预览主题列表
 * @type {string[]}
 */
const previewThemes = [
  'default',
  'github',
  'vuepress',
  'mk-cute',
  'smart-blue',
  'cyanosis'
]

/**
 * 内置代码主题列表
 * @type {string[]}
 */
const codeThemes = [
  'atom',
  'a11y',
  'github',
  'gradient',
  'kimbie',
  'paraiso',
  'qtcreator',
  'stackoverflow'
]

/**
 * 当前选中的预览主题
 * @type {import('vue').Ref<string>}
 */
const previewTheme = ref('cyanosis')

/**
 * 当前选中的代码主题
 * @type {import('vue').Ref<string>}
 */
const codeTheme = ref('atom')

/**
 * 暗黑模式状态
 * @type {import('vue').Ref<boolean>}
 */
const isDarkMode = ref(false)

/**
 * 阅读模式状态
 * @type {import('vue').Ref<boolean>}
 */
const isReadingMode = ref(false)

/**
 * 工具菜单显示状态
 * @type {import('vue').Ref<boolean>}
 */







 

 //评论逻辑
const comments = ref<ArticleCommentVO[]>([]);
const commentReplies = ref<Map<number, ArticleCommentVO[]>>(new Map());
const loadingComments = ref(false);
const commentError = ref('');

 function getUserNameByReplyId(parentId:any,replyId:any){
    //得到@的用户名
    console.log("parentId:"+parentId+" replyId:"+replyId)
    //回复parent
    if(parentId === replyId){
      const parentComment = comments.value.find(c => c.articleComment.id === parentId);
      console.log("回复"+parentComment?.username)
      return parentComment ? parentComment.username : '';
}
  //回复kid中的评论
  //得到当前评论的回复列表
  const replyList = commentReplies.value.get(parentId);
  if(replyList){
    const replyComment = replyList.find(c => c.articleComment.id === replyId);
          console.log("回复"+replyComment?.username)
    return replyComment ? replyComment.username : '';
  }
 }
const showToolMenu = ref(false)
// 评论相关响应数据
const commentContent = ref('')
const replyContent = ref('')
const showReplyDialog = ref(false)
const currentReplyInfo = ref<{
  parentId: number,
  replyToId: number | null,
  replyToUser: string
} | null>(null)
const isLoggedIn = ref(userStore.user !== null)
const expandedComments = ref(new Set())





// 组件挂载时获取文章数据并添加滚动监听
const loadComments = async (articleId: number) => {
  try {
    loadingComments.value = true;
    const response = await getTopComments(articleId);    
    if (Array.isArray(response.data.data)) {
      comments.value = response.data.data.map((comment: ArticleCommentVO) => {
        // 确保 replyCount 为数字类型
        if (comment.articleComment) {
          comment.articleComment.replyCount = Number(comment.articleComment.replyCount || 0);
        }
        return comment;
      });
      console.log('评论列表:', comments.value); // 添加日志以便调试
      // 清空回复缓存
      commentReplies.value.clear();
      expandedComments.value.clear();
    }
  } catch (error) {
    console.error('加载评论失败:', error);
    commentError.value = '加载评论失败，请稍后重试';
  } finally {
    loadingComments.value = false;
  }
};

/**
 * 切换回复列表显示状态
 */
const toggleReplies = async (commentId: number) => {
  // 如果评论已展开，则收起
  if (expandedComments.value.has(commentId)) {
    expandedComments.value.delete(commentId);
    return;
  }
  
  // 如果评论未展开，加载回复并展开
  try {
    const response = await getKidComments(commentId);
    if (Array.isArray(response.data.data)) {
      commentReplies.value.set(commentId, response.data.data);
      expandedComments.value.add(commentId);
      console.log('加载回复成功:', response.data.data);
    }
  } catch (error) {
    console.error('加载回复失败:', error);
    ElMessage.error('加载回复失败，请重试');
  }
}
/**
 * 提交评论
 */
const submitComment = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再评论')
    return
  }
  
  if (!commentContent.value.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  try {
    const commentData: ArticleCommentDTO = {
      id: null,
      parentId: null,
      articleId: articleData.value.id,
      userId: userStore.user?.id || 0,
      replyId: null,
      content: commentContent.value.trim(),
      status: 1,
      likeCount: 0,
      replyCount: 0,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    
    await createComment(commentData)
    ElMessage.success('评论成功！')
    commentContent.value = ''
    // 重新加载评论列表
    await loadComments(articleData.value.id)
  } catch (error) {
    console.error('提交评论失败:', error)
    ElMessage.error('评论失败，请重试')
  }
}







/**
 * 显示回复输入框
 */
const showReplyInput = (comment: ArticleCommentVO, replyToComment?: ArticleCommentVO) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再回复')
    return
  }
  
  // 设置回复信息
  currentReplyInfo.value = {
    parentId: comment.articleComment.parentId || comment.articleComment.id, // 如果是子回复，使用其parentId
    replyToId: replyToComment ? replyToComment.articleComment.id : comment.articleComment.id,
    replyToUser: replyToComment ? replyToComment.username : comment.username
  }
  
  // 显示弹窗
  showReplyDialog.value = true
}
/**
 * 提交回复
 */
const submitReply = async () => {
  if (!replyContent.value.trim() || !currentReplyInfo.value) {
    ElMessage.warning('回复内容不能为空')
    return
  }

  try {
    const replyData: ArticleCommentDTO = {
      id: null,
      parentId: currentReplyInfo.value.parentId,
      articleId: articleData.value.id,
      userId: userStore.user?.id || 0,
      replyId: currentReplyInfo.value.replyToId,
      content: replyContent.value.trim(),
      status: 1,
      likeCount: 0,
      replyCount: 0,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    
    await createComment(replyData)
    ElMessage.success('回复成功！')
    
    // 清空回复内容并关闭弹窗
    cancelReply()
    
    // 如果评论已展开，重新加载回复
    if (expandedComments.value.has(currentReplyInfo?.value?.parentId)) {
      await toggleReplies(currentReplyInfo?.value?.parentId)
    }
    
    // 重新加载主评论列表以更新回复数
    await loadComments(articleData?.value?.id)
  } catch (error) {
    console.error('提交回复失败:', error)
    ElMessage.error('回复失败，请重试')
  }
}

/**
 * 取消回复
 */
const cancelReply = () => {
  showReplyDialog.value = false
  replyContent.value = ''
  currentReplyInfo.value = null
}

/**
 * 删除评论
 */
const handleDeleteComment = async (comment: ArticleCommentVO) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }

  if (comment.articleComment.userId !== userStore.user?.id) {
    ElMessage.warning('只能删除自己的评论')
    return
  }

  try {
    await ElMessageBox.confirm(
      '确定要删除这条评论吗？删除后将无法恢复。',
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 根据是否为父评论调用不同的删除接口
    if (comment.articleComment.parentId === null) {
      await deleteParentComment(comment.articleComment.id)
    } else {
      await deleteKidComment(comment.articleComment.id)
    }
    
    ElMessage.success('评论删除成功')
    
    // 重新加载评论列表
    await loadComments(articleData.value.id)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}








/**
 * 点赞评论
 */
const handleLikeComment = async (comment: ArticleCommentVO) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再点赞')
    return
  }

  try {
    if (comment.isLike) {
      comment.articleComment.likeCount = Math.max(0, (comment.articleComment.likeCount || 0) - 1)
    } else {
      comment.articleComment.likeCount = (comment.articleComment.likeCount || 0) + 1
    }
    comment.isLike = !comment.isLike
    await likeComment(comment.articleComment.id)
    ElMessage.success(comment.isLike ? '点赞成功！' : '取消点赞成功！')
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}




















/**
 * 格式化时间
 */
const formatTime = (time: any) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  // 小于1分钟显示"刚刚"
  if (diff < 60000) {
    return '刚刚'
  }
  // 小于1小时显示"X分钟前"
  if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  }
  // 小于24小时显示"X小时前"
  if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  }
  // 小于30天显示"X天前"
  if (diff < 2592000000) {
    return `${Math.floor(diff / 86400000)}天前`
  }
  // 其他情况显示具体日期
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}


const getArticleData = async () => {
  const articleId = parseInt(route.params.id as string)
  console.log(articleId)
  if (!articleId) {
    ElMessage.error('文章ID无效')
    return
  }
  try {
    loading.value = true
     articleData.value= await getArticleById(articleId)
    
    // 等待DOM更新后生成目录
    await nextTick()
    generateToc()
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
  } finally {
    loading.value = false
    await incrementViewCount(articleId)
  }
}

/**
 * 处理目录变化事件
 */
const handleTocChange = (toc: any[]) => {
  if (toc && toc.length > 0) {
    const tocItemsData: TocItem[] = toc.map((item: any) => ({
      id: item.id || item.href?.replace('#', '') || '',
      text: item.text || '',
      level: item.level || 1,
      active: false
    }))

    tocItems.value = tocItemsData
  } else {
    tocItems.value = []
  }
}

/**
 * 生成目录（备用方法）
 */
const generateToc = () => {
  setTimeout(() => {
    if (mdPreviewRef.value) {
      // 尝试获取MdPreview内部的目录数据
      const previewElement = mdPreviewRef.value.$el
      const headings = previewElement.querySelectorAll('h1, h2, h3, h4, h5, h6')

      if (headings.length > 0) {
        const toc: TocItem[] = []

        headings.forEach((heading: Element, index: number) => {
          const level = parseInt(heading.tagName.charAt(1))
          const text = heading.textContent || ''
          const id = heading.id || `heading-${index}`

          // 如果没有id，为标题添加id
          if (!heading.id) {
            heading.id = id
          }

          toc.push({
            id,
            text,
            level,
            active: false
          })
        })

        tocItems.value = toc
      }
    }
  }, 1000)
}



/**
 * 点击目录项
 */
const scrollToHeading = (id: string) => {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

/**
 * 更新当前活动的目录项
 */
const updateActiveToc = () => {
  const headings = document.querySelectorAll('.markdown-content h1, .markdown-content h2, .markdown-content h3, .markdown-content h4, .markdown-content h5, .markdown-content h6')
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop

  let currentActiveId = ''

  headings.forEach((heading) => {
    const rect = heading.getBoundingClientRect()
    if (rect.top <= 100) { // 100px的偏移量
      currentActiveId = heading.id
    }
  })

  if (currentActiveId !== activeTocId.value) {
    activeTocId.value = currentActiveId
    // 更新目录项的active状态
    tocItems.value.forEach(item => {
      item.active = item.id === currentActiveId
    })
  }
}

/**
 * 处理滚动事件
 */
const handleScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const headerHeight = 350 // header区域的高度
  const offset = 0 // 额外的偏移量，确保目录不会紧贴header

  showToc.value = scrollTop > headerHeight + offset

  // 更新目录活动状态
  updateActiveToc()
}

/**
 * 删除文章
 */
const handleDeleteArticle = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这篇文章吗？删除后将无法恢复。',
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    isDeleting.value = true
    const articleId = articleData.value?.id
    await deleteArticle(articleId)
    ElMessage.success('文章删除成功')
    // 跳转到分类页面
    router.push('/')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文章失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  } finally {
    isDeleting.value = false
  }
}

/**
 * 跳转到文章编辑页面
 * @description 跳转到 /publish/:id 页面进行文章编辑
 */
const handleEditArticle = () => {
  router.push({ name: 'Publish', params: { id: articleData.value.id } })
}

/**
 * 处理文章点赞
 */
const handleLikeArticle = async () => {
  try {
    await likeArticle(articleData.value.id)
    // 更新前端显示的点赞数
    articleData.value.likeCount += 1
    ElMessage.success('点赞成功')
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败，请稍后重试')
  }
}

/**
 * 切换暗黑模式
 */
const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value
  // 应用暗黑模式样式
  document.body.classList.toggle('dark-mode', isDarkMode.value)
  // 保存设置到localStorage
  localStorage.setItem('articleDarkMode', isDarkMode.value.toString())
}

/**
 * 切换阅读模式
 */
const toggleReadingMode = async () => {
  isReadingMode.value = !isReadingMode.value

  if (isReadingMode.value) {
    // 进入全屏模式
    try {
      if (document.documentElement.requestFullscreen) {
        await document.documentElement.requestFullscreen()
      } else if ((document.documentElement as any).webkitRequestFullscreen) {
        await (document.documentElement as any).webkitRequestFullscreen()
      } else if ((document.documentElement as any).msRequestFullscreen) {
        await (document.documentElement as any).msRequestFullscreen()
      }
    } catch (error) {
      console.warn('无法进入全屏模式:', error)
    }
  } else {
    // 退出全屏模式
    try {
      if (document.exitFullscreen) {
        await document.exitFullscreen()
      } else if ((document as any).webkitExitFullscreen) {
        await (document as any).webkitExitFullscreen()
      } else if ((document as any).msExitFullscreen) {
        await (document as any).msExitFullscreen()
      }
    } catch (error) {
      console.warn('无法退出全屏模式:', error)
    }
  }

  // 应用阅读模式样式
  document.body.classList.toggle('reading-mode', isReadingMode.value)
}



/**
 * 切换工具菜单显示状态
 */
const toggleToolMenu = () => {
  showToolMenu.value = !showToolMenu.value
}

/**
 * 回到顶部
 */
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
  // 关闭工具菜单
  showToolMenu.value = false
}

/**
 * 组件卸载时移除滚动监听
 */
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)

  // 移除全屏状态变化监听
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
  document.removeEventListener('webkitfullscreenchange', handleFullscreenChange)
  document.removeEventListener('msfullscreenchange', handleFullscreenChange)

  // 移除暗黑模式和阅读模式
  document.body.classList.remove('dark-mode')
  document.body.classList.remove('reading-mode')
})

/**
 * 获取分类名称
 * @param categoryId 分类ID
 * @returns 分类名称
 */
const getCategoryName = (categoryId: number): string => {
  const category = categories.value.find(cat => cat.id === categoryId)
  return category ? category.name : ''
}

/**
 * 获取分类样式
 * @param categoryId 分类ID
 * @returns 样式对象
 */
const getCategoryStyle = (categoryId: number) => {
  const category = categories.value.find(cat => cat.id === categoryId)
  const color = category?.color || '#19c6c9'
  return {
    background: color,
    color: '#fff',
    padding: '5px 12px',
    borderRadius: '20px',
    fontSize: '0.8rem',
    backdropFilter: 'blur(10px)',
    border: '1px solid rgba(255, 255, 255, 0.2)'
  }
}

/**
 * 加载分类列表（优先本地缓存）
 */
const loadCategories = async () => {
}

/**
 * 监听全屏状态变化
 */
const handleFullscreenChange = () => {
  const isFullscreen = !!(document.fullscreenElement || (document as any).webkitFullscreenElement || (document as any).msFullscreenElement)
  if (!isFullscreen && isReadingMode.value) {
    // 如果退出了全屏但还在阅读模式，则退出阅读模式
    isReadingMode.value = false
    document.body.classList.remove('reading-mode')
  }
}

onMounted(async () => {
  await getArticleData();
  await loadCategories();
  
  // 获取评论列表
  if (articleData.value.id) {
    await loadComments(articleData.value.id);
  }
  
  window.addEventListener('scroll', handleScroll);

  // 添加全屏状态变化监听
  document.addEventListener('fullscreenchange', handleFullscreenChange);
  document.addEventListener('webkitfullscreenchange', handleFullscreenChange);
  document.addEventListener('msfullscreenchange', handleFullscreenChange);

  // 检查是否已经启用了暗黑模式（可以从localStorage读取）
  const savedDarkMode = localStorage.getItem('articleDarkMode');
  if (savedDarkMode === 'true') {
    isDarkMode.value = true;
    document.body.classList.add('dark-mode');
  }
});
</script>

<style scoped>
.article-page {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}

.dark-mode .article-page {
  background: #000;
}

.article-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
}

/* Header部分样式 */
.article-header {
  height: 280px;
  position: relative;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  position: relative;
}

.cover-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 删除按钮容器 */
.delete-button-container {
  position: absolute;
  bottom: 20px;
  right: 20px;
  z-index: 10;
}

.delete-button, .edit-button {
  background: rgba(220, 53, 69, 0.9);
  border: none;
  color: white;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  padding: 8px 15px;
  font-size: 0.9rem;
}

.edit-button {
  background: rgba(52, 152, 219, 0.9);
}

.delete-button:hover {
  background: rgba(220, 53, 69, 1);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 53, 69, 0.3);
}

.edit-button:hover {
  background: rgba(52, 152, 219, 1);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.7) 0%, rgba(0, 0, 0, 0.3) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.article-info {
  text-align: center;
  color: white;
  padding: 0 20px;
}

.article-title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 15px;
  line-height: 1.2;
}

.article-meta {
  margin-top: 12px;
}

.meta-info-row {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.95);
}

.divider {
  color: rgba(255, 255, 255, 0.6);
  margin: 0 6px;
}

.category-name {
  color: #c79fe6;
  font-weight: 500;
}

.stats-group {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.9rem;
  opacity: 0.9;
  transition: opacity 0.2s;
}

.stat-item:hover {
  opacity: 1;
}

/* Content部分样式 */
.article-content {
  flex: 1;
  margin: 20px;
}

.content-wrapper {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  max-width: 79%;
  /* margin-left: 20px; */
  /* background-color: red; */
}

.loading-container {
  padding: 20px;
}

.markdown-content {
  line-height: 1.8;
  color: #2c3e50;
}

.markdown-content h1,
.markdown-content h2,
.markdown-content h3,
.markdown-content h4,
.markdown-content h5,
.markdown-content h6 {
  margin: 30px 0 15px 0;
  color: #2c3e50;
  font-weight: 600;
}

.markdown-content h1 {
  font-size: 2rem;
  border-bottom: 3px solid #3498db;
  padding-bottom: 10px;
}

.markdown-content h2 {
  font-size: 1.7rem;
  border-bottom: 2px solid #ecf0f1;
  padding-bottom: 8px;
}

.markdown-content h3 {
  font-size: 1.4rem;
}

.markdown-content h4 {
  font-size: 1.2rem;
}

.markdown-content p {
  margin: 15px 0;
  font-size: 1rem;
}

.markdown-content pre {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 20px 0;
  border-left: 4px solid #3498db;
}

.markdown-content code {
  background: #f1f2f6;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
}

.markdown-content ul {
  margin: 15px 0;
  padding-left: 20px;
}

.markdown-content li {
  margin: 8px 0;
}

.markdown-content table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
}

.markdown-content th,
.markdown-content td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}

.markdown-content th {
  background: #f8f9fa;
  font-weight: bold;
}

/* 隐藏md-editor-v3的内置目录 */
.markdown-content .md-toc {
  display: none !important;
}

/* 文章摘要样式 */
.article-summary {
  padding: 5px 0;
  border-bottom: 1px solid #e0e0e0;
}

.summary-title {
  margin: 0 0 12px 0;
  font-size: 1.1rem;
  font-weight: 800;
  color: #c79fe6;
}

.summary-text {
  margin: 0;
  line-height: 1.6;
  font-size: 1rem;
  color: #c79fe6;
}

/* 文章内容主体 */
.content-main {
  margin-bottom: 40px;
}

/* 文章时间信息样式 */
.article-time-info {
  display: flex;
  justify-content: center;
  gap: 30px;
  padding: 20px 0;
  border-top: 1px solid #e0e0e0;
  margin-top: 30px;
  margin-bottom: 40px;
}

.time-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-label {
  color: #666;
  font-size: 0.9rem;
  font-weight: 500;
}

.time-value {
  color: #333;
  font-size: 0.9rem;
  font-weight: 600;
}

/* 评论区样式 */
.article-comments-section {
  margin: 10px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  max-width: 79%;
}

.comments-loading {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
}

.comments-error {
  padding: 20px;
  text-align: center;
  color: #f56c6c;
  background-color: #fef0f0;
  border-radius: 8px;
  margin: 20px 0;
}

.article-comments {
  padding: 20px;
}

.comments-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
}

/* 评论表单样式 */
.comment-form {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}

.comment-input-wrapper {
  flex: 1;
}

.comment-form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.comment-tips {
  color: #909399;
  font-size: 0.9rem;
}

/* 评论列表样式 */
.comments-list {
  margin-top: 30px;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.comment-author {
  font-weight: 600;
  color: #2c3e50;
  font-size: 1rem;
}

.comment-ip {
  color: #909399;
  font-size: 0.85rem;
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 4px;
}

.comment-time {
  color: #909399;
  font-size: 0.85rem;
}

.comment-text {
  color: #2c3e50;
  font-size: 0.95rem;
  line-height: 1.6;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  gap: 15px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 0.9rem;
  cursor: pointer;
  transition: color 0.2s;
}

.action-item:hover {
  color: #c79fe6;
}

.action-item.liked {
  color: #c79fe6;
}

.delete-action:hover {
  color: #F56C6C !important;
}

/* 回复弹窗样式 */
.reply-dialog-content {
  padding: 10px 0;
}

.reply-to-info {
  margin-bottom: 10px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  color: #409EFF;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.action-item span {
  font-size: 0.9rem;
}

/* 回复列表样式 */
.reply-list {
  margin-top: 15px;
  margin-left: 45px;
}

.reply-item {
  display: flex;
  gap: 10px;
  padding: 10px 0;
}

.reply-content {
  flex: 1;
}

.reply-to {
  color: #409EFF;
  margin-right: 5px;
  font-weight: 500;
}

/* 暗黑模式样式 */
.dark-mode .comments-title {
  color: #eee;
}

.dark-mode .comment-author {
  color: #eee;
}

.dark-mode .comment-text {
  color: #ddd;
}

.dark-mode .comment-item {
  border-bottom-color: #333;
}

.dark-mode .article-comments-section {
  background: #000;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
  border: 1px solid #333;
}

.dark-mode .article-comments {
  border-top-color: #333;
}



.dark-mode .comment-ip {
  background: #2c2c2c;
}

.dark-mode .action-item:hover {
  color: #79bbff;
}

/* 目录部分样式 */
.article-toc {
  position: fixed;
  top: 20px;
  right: 0px;
  width: 280px;
  max-height: calc(100vh - 120px);
  background: rgba(255, 255, 255, 0.85);
  overflow-y: hidden;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  z-index: 100;
  visibility: hidden;
  transform: translateX(20px);
  transition: all 0.3s ease;
}

.toc-visible {
  visibility: visible;
  transform: translateX(0);
}

.toc-wrapper {
  padding: 20px;
}

.toc-title {
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 15px;
  color: #2c3e50;
  border-bottom: 2px solid #3498db;
  padding-bottom: 10px;
}

.toc-content {
  max-height: calc(100vh - 120px);
  overflow-y: auto;
}

.toc-empty {
  text-align: center;
  color: #7f8c8d;
  font-size: 0.9rem;
  line-height: 1.6;
  padding: 20px 0;
}

.toc-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.toc-item {
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.9rem;
  color: #666;
  border-left: 3px solid transparent;
}

.toc-item:hover {
  background: #f8f9fa;
  color: #3498db;
  border-left-color: #3498db;
}

.toc-item-active {
  background: #e3f2fd;
  color: #1976d2;
  border-left-color: #1976d2;
  font-weight: 500;
}

.toc-level-1 {
  font-weight: 600;
  font-size: 1rem;
}

.toc-level-2 {
  padding-left: 20px;
  font-size: 0.9rem;
}

.toc-level-3 {
  padding-left: 35px;
  font-size: 0.85rem;
}

.toc-level-4 {
  padding-left: 50px;
  font-size: 0.8rem;
}

.toc-level-5 {
  padding-left: 65px;
  font-size: 0.75rem;
}

.toc-level-6 {
  padding-left: 80px;
  font-size: 0.7rem;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .article-toc {
    display: none;
  }

  .article-content {
    margin: 20px;
  }

  .content-wrapper {
    padding: 30px 20px;
  }
}

@media (max-width: 768px) {
  .article-container {
    margin-left: 0;
  }

  .article-header {
    height: 300px;
  }

  .article-title {
    font-size: 2rem;
  }

  .meta-row {
    gap: 20px;
  }

  .content-wrapper {
    padding: 20px 15px;
  }

  .markdown-content h2 {
    font-size: 1.5rem;
  }

  .markdown-content h3 {
    font-size: 1.3rem;
  }

  .article-time-info {
    flex-direction: column;
    gap: 15px;
    align-items: flex-end;
  }

  .article-summary {
    padding: 15px;
    margin-bottom: 20px;
  }

  .summary-title {
    font-size: 1.1rem;
  }

  .summary-text {
    font-size: 0.95rem;
  }
}

.article-bg-color {
  position: fixed;
  right: 0;
  top: 23vh;
  width: 26vw;
  height: 80vh;
  z-index: 100;
  /* background: url('@/assets/image.png') center center / cover no-repeat;
 */
  background-image: url('@/assets/image.png');
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
  cursor: pointer;
  pointer-events: auto;
}


.reading-mode .reading-mode-toggle:hover {
  transform: scale(1.1);
}

.article-container {
  position: relative;
  /* z-index: 1; 这一行已去掉或注释 */
}

.theme-switch-bar {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 20px;
  margin-bottom: 0;
}

.theme-switch-btns {
  display: flex;
  gap: 12px;
}

.theme-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #aaa;
  border-radius: 8px;
  color: #fff;
  padding: 8px 20px;
  font-size: 0.95rem;
  font-weight: 500;
  transition: border-color 0.2s;
}

.theme-btn:hover {
  border-color: #ccc;
}

.theme-label {
  font-size: 0.95rem;
  color: #fff;
  letter-spacing: 0.5px;
}

.theme-value {
  font-size: 0.95rem;
  color: #fff;
  font-weight: 500;
  margin-left: 4px;
}

@media (max-width: 768px) {
  .theme-switch-bar {
    justify-content: center;
    margin-top: 6px;
  }

  .theme-switch-btns {
    gap: 8px;
  }

  .theme-btn {
    padding: 2px 10px;
    font-size: 13px;
  }

  .theme-label {
    font-size: 12px;
  }

  .theme-value {
    font-size: 13px;
  }
}

.theme-switch-bar-in-cover {
  margin-top: 38px;
  /* 向下移动更多 */
  display: flex;
  justify-content: center;
}

/* 暗黑模式样式 */
.dark-mode-btn {
  /* background: rgba(30, 30, 30, 0.4); */
  border-color: #aaa;
}

.dark-mode-btn:hover {
  border-color: #ccc;
  /* background: rgba(30, 30, 30, 0.6); */
}

/* 阅读模式样式 */
.reading-mode .article-container {
  margin-left: 0;
}

.reading-mode .article-header {
  display: none;
}

/* 阅读模式下隐藏侧边栏 */
.reading-mode .article-page>div:first-child {
  display: none;
}

/* 暗黑模式全局样式 */
.dark-mode .article-container {
  background: #000;
  color: #eee;
}

.dark-mode .content-wrapper {
  background: #000;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
  border: 1px solid #aaa;
}

.dark-mode .markdown-content {
  color: #ddd;
}

.dark-mode .markdown-content h1,
.dark-mode .markdown-content h2,
.dark-mode .markdown-content h3,
.dark-mode .markdown-content h4,
.dark-mode .markdown-content h5,
.dark-mode .markdown-content h6 {
  color: #eee;
}

.dark-mode .markdown-content h1 {
  border-bottom-color: #555;
}

.dark-mode .markdown-content h2 {
  border-bottom-color: #444;
}

.dark-mode .markdown-content pre {
  background: #111;
  border-left-color: #555;
}

.dark-mode .markdown-content code {
  background: #111;
  color: #ddd;
}

.dark-mode .markdown-content table th,
.dark-mode .markdown-content table td {
  border-color: #444;
}

.dark-mode .markdown-content table th {
  background: #111;
}

.dark-mode .article-time-info {
  border-top-color: #444;
}

.dark-mode .time-label {
  color: #aaa;
}

.dark-mode .time-value {
  color: #ddd;
}

.dark-mode .article-toc {
  background: rgba(0, 0, 0, 0.85);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.2);
  border: 1px solid #aaa;
}

.dark-mode .toc-title {
  color: #ddd;
  border-bottom-color: #555;
}

.dark-mode .toc-item {
  color: #bbb;
}

.dark-mode .toc-item:hover {
  background: #111;
  color: #3498db;
}

.dark-mode .toc-item-active {
  background: #111;
  color: #3498db;
}

.dark-mode .article-summary {
  border-bottom-color: #444;
}

.dark-mode .summary-title {
  color: #c79fe6;
}

.dark-mode .summary-text {
  color: #c79fe6;
}

/* 工具按钮组件样式 */
.tool-buttons {
  position: fixed;
  top: 130px;
  right: 268px;
  z-index: 1000;
}

.tool-main-btn {
  width: 50px;
  height: 50px;
  /* background: #3498db; */
  /* border-radius: 50%; */
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  /* box-shadow: 0 4px 16px rgba(52, 152, 219, 0.3); */
  transition: all 0.3s ease;
  margin-bottom: 10px;
}


.tool-rotating {
  transition: transform 0.3s ease;
}

.tool-rotating.rotating {
  transform: rotate(45deg);
}

.tool-icon {
  width: 24px !important;
  height: 24px !important;
  color: white;
}

.tool-menu {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
}

.tool-menu-item {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}



.tool-menu-icon {
  width: 30px !important;
  height: 30px !important;
  color: white;
}

.rocket-icon {
  animation: rocket-bounce 2s infinite;
}

@keyframes rocket-bounce {

  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-3px);
  }
}

/* 工具菜单动画 */
.tool-menu-enter-active,
.tool-menu-leave-active {
  transition: all 0.3s ease;
}

.tool-menu-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.tool-menu-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.tool-menu-enter-to,
.tool-menu-leave-from {
  opacity: 1;
  transform: translateY(0);
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .tool-buttons {
    bottom: 20px;
    right: 20px;
  }

  .tool-main-btn {
    width: 45px;
    height: 45px;
  }

  .tool-menu-item {
    width: 40px;
    height: 40px;
  }

  .tool-icon {
    width: 20px !important;
    height: 20px !important;
  }

  .tool-menu-icon {
    width: 18px !important;
    height: 18px !important;
  }
}
</style>