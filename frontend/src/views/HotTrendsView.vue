<template>
  <div class="hot-trends-view">
    <SiderBar />
    <div class="main-content">
      <HotTrends />
    </div>
  </div>
</template>

<script lang="ts" setup>
import SiderBar from '@/components/SiderBar.vue'
import HotTrends from '@/components/HotTrends.vue'
</script>

<style scoped>
.hot-trends-view {
  display: flex;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  margin-left: 64px; /* 侧边栏宽度 */
}

@media (max-width: 768px) {
  .main-content {
    margin-left: 0;
  }
}
</style>