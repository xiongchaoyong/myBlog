<template>
  <Nav />
  <SiderBar></SiderBar>

  <div v-if="loading" class="chat">
    <Loading :show="loading" text="数据正在赶来的路上,请稍等!" :mask="true"> </Loading>>
  </div>
  <div class="chat" v-else>
    <div class="chatMes-container">
      <div class="change">
        <div
          :class="['switch-btn', 'privateMes', { active: isPrivateChat }]"
          @click="toPrivateMes"
        >
          私信
        </div>
        <div
          :class="['switch-btn', 'groupMes', { active: isGroupChat }]"
          @click="toGroupChat"
        >
          群聊
        </div>
      </div>
      <div
            v-if="
              messageStore.getMesList.length === 0    &&   isPrivateChat      "
            class="empty-notice"
          >
            <el-icon class="empty-icon">
              <Bell />
            </el-icon>
            <p>暂无私信</p>
          </div>
      <el-scrollbar max-height="calc(100vh - 100px)" v-if="isPrivateChat">
        <el-timeline>
          <el-timeline-item
            v-for="(message, index) in messageStore.getMesList"
            :key="index"
            :timestamp="formatDate(message.createdAt)"
            placement="top"
            @click.native="
              messageStore.setCurChatUserAndIndex(
                message.senderId === loginStore.user.id
                  ? message.receiverId
                  : message.senderId,
                index
              ) 
            "
          >
            <el-card
              :class="{
                'unread-message': !message.isRead,
                'selected-message': index === messageStore.getCurMesListIndex,
              }"
            >
              <div class="message">
                <div class="avatar">
                  <Avatar
                    :avatar="message.avatar"
                    :username="message.username || '用户'"
                    :style="{ width: '50px', height: '50px', fontSize: '20px' }"
                  />
                </div>
                <div class="content">
                  <div class="info">
                    <span class="name">{{ message.username }}</span>
                    <span class="time">{{
                      formatDate(message.createdAt, "time")
                    }}</span>
                  </div>
                  <div class="text">
                    {{
                      message.senderId === loginStore.user.id
                        ? ""
                        : message.username + ": "
                    }}{{ message.content }}
                  </div>
                </div>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-scrollbar>

      <el-scrollbar max-height="calc(100vh - 100px)" v-if="isGroupChat">
        <div
            v-if="
              groupMessageStore.getMesList.length === 0    &&   isGroupChat      "
            class="empty-notice"
          >
            <el-icon class="empty-icon">
              <Bell />
            </el-icon>
            <p>暂无群聊</p>
          </div>
        <el-timeline>
          <el-timeline-item
            v-for="(message, index) in groupMessageStore.getMesList"
            :key="index"
            :timestamp="formatDate(message.messageTime)"
            placement="top"
            @click.native="groupMessageStore.setCurMesListIndex(index)"
          >
            <el-card
              :class="{
                // 'unread-message': !message.isRead,
                'selected-message':
                  index === groupMessageStore.getCurMesListIndex,
              }"
            >
              <div class="message">
                <div class="avatar">
                  <img :src="message.groupAvatar" alt="avatar" />
                </div>
                <div class="content">
                  <div class="info">
                    <span class="name">{{ message.groupName }}</span>
                    <span class="time">{{
                      formatDate(message.messageTime, "time")
                    }}</span>
                  </div>
                  <div class="text">
                    {{
                      message.senderId === loginStore.user.id
                        ? ""
                        : message.senderName + ": "
                    }}{{ message.content }}
                  </div>
                </div>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-scrollbar>
    </div>
    <div class="chatContainer" v-if="isPrivateChat">
      <!-- 右侧聊天区域 -->
      <div class="chat-header">
        <span>{{ messageStore.getCurChatUser?.username || "聊天" }}</span>
        <div class="header-controls">
          <span class="control-label">背景</span>
          <el-switch v-model="isBgImageEnabled" />
        </div>
      </div>
      <div class="chat-messages-area" :style="chatAreaStyle">
        <div v-if="messageStore.getLoadingCurMessage" >
          <Loading :show="messageStore.getLoadingCurMessage" text="数据正在来的路上,请耐心等待哦!" ></Loading>
        </div>
        <el-scrollbar v-else ref="messageScrollbar" max-height="calc(100vh - 200px)" >
          <div class="chat-messages">
            <div
              v-for="(message, index) in messageStore.getCurMesList"
              :key="index"
              class="chat-message"
              :class="{
                'self-message': message.senderId === loginStore.user.id,
              }"
            >
              <div class="message-bubble">
                <div class="message-avatar">
                  <Avatar
                    :avatar="
                      message.senderId === loginStore.user.id
                        ? loginStore.user.avatar
                        : messageStore.getCurChatUser?.avatar
                    "
                    :username="
                      message.senderId === loginStore.user.id
                        ? loginStore.user.username
                        : messageStore.getCurChatUser?.username || '用户'
                    "
                  />
                </div>
                <div class="message-content">
                  <div class="message-time">
                    {{ formatDate(message.createdAt, "datetime") }}
                  </div>
                  <div class="message-text">{{ message.content }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-scrollbar>
      </div>
      <div class="chat-input-area">
        <el-row align="middle">
          <el-col :span="21">
            <el-input
              v-model="messageInput"
              :autosize="{ minRows: 1, maxRows: 4 }"
              type="textarea"
              placeholder="请输入消息..."
              resize="none"
              @keyup.enter.native="sendMessage"
            />
          </el-col>
          <el-col :span="3" style="text-align: right">
            <el-button type="primary" @click="sendMessage">发送</el-button>
          </el-col>
        </el-row>
      </div>
    </div>

    <div class="chatContainer" v-if="isGroupChat">
      <!-- 右侧聊天区域 -->
      <div class="chat-header">
        <span>{{ groupMessageStore.getCurGroup?.groupName || "群聊" }}</span>
        <div class="header-controls">
          <span class="control-label">背景</span>
          <el-switch v-model="isBgImageEnabled" />
        </div>
      </div>
      <div class="chat-messages-area" :style="chatAreaStyle">
        <el-scrollbar ref="messageScrollbar" max-height="calc(100vh - 200px)">
          <div class="chat-messages">
            <div
              v-for="(message, index) in groupMessageStore.getCurMesList"
              :key="index"
              class="chat-message"
              :class="{
                'self-message': message.senderId === loginStore.user.id,
              }"
            >
              <div class="message-bubble">
                <div class="message-avatar">
                  <Avatar
                    :avatar="
                      message.senderId === loginStore.user.id
                        ? loginStore.user.avatar
                        : message.senderAvatar
                    "
                    :username="
                      message.senderId === loginStore.user.id
                        ? loginStore.user.username
                        : message.senderName || '用户'
                    "
                  />
                </div>
                <div class="message-content">
                  <div class="message-time">
                    {{ formatDate(message.messageTime, "datetime") }}
                  </div>
                  <div class="message-text">{{ message.content }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-scrollbar>
      </div>
      <div class="chat-input-area">
        <el-row align="middle">
          <el-col :span="21">
            <el-input
              v-model="messageInput"
              :autosize="{ minRows: 1, maxRows: 4 }"
              type="textarea"
              placeholder="请输入消息..."
              resize="none"
              @keyup.enter.native="sendGroupMessage"
            />
          </el-col>
          <el-col :span="3" style="text-align: right">
            <el-button type="primary" @click="sendGroupMessage">发送</el-button>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, watch, onUnmounted, computed } from "vue";
import { ElScrollbar, ElInput, ElButton, ElRow, ElCol } from "element-plus";
import { ElNotification } from "element-plus";
import { CloseBold } from "@element-plus/icons-vue";
import PrivateMessage from "@/api/PrivateMessage";
import { useUserStore } from "@/stores/userStore";
import { useMessageStore } from "@/stores/messageStore";
import { useWsChatStore } from "@/stores/wsChatStore";
import formatDate from "@/utils/timeFormate";
import { useGroupMessageStore } from "@/stores/groupMessageStore";
import GroupMessage from "@/api/GroupMessage";
import Avatar from "./Avatar.vue";
import Nav from "./Nav.vue";
import SiderBar from "@/components/SiderBar.vue";
import Loading from "@/components/Loading.vue";

// 创建store实例
const loginStore = useUserStore();
const messageStore = useMessageStore();
const wsChatStore = useWsChatStore();
const groupMessageStore = useGroupMessageStore();

const messageInput = ref("");
const messageScrollbar = ref(null);
const isBgImageEnabled = ref(false);

const chatAreaStyle = computed(() => {
  if (loginStore.user?.cover && isBgImageEnabled.value) {
    return {
      "--chat-bg-image": `url(${loginStore.user.cover})`,
    };
  }
  return {};
});



//实现私信和群聊切换
const isPrivateChat = ref(true);
const isGroupChat = ref(false);
const toPrivateMes = () => {
  isPrivateChat.value = true;
  isGroupChat.value = false;
};
const toGroupChat = () => {
  isPrivateChat.value = false;
  isGroupChat.value = true;
};

/**
 * 发送消息函数
 */
const sendMessage = async () => {
  if (messageInput.value.trim() === "") {
    ElNotification({
      type: "warning",
      title: "warning",
      message: "信息不能为空!",
      closeIcon: CloseBold,
    });
    return;
  }

  const newMessage = {
    content: messageInput.value,
    createdAt: Date.now(), //时间戳
    senderId: loginStore.user.id,
    receiverId: messageStore.getCurChatUser?.id,
  };
  //ws

  const mes = {
    ...newMessage,
    avatar: loginStore.user.avatar,
    username: loginStore.user.username,
    type: "private",
  };
  wsChatStore.sendMessage(mes);

  // 添加到当前消息列表
  messageStore.addMesListAll(newMessage);
  messageStore.updateMesList(newMessage);
  //存入数据库
  await PrivateMessage.sendMessage(newMessage);
  messageInput.value = "";
  // 发送消息后直接跳转到底部
  instantJumpToBottom();
};
//发送群聊信息
const sendGroupMessage = async () => {
  if (messageInput.value.trim() === "") {
    ElNotification({
      type: "warning",
      title: "warning",
      message: "信息不能为空!",
      closeIcon: CloseBold,
    });
    return;
  }

  const newMessage = {
    content: messageInput.value,
    senderId: loginStore.user.id,
    groupId: groupMessageStore.getCurGroup.groupId,
  };

  //ws

  const mes = {
    ...newMessage,
    senderAvatar: loginStore.user.avatar,
    senderName: loginStore.user.username,
    type: "group",
    messageTime: Date.now(),
  };
  wsChatStore.sendMessage(mes);
  //存入数据库
  await GroupMessage.sendGreetMessage(newMessage);
  groupMessageStore.updateMesList({
    ...newMessage,
    messageTime: Date.now(),
  });
  messageInput.value = "";
  // 发送消息后直接跳转到底部
  instantJumpToBottom();
};
const loading = ref(true);
onMounted(async () => {
  if (
    !messageStore.getMesListAll &&
    !messageStore.getMesList &&
    !groupMessageStore.getMesListAll &&
    !groupMessageStore.getMesList
  ) {
    loading.value = true;
    // 获取所有消息列表
   await messageStore.fetchMesListAll();
    // 获取当前聊天用户的消息列表
   await messageStore.fetchMesList();
    // 初始加载后直接跳转到底部
    instantJumpToBottom();
   await groupMessageStore.fetchMesList();
   await groupMessageStore.fetchMesListAll();
    // 连接WebSocket
    wsChatStore.connect(loginStore.user.id);
  }
  loading.value = false;
});

onUnmounted(() => {
  wsChatStore.disconnect();
});

//群聊数据处理

/**
 * 滚动到底部的函数
 * @param {boolean} smooth - 是否使用平滑滚动，默认为false（直接跳转）
 */
const scrollToBottom = (smooth = false) => {
  nextTick(() => {
    setTimeout(() => {
      if (messageScrollbar.value) {
        const scrollbar = messageScrollbar.value;
        const scrollContainer = scrollbar.$el.querySelector(
          ".el-scrollbar__wrap"
        );
        if (scrollContainer) {
          // 直接跳转到底部，不使用平滑滚动
          scrollContainer.scrollTo({
            top: scrollContainer.scrollHeight,
            behavior: smooth ? "smooth" : "auto",
          });
        }
      }
    }, 100); // 添加100ms延迟确保DOM完全更新
  });
};

/**
 * 最直接的方式跳转到底部（使用scrollTop属性）
 */
const instantJumpToBottom = () => {
  nextTick(() => {
    if (messageScrollbar.value) {
      const scrollbar = messageScrollbar.value;
      const scrollContainer = scrollbar.$el.querySelector(
        ".el-scrollbar__wrap"
      );
      if (scrollContainer) {
        // 直接设置scrollTop，最快速的方式
        scrollContainer.scrollTop = scrollContainer.scrollHeight;
      }
    }
  });
};
/**
 * 平滑滚动到底部
 */
const smoothScrollToBottom = () => {
  scrollToBottom(true);
};

/**
 * 监听当前聊天消息列表的变化，自动滚动到底部
 */
watch(
  [
    () => messageStore.getCurChatUser,
    () => messageStore.getMesListAll,
    () => groupMessageStore.getCurGroup,
    groupMessageStore.getMesListAll,
  ],
  () => {
    instantJumpToBottom(); // 使用最直接的方式跳转到底部
  },
  { deep: true }
);
</script>

<style scoped lang="scss">
.chat {
  display: flex;
  height: 100%;
  margin-left: 60px;
}

.empty-notice {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;

  .empty-icon {
    font-size: 48px;
    margin-bottom: 10px;
  }

  p {
    margin: 0;
    font-size: 14px;
  }
}

.chatContainer {
  height: 92.5vh;
  width: 70%;
  display: flex;
  flex-direction: column;
  border: 1px solid #ebeef5;
  background-color: #fff;
}

.chatMes-container {
  padding: 20px;
  // background-color: rebeccapurple
  display: flex;
  flex-direction: column;
  height: 92.5vh;
  /* Or a fixed height for the chat area */
  width: 30%;
  /* Constrain the overall chat area width */
  border: 1px solid #ebeef5;
  /* Add a border to visualize the chat area */
}

.el-scrollbar {
  flex: 1;
  /* Make scrollbar take available vertical space */
  overflow-x: hidden;
  /* Hide horizontal scrollbar if content overflows */
}

.message {
  display: flex;
  overflow: hidden;
  /* Ensure content inside message is clipped */

  .avatar {
    margin-right: 15px;

    img {
      width: 50px;
      height: 50px;
      border-radius: 50%;
    }
  }

  .content {
    flex: 1;
    min-width: 0;
    overflow: hidden;

    .info {
      display: flex;
      align-items: baseline;
      margin-bottom: 8px;

      .name {
        font-weight: bold;
        font-size: 16px;
        margin-right: 12px;
      }

      .time {
        font-size: 14px;
        color: #999;
      }
    }

    .text {
      background: #f0f0f0;
      padding: 8px 12px;
      border-radius: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      /* 单行截断 */
      max-width: 100%;
      font-size: 14px;
      line-height: 1.2;
      box-sizing: border-box;
    }
  }
}

.el-card {
  width: 80%;
  /* 恢复到最初的宽度 */
  border-radius: 12px !important;
  /* 恢复到 Element Plus 默认或更简单的圆角 */
  position: relative;
  /* 为小红点定位做准备 */
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}

/* 选中消息的样式 */
.selected-message {
  border: 2px solid #409eff !important;
  background-color: #f0f9ff !important;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2) !important;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(64, 158, 255, 0.3) !important;
  }
}

/* 未读消息小红点样式 */
.unread-message::before {
  content: "";
  position: absolute;
  top: 8px;
  right: 8px;
  width: 10px;
  height: 10px;
  background-color: #ff4757;
  border-radius: 50%;
  z-index: 1;
}

.chat-header {
  position: relative;
  padding: 15px;
  text-align: center;
  font-weight: bold;
  font-size: 18px;
  border-bottom: 1px solid #ebeef5;
  background-color: #f8f9fa;
}

.header-controls {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  gap: 8px;
}

.control-label {
  font-size: 14px;
  font-weight: normal;
  color: #606266;
}

.chat-messages-area {
  flex: 1;
  overflow: hidden;
  padding: 15px;
  position: relative;
  isolation: isolate;
  /* Create a new stacking context */
}

.chat-messages-area::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: var(--chat-bg-image);
  background-size: cover;
  background-position: center;
  filter: blur(2px) brightness(0.85);
  z-index: -1;
}

/* 隐藏滚动条 */
.chat-messages-area :deep(.el-scrollbar__bar) {
  display: none;
}

.chat-messages-area :deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}

.chat-messages-area :deep(.el-scrollbar__view) {
  overflow-x: hidden;
}

.chat-messages {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.chat-message {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
}

.chat-message.self-message {
  justify-content: flex-end;
}

.message-bubble {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  max-width: 70%;
}

.self-message .message-bubble {
  flex-direction: row-reverse;
  justify-content: flex-end;
}

.message-avatar {
  flex-shrink: 0;
}

.message-avatar img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 2px solid #e0e0e0;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-time {
  font-size: 14px;
  color: #999;
  margin-bottom: 4px;
}

.self-message .message-time {
  text-align: right;
}

.message-text {
  background-color: #f0f0f0;
  padding: 12px 18px;
  border-radius: 12px;
  font-size: 18px;
  line-height: 1.6;
  color: #333;
  word-wrap: break-word;
  max-width: 100%;
  box-sizing: border-box;
  display: inline-block;
}

.self-message .message-text {
  background-color: #007bff;
  color: #fff;
}

.chat-input-area {
  padding: 15px;
  border-top: 1px solid #ebeef5;
  background-color: #f8f9fa;
}

// 切换按钮美化
.change {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 18px;
  gap: 0;
  width: 100%;
}

.switch-btn {
  flex: 1;
  padding: 12px 0;
  border-radius: 0;
  background: #f4f4f4;
  color: #666;
  font-size: 18px;
  font-weight: 500;
  cursor: pointer;
  border: 1.5px solid #e0e0e0;
  border-right: none;
  transition: all 0.2s;
  box-shadow: none;
  user-select: none;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.switch-btn:last-child {
  border-right: 1.5px solid #e0e0e0;
}

.switch-btn:hover {
  background: #e6f0ff;
  color: #409eff;
  border-color: #b3d8ff;
}

.switch-btn.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
  box-shadow: none;
}
</style>
