<template>
  <div class="layout-container">
    <!-- 主要内容 -->
    <div class="main-content">
     <h3>朝永的博客</h3>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import router from '@/router'

// 动画状态控制
const showAvatar = ref(false)
const showText = ref(false)
const showButtons = ref(false)
const showCursor = ref(false)

// 打字机效果相关
const displayText = ref('')
const fullText = '你好，我是X，欢迎来到我的博客'
let typewriterIndex = 0

// 路由跳转
const gotoHome = () => {
  router.push('/')
}

const gotoAbout = () => {
  router.push('/about')
}

// 打字机效果
const typeWriter = () => {
  if (typewriterIndex < fullText.length) {
    displayText.value += fullText.charAt(typewriterIndex)
    typewriterIndex++
    setTimeout(typeWriter, 80) // 每个字符间隔80ms  } else {
    // 打字完成后保持光标闪烁，快速显示按钮
    setTimeout(() => {
      showButtons.value = true
    }, 100)
  }
}

// 启动动画序列
const startAnimationSequence = () => {
  // 1. 头像上升动画
  setTimeout(() => {
    showAvatar.value = true
  }, 300)

  // 2. 头像动画完成后显示文字和光标
  setTimeout(() => {
    showText.value = true
    showCursor.value = true
    typeWriter()
  }, 800)
}

onMounted(() => {
  startAnimationSequence()
})
</script>
<style scoped>
.layout-container {
  background-image: url('@/assets/bgc.jpeg');
  background-size: cover;
  background-position: center;
  
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 加上遮罩层 */
.layout-container::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* background: rgba(0, 0, 0, 0.4); 黑色遮罩，透明度40% */
  background: linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.2));
  z-index: 1;
}

/* 让内容显示在遮罩上方 */
.layout-container > * {
  position: relative;
  z-index: 2;
}

.main-content {
  z-index: 10;
  width: 100%;
  text-align: center;
  
}
.main-content h3{
  font-size: 42px;
  font-weight: bold;
  color: #ffffff;
  margin: 0;
  text-shadow: 0 0 20px rgba(255, 255, 255, 0.5);
  letter-spacing: 2px;
}

/* 头像样式 */
.avatar-container {
  opacity: 0;
  transition: all 1s ease-out;
  margin-bottom: 30px; /* 代替固定 top */
}

.avatar-container.avatar-animate {
  opacity: 1;
  transform: translateY(0); /* 头像上升动画保留 */
}

.avatar {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  border: 4px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 0 30px rgba(255, 255, 255, 0.3);
  object-fit: cover;
}

/* 文字容器 */
.text-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: calc(100% - 20px);
  margin-bottom: 30px; /* 代替固定 top */
}

.greeting {
  font-size: 56px;
  font-weight: 300;
  color: #ffffff;
  margin: 0;
  text-shadow: 0 0 20px rgba(255, 255, 255, 0.5);
  letter-spacing: 2px;
}

.cursor {
  font-size: 56px;
  color: #ffffff;
  margin-left: 5px;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

/* 按钮样式 */
.buttons-container {
  display: flex;
  gap: 30px;
  opacity: 0;
  animation: fadeInUp 0.8s ease-out forwards;
  justify-content: center;
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.btn {
  padding: 15px 40px;
  font-size: 18px;
  font-weight: 500;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
  min-width: 160px;
  backdrop-filter: blur(10px);
}

.btn-primary {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 25px rgba(255, 255, 255, 0.1);
}

.btn-primary:hover {
  transform: translateY(-3px);
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 12px 35px rgba(255, 255, 255, 0.2);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 25px rgba(255, 255, 255, 0.1);
}

.btn-secondary:hover {
  transform: translateY(-3px);
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 12px 35px rgba(255, 255, 255, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .greeting { font-size: 42px; }
  .cursor { font-size: 42px; }
  .avatar { width: 120px; height: 120px; }
  .btn { padding: 12px 30px; font-size: 16px; }
  .buttons-container { flex-direction: column; gap: 20px; }
}

</style>