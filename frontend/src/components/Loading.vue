<template>
  <div v-if="show">
    <!-- 遮罩开启时 -->
    <div v-if="mask" class="loading-mask"></div>

    <!-- loader 内容 -->
    <div
      class="loading-content"
      :class="{ 'full-screen': mask }"
    >
      <div class="loader"></div>
      <p v-if="text" class="loading-text">{{ text }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
interface LoadingProps {
  show: boolean
  text?: string
  mask?: boolean
}

const props = defineProps<LoadingProps>()
const { show, text, mask = false } = props
</script>

<style scoped>
/* 遮罩层 */
.loading-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: transparent; /* 可改成半透明黑色 */
  pointer-events: auto; /* 阻止点击 */
  z-index: 998;
}

/* loader 内容 */
.loading-content {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 999;
}

/* 遮罩开启时全屏居中 */
.loading-content.full-screen {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

/* 遮罩关闭时相对父元素居中 */
.loading-content:not(.full-screen) {
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

/* loader 动画 */
.loader {
  width: 140px;
  height: 80px;
  background: url("@/assets/runner-sprite.png") 0 0 no-repeat;
  background-size: 1260px 80px;
  animation: run 1.5s steps(9) infinite;
}

@keyframes run {
  from { background-position: 0 0; }
  to   { background-position: -1260px 0; }
}

.loading-text {
  margin-top: 8px;
  font-size: 14px;
  color: #333;
}
</style>
