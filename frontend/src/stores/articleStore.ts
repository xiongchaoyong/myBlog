import { defineStore } from "pinia";
import { ref } from "vue";
import { getArticles, getArticlesIds, type Article } from "@/api/article";

export const useArticleStore = defineStore("article", () => {
  const allArticlesById = ref<Record<number, Article>>({});

  // 分类映射，key = categoryId（首页用 0）
  const categoryMap = ref<
    Record<
      number,
      {
        articleIds: number[];
        currentPage: number;
        totalPages: number;
        pageLoading: Record<number, boolean>;
        pageLoaded: Record<number, boolean>;
        count: number;
      }
    >
  >({});

  // 获取分类当前页面的文章
  function getArticlesByCategoryId(categoryId: number, page: number) {
    const cat = categoryMap.value[categoryId];
    if (!cat) return [];
    let start = (page - 1) * 5;
    let curIds = cat.articleIds.slice(start, start + 5);
    if (categoryId === 0) return curIds.map((id) => allArticlesById.value[id]);
    else {
      start = (page - 1) * 12;
      curIds = cat.articleIds.slice(start, start + 12);
      return curIds.map((id) => allArticlesById.value[id]);
    }
  }

  // 获取分页信息，可指定页码
  function getCategoryPageInfo(categoryId: number, page?: number) {
    const cat = categoryMap.value[categoryId];
    if (!cat)
      return { currentPage: 0, totalPages: 0, isPageLoading: false, count: 0 };

    const targetPage = page ?? cat.currentPage;

    return {
      currentPage: cat.currentPage,
      totalPages: cat.totalPages,
      isPageLoading: cat.pageLoading[targetPage] || false,
      count: cat.count,
    };
  }

  function hasMore(categoryId: number) {
    const cat = categoryMap.value[categoryId];
    if (!cat) return false;
    return cat.currentPage < cat.totalPages;
  }

  // 拉取某分类 id 的某一页
  async function fetchArticlesByCategoryId(categoryId: number, page: number) {
    if (!categoryMap.value[categoryId]) {
      categoryMap.value[categoryId] = {
        articleIds: [],
        currentPage: 0,
        totalPages: 0,
        pageLoading: {},
        pageLoaded: {},
        count: 0,
      };
    }

    const cat = categoryMap.value[categoryId];
    if (cat.pageLoading[page]) return; // 正在请求或已加载
    if (cat.pageLoaded[page]) return; // 已加载
    cat.pageLoading[page] = true;
    cat.pageLoaded[page] = true;

    try {
      let res;
      if (categoryId === 0) {
        res = await getArticles(page, 5);
      } else {
        res = await getArticles(page, 12, categoryId);
      }

      const data = res.data.data;

      // 保存文章到 allArticlesById，并记录 id
      data.articles.forEach((article: Article) => {
        allArticlesById.value[article.id] = article;
      });
      //第一次添加全部ids
      if (cat.articleIds.length === 0) {
        if (categoryId === 0) {
          const res = await getArticlesIds();
          cat.articleIds = res.data.data;
        } else {
          const res = await getArticlesIds(categoryId);
          cat.articleIds = res.data.data;
        }
      }

      cat.count = data.count;
      // 计算总页数
      const pageSize = categoryId === 0 ? 5 : 12;
      cat.totalPages = Math.ceil(cat.count / pageSize);

      cat.currentPage = page;
    } finally {
      cat.pageLoading[page] = false;
    }
  }

  // 根据 id 获取文章详情
  function getArticleById(id: number) {
    return allArticlesById.value[id];
  }

  function incrementViewCount(articleId: number) {
    const article = allArticlesById.value[articleId];
    if (article) article.viewCount++;
  }
  function deleteArticle(articleId: number, categoryId: number) {
    // 1. 删除分类列表里的 id
    const cat = categoryMap.value[categoryId];
    if (cat) {
      const index = cat.articleIds.indexOf(articleId);
      if (index > -1) {
        cat.articleIds.splice(index, 1);
        // 更新总数
        cat.count--;
        // 更新总页数
        const pageSize = categoryId === 0 ? 5 : 12;
        cat.totalPages = Math.ceil(cat.count / pageSize);
        // 保证 currentPage >= 1
        if (cat.currentPage > cat.totalPages) {
          cat.currentPage = Math.max(cat.totalPages, 1);
        }
      }
    }

    // 2. 如果是分类文章，还要从首页 (0 分类) 删除
    if (categoryId !== 0) {
      const homeCat = categoryMap.value[0];
      if (homeCat) {
        const index = homeCat.articleIds.indexOf(articleId);
        if (index > -1) {
          homeCat.articleIds.splice(index, 1);
          homeCat.count--;
          homeCat.totalPages = Math.ceil(homeCat.count / 5);
          if (homeCat.currentPage > homeCat.totalPages) {
            homeCat.currentPage = Math.max(homeCat.totalPages, 1);
          }
        }
      }
    }

    // 3. 最后检查这篇文章是否仍在任何分类中存在
    const stillExists = Object.values(categoryMap.value).some((cat) =>
      cat.articleIds.includes(articleId)
    );
    if (!stillExists) {
      delete allArticlesById.value[articleId];
    }
  }

  // 新增文章
  function addArticle(article: Article) {
    // 1. 放入全局字典
    allArticlesById.value[article.id] = article;

    // 2. 找到该分类的缓存
    if (!categoryMap.value[article.categoryId]) {
      categoryMap.value[article.categoryId] = {
        articleIds: [],
        currentPage: 0,
        totalPages: 0,
        count: 0,
        pageLoading: {},
        pageLoaded: {},
      };
    }
    const cat = categoryMap.value[article.categoryId];

    // 3. 加入分类文章列表（避免重复）
    if (!cat.articleIds.includes(article.id)) {
      cat.articleIds.unshift(article.id); // 新文章通常加在最前
      cat.count += 1;
    }

    // 4. 首页 (categoryId = 0) 也要加
    if (!categoryMap.value[0]) {
      categoryMap.value[0] = {
        articleIds: [],
        currentPage: 0,
        totalPages: 0,
        count: 0,
        pageLoading: {},
        pageLoaded: {},
      };
    }
    const homeCat = categoryMap.value[0];
    if (!homeCat.articleIds.includes(article.id)) {
      homeCat.articleIds.unshift(article.id);
      homeCat.count += 1;
    }
  }
  // 更新文章
  function updateArticle(article: Article) {
    const existing = allArticlesById.value[article.id];
    if (!existing) {
      console.warn(`updateArticle: 文章 ${article.id} 不存在，已忽略`);
      return;
    }

    // 用新数据覆盖旧的（浅拷贝更新，避免丢失响应式引用）
    allArticlesById.value[article.id] = {
      ...existing,
      ...article,
    };
  }

  return {
    allArticlesById,
    categoryMap,
    getArticlesByCategoryId,
    fetchArticlesByCategoryId,
    hasMore,
    getArticleById,
    getCategoryPageInfo,
    incrementViewCount,
    deleteArticle,
    addArticle,
    updateArticle,
  };
});
