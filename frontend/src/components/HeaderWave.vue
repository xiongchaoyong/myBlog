<template>
  <div class="all">
    <div class="layout"></div>

    <!-- 居中文字 -->
    <div class="center-text">{{ text }}</div>

    <waves class="waves-bottom" height="3rem" />
  </div>
</template>

<script lang="ts" setup>
import { defineProps } from 'vue'
import waves from "@/components/waves.vue";

// 接收文字参数
const props = defineProps({
  text: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.all {
  position: relative; /* 容器必须相对定位，让波浪绝对定位相对它 */
  height: 35vh;
  width: 100%;
  overflow: hidden; /* 防止波浪溢出容器 */
}

.layout {
  width: 100%;
  height: 35vh;
  background-image: url('@/assets/bgc.jpeg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative; /* 遮罩层要基于它 */
  z-index: 1;
}

/* 遮罩层 */
.layout::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 35vh;
  background: linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.5));
  z-index: 2;
}

/* 居中文字 */
.center-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 4; /* 高于遮罩和波浪 */
  color: #fff;
  font-size: 3rem;
  text-align: center;
  text-shadow: 0 0 10px rgba(0,0,0,0.5);
  pointer-events: none; /* 不阻挡鼠标 */
}

/* 波浪悬浮在底部 */
.waves-bottom {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 3; /* 高于遮罩和图片 */
  pointer-events: none; /* 防止波浪阻挡鼠标事件 */
}
</style>
