import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { getCategories, type Category } from '@/api/category'
import { getCategoriesWithCount } from '@/api/category'

/**
 * 分类数据缓存store
 * @description 缓存分类列表，避免重复请求
 */
export const useCategoryStore = defineStore('category', () => {
  /** 分类列表 */
  const categories = ref<Category[]>([])
  /** 是否已加载过分类 */
  const loaded = ref(false)


  const getCurCategory=(id:number)=>{
    return categories.value.find((item) => item.id === id)
  }
  const getAllCategory=computed(()=>{
    return categories.value
  })
  /**
   * 获取分类列表，优先用缓存
   * @returns 分类数组
   */
  const fetchCategories = async () => {
    if (!loaded.value) {
      categories.value = await getCategories()
      loaded.value = true
    }
    return categories.value
  }

  /**
   * 强制刷新分类列表
   */
  const refreshCategories = async () => {
    categories.value = await getCategories()
    loaded.value = true
    return categories.value
  }

  /**
   * 带文章数的分类缓存
   */
  const categoriesWithCount = ref<Category[]>([])
  const loadedWithCount = ref(false)

  /**
   * 获取带文章数的分类，优先用缓存
   */
  const fetchCategoriesWithCount = async () => {
    if (!loadedWithCount.value) {
      categoriesWithCount.value = await getCategoriesWithCount()
      loadedWithCount.value = true
    }
    return categoriesWithCount.value
  }
  /**
   * 强制刷新带文章数的分类
   */
  const refreshCategoriesWithCount = async () => {
    categoriesWithCount.value = await getCategoriesWithCount()
    loadedWithCount.value = true
    return categoriesWithCount.value
  }

  return {
    categories,
    loaded,
    fetchCategories,
    refreshCategories,
    categoriesWithCount,
    loadedWithCount,
    fetchCategoriesWithCount,
    refreshCategoriesWithCount,
    getCurCategory,
    getAllCategory
  }
}) 