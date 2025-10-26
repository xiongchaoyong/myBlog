import request from '@/utils/request'

/**
 * 分类相关API接口
 * @author Your Name
 * @date 2024-01-01
 */

// 分类接口
export interface Category {
  id: number
  name: string
  color?: string
  articleCount?: number // 该分类下的文章数量
  createTime?: number[] // [year, month, day, hour, minute]
  updateTime?: number[] // [year, month, day, hour, minute]
}

/**
 * 获取分类列表
 * @returns Promise<Category[]>
 */
export const getCategories = async (): Promise<Category[]> => {
  try {
    const response = await request.get('/user/categories')
    if (response.data.code === 1) {
      return response.data.data as Category[]
    } else {
      throw new Error(response.data.msg || '获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    throw error
  }
}

/**
 * 创建分类
 * @param categoryData 分类数据
 * @returns Promise<Category>
 */
export const createCategory = async (categoryData: { name: string; color?: string }): Promise<Category> => {
  try {
    const response = await request.post('/user/categories/create', categoryData)
    if (response.data.code === 1) {
      return response.data.data as Category
    } else {
      throw new Error(response.data.msg || '创建分类失败')
    }
  } catch (error) {
    console.error('创建分类失败:', error)
    throw error
  }
}

/**
 * 获取分类列表及其文章数量
 * @returns Promise<Category[]>
 */
export const getCategoriesWithCount = async (): Promise<Category[]> => {
  try {
    const response = await request.get('/user/categories/with-count')
    if (response.data.code === 1) {
      return response.data.data as Category[]
    } else {
      throw new Error(response.data.msg || '获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    throw error
  }
}

export const getCategoryCount=()=>{
  return request({
    url:'/user/categories/getCategoryCount',
    method:'get'
  })
}