<template>
  <div 
    :class="['header_box', headerState.headerClass]" 
    :style="{ 
      background: headerState.scrollTop < 50 ? 'transparent' : 'rgba(142, 142, 143, 0.8)',
    }" 
  > 
    <div class="pc_menu flex_r_between"> 
      <div class="sub-avatar"> 
        <router-link v-if="userAvatar" to="/" >
          <el-avatar class="el-avatar" :src="userAvatar" /> 
        </router-link> 
        <span v-else class="site-title" @click="$router.push('/')">X</span>
      </div> 
      <div class="flex_r_around"> 
        <el-menu 
          class="sub-menu" 
          mode="horizontal" 
          :default-active="getPath" 
          :ellipsis="false" 
          menu-trigger="click" 
          @select="handleSelect" 
        > 
          <!-- 主页 -->
          <el-menu-item index="home" @click="$router.push('/')">
            <el-icon style="margin-right:5px;"><HomeFilled /></el-icon>
            主页
          </el-menu-item>
          
          <!-- 发表文章 -->
          <el-menu-item index="publish" @click="publish">
            <el-icon style="margin-right:5px;"><EditPen /></el-icon>
            发表文章
          </el-menu-item>

          <!-- 热门 -->
          <el-menu-item index="hot-trends" @click="$router.push('/hot-trends')">
            <el-icon style="margin-right:5px;"><Histogram /></el-icon>
            热门
          </el-menu-item>

          <!-- 社交系统 -->
          <el-menu-item index="social-system" @click="gotoSocial">
            <el-icon style="margin-right:5px;"><ChatDotRound /></el-icon>
            社交系统
          </el-menu-item>

          <!-- 工具 -->
          <el-menu-item index="tool-cards" @click="$router.push('/tool-cards')">
            <el-icon style="margin-right:5px;"><Collection /></el-icon>
            工具
          </el-menu-item>

          <!-- AI聊天 -->
          <el-menu-item index="ai-chat" @click="openAIChat">
            <el-icon style="margin-right:5px;"><Service /></el-icon>
            智能唯
          </el-menu-item>

          <!-- 动态 -->
          <el-menu-item index="moments" @click="openMoments">
            <el-icon style="margin-right:5px;"><Bell /></el-icon>
            动态
          </el-menu-item>

          <!-- 登录/用户 -->
          <el-menu-item
            v-if="!isLoggedIn"
            index="login"
            @click="showLoginDialog"
          >
            <el-icon style="margin-right:5px;"><User /></el-icon>
            登录
          </el-menu-item>
          <el-sub-menu v-else index="user">
            <template #title>
              <el-icon style="margin-right:5px;"><User /></el-icon>
              <span>{{ username }}</span>
            </template>
            <el-menu-item index="logout" @click="showUserMenu">退出登录</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div> 
    </div>

    <!-- 登录弹窗 -->
    <Login
      v-if="loginVisible"
      :visible="loginVisible"
      @close="loginVisible = false"
      @login-success="handleLoginSuccess"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { 
  HomeFilled, 
  EditPen, 
  Histogram, 
  ChatDotRound, 
  Collection, 
  Service, 
  Bell, 
  User 
} from '@element-plus/icons-vue'
import { storeToRefs } from 'pinia'
import Login from '@/components/Login.vue'
import { useUserStore } from '@/stores/userStore'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { isLoggedIn, username, userAvatar, avatarBgColor, avatarText } =
  storeToRefs(userStore)

// 导航栏状态
const headerState = reactive({
  headerClass: '',
  scrollTop: 0,
  lastScrollTop: 0,
})

// 获取当前路径作为激活菜单项
const getPath = computed(() => {
  const path = route.path
  if (path === '/') return 'home'
  if (path === '/hot-trends') return 'hot-trends'
  if (path === '/publish') return 'publish'
  if (path === '/chat') return 'social-system'
  if (path === '/tool-cards') return 'tool-cards'
  if (path === '/ai-chat') return 'ai-chat'
  if (path === '/moments') return 'moments'
  return path.substring(1)
})

const gotoSocial = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录哦！')
    return
  } else {
    router.push('/chat')
  }
}

const publish = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录哦！')
    return
  } else {
    router.push('/publish')
  }
}

const openMoments = () => {
  router.push('/moments')
}

const openAIChat = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录哦！')
    return
  } else router.push('/ai-chat')
}

const loginVisible = ref(false)

const checkLoginStatus = () => {
  userStore.restoreFromLocalStorage()
}

const showLoginDialog = () => {
  loginVisible.value = true
}

const showUserMenu = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '退出登录', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      handleLogout()
    })
    .catch(() => {})
}

const handleLogout = () => {
  userStore.logout()
  router.push('/')
  ElMessage.success('退出登录成功')
}

const handleLoginSuccess = () => {
  location.reload()
}

const handleSelect = (key: string) => {
  console.log('选中菜单项:', key)
}

// 监听滚动事件，控制导航栏显示/隐藏
const handleScroll = () => {
  headerState.scrollTop = window.pageYOffset || document.documentElement.scrollTop
  if (headerState.scrollTop > 50) {
    if (headerState.scrollTop > headerState.lastScrollTop) {
      headerState.headerClass = 'hide-header'
    } else {
      headerState.headerClass = 'fixed-header'
    }
  } else {
    headerState.headerClass = ''
  }
  headerState.lastScrollTop = headerState.scrollTop
}

onMounted(() => {
  checkLoginStatus()
  window.addEventListener('scroll', handleScroll)
})
</script>
<style scoped>
.header_box { 
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 999;
  transition: background 0.3s ease;
}

.header_box .sub-avatar { 
  padding: 5px 0 0 0; 
  display: flex; 
  align-items: center; 
} 

.header_box .sub-menu { 
  height: 53px; 
  background: transparent;
  border-bottom: none;
  font-size: 15px; /* 菜单文字变小 */
  font-weight: bold;
}

/* 去掉菜单底部的蓝色线条和默认背景 */
:deep(.el-menu--horizontal > .el-menu-item),
:deep(.el-menu--horizontal > .el-sub-menu .el-sub-menu__title) {
  color: #ffffff;
  background-color: transparent !important;
  border-bottom: none !important;
  padding: 0 10px !important; /* 减小左右间距 */
  font-size: 13px;
  transition: color 0.3s ease;
}

/* 悬浮或聚焦时文字变蓝，但背景仍保持透明 */
:deep(.el-menu--horizontal > .el-menu-item:hover),
:deep(.el-menu--horizontal > .el-menu-item:focus),
:deep(.el-menu--horizontal > .el-sub-menu__title:hover),
:deep(.el-menu--horizontal > .el-sub-menu__title:focus) {
  color: #e182f7 !important;
  background-color: transparent !important;
}

/* 去掉选中项的背景和底部线条，保持白色文字 */
:deep(.el-menu--horizontal > .el-menu-item.is-active),
:deep(.el-menu--horizontal > .el-sub-menu.is-active .el-sub-menu__title) {
  background-color: transparent !important;
  color: #ffffff !important; /* 保持白色文字 */
  border-bottom: none !important;
}

/* 有些 Element Plus 版本会加伪元素背景，这里清理掉 */
:deep(.el-menu-item.is-active::before),
:deep(.el-sub-menu__title.is-active::before) {
  background: none !important;
}



.pc_menu { 
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.pc_menu :deep(.el-sub-menu__icon-arrow) { 
  display: none; 
} 

.flex_r_between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.flex_r_around {
  display: flex;
  align-items: center;
}

:deep(.el-menu--horizontal > .el-sub-menu .el-sub-menu__title:hover) { 
  background-color: transparent; 
} 

.site-title {
  font-size: 1.2rem;
  font-weight: bold;
  cursor: pointer;
  color: #ffffff;
}

.site-title {
  font-size: 1.3rem;
  color: #ffffff;
}

.el-avatar {
  cursor: pointer;
}

.hide-header { 
  animation-name: hideHeader; 
  animation-duration: 0.8s; 
  animation-fill-mode: forwards; 
} 

.fixed-header { 
  animation-name: header; 
  animation-duration: 0.8s; 
  animation-fill-mode: forwards; 
} 

@keyframes header { 
  0% { 
    transform: translateY(-52px); 
  } 
  100% { 
    transform: translateY(0px); 
  } 
} 

@keyframes hideHeader { 
  0% { 
    transform: translateY(0px); 
  } 
  100% { 
    transform: translateY(-52px); 
  } 
} 
</style>

