<template>
  <div class="hot-trends">
    <div class="trends-container">
      <div class="trend-card" v-for="platform in platforms" :key="platform.name">
        <div class="card-header">
          <div class="platform-info">
            <svg class="platform-icon" :style="{ fill: platform.color }">
              <use :xlink:href="platform.icon"></use>
            </svg>
            <h3 class="platform-name">{{ platform.name }}</h3>
          </div>
        </div>
        <div class="trends-list" v-if="platform.trends.length > 0">
          <div 
            class="trend-item" 
            v-for="(item, index) in platform.trends" 
            :key="index"
            @click="openTrend(item.url)"
          >
            <span class="rank" :class="getRankClass(index)">{{ index + 1 }}</span>
            <span class="title">{{ item.title }}</span>
            <span class="hot-value" v-if="item.hotValue">{{ item.hotValue }}</span>
          </div>
        </div>
        <div class="error-state" v-else-if="platform.isLoading">
          <img src="@/assets/image.png" alt="正在加载" class="error-image" />
          <div class="error-content">
            <p class="error-title">正在加载</p>
            <p class="error-contact">请稍候...</p>
          </div>
        </div>
        <div class="error-state" v-else-if="platform.hasError">
          <img src="@/assets/image.png" alt="接口失效" class="error-image" />
          <div class="error-content">
            <p class="error-title">接口失效</p>
            <p class="error-contact">请联系站主qq1846326845</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'

interface TrendItem {
  title: string
  url: string
  hotValue?: string
}

interface Platform {
  name: string
  icon: string
  color: string
  updateTime: string
  trends: TrendItem[]
  apiKey?: string
  isLoading?: boolean
  hasError?: boolean
}

const platforms = ref<Platform[]>([
  {
    name: '抖音热搜',
    icon: '#icon-douyin',
    color: '#010101',
    updateTime: '加载中...',
    trends: [],
    apiKey: 'douyin',
    isLoading: true,
    hasError: false
  },
  {
    name: 'B站热搜',
    icon: '#icon-Bzhan',
    color: '#00a1d6',
    updateTime: '加载中...',
    trends: [],
    apiKey: 'bilibili',
    isLoading: true,
    hasError: false
  },
  {
    name: 'CSDN',
    icon: '#icon-csdn',
    color: '#fc5531',
    updateTime: '加载中...',
    trends: [],
    apiKey: 'csdn',
    isLoading: true,
    hasError: false
  },
  {
    name: '百度热搜',
    icon: '#icon-baidu',
    color: '#2932e1',
    updateTime: '加载中...',
    trends: [],
    apiKey: 'baidu',
    isLoading: true,
    hasError: false
  },
  {
    name: '今日头条',
    icon: '#icon-toutiao',
    color: '#ff4757',
    updateTime: '加载中...',
    trends: [],
    apiKey: 'toutiao',
    isLoading: true,
    hasError: false
  },
  {
    name: '微博热搜',
    icon: '#icon-weibo',
    color: '#e6162d',
    updateTime: '加载中...',
    trends: [],
    apiKey: 'weibo',
    isLoading: true,
    hasError: false
  }
])

// 移除模拟数据，当接口失效时显示错误状态

const getRankClass = (index: number) => {
  if (index < 3) return 'top-three'
  return ''
}

const openTrend = (url: string) => {
  if (url && url !== '#') {
    window.open(url, '_blank')
  }
}

// 格式化热度值
const formatHotValue = (hotValue: number): string => {
  if (hotValue >= 10000000) {
    return `${(hotValue / 10000000).toFixed(1)}千万`
  } else if (hotValue >= 10000) {
    return `${(hotValue / 10000).toFixed(1)}万`
  } else {
    return hotValue.toString()
  }
}

// 获取所有平台热搜数据
const fetchAllTrends = async () => {
  try {
    const response = await fetch('https://hotpy.onrender.com')
    const result = await response.json()
    
    if (result.code === 0 && result.data) {
      const currentTime = new Date().toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
      
      platforms.value.forEach(platform => {
        const apiKey = platform.apiKey
        if (apiKey && result.data[apiKey] && result.data[apiKey].length > 0) {
          // 有真实数据的平台
          platform.trends = result.data[apiKey].map((item: any) => ({
            title: item.word,
            url: item.link ? item.link.trim().replace(/`/g, '') : '#',
            hotValue: item.hot_value ? (typeof item.hot_value === 'number' ? formatHotValue(item.hot_value) : item.hot_value) : undefined
          }))
          platform.updateTime = currentTime
          platform.isLoading = false
          platform.hasError = false
        } else {
          // 没有数据的平台显示错误状态
          platform.trends = []
          platform.updateTime = '接口失效'
          platform.isLoading = false
          platform.hasError = true
        }
      })
    } else {
      // API返回错误，所有平台显示错误状态
      platforms.value.forEach(platform => {
        platform.trends = []
        platform.updateTime = '接口失效'
        platform.isLoading = false
        platform.hasError = true
      })
    }
  } catch (error) {
    console.error('获取热搜数据失败:', error)
    // 如果API失败，所有平台显示错误状态
    platforms.value.forEach(platform => {
      platform.trends = []
      platform.updateTime = '接口失效'
      platform.isLoading = false
      platform.hasError = true
    })
  }
}

const loadTrends = async () => {
  await fetchAllTrends()
}

onMounted(() => {
  loadTrends()
})
</script>

<style scoped>
.hot-trends {
  padding: 20px;
  background: white;
  min-height: 100vh;
}

.trends-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 16px;
  max-width: 1400px;
  margin: 0 auto;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
}

.trend-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e0e0e0;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  height: fit-content;
  max-height: 480px;
  overflow: hidden;
}

.trend-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.platform-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.platform-icon {
  width: 24px;
  height: 24px;
}

.platform-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.update-time {
  font-size: 12px;
  color: #999;
  background: #f5f5f5;
  padding: 4px 8px;
  border-radius: 12px;
}

.trends-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 380px;
  overflow-y: auto;
}

.trend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  min-height: 32px;
}

.trend-item:hover {
  background-color: #f8f9fa;
}

.error-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  gap: 20px;
}

.error-image {
  width: 120px;
  height: 120px;
  object-fit: contain;
  opacity: 0.8;
  flex-shrink: 0;
}

.error-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: center;
}

.error-title {
  color: #333;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  line-height: 1.4;
}

.error-contact {
  color: #b19cd9;
  font-size: 16px;
  font-weight: 500;
  margin: 0;
  line-height: 1.4;
}

.rank {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  background: #e9ecef;
  color: #6c757d;
  flex-shrink: 0;
}

.rank.top-three {
  background: linear-gradient(45deg, #ff6b6b, #feca57);
  color: white;
}

.title {
  flex: 1;
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-value {
  font-size: 12px;
  color: #ff6b6b;
  font-weight: 500;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .trends-container {
    grid-template-columns: 1fr;
    gap: 16px;
    padding: 0 10px;
  }
  
  .trend-card {
    padding: 16px;
  }
  
  .platform-name {
    font-size: 16px;
  }
}
</style>