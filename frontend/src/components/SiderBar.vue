<template>
  <div class="sider-bar" :class="{ absolute: absolute }">
    <el-menu
      popper-effect="light"
      default-active="2"
      class="el-menu-vertical-demo sidebar-menu-flex"
      :collapse="true"
      @open="handleOpen"
      @close="handleClose"
    >
      <!-- 主页 -->
      <el-menu-item index="home" @click="$router.push('/')">
        <svg style="width:1.3rem;height:1.3rem;">
          <use xlink:href="#icon-zhuye1" fill="#1296db"></use>
        </svg>
        <template #title>主页</template>
      </el-menu-item>

  
      <!-- 发表文章 -->
      <el-menu-item index="publish" @click="publish">
        <el-icon>
          <EditPen />
        </el-icon>
        <template #title>发表文章</template>
      </el-menu-item>

      <!-- 热门 -->
      <el-menu-item index="hot-trends" @click="$router.push('/hot-trends')">
        <svg style="width:1.3rem;height:1.3rem;">
          <use xlink:href="#icon-remen" fill="#1296db"></use>
        </svg>
        <template #title>热门</template>
      </el-menu-item>

      <!-- 社交系统 -->
      <el-menu-item index="social-system" @click="gotoSocial">
        <svg style="width:1.3rem;height:1.3rem;">
          <use xlink:href="#icon-liaotian" fill="#1296db"></use>
        </svg>
        <template #title>社交系统</template>
      </el-menu-item>

      <!-- 工具 -->
      <el-menu-item index="tool-cards" @click="$router.push('/tool-cards')">
        <svg style="width:1.3rem;height:1.3rem;">
          <use xlink:href="#icon-biaoqian" fill="#1296db"></use>
        </svg>
        <template #title>工具</template>
      </el-menu-item>

      <el-menu-item index="ai-chat" @click="openAIChat">
        <img
          src="@/assets/呆唯.png"
          alt="AI Chat"
          style="width: 24px; height: 24px;"
        />
        <template #title>智能唯</template>
      </el-menu-item>

      <!-- 动态 -->
      <el-menu-item index="moments" @click="openMoments">
        <svg style="width:1.3rem;height:1.3rem;">
          <use xlink:href="#icon-dongtai" fill="#1296db"></use>
        </svg>
        <template #title>动态</template>
      </el-menu-item>

      <div class="sidebar-menu-bottom">
        <el-menu-item
          v-if="!isLoggedIn"
          index="login"
          @click="showLoginDialog"
        >
          <el-icon>
            <svg style="width:1.3rem;height:1.3rem;">
              <use xlink:href="#icon-wode" fill="#1296db"></use>
            </svg>
          </el-icon>
          <template #title>登录</template>
        </el-menu-item>
        <div
          v-if="isLoggedIn"
          class="user-avatar-container"
          @click="showUserMenu"
        >
          <div
            class="user-avatar"
            :style="{ backgroundColor: userAvatar ? 'transparent' : avatarBgColor }"
          >
            <img
              v-if="userAvatar"
              :src="userAvatar"
              alt="用户头像"
              class="avatar-img"
            />
            <span v-else>{{ avatarText }}</span>
          </div>
        </div>
      </div>
    </el-menu>

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
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { EditPen } from '@element-plus/icons-vue'
import { storeToRefs } from 'pinia'
import Login from '@/components/Login.vue'
import { useUserStore } from '@/stores/userStore'
import { useRouter } from 'vue-router'

const props = defineProps({
  absolute: { type: Boolean, default: false },
})

const router = useRouter()
const userStore = useUserStore()
const { isLoggedIn, username, userAvatar, avatarBgColor, avatarText } =
  storeToRefs(userStore)

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

const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}

const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}

onMounted(() => {
  checkLoginStatus()
})
</script>

<style>
.sider-bar {
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 999;
  position: fixed;
}

.sider-bar.absolute {
  position: absolute;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  height: 100vh;
}

.el-menu-vertical-demo.el-menu--collapse {
  height: 100vh;
}

.el-menu {
  height: 100vh;
  border-right: none;
}

.el-menu-vertical-demo.sidebar-menu-flex {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.sidebar-menu-bottom {
  margin-top: auto;
  display: flex;
  flex-direction: column;
}

.user-avatar-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: background-color 0.3s;
  border-radius: 4px;
  margin: 0 5px;
}

.user-avatar-container:hover {
  background-color: rgba(64, 158, 255, 0.1);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 14px;
  flex-shrink: 0;
  overflow: hidden;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}
</style>
