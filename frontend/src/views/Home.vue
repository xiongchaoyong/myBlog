<template>
  <div class="allall">
          <SiderBar />

    <Layout />
    <div class="all">

      <!-- 文章列表区域 -->
      <div class="article-list" ref="articleListRef">
        <div class="articles-container">
            <!-- 有数据 -->
            <template v-if="!isLoading && articles?.length > 0">
              <div
                v-for="(article, index) in articles"
                :key="article?.id"
                class="article-item"
                :class="{ 'article-reverse': (index + 1) % 2 === 0 }"
                @click="handleArticleClick(article)"
              >
                <div class="article-cover">
                  <img :src="article?.cover" :alt="article?.title" />
                </div>
                <div class="article-content">
                  <h3 class="article-title">{{ article?.title }}</h3>
                  <p class="article-desc">{{ article?.summary }}</p>
                  <div class="article-meta">
                    <div class="meta-row">
                      <span class="view-count">
                        <svg style="width: 1rem; height: 1rem">
                          <use xlink:href="#icon-chakan" fill="#1296db"></use>
                        </svg>
                        {{ article?.viewCount }} 观看
                      </span>
                      <span class="like-count">
                        <svg style="width: 1rem; height: 1rem">
                          <use xlink:href="#icon-icon" fill="#1296db"></use>
                        </svg>
                        {{ article?.likeCount }} 点赞
                      </span>
                      <span class="comment-count">
                        <svg style="width: 1rem; height: 1rem">
                          <use xlink:href="#icon-pinglun" fill="#1296db"></use>
                        </svg>
                        {{ article?.commentCount }} 评论
                      </span>
                    </div>
                    <div class="category-container">
                      <span
                        v-if="getCategoryName(article?.categoryId)"
                        class="category-tag"
                        :style="getCategoryStyle(article?.categoryId)"
                      >
                        {{ getCategoryName(article?.categoryId) }}
                      </span>
                    </div>
                    <div class="time-row">
                      <span class="publish-time"
                        >发布时间：{{
                          formatFullDate(article?.publishTime)
                        }}</span
                      >
                      <span class="update-time"
                        >更新时间：{{
                          formatFullDate(article?.updateTime)
                        }}</span
                      >
                    </div>
                  </div>
                </div>
              </div>
            </template>

            <!-- 骨架屏 -->
            <template v-else-if="isLoading">
              <div
                v-for="i in skeletonCount"
                :key="i"
                class="article-item"
                :class="{ 'article-reverse': i % 2 === 0 }"
              >
                <div class="article-cover skeleton-cover">
                  <el-skeleton-item
                    variant="image"
                    style="width: 100%; height: 100%"
                  />
                </div>
                <div class="article-content">
                  <el-skeleton style="width: 100%">
                    <template #template>
                      <el-skeleton-item
                        variant="h3"
                        style="width: 80%; margin-bottom: 16px"
                      />
                      <div class="meta-row">
                        <el-skeleton-item
                          variant="text"
                          style="width: 30%; margin-right: 10px"
                        />
                        <el-skeleton-item
                          variant="text"
                          style="width: 30%; margin-right: 10px"
                        />
                        <el-skeleton-item variant="text" style="width: 30%" />
                      </div>
                      <div style="margin-top: 8px">
                        <el-skeleton-item variant="text" style="width: 20%" />
                      </div>
                      <div class="meta-row time-row" style="margin-top: 8px">
                        <el-skeleton-item
                          variant="text"
                          style="width: 45%; margin-right: 10px"
                        />
                        <el-skeleton-item variant="text" style="width: 45%" />
                      </div>
                    </template>
                  </el-skeleton>
                </div>
              </div>
            </template>

            <!-- 无数据提示 -->
            <div v-else class="no-data">
              <el-empty description="暂无文章" />
            </div>
            <!-- 加载状态 -->
            <div v-if="isLoadingMore" class="loading-more">
              <el-icon class="loading-icon"><Loading /></el-icon>
              <span>加载中...</span>
            </div>
            <div v-else-if="!hasMore" class="no-more">
              已经到底啦 ~
            </div>
          </div>

      </div>

      <!-- 右边的内容 -->
      <div class="right-content">
        <div class="profile-card">
          <!-- 背景图 -->
          <div class="banner">
            <img src="../assets/轻音.png" alt="banner" />
          </div>

          <!-- 头像 & 博客信息 -->
          <div class="profile-info">
            <img class="avatar" src="../assets/qq头像.jpg" alt="avatar" />
            <h2>X的个人博客</h2>
          </div>

          <!-- 数据统计 -->
          <div class="stats">
            <div>
              <span>文章</span>
              <strong>{{ myArticleCount}}</strong>
            </div>
            <div>
              <span>分类</span>
              <strong>{{ categories.length }}</strong>
            </div>
            <div>
              <span>动态</span>
              <strong>{{ myPostCount }}</strong>
            </div>
          </div>

          <!-- 社交图标 -->
          <div class="icons">
            <span class="icon icon-container" title="QQ">
              <svg style="width: 1.3rem; height: 1.3rem">
                <use xlink:href="#icon-QQ" fill="#1296db"></use>
              </svg>
              <img
                class="hover-img"
                src="../assets/qq二维码.jpg"
                alt="QQ二维码"
              />
            </span>

            <span class="icon icon-container" title="微信">
              <svg style="width: 1.3rem; height: 1.3rem">
                <use xlink:href="#icon-weixin" fill="#1aad19"></use>
              </svg>
              <img
                class="hover-img"
                src="../assets/微信二维码.jpg"
                alt="微信二维码"
              />
            </span>

            <span class="icon" title="Gitee">
              <svg style="width: 1.3rem; height: 1.3rem">
                <use xlink:href="#icon-gitee" fill="#c71d23"></use>
              </svg>
            </span>

            <span class="icon" title="GitHub">
              <svg style="width: 1.3rem; height: 1.3rem">
                <use xlink:href="#icon-GitHub" fill="#181717"></use>
              </svg>
            </span>

            <span class="icon" title="抖音">
              <svg style="width: 1.3rem; height: 1.3rem">
                <use xlink:href="#icon-douyin" fill="#010101"></use>
              </svg>
            </span>

            <a
              href="https://space.bilibili.com/2062571671?spm_id_from=333.1007.0.0"
              target="_blank"
            >
              <span class="icon" title="B站">
                <svg style="width: 1.3rem; height: 1.3rem">
                  <use xlink:href="#icon-Bzhan" fill="#00a1d6"></use>
                </svg>
              </span>
            </a>
          </div>
        </div>
        <div class="wordCloud">
          <CategroyWord></CategroyWord>
        </div>

        <!-- <el-carousel   :interval="3000" height="260px" motion-blur arrow="never">
          <el-carousel-item v-for="(item, index) in slides" :key="index">
            <div class="card-content">
              <img v-if="item?.imageUrls?.length===0" class="card-image" src="../assets/默认封面.png" alt="">
              <img v-else
                :src="item?.imageUrls[0]"
                class="card-image"
              />
              <span class="time">{{ item?.createdAt }}</span>
              <p class="text">{{ item?.content }}</p>
            </div>
          </el-carousel-item>
        </el-carousel> -->

        <div class="info-card">
          <div class="card-header">
            <span>「网站资讯」</span>
          </div>
          <div class="card-body">
            <div class="info-item">
              <span>注册用户:</span>
              <strong>{{ allUserCount }}</strong>
            </div>
            <div class="info-item">
              <span>文章数目：</span>
              <strong>{{ myArticleCount }}</strong>
            </div>
            <div class="info-item">
              <span>动态数目：</span>
              <strong>{{ postCount }}</strong>
            </div>
            <div class="info-item">
              <span>运行时间：</span>
              <strong>{{ getRunningDays("2025-09-07") }} 天</strong>
            </div>
            <div class="info-item">
              <span>博客访问次数：</span>
              <strong>5.5w</strong>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
      <waves></waves>

</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useArticleStore } from "@/stores/articleStore";
import { useCategoryStore } from "@/stores/categoryStore";
import { type Article, type ScrollPageVO } from "@/api/article";
import { type Category } from "@/api/category";
import { formatFullDate } from "@/utils/dateUtils";
import { ElSkeleton, ElSkeletonItem, ElEmpty } from "element-plus";
import { Loading } from '@element-plus/icons-vue';
import SiderBar from "@/components/SiderBar.vue";
import Layout from "./Layout.vue";
import CategroyWord from "@/components/CategoryWord.vue";
import type { Moment } from "@/api/Post";
import Post from "@/api/Post";
import {getMyArticleCount} from "@/api/article";
import waves from "@/components/waves.vue";
import { getScrollArticles } from "@/api/article";


const categories = ref<Category[]>([]);
const router = useRouter();
const articleStore = useArticleStore();
const categoryStore = useCategoryStore();

//优化分页组件显示
const isLoading = ref(true);

// 状态变量
const articleListRef = ref<HTMLElement | null>(null);
const isLoadingMore = ref(false);
const hasMore = ref(true);
const max = ref(Date.now()); // 初始化为当前时间戳
const offset = ref(0);
const articles = ref<Article[]>([]);

// 获取分页文章的方法
const fetchScrollPage = async (currentMax: number, currentOffset: number): Promise<ScrollPageVO> => {
  const response = await getScrollArticles(currentMax, currentOffset);

  if (response.data.code === 1) {
    return response.data.data;
  }else{
hasMore.value = false;
  return {
    objects: [],
    max: 0,
    offset: 0
  };
  }
  
};

onMounted(async () => {
  try {
    // 获取初始文章列表
    const pageInfo = await fetchScrollPage(max.value, offset.value);
    articles.value = pageInfo.objects;
    max.value = pageInfo.max;
    offset.value = pageInfo.offset;
  } catch (error) {
    console.error('初始化文章列表失败:', error);
    ElMessage.error('加载文章失败，请稍后重试');
  } finally {
    isLoading.value = false;
  }
});

// 加载下一页数据
const loadNextPage = async () => {
  if (isLoadingMore.value || !hasMore.value) return;
  
  isLoadingMore.value = true;
  try {
    // 获取下一页信息
    const pageInfo = await fetchScrollPage(max.value, offset.value);
    console.log(pageInfo);
    if (pageInfo== null || pageInfo == undefined) {
      hasMore.value = false;
      return;
    }

    // 直接更新文章列表和分页信息
    articles.value = [...articles.value, ...pageInfo.objects];
    max.value = pageInfo.max;
    offset.value = pageInfo.offset;
    if(articles?.value?.length % 5 !== 0){
      hasMore.value = false;
      console.log("没有更多文章了");
    }
  } catch (error) {
    console.error('加载更多文章失败:', error);
    ElMessage.error('加载更多文章失败，请稍后重试');
  } finally {
    isLoadingMore.value = false;
  }
};

// 滚动事件处理
const handleScroll = async () => {
  if (!articleListRef.value) return;
  
  const articleList = articleListRef.value;
  const articleListBottom = articleList.getBoundingClientRect().bottom;
  const windowHeight = window.innerHeight;
  
  // 当文章列表底部距离视口底部小于100px时加载更多
  if (articleListBottom - windowHeight <= 100) {
    await loadNextPage();
  }
};

// 添加和移除滚动监听
onMounted(() => {
  window.addEventListener('scroll', handleScroll);
});

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
});

//骨架屏数量（固定为5个）
const skeletonCount = 5;

//点击文章跳转
const handleArticleClick = (article: Article) => {
  router.push(`/article/${article.id}`);
};



















const slides = ref<Moment[]>([]);
const postCount = ref(0);
const myPostCount = ref(0);
const allUserCount = ref(0);
const myArticleCount = ref(0);
import User from "@/api/User";
import { usePostStore } from "@/stores/postStroe";
const postStore = usePostStore();
//动态初始化
//获取前五条
//获取动态总数量、个人的数量
//获取注册人数
//我的文章数目
onMounted(async () => {
  // if(postStore.getMonentList?.length > 0){
  //   for(let i=0; i<5;i++){
  //     slides.value.push(postStore.getMonentList[i]);
  //   }
  // }else{
  //   const postRes = await Post.getFivePosts();
  //   slides.value = postRes.data.data;
  // }
  const postCountRes = await Post.getPostsCount();
    postCount.value = postCountRes.data.data;

  const myPostCountRes = await Post.getMyPostsCount();
    myPostCount.value = myPostCountRes.data.data;

  const allUserCountRes = await User.getUserCount();
    allUserCount.value = allUserCountRes.data.data;

  const myArticleCountRes = await getMyArticleCount();
  myArticleCount.value = myArticleCountRes.data.data;
});


const getCategoryName = (categoryId: number): string => {
  const category = categories.value.find((cat) => cat.id === categoryId);
  return category ? category.name : "";
};

const getCategoryStyle = (categoryId: number) => {
  const category = categories.value.find((cat) => cat.id === categoryId);
  const color = category?.color || "#19c6c9";
  return {
    background: color,
    color: "#fff",
    padding: "2px 8px",
    borderRadius: "12px",
    fontSize: "0.7rem",
    fontWeight: "500",
  };
};

function getRunningDays(startDate: string): number {
  const start = new Date(startDate).getTime();
  const now = Date.now();
  const diff = now - start;
  return Math.floor(diff / (1000 * 60 * 60 * 24));
}
</script>

<style scoped>
.allall {
  display: flex;
  flex-direction: column;
}
.all {
  display: flex;
  min-height: 100vh;
  /* background-color: #ffe1ff !important; */
  position: relative;
}

.article-list {
  flex: 3;
  max-width: 60%;
  margin-left: 150px;
  padding: 20px;
}

.article-item {
  display: flex;
  align-items: stretch;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
  cursor: pointer;
  min-height: 230px;
  margin-bottom: 20px;
}

.article-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.article-item.article-reverse {
  flex-direction: row-reverse;
}

.article-cover {
  flex: 3; /* 图片缩小 */
  height: 230px;
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.article-item:hover .article-cover img {
  transform: scale(1.08);
}

.article-content {
  flex: 4;
  padding: 22px 28px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.article-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 10px;
  color: #222;
  line-height: 1.4;
}

.article-desc {
  font-size: 1rem;
  color: #555;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  display: -moz-box;
  display: box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.article-meta {
  font-size: 0.85rem;
  color: #666;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.category-container {
  margin-top: 4px;
}

.category-tag {
  display: inline-block;
  border-radius: 12px;
  padding: 2px 8px;
  font-size: 0.75rem;
  font-weight: 500;
  white-space: nowrap;
}

.time-row {
  font-size: 0.85rem;
  color: #999;
  display: flex;
  gap: 20px; /* 两个时间横排 */
  margin-top: 6px;
}

.right-content {
  /* width: 220px; */
  margin-left: 20px;
  flex: 1;
  /* background-color: red; */
}

.profile-card {
  width: 320px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  text-align: center;
  font-family: "Arial", sans-serif;
  margin-top: 20px;
}

.banner img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.profile-info {
  margin-top: -20px;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: 3px solid #fff;
  background: #eee;
}

.profile-info h2 {
  margin: 10px 0 5px;
  font-size: 18px;
}

.profile-info p {
  font-size: 14px;
  color: #666;
}

.stats {
  display: flex;
  justify-content: space-around;
  margin: 15px 0;
}

.stats div span {
  display: block;
  font-size: 13px;
  color: #888;
}

.stats div strong {
  font-size: 16px;
  color: #333;
}

.gitee-btn a {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #772525;
  color: #fff;
  padding: 8px 16px;
  border-radius: 8px;
  text-decoration: none;
  font-size: 14px;
}

.icons {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin: 15px 0;
}

.icon {
  position: relative;
  cursor: pointer;
}

.icon svg {
  transition: transform 0.2s ease;
}

.icon:hover svg {
  transform: scale(1.2);
}

.icon-container .hover-img {
  display: none;
  position: absolute;
  top: -110px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  border: 2px solid #ddd;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.icon-container:hover .hover-img {
  display: block;
}

.info-card {
  width: 320px;
  margin-top: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 16px 30px;
  font-size: 16px;
  color: #333;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: 10000;
  margin-bottom: 12px;
  font-size: 19px;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
}

.info-item strong {
  font-weight: bold;
}

.card-content {
  margin-top: 20px;
  background-color: white;
  width: 320px;
  position: relative;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}
/* 深度选择器覆盖 Element Plus 指示器样式 */
::v-deep(.el-carousel__indicators li button) {
  background-color: #e684e6; /* 未激活颜色 */
}

::v-deep(.el-carousel__indicators li.is-active button) {
  background-color: #ef2def; /* 激活颜色 */
}

.time {
  position: absolute;
  top: 8px;
  right: 12px;
  font-size: 12px;
  color: #fff;
  background: rgba(0, 0, 0, 0.4);
  padding: 2px 6px;
  border-radius: 4px;
}

.text {
  padding: 12px;
  font-size: 15px;
  color: #222;
  font-weight: bold;

  white-space: nowrap; /* 不换行 */
  overflow: hidden; /* 超出隐藏 */
  text-overflow: ellipsis; /* 超出显示省略号 */
}

.loading-more, .no-more {
  text-align: center;
  padding: 20px 0;
  color: #666;
  font-size: 14px;
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.loading-icon {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
