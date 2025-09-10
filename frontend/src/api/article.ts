import request from '@/utils/request'

// 文章表单接口
export interface ArticleForm {
  title: string
  content: string
  cover?: string
  summary?: string
  categoryId?: number
}

// 文章响应接口
export interface Article {
  id: number
  publishTime: number[] // [year, month, day, hour, minute]
  updateTime: number[] // [year, month, day, hour, minute]
  viewCount: number
  likeCount: number
  commentCount: number
  title: string
  content: string
  cover: string
  summary: string
  categoryId: number
  userId:number
}

// 草稿接口（本地存储使用）
export interface Draft {
  id: number
  title: string
  content: string
  cover: string
  summary: string
  categoryId?: number
  createTime: string
  updateTime: string
}


/**
 * 发表文章
 * @param articleData 文章数据
 * @returns Promise<Article>
 */
export const publishArticle = async (articleData: ArticleForm): Promise<Article> => {
  try {
    const response = await request.post('/user/articles/publish/publish', articleData)
    if (response.data.code === 1) {
      return response.data.data as Article
    } else {
      throw new Error(response.data.msg || '发表文章失败')
    }
  } catch (error) {
    console.error('发表文章失败:', error)
    throw error
  }
}

export const getArticles = (page: number, size: number, categoryId?: number) => {
  const params: Record<string, number> = { page, size }
  if (categoryId !== undefined && categoryId !== 0) {
    params.categoryId = categoryId
  }
  return request.get('/user/articles', { params })
}

export const getArticlesIds = (categoryId?: number) => {
  const params: Record<string, number> = {}
  if (categoryId !== undefined && categoryId !== 0) {
    params.categoryId = categoryId
  }
  return request.get('/user/articles/ids', { params })
}


/**
 * 获取文章详情
 * @param articleId 文章ID
 * @returns Promise<Article>
 */
export const getArticleById = async (articleId: number): Promise<Article> => {
  try {
    const response = await request.get(`/user/articles/${articleId}`)
    if (response.data.code === 1) {
      return response.data.data as Article
    } else {
      throw new Error(response.data.msg || '获取文章详情失败')
    }
  } catch (error) {
    console.error('获取文章详情失败:', error)
    throw error
  }
}


/**
 * 点赞文章
 * @param articleId 文章ID
 * @returns Promise<void>
 */
export const likeArticle = async (articleId: number): Promise<void> => {
  try {
    const response = await request.post(`/user/articles/${articleId}/like`)
    if (response.data.code !== 1) {
      throw new Error(response.data.msg || '点赞失败')
    }
  } catch (error) {
    console.error('点赞文章失败:', error)
    throw error
  }
}

/**
 * 增加文章浏览量
 * @param articleId 文章ID
 * @returns Promise<void>
 */
export const incrementViewCount = async (articleId: number): Promise<void> => {
  try {
    const response = await request.post(`/user/articles/${articleId}/view`)
    if (response.data.code !== 1) {
      throw new Error(response.data.msg || '操作失败')
    }
  } catch (error) {
    console.error('增加浏览量失败:', error)
    throw error
  }
}

/**
 * 删除文章
 * @param articleId 文章ID
 * @returns Promise<void>
 */
export const deleteArticle = async (articleId: number): Promise<void> => {
  try {
    const response = await request.delete(`/user/articles/delete/${articleId}`)
    if (response.data.code === 1) {
      return
    } else {
      throw new Error(response.data.msg || '删除文章失败')
    }
  } catch (error) {
    console.error('删除文章失败:', error)
    throw error
  }
}

/**
 * 更新文章
 * @param articleId 文章ID
 * @param articleData 文章数据
 * @returns Promise<Article>
 */
export const updateArticle = async (articleId: number, articleData: ArticleForm): Promise<Article> => {
  try {
    const response = await request.put(`/user/articles/updateById/${articleId}`, articleData)
    if (response.data.code === 1) {
      return response.data.data as Article
    } else {
      throw new Error(response.data.msg || '更新文章失败')
    }
  } catch (error) {
    console.error('更新文章失败:', error)
    throw error
  }
}



export const aiJudge=(articleData: ArticleForm)=>{
  return request({
    url: '/user/articles/aiJudge',
    method: 'post',
    data:articleData
  })
}

















/**
 * 获取本地存储的草稿列表
 * @returns Draft[]
 */
export const getLocalDrafts = (): Draft[] => {
  try {
    const drafts = localStorage.getItem('article_drafts')
    return drafts ? JSON.parse(drafts) : []
  } catch (error) {
    console.error('获取本地草稿失败:', error)
    return []
  }
}

/**
 * 删除本地草稿
 * @param draftId 草稿ID
 */
export const deleteLocalDraft = (draftId: number): void => {
  try {
    const drafts = getLocalDrafts()
    const filteredDrafts = drafts.filter(draft => draft.id !== draftId)
    localStorage.setItem('article_drafts', JSON.stringify(filteredDrafts))
  } catch (error) {
    console.error('删除本地草稿失败:', error)
  }
}

/**
 * 清空所有本地草稿
 */
export const clearAllLocalDrafts = (): void => {
  try {
    localStorage.removeItem('article_drafts')
  } catch (error) {
    console.error('清空本地草稿失败:', error)
  }
}


export const getMyArticleCount = async () => {
  return request({
    url: '/user/articles/getMyArticleCount',
    method: 'get',
  })
}


