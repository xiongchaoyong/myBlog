<template>
  <div class="category-card">
    <h3 class="category-title">「分类」
</h3>
    <div ref="wordCloudRef" class="category-tags-cloud"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCategories } from '@/api/category'
import * as echarts from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { TooltipComponent } from 'echarts/components'
import 'echarts-wordcloud'
import { useRouter } from 'vue-router'

// 注册 ECharts 必要组件
echarts.use([CanvasRenderer, TooltipComponent])

interface Category {
  id: number
  name: string
  color?: string
}

const categories = ref<Category[]>([])
const wordCloudRef = ref<HTMLDivElement | null>(null)
let wordCloudChart: echarts.ECharts | null = null
const router = useRouter()

const tagColors = [
  '#19c6c9', '#67b26f', '#f7971e', '#f857a6',
  '#a1c4fd', '#c471f5', '#f64f59', '#43cea2',
  '#ff6a00', '#ffb347'
]

const initWordCloud = () => {
  if (!wordCloudRef.value) return
  if (wordCloudChart) {
    wordCloudChart.dispose()
  }
  wordCloudChart = echarts.init(wordCloudRef.value)
  updateWordCloud()
}

const updateWordCloud = () => {
  if (!wordCloudChart) return

  const data = categories.value.map(cat => ({
    name: cat.name,
    value: Math.random() * 100 + 50,
    categoryId: cat.id,
    textStyle: { 
      color: cat.color || tagColors[cat.id % tagColors.length] 
    }
  }))

  wordCloudChart.setOption({
    tooltip: { show: false },
    series: [{
      type: 'wordCloud',
      shape: 'circle',
      width: '100%',
      height: '100%',
      sizeRange: [20, 24],
      rotationRange: [0, 0],
      gridSize: 8,
      textStyle: { fontWeight: 'bold' },
      emphasis: { focus: 'self' }, // 鼠标悬浮只高亮当前标签
      data
    }]
  })

  wordCloudChart.on('click', (params: any) => {
    if (params.data && params.data.categoryId) {
      router.push({
        path: '/category-articles',
        query: { categoryId: params.data.categoryId }
      })
    }
  })
}

const loadCategories = async () => {
  try {
    const categoryList = await getCategories()
    categories.value = categoryList.map((cat: Category) => ({
      id: cat.id,
      name: cat.name,
      color: cat.color || tagColors[cat.id % tagColors.length]
    }))
    updateWordCloud()
  } catch (err) {
    console.error('获取分类失败:', err)
    ElMessage.error('获取分类失败')
  }
}

onMounted(async () => {
  await loadCategories()
  initWordCloud()
  window.addEventListener('resize', resizeChart)
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeChart)
  if (wordCloudChart) {
    wordCloudChart.dispose()
    wordCloudChart = null
  }
})

const resizeChart = () => {
  if (wordCloudChart) {
    wordCloudChart.resize()
  }
}
</script>

<style scoped>
.category-card {
  width: 320px;
  min-height: 200px;
  margin-top: 20px;
  padding: 16px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}


.category-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
  /* text-align: center; */
  color: #333;
}

.category-tags-cloud {
  width: 100%;
  min-height: 200px; 
}
</style>
