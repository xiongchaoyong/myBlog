// stores/chat.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useMessageStore } from './messageStore'
import { useGroupMessageStore } from './groupMessageStore'

export const useWsChatStore = defineStore('chat', () => {
  const messageStore = useMessageStore()
  const groupMessageStore = useGroupMessageStore()
  const ws = ref(null)
  const isConnected = ref(false)
  const userId = ref(null)

  function connect(id) {
    userId.value = id
    ws.value = new WebSocket(`${import.meta.env.VITE_WS_URL}?userId=${id}`)
    ws.value.onopen = () => {
      isConnected.value = true
      console.log('WebSocket 连接成功')
    }

    ws.value.onmessage = (event) => {
      const msg = JSON.parse(event.data) // 整体消息
      const newMsg = msg.to // 取出消息体
      messageStore.addMesListAll(newMsg)
      messageStore.updateMesListByIdS(newMsg)
      groupMessageStore.addMesListAll(newMsg)
      groupMessageStore.updateMesListByIdS(newMsg)

    }

    ws.value.onclose = () => {
      isConnected.value = false
      console.log('WebSocket 已断开')
    }

    ws.value.onerror = (err) => {
      console.error('WebSocket 错误', err)
    }
  }

  function sendMessage(toUserId, msgContent) {
    if (isConnected.value && ws.value) {
      const msg = {
        to: toUserId,
        msg: msgContent,
      }
      ws.value.send(JSON.stringify(msg))
    }
  }

  function disconnect() {
    if (ws.value) {
      ws.value.close()
      ws.value = null
      isConnected.value = false
      userId.value = null
    }
  }

  return {
    ws,
    isConnected,
    // messages,
    userId,
    connect,
    sendMessage,
    disconnect,
  }
})
