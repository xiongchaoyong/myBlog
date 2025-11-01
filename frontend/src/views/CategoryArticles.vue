<template>
  <div class="category-articles-layout">
    <!-- <StarryBackground /> -->
    <SiderBar class="sidebar" />
    <div class="category-main">
      <div class="category-articles-area">
        <div class="category-header">
          <div class="category-desc-left">
            标签
            <el-icon><Pear /></el-icon> {{ curCategory?.name || "全部文章" }}
          </div>
        </div>
        <div v-if="isLoading">
          <Loading
            :show="isLoading"
            text="文章正在加载中哦!"
            :mask="true"
          ></Loading>
        </div>
        <!-- <el-empty v-else-if="articles.length === 0" description="暂无文章" /> -->
        <div v-else class="articles-grid">
          <div
            v-for="article in articles"
            :key="article.id"
            class="article-card"
            @click="handleArticleClick(article)"
          >
            <div class="article-cover">
              <img
                :src="article.cover || '/default-cover.jpg.svg'"
                alt="文章封面"
              />
            </div>
            <div class="article-info">
              <div class="article-title">{{ article.title }}</div>
              <div class="article-meta-row article-meta-top">
                <span class="publish-time">{{
                  formatShortDate(article.publishTime)
                }}</span>
              </div>
              <div class="article-meta-row article-meta-bottom">
                <span class="view-count">
                  <el-icon>
                    <View />
                  </el-icon>
                  {{ article.viewCount }}
                </span>
                <span class="like-count">
                  <el-icon>
                    <Star />
                  </el-icon>
                  {{ article.likeCount }}
                </span>
                <span class="comment-count">
                  <el-icon>
                    <ChatDotRound />
                  </el-icon>
                  {{ article.commentCount }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 将分页按钮移到文章区域外部 -->
      <!-- <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="12"
          layout="total, prev, pager, next, jumper"
          :total="pageInfo.count"
          @current-change="handleCurrentChange"
          background
          hide-on-single-page
        />
      </div> -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onActivated } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { View, Star, ChatDotRound } from "@element-plus/icons-vue";
import { useArticleStore } from "@/stores/articleStore";
import { formatShortDate } from "@/utils/dateUtils";
import type { Article } from "@/api/article";
import SiderBar from "@/components/SiderBar.vue";
import Loading from "@/components/Loading.vue";
interface Category {
  id: number;
  name: string;
  color?: string;
}
const articleStore = useArticleStore();

const articles=ref<Article[]>([]);
const isLoading = ref(false);
const router = useRouter();
const route = useRoute();
const selectedCategoryId = ref<number>();
const curCategory = ref();
onMounted(async () => {
  // 从路由参数获取分类ID
  // const categoryId = Number(route.query.categoryId);
  // if (!isNaN(categoryId)) {
  //   selectedCategoryId.value = categoryId;
  // }
  // if (categoryStore.getAllCategory.length === 0)
  //   await categoryStore.fetchCategories();
  // curCategory.value = categoryStore.getCurCategory(
  //   selectedCategoryId.value as number
  // );
  // isLoading.value = true;
  // await articleStore.fetchArticlesByCategoryId(
  //   selectedCategoryId.value as number,
  //   1
  // );
  // console.log(curCategory.value);
  // isLoading.value = false;
});

// const pageInfo = computed(() =>
//   articleStore.getCategoryPageInfo(
//     selectedCategoryId.value as number,
//     currentPage.value
//   )
// );

// const articles = computed(() => {
//   return articleStore.getArticlesByCategoryId(
//     selectedCategoryId.value as number,
//     currentPage.value
//   );
// });

// const currentPage = ref(1);
// // 切页回调
// async function handleCurrentChange(page: number) {
//   currentPage.value = page;
//   // 如果当前页还没拉取过，就请求
//   await articleStore.fetchArticlesByCategoryId(
//     selectedCategoryId.value as number,
//     page
//   );
// }

/**
 * 处理页面变化
 */
// const handlePageChange = (page: number) => {
//   currentPage.value = page;
// };

/**
 * 处理文章点击
 */
const handleArticleClick = (article: Article) => {
  router.push(`/article/${article.id}`);
};

/**
 * 加载分类列表
 */
// const loadCategories = async () => {
//   try {
//     const categoryList = await categoryStore.fetchCategories()
//     categories.value = categoryList.map(cat => ({
//       id: cat.id,
//       name: cat.name,
//       color: cat.color
//     }))
//   } catch (error) {
//     console.error('获取分类列表失败:', error)
//     ElMessage.error('获取分类列表失败')
//   }
// }
// onMounted(async () => {

//   await loadCategories()
// })
// 组件激活时智能刷新数据
onActivated(async () => {
  // 从路由参数获取分类ID
  const categoryId = Number(route.query.categoryId);
  if (!isNaN(categoryId) && categoryId !== selectedCategoryId.value) {
    selectedCategoryId.value = categoryId;
  }
});
</script>

<style scoped>
.category-articles-layout {
  background-color: #ffe1ff;
  display: flex;
  min-height: 100vh;
  height: 100vh;
  overflow: hidden;
  position: relative;
}

.sidebar {
  width: 220px;
  min-width: 180px;
  background: transparent;
  height: 100vh;
  z-index: 10;
}

/* .category-main {
  flex: 1;
  padding: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 90vh;
  width: 90vw;
  overflow: hidden;
  z-index: 5;
  margin-top: 3%;
  margin-right: 2%;
} */

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 20px;
}

.category-desc-left {
  font-size: 2rem;
  font-weight: 700;
  color: #888;
  /* margin-bottom: 12px; */
  text-align: left;
  margin-left: 6px;
}

.category-articles-area {
  background-color: #fad9fa;
  box-shadow: rgba(0, 0, 0, 0.16) 0px 1px 4px;
  width: 90vw;
  height: 85vh;
  /* 调整高度以适应3行文章 */
  border-radius: 10px;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  margin: 30px 0 0 100px;
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: repeat(3, 1fr);
  /* 从2行改为3行 */
  gap: 15px 18px;
  margin-bottom: 18px;
  width: 100%;
  justify-items: center;
  align-items: center;
}

.article-card {
  background: rgba(248, 249, 250, 0.9);
  border-radius: 12px;
  box-shadow: rgba(9, 30, 66, 0.25) 0px 1px 1px,
    rgba(9, 30, 66, 0.13) 0px 0px 1px 1px;
  display: flex;
  overflow: hidden;
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 190px;
  /* 调整高度，使其在3行布局中更协调 */
  width: 18vw;
  min-width: 180px;
  max-width: 260px;
  justify-self: center;
}

.article-card:hover {
  box-shadow: 0 8px 25px rgba(25, 198, 201, 0.15);
  transform: translateY(-2px) scale(1.02);
}

.article-cover {
  width: 100%;
  height: 110px;
  overflow: hidden;
  flex-shrink: 0;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.article-info {
  flex: 1;
  padding: 8px 10px 6px 10px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.article-title {
  font-size: 1rem;
  font-weight: 600;
  color: #222;
  margin: 0 0 6px 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta-row {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 0.92rem;
  color: #888;
}

.article-meta-top {
  margin-bottom: 2px;
}

.article-meta-bottom {
  font-size: 0.92rem;
  color: #888;
}

.publish-time {
  color: #888;
  font-size: 0.92rem;
}

.view-count,
.like-count,
.comment-count {
  display: flex;
  align-items: center;
  gap: 2px;
}

.pagination-bottom {
  margin-top: 20px;
  padding-top: 10px;
  border-top: 1px dashed #eaeaea;
  display: flex;
  justify-content: center;
}

.pagination-container {
  margin-top: 20px;
  padding: 10px 0;
  display: flex;
  justify-content: center;
  width: 90vw;
  border-radius: 10px;
  margin-left: 100px;
}
</style>
