<template>
    <HeaderWave text="热搜" />
        <SiderBar />
  <div class="hot-trends-view">
      <HotTrends />
  </div>
</template>

<script lang="ts" setup>
import SiderBar from '@/components/SiderBar.vue'
import HotTrends from '@/components/HotTrends.vue'
import HeaderWave from '@/components/HeaderWave.vue'
</script>

<style scoped>
.hot-trends-view {
  display: flex;
  min-height: 100vh;
  margin-left: 150px;
  border-radius: 12px;
  border: 1px solid #e0e0e0;
  width: 77%;
}
</style>