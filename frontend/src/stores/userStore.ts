import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { type User } from '@/api/uuser'
import { useMessageStore } from './messageStore'
import { useFriendStore } from './friendStore'
import { useNotificationStore } from './notificationStore'
import { useGroupChatStore } from './groupChatStore'
import { useGroupMessageStore } from './groupMessageStore'
import { useWsChatStore } from './wsChatStore'
import { useAIChatStore } from './aiChatStore'  
export const useUserStore = defineStore('user', () => {
    const messageStore = useMessageStore();
  const groupMessageStore = useGroupMessageStore();
  const friendStore = useFriendStore();
  const notificationStore = useNotificationStore();
  const groupChatStore = useGroupChatStore();
  const wsChatStore = useWsChatStore()
  const aiChatStore = useAIChatStore()
  // 状态
  const isLoggedIn = ref(false)
  const user = ref<User | null>(null)
  const token = ref('')

  // 计算属性
  const username = computed(() => user.value?.username || '')
  const userAvatar = computed(() => user.value?.avatar || '')
  const userId = computed(() => user.value?.id || null)

  // 生成头像背景色
  const avatarBgColor = computed(() => {
    if (!username.value) return '#409EFF'
    const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#9C27B0', '#FF9800', '#4CAF50']
    const index = username.value.charCodeAt(0) % colors.length
    return colors[index]
  })

  // 头像显示文本
  const avatarText = computed(() => {
    if (!username.value) return ''
    return username.value.charAt(0).toUpperCase()
  })

  // 方法
  /**
   * 设置用户登录状态
   */
  const setLoginStatus = (status: boolean) => {
    isLoggedIn.value = status
    if (!status) {
      user.value = null
      token.value = ''
    }
  }

  /**
   * 设置用户信息
   */
  const setUser = (userInfo: User) => {
    user.value = userInfo
    isLoggedIn.value = true
  }

  /**
   * 设置token
   */
  const setToken = (tokenValue: string) => {
    token.value = tokenValue
  }

  /**
   * 登录成功处理
   */
  const loginSuccess = (userInfo: User, tokenValue: string) => {
    // 设置token
    setToken(tokenValue)
    
    // 设置用户信息
    setUser(userInfo)

    // 持久化到localStorage
    saveToLocalStorage()

    return { success: true, user: userInfo }
  }

  /**
   * 退出登录
   */
   const logout = () => {
    messageStore.makeNull();
    groupMessageStore.makeNull();
    friendStore.makeNull();
    notificationStore.makeNull();
    groupChatStore.makeNullAndRemove();
    clearLocalStorage()
    setLoginStatus(false)
    wsChatStore.disconnect()
    aiChatStore.clear()
    // location.reload()
  };

  /**
   * 保存到localStorage
   */
  const saveToLocalStorage = () => {
    if (user.value) {
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('user', JSON.stringify(user.value))
    }
    if (token.value) {
      localStorage.setItem('token', token.value)
    }
  }

  /**
   * 从localStorage清除数据
   */
  const clearLocalStorage = () => {
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  /**
   * 从localStorage恢复状态
   */
  const restoreFromLocalStorage = () => {
    const loggedIn = localStorage.getItem('isLoggedIn') === 'true'
    const storedToken = localStorage.getItem('token') || ''

    // 恢复token
    if (storedToken) {
      setToken(storedToken)
    }

    // 恢复用户信息
    if (loggedIn) {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        try {
          const storedUser = JSON.parse(userStr)
          setUser(storedUser)
        } catch (e) {
          console.error('解析用户信息失败:', e)
          // 如果解析失败，清除无效数据
          clearLocalStorage()
        }
      }
    }
  }

  /**
   * 更新用户信息
   */
  const updateUser = (updates: Partial<User>) => {
    if (user.value) {
      user.value = { ...user.value, ...updates }
      saveToLocalStorage()
    }
  }


  return {
    // 状态
    isLoggedIn,
    user,
    token,

    // 计算属性
    username,
    userAvatar,
    userId,
    avatarBgColor,
    avatarText,

    // 方法
    setLoginStatus,
    setUser,
    setToken,
    loginSuccess,
    logout,
    saveToLocalStorage,
    clearLocalStorage,
    restoreFromLocalStorage,
    updateUser
  }
})