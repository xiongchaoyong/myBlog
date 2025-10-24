<template>
  <HeaderWave text="XChat"></HeaderWave>
  <SiderBar></SiderBar>
    <div class="ai-chat-container">
      <!-- 头部导航条   top -->
      <div class="chat-header">
        <!-- <h3>ChatX</h3> -->

      <!-- 聊天消息区域 -->
         <div v-if="waitSessionList"  class="chat" >
             <Loading :show="waitSessionList" text="数据加载中哦!"></Loading>
          </div>

      <div class="chat" v-else>
        <div class="session-list" v-if="aiChatStore.getSessionList.length > 0">
          <!-- 会话列表头部 -->
          <div class="session-top">
            <p>聊天记录</p>
            <el-icon class="my-CirclePlus" @click="newChat" v-if="!isAdding">
              <CirclePlus />
            </el-icon>
            <el-icon class="my-CirclePlus" v-if="isAdding"><Cherry /></el-icon>
          </div>

          <!-- 会话列表渲染,item -->
          <div
            v-for="(session, index) in aiChatStore.getSessionList"
            :key="index"
            class="session-item"
            :class="{
              activeItem: session.id === aiChatStore.getCurSession?.id,
            }"
            @click="setIndex(index)"
          >
            <div class="session-title">{{ session.title }}</div>

            <div class="bottom">
              <div class="session-time">
                {{ formatCreatedTime(session.createdTime) }}
              </div>
              <div class="session-do" @click.stop="handleSessionDo(index)">
                <el-icon><Delete /></el-icon>
              </div>
            </div>
          </div>
        </div>

        <!-- 消息区域 -->
        <div class="chat-messages" ref="messagesContainer">

          <div v-if="aiChatStore.getCurSession?.isWaiting ">
            <Loading :show="aiChatStore.getCurSession?.isWaiting" text="正在获取聊天信息哦!"></Loading>
          </div>

          <!-- 创建新聊天欢迎消息   只有当没有会话时才显示-->
          <div class="welcome-message" v-else-if="!aiChatStore.getCurSession">
            <div class="welcome-content">
              <div class="welcome-icon">
                <img
                  src="@/assets/呆唯.png"
                  alt="AI Chat"
                  style="width: 80px; height: 80px"
                />
              </div>
              <h1 class="welcome-title">
                很高兴遇见你! {{ userStore.user?.username }}
              </h1>
              <p class="welcome-tip">开始新聊天,AI 助手将为您提供帮助</p>

              <button class="new-chat-button" @click="newChat">
                <el-icon><Cpu /></el-icon>
                <span class="text">新聊天</span>
              </button>
            </div>
          </div>

          <!-- 输入问题欢迎消息 -->
          <div
            class="welcome-message"
            v-else-if="
              aiChatStore.getCurMesList.length === 0
            "
          >
            <div class="welcome-content">
              <div class="welcome-icon">
                <img
                  src="@/assets/呆唯.png"
                  alt="AI Chat"
                  style="width: 80px; height: 80px"
                />
              </div>
              <h1>我是呆唯,开始输入您的问题吧!</h1>
            </div>
          </div>

          <!-- 消息列表渲染 -->

          <div
            v-for="(message, index) in aiChatStore.getCurMesList"
            v-else
            :key="index"
            :class="['message', message.role]"
          >
            <div class="message-avatar">
              <el-avatar
                v-if="message.role === 'user'"
                :size="50"
                :src="userStore.userAvatar"
                :style="{ backgroundColor: userStore.avatarBgColor }"
              >
                {{ userStore.avatarText }}
              </el-avatar>

              <el-avatar v-else :size="50" :src="'@/assets/呆唯.png'">
              </el-avatar>
            </div>
            <!-- <div class="mes"> -->

            <div class="message-content">
              <div
                class="message-text"
                v-html="formatMessage(message.content)"
              ></div>

              <!-- </div> -->

              <!-- 消息操作 -->
              <div
                class="message-actions"
                v-if="message.role === 'ai' && !message.isWaiting"
              >
                <el-button
                  size="small"
                  text
                  @click="copyMessage(message.content)"
                >
                  <el-icon><CopyDocument /></el-icon>
                  复制
                </el-button>

                <el-button
                  size="small"
                  text
                  @click="regenerateResponse(message.id, index)"
                  v-if="index === aiChatStore.getCurMesList.length - 1"
                >
                  <el-icon><Refresh /></el-icon>
                  重新生成
                </el-button>
              </div>

              <!-- 重试 -->
              <div
                class="message-actions"
                v-if="
                  index === aiChatStore.getCurMesList.length - 1 &&
                  message.role === 'user' &&
                  !aiChatStore.getCurSession?.isLoading
                "
              >
                <el-button size="small" text @click="retry(index)">
                  <el-icon><Refresh /></el-icon>
                  重试
                </el-button>
              </div>
            </div>
          </div>
          <!-- AI思考状态,三个小点-->
          <div
            v-if="aiChatStore.getCurSession?.isLoading"
            class="message ai thinking"
          >
            <div class="message-avatar">
              <el-avatar :size="50" :src="'@/assets/呆唯.png'"> </el-avatar>
            </div>
            <div class="message-content">
              <div class="message-text">
                <div class="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入框区域 -->
    <div class="chat-input">
      <div class="input-container">
        <div class="input-wrapper">
          <textarea
            class="message-input"
            placeholder="选择聊天或新建聊天才能提问哦!"
            rows="1"
            v-if="!aiChatStore.getCurSession"
            readonly
          ></textarea>
          <textarea
            class="message-input"
            placeholder="等数据更新完成就可以聊天啦!"
            rows="1"
            v-else-if="aiChatStore.getCurSession?.isWaiting"
            readonly
          ></textarea>



          <textarea
            v-else
            v-model="inputMessage"
            class="message-input"
            placeholder="发送消息..."
            @keydown="handleKeyDown"
            
            ref="messageInput"
            rows="1"
          ></textarea>

          <button
            class="send-button"
            :disabled=
              "!inputMessage.trim() || aiChatStore.getCurSession?.isLoading
              || aiChatStore.getCurMesList[aiChatStore.getCurMesList?.length - 1]?.isWaiting
              || aiChatStore.getCurMesList[aiChatStore.getCurMesList?.length - 1]?.role === 'user'
            "
            
            @click="handleSendMessage"
          >
            <svg
              v-if="!aiChatStore.getCurSession?.isLoading"
              class="send-icon"
              viewBox="0 0 24 24"
              fill="none"
            >
              <path
                d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"
                fill="currentColor"
              />
            </svg>
            <div v-else class="loading-spinner"></div>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, watch } from "vue";
import { ElMessage, ElMessageBox, ElNotification } from "element-plus";
import { CopyDocument, Refresh } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/userStore";
import { useAIChatStore } from "@/stores/aiChatStore";
import type { ChatMessage } from "@/api/chatMessage";
import chatMessage from "@/api/chatMessage";
import session, { type Session } from "@/api/session";
import SiderBar from "./SiderBar.vue";
import Loading from "./Loading.vue";
import HeaderWave from "./HeaderWave.vue";
//初始化
// 组件挂载后滚动到底部
const waitSessionList=ref(true)
onMounted(async () => {
  await aiChatStore.fetchSessionList();
  waitSessionList.value=false
  scrollToBottom();
});
// 响应式数据
const inputMessage = ref("");
const isWaitingForAI = ref(false);
const messagesContainer = ref<HTMLElement>();
const messageInput = ref<HTMLTextAreaElement>();
const userStore = useUserStore();
const aiChatStore = useAIChatStore();

//更新会话
const setIndex = async (index: number) => {
  await aiChatStore.setCurSession(index);
  await aiChatStore.switchSession(index);
  scrollToBottom();
};

//newChat,新建会话
const isAdding = ref(false);
const newChat = async () => {
  //防抖动
  if (isAdding.value) return;
  isAdding.value = true;

  //实时更新
  aiChatStore.addSession({
    title: "New Chat",
    id: -1,
    userId: userStore.user?.id as number,
    createdTime: getTimeArray(new Date()),
    isLoading: false,
    isWaiting: true,
  });
  aiChatStore.setCurSession(aiChatStore.getSessionList.length - 1);
  //在all中添加空的当前会话
  //回滚
  try {
    const res = await session.addSession({
      title: "New Chat",
      id: -1,
      userId: userStore.user?.id as number,
      createdTime: [],
      isLoading: false,
      isWaiting: true,
    });
    if (res.data.code === 1) {
      aiChatStore.getSessionList[aiChatStore.getSessionList.length - 1].id =
        res.data.data;
      aiChatStore.getSessionList[
        aiChatStore.getSessionList.length - 1
      ].isWaiting = false;
      ElNotification({
        title: "成功",
        message: "新建会话成功",
        type: "success",
      });
      aiChatStore.getAllMesList[res.data.data]=[]
      aiChatStore.setCurMesList(aiChatStore.getAllMesList[aiChatStore.getCurSession?.id as number])
    }else{
      ElNotification({
        title: "失败",
        message: "新建会话失败",
        type: "error",
      });
      //数据回滚
    aiChatStore.getSessionList.splice(aiChatStore.getSessionList.length - 1, 1);
    aiChatStore.setCurSession(aiChatStore.getSessionList.length - 1);
    }
  } catch (err: any) {
    ElNotification({
      title: "失败",
      message: err.message,
      type: "error",
    });
    //数据回滚
    aiChatStore.getSessionList.splice(aiChatStore.getSessionList.length - 1, 1);
    aiChatStore.setCurSession(aiChatStore.getSessionList.length - 1);
  } finally {
    isAdding.value = false;
  }
};

//删除session
const handleSessionDo = async (id: number) => {
  const curId = aiChatStore.getCurSession?.id;
  const deleteId = aiChatStore.getSessionList[id]?.id;
  const confirm = await ElMessageBox.confirm("确定删除该会话吗？", "删除会话", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  });
  if (!confirm) {
    return;
  }
  await aiChatStore.deleteSession(deleteId as number, curId as number);
  scrollToBottom()
};

/**
 * 发送消息
 */
const handleSendMessage = async () => {
  if (!inputMessage.value.trim()) {
    return;
  }
  const curSession = ref();
  if (aiChatStore.getCurSession) curSession.value = aiChatStore.getCurSession;

  const currentMessage = inputMessage.value.trim();
  inputMessage.value = "";

  const userMessageObj: ChatMessage = {
    content: currentMessage,
    sessionId: curSession.value?.id,
    userId: userStore.user?.id as number,
    role: "user",
    isWaiting: true,
    id: -1,
  };
  curSession.value.isLoading = true;

  aiChatStore.getAllMesList[curSession.value?.id as number].push(
    userMessageObj
  );

  scrollToBottom();

  const id = await aiChatStore.addMesDto({
    content: currentMessage,
    sessionId: curSession.value?.id,
    userId: userStore.user?.id as number,
    role: "user",
    isWaiting: true,
    id: -1,
  });

  //在这里再更新id，不要等待请求完成后再addmes，有延迟
  userMessageObj.id = id;

  await nextTick();
  scrollToBottom();

  // 调用后端AI接口，流式接收回复
  await callAIStream(currentMessage, curSession.value?.id);

  //更新sessiontitle
  const res = await session.updateSessionTitle({
    sessionId: curSession.value?.id,
    title: currentMessage,
  });
  if (res.data.code === 1) {
    //根据返回结果的id和title更新,循环匹配id
    for (let i = 0; i < aiChatStore.getSessionList.length; i++) {
      if (aiChatStore.getSessionList[i].id === res.data.data.sessionId) {
        aiChatStore.getSessionList[i].title = res.data.data.title;
      }
    }
  }
};

//调用后端AI接口，流式接收回复
const callAIStream = async (userMessage: string, sessionId: number) => {
  try {
    // 创建AI消息对象，初始内容为空
    const aiMessage: ChatMessage = {
      role: "ai",
      content: "",
      isWaiting: true,
      id: -1,
      sessionId: sessionId,
      userId: userStore.user?.id as number,
    };

    // 构建请求URL
    const apiUrl = `${
      import.meta.env.VITE_SERVER_URL || ""
    }/ai/chat?message=${encodeURIComponent(userMessage || "")}`;

    const response = await fetch(apiUrl, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: ` ${userStore.token}`,
      },
    });
    const reader = response?.body?.getReader();
    const decoder = new TextDecoder();
    let done = false;
    let result = "";

    for (let i = 0; i < aiChatStore.getSessionList.length; i++) {
      if (aiChatStore.getSessionList[i].id === sessionId) {
        aiChatStore.getSessionList[i].isLoading = false;
      }
    }

    aiChatStore.getAllMesList[sessionId as number].push(aiMessage);

    while (!done) {
      const readResult = await reader?.read();
      const value = readResult?.value;
      const readerDone = readResult?.done || false;
      done = readerDone;
      result += decoder.decode(value, { stream: true });

      // 每次接收到流的数据，更新最后的 AI 信息
      aiChatStore.getAllMesList[sessionId as number][
        aiChatStore.getAllMesList[sessionId as number].length - 1
      ].content = result;

      scrollToBottom();
    }

    // 最终更新和滚动
    await nextTick();
    scrollToBottom();
    aiChatStore.getAllMesList[sessionId as number][
      aiChatStore.getAllMesList[sessionId as number].length - 1
    ].isWaiting = false;
    const id = await aiChatStore.addMesDto({
      id: -1,
      content: result,
      sessionId: sessionId,
      userId: userStore.user?.id as number,
      role: "ai",
      isWaiting: false,
    });
    // aiChatStore.getCurMesList[aiChatStore.getCurMesList?.length - 1].id = id;
    // aiChatStore.getCurMesList[aiChatStore.getCurMesList?.length - 1].isWaiting =
    //   false;
    aiChatStore.getAllMesList[sessionId as number][
      aiChatStore.getAllMesList[sessionId as number].length - 1
    ].id = id;
  } catch (error) {
    console.error("AI接口调用失败:", error);

    // 如果失败，显示错误消息
    const errorMessage: ChatMessage = {
      role: "ai",
      content: "抱歉，AI服务暂时不可用，请稍后重试。",
      isWaiting: false,
      id: -1,
      sessionId: sessionId,
      userId: userStore.user?.id as number,
    };

    aiChatStore.getAllMesList[sessionId as number].push(errorMessage);
    isWaitingForAI.value = false;
    for (let i = 0; i < aiChatStore.getSessionList.length; i++) {
      if (aiChatStore.getSessionList[i].id === sessionId) {
        aiChatStore.getSessionList[i].isLoading = false;
      }
    }
    // 滚动到底部
    await nextTick();
    scrollToBottom();
  }
};

//重新生成回复
const regenerateResponse = async (messageId: number, index: number) => {
  const userMessage = aiChatStore.getCurMesList[index - 1].content;
  // 删除当前的AI回复
  aiChatStore.getCurMesList.splice(index, 1);

  for (let i = 0; i < aiChatStore.getSessionList.length; i++) {
    if (aiChatStore.getSessionList[i].id === aiChatStore.getCurSession?.id) {
      aiChatStore.getSessionList[i].isLoading = true;
    }
  }

  scrollToBottom();
  await callAIStream(userMessage, aiChatStore.getCurSession?.id as number);
  await chatMessage.deleteMsg(messageId);
};

const retry = async (index: number) => {
  const userMessage = aiChatStore.getCurMesList[index].content;

  for (let i = 0; i < aiChatStore.getSessionList.length; i++) {
    if (aiChatStore.getSessionList[i].id === aiChatStore.getCurSession?.id) {
      aiChatStore.getSessionList[i].isLoading = true;
    }
  }

  scrollToBottom();
  await callAIStream(userMessage, aiChatStore.getCurSession?.id as number);
};

//滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    console.log(messagesContainer.value.scrollHeight);
  }
};

//格式化消息内容
const formatMessage = (content: string): string => {
  return content
    .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>")
    .replace(/\*(.*?)\*/g, "<em>$1</em>")
    .replace(/`(.*?)`/g, "<code>$1</code>")
    .replace(/\n/g, "<br>");
};

// 监听消息变化，自动滚动
watch(()=>aiChatStore.getCurMesList.length, async () => {
  await nextTick();
    scrollToBottom();
});

watch(()=>aiChatStore.getSessionList.length, async () => {
  await nextTick();
  scrollToBottom()
  
})

// 监听输入内容变化，自动调整高度
watch(inputMessage, () => {
  nextTick(() => {
    if (messageInput.value) {
      messageInput.value.style.height = "auto";
      messageInput.value.style.height =
        Math.min(messageInput.value.scrollHeight, 200) + "px";
    }
  });
});

//处理键盘回车
const handleKeyDown = (event: KeyboardEvent) => {
  if(  aiChatStore.getCurMesList?.length > 0 &&(aiChatStore.getCurMesList[aiChatStore.getCurMesList?.length - 1].isWaiting  //信息正在输出
    || aiChatStore.getCurMesList[aiChatStore.getCurMesList?.length - 1].role === "user" //重试的情况
    ||aiChatStore.getCurSession?.isLoading)  
  ) {
    return
  }
  if (event.key === "Enter" && !event.shiftKey) {
    event.preventDefault();
    handleSendMessage();
  }
  // 自动调整输入框高度
  nextTick(() => {
    if (messageInput.value) {
      messageInput.value.style.height = "auto";
      messageInput.value.style.height = messageInput.value.scrollHeight + "px";
    }
  });
};

//复制消息内容
const copyMessage = async (content: string) => {
  try {
    await navigator.clipboard.writeText(content);
    ElMessage.success("消息已复制到剪贴板");
  } catch (err) {
    ElMessage.error("复制失败，请手动复制");
  }
};

// 工具函数
function getTimeArray(date: Date): number[] {
  const year = date.getFullYear();
  const month = date.getMonth() + 1; // 月份从 0 开始，所以需要加 1
  const day = date.getDate();
  const hours = date.getHours();
  const minutes = date.getMinutes();
  const seconds = date.getSeconds();
  return [year, month, day, hours, minutes, seconds];
}

function formatCreatedTime(timeArray: number[]): string {
  if (!Array.isArray(timeArray) || timeArray.length < 5) {
    return "";
  }
  const month = timeArray[1];
  const day = timeArray[2];
  const hour = timeArray[3].toString().padStart(2, "0");
  const minute = timeArray[4].toString().padStart(2, "0");

  return `${month}-${day} ${hour}:${minute}`;
}
</script>

<style scoped lang="scss">
/* 当前会话高亮 */
.session-item.activeItem {
  background-color: #e6f7ff; /* 淡蓝色背景 */
  border-left: 4px solid #1890ff; /* 左侧蓝色标记 */
}
.ai-chat-container {
  width: 100%;
  // position: relative;
  background-color: white;
}

.chat-header {
  background: white;
  padding: 20px;
  border-bottom: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.chat-title {
  text-align: center;
}

.chat-title h2 {
  margin: 0 0 8px 0;
  color: #1e293b;
  font-size: 28px;
  font-weight: 700;
}

.chat-title p {
  margin: 0;
  color: #64748b;
  font-size: 16px;
}
.chat {
  display: flex;
  height: 80vh;
}

.chat-messages {
  position: relative;
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  margin: 2px;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.welcome-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  min-height: 500px;
  padding: 40px 20px;
}

.welcome-content {
  /* background-color: #3b82f6; */
  text-align: center;
  max-width: 600px;
  width: 100%;
}

.welcome-icon {
  margin-bottom: 32px;
}

.ai-icon {
  width: 80px;
  height: 80px;
  opacity: 0.9;
}

.welcome-title {
  font-size: 50px;
  font-weight: 100;
  color: #111827;
  margin: 0 0 16px 0;
  letter-spacing: -0.025em;
  line-height: 1.1;
}

.welcome-subtitle {
  font-size: 20px;
  color: #6b7280;
  margin: 0 0 48px 0;
  font-weight: 400;
  letter-spacing: -0.01em;
}

.welcome-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 24px;
  margin: 0 0 48px 0;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.feature-icon {
  font-size: 32px;
  line-height: 1;
}

.feature-text {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
  letter-spacing: 0.025em;
}

.welcome-tip {
  font-size: 16px;
  color: #9ca3af;
  margin: 0;
  font-weight: 400;
  line-height: 1.5;
}

.message {
  display: flex;
  margin-bottom: 32px;
  animation: fadeInUp 0.4s ease-out;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  margin: 0 20px;
}
.mes {
  display: flex;
  flex-direction: column;
}
.message-content {
  max-width: 70%;
  // background: #f8fafc;
  border-radius: 18px;
  padding: 15px 20px;
  border: 1px solid #e2e8f0;
  // display: contents;
}

.message.user .message-content {
  background: #f9f1fe;
  color: white;
  border: 1px solid #e5e7eb;
}

.message.ai .message-content {
  background: #edf5f7;
  border: 1px solid #e2e8f0;
}

.message.thinking .message-content {
  background: #edf5f7;
  border: 1px solid #bae6fd;
}

.message.waiting .message-content {
  background: #edf5f7;
  border: 1px solid #d1d5db;
  opacity: 0.8;
}

.waiting-content {
  position: relative;
}

.waiting-indicator {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
  font-size: 14px;
}

.thinking-dots {
  display: flex;
  gap: 4px;
  align-items: center;
}

.thinking-dots span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: #9ca3af;
  animation: thinking 1.4s infinite ease-in-out;
}

.thinking-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.thinking-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes thinking {
  0%,
  80%,
  100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 移除消息头部样式，因为不再需要 */

.message-text {
  line-height: 1.7;
  word-wrap: break-word;
  font-size: 16px;
}

.message.user .message-text {
  color: #374151;
}

.message-actions {
  margin-top: 5px;
  margin-left: 7px;
  display: flex;
  text-align: center;
  justify-content: left;
}

.typing-indicator {
  display: flex;
  gap: 6px;
  align-items: center;
}

.typing-indicator span {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #3b82f6;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%,
  80%,
  100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.chat-input {
  background: white;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
  // position: sticky;
  bottom: 0;
}

.input-container {
  max-width: 768px;
  margin: 0 auto;
}

.input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 24px;
  padding: 8px 8px 8px 16px;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.input-wrapper:focus-within {
  border-color: #e4d3ee;
  box-shadow: 0 0 0 1px #e4d3ee, 0 2px 8px rgba(0, 0, 0, 0.1);
}

.message-input {
  flex: 1;
  min-height: 24px;
  max-height: 120px;
  padding: 8px 0;
  border: none;
  outline: none;
  resize: none;
  font-family: inherit;
  font-size: 16px;
  line-height: 1.5;
  color: #374151;
  background: transparent;
  overflow-y: auto;
}

.message-input::placeholder {
  color: #9ca3af;
  font-style: normal;
}

.send-button {
  background: #e4d3ee;
  color: white;
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  padding: 0;
  /* 设置动画？ */
  transition: all 0.7s;
}

.send-button:hover:not(:disabled) {
  background: #e4d3ee;
  transform: scale(1.2);
}

.send-button:active:not(:disabled) {
  transform: scale(0.95);
}

.send-button:disabled {
  background: #d1d5db;
  cursor: not-allowed;
  transform: none;
}

.send-icon {
  width: 16px;
  height: 16px;
}

.loading-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 滚动条样式 */
.chat-messages {
  overflow-y: scroll;
  scrollbar-width: none; /* 对于 Firefox */
  -ms-overflow-style: none; /* 对于 IE 和 Edge */
}

.chat-messages::-webkit-scrollbar {
  display: none; /* 对于 Chrome、Safari 和 Opera */
}
.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .message-content {
    max-width: 85%;
  }

  .chat-header {
    padding: 15px;
  }

  .chat-messages {
    padding: 15px;
    margin: 15px;
  }

  .chat-input {
    padding: 16px 20px;
  }

  .input-wrapper {
    border-radius: 20px;
    padding: 6px 6px 6px 14px;
  }

  .message-input {
    min-height: 20px;
    font-size: 15px;
  }

  .send-button {
    width: 28px;
    height: 28px;
  }

  .send-icon {
    width: 14px;
    height: 14px;
  }

  .loading-spinner {
    width: 12px;
    height: 12px;
  }

  .welcome-message {
    min-height: 400px;
    padding: 20px 16px;
  }

  .welcome-title {
    font-size: 36px;
  }

  .welcome-subtitle {
    font-size: 18px;
    margin-bottom: 32px;
  }

  .welcome-features {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    margin-bottom: 32px;
  }

  .feature-icon {
    font-size: 28px;
  }

  .feature-text {
    font-size: 13px;
  }

  .welcome-tip {
    font-size: 15px;
  }

  .chat-title h2 {
    font-size: 24px;
  }
}

.session-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  /* background-color: #f5f5f7; 背景色 */
  background-color: white;
  /* border-radius: 12px; */
  /* max-height: 400px; */
  overflow-y: auto;
  width: 280px;
}

/* 滚动条美化 */
.session-list::-webkit-scrollbar {
  width: 6px;
}
.session-list::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.session-item {
  background-color: #fff;
  padding: 12px 16px;
  border-radius: 5px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.2s ease;
  justify-content: space-between;
  align-items: center;
}

.session-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.session-title {
  font-weight: 600;
  font-size: 18px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  /* max-width: 70%; */
}

.session-time {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
  margin-top: 10px;
  /* margin-left: 150px; */
}
.new-chat-button {
  /* display: flex; */
  align-items: center;
  padding: 10px 20px;
  background-color: #1e293b; /* ChatGPT 官网的绿色 */
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 50px;
}

.new-chat-button:hover {
  background-color: #000;
}

.new-chat-button .text {
  margin-left: 10px;
  font-size: 20px;
}

.session-do {
  display: none;
}
.session-item:hover .session-do {
  display: block;
}
.bottom {
  display: flex;
  justify-content: space-between;
}
.my-CirclePlus {
  font-size: 30px;
}

.session-top {
  position: sticky;
  top: 0;
  width: 100%;
  height: 50px;
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  p {
    font-size: 20px;
  }
  color: #777;
}
</style>
