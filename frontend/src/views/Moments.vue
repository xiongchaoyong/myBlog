<template>
  <HeaderWave text="动态" />
  <SiderBar />
  <div class="moments">
    <div v-if="isLoading">
      <Loading :show="isLoading" text="请稍等哦!" :mask="true"></Loading>
    </div>
    <!-- 朋友圈封面区域 -->
    <!-- <div class="cover-section">
      <div class="cover">
        <el-image
          :src="loginStore.user?.cover || defaultCover"
          fit="cover"
          class="cover-image"
        />

        
    </div> -->

    <div class="publish-btn" v-if="loginStore.isLoggedIn && !isLoadingPosts">
      <el-button type="primary" @click="showPublishDialog = true">
        <el-icon>
          <Edit />
        </el-icon>
        发布
      </el-button>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div v-if="isLoadingPosts" class="moments-list">
        <Loading :show="isLoadingPosts" text="动态正在加载哦!"></Loading>
      </div>
      <!-- 动态列表 -->
      <div class="moments-list" v-else>
        <div
          v-if="postStore.getMonentList?.length === 0 && !isLoadingPosts"
          class="empty-moments"
        >
          <el-icon class="empty-icon">
            <ChatDotRound />
          </el-icon>
          <p>还没有动态，快来发布第一条吧！</p>
        </div>

        <div v-else class="moments-container">
          <div
            v-for="(moment, index) in postStore.getMonentList"
            :key="moment.id"
            class="moment-item"
          >
            <!-- 用户头像 -->
            <div class="moment-avatar">
              <Avatar
                :avatar="moment.author?.avatar"
                :username="moment.author?.username || '用户'"
                :style="{ width: '50px', height: '50px', fontSize: '20px' }"
              />
            </div>

            <!-- 动态内容 -->
            <div class="moment-content">
              <!-- 用户信息 -->
              <div class="moment-header">
                <span class="username">{{ moment?.author?.username }}</span>
                <span class="time">{{ moment?.createdAt }}</span>
              </div>

              <!-- 动态文字 -->
              <div v-if="moment.content" class="moment-text">
                {{ moment.content }}
              </div>

              <!-- 动态图片 -->
              <div
                v-if="moment.imageUrls && moment.imageUrls.length > 0"
                class="moment-images"
              >
                <div
                  v-for="(image, index) in moment.imageUrls.slice(0, 9)"
                  :key="index"
                  class="image-item"
                >
                  <el-image
                    :src="image"
                    fit="cover"
                    :preview-src-list="moment.imageUrls"
                    :initial-index="index"
                    preview-teleported
                    hide-on-click-modal
                  />
                </div>
              </div>

              <!-- 动态操作 -->
              <div class="moment-actions">
                <div class="action-buttons">
                  <el-button
                    type="text"
                    size="small"
                    @click="handleLike(moment)"
                    :class="{
                      liked: moment.likes?.some(
                        (like) => like.userId === loginStore.user?.id
                      ),
                    }"
                    :loading="isDoingLike && moment.id == currentDoingLikeId"
                  >
                    <el-icon
                      :style="{
                        color: moment.likes?.some(
                          (like) => like.userId === loginStore.user?.id
                        )
                          ? '#FFBBFF'
                          : 'inherit',
                      }"
                      :size="20"
                    >
                      <Apple />
                    </el-icon>
                    {{ moment?.likes?.length || 0 }}
                  </el-button>
                  <el-button
                    type="text"
                    size="small"
                    @click="handleComment(moment)"
                  >
                    <el-icon :size="20">
                      <ChatDotRound />
                    </el-icon>
                    评论
                  </el-button>
                </div>

                <!-- 点赞列表 -->
                <div v-if="moment.likes?.length > 0" class="like-list">
                  <el-icon style="color: #ffbbff; font-size: 18px"
                    ><Apple
                  /></el-icon>
                  <span
                    v-for="like in moment.likes"
                    :key="like.user?.id"
                    style="font-size: 18px"
                  >
                    <el-tooltip
                      effect="light"
                      :content="like.user?.username || '用户'"
                      placement="top"
                    >
                      <Avatar
                        :avatar="like.user?.avatar"
                        :username="like.user?.username || '用户'"
                        :style="{
                          width: '35px',
                          height: '35px',
                          fontSize: '12px',
                          cursor: 'pointer',
                        }"
                      />
                    </el-tooltip>
                  </span>
                </div>

                <!-- 评论列表 -->
                <div v-if="moment.comments?.length > 0" class="comment-list">
                  <div
                    v-for="comment in moment.comments"
                    :key="comment.id"
                    class="comment-item"
                  >
                    <div>
                      <span class="comment-user"
                        >{{ comment.user?.username }}
                      </span>
                      <span class="comment-content"
                        >:{{ comment.content }}</span
                      >
                    </div>
                    <span class="comment-time">{{ comment.createdAt }}</span>
                  </div>
                </div>

                <!-- 删除按钮，仅本人动态显示，右上角 -->
                <el-icon
                  v-if="moment.userId === loginStore.user?.id"
                  class="delete-icon"
                  @click="handleDeleteMoment(moment)"
                  style="
                    position: absolute;
                    right: 10px;
                    top: 10px;
                    z-index: 2;
                    cursor: pointer;
                    font-size: 22px;
                    color: #f56c6c;
                  "
                >
                  <Delete />
                </el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 发布动态弹窗 -->
    <el-dialog v-model="showPublishDialog" title="发布动态" width="500px">
      <el-form :model="publishForm" label-width="80px">
        <el-form-item label="内容">
          <el-input
            v-model="publishForm.content"
            type="textarea"
            :rows="4"
            placeholder="分享你的生活..."
          />
        </el-form-item>

        <el-form-item label="图片">
          <el-upload
            ref="upload"
            :action="`${uploadUrl}/user/upload`"
            :headers="headers"
            list-type="picture-card"
            :on-change="handleImageChange"
            :on-success="handleImageSuccess"
            :file-list="publishForm.images"
            :limit="9"
            :on-exceed="handleImageExceed"
            multiple
          >
            <el-icon>
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPublishDialog = false">取消</el-button>
          <el-button :loading="isLoading" type="primary" @click="handlePublish"
            >发布</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import {
  Edit,
  ChatDotRound,
  Star,
  Plus,
  Delete,
} from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import SiderBar from "@/components/SiderBar.vue";
import { usePostStore } from "@/stores/postStroe";
import Post, { type Comment, type Like, type Moment } from "@/api/Post";
import PostComment from "@/api/PostComment";
import PostLike from "@/api/PostLike";
import { useUserStore } from "@/stores/userStore";
import Avatar from "@/components/Avatar.vue";
import defaultCover from "@/assets/默认封面.png";
import Loading from "@/components/Loading.vue";
import type { UploadUserFile } from "element-plus";
import formatDate from "@/utils/timeFormate";
import HeaderWave from "@/components/HeaderWave.vue";

const uploadUrl = import.meta.env.VITE_SERVER_URL;

//store
const postStore = usePostStore();
const loginStore = useUserStore();

//upload的action设置请求头token
const token = localStorage.getItem("token") || "";

const headers = {
  token: `${token}`,
};

const isLoading = ref(false);

onMounted(() => {
  fetchMomentsList();
});
//初始化
const isLoadingPosts = ref(false);
const fetchMomentsList = async () => {
  if (postStore.getMonentList?.length > 0) {
    //先用本地缓存，同时请求获得最新的数据
    const res = await Post.getPosts();
    if (res.data.code === 1) {
      postStore.setMomentsList(res.data.data);
    }
    return;
  }
  isLoadingPosts.value = true;
  try {
    const res = await Post.getPosts();
    if (res.data.code === 1) {
      postStore.setMomentsList(res.data.data);
      isLoadingPosts.value = false;
    } else {
      ElMessage.error("获取动态失败");
    }
  } catch (err) {
    isLoadingPosts.value = false;
    ElMessage.error("获取动态失败");
  }
};

//处理点赞操作
const isDoingLike = ref(false);
const currentDoingLikeId = ref<number | null>(null);
const handleLike = async (moment: Moment) => {
  currentDoingLikeId.value = moment.id;
  const isLiked = moment.likes.some(
    (like) => like.userId === loginStore.user?.id
  );
  const likeObj = ref<Like>({
    userId: loginStore.user?.id as number,
    user: {
      id: loginStore.user?.id as number,
      username: loginStore.user?.username as string,
      avatar: loginStore.user?.avatar as string,
    },
  });
  if (!isLiked) {
    //实时更新
    moment.likes.push(likeObj.value);
    // 添加点赞
    try {
      isDoingLike.value = true;
      const res = await PostLike.addLike({
        postId: moment.id,
        userId: loginStore.user?.id,
      });
      if (res.data.code == 1) {
        ElMessage.success("点赞成功");
      } else {
        const index = moment.likes.findIndex(
          (like) => like.userId === loginStore.user?.id
        );
        if (index !== -1) {
          moment.likes.splice(index, 1);
        }
        ElMessage.error("点赞失败");
      }
    } catch (err) {
      const index = moment.likes.findIndex(
        (like) => like.userId === loginStore.user?.id
      );
      if (index !== -1) {
        moment.likes.splice(index, 1);
      }
      ElMessage.error("点赞失败");
    } finally {
      isDoingLike.value = false;
      currentDoingLikeId.value = null;
    }
  } else {
    // 实时更新
    const index = moment.likes.findIndex(
      (like) => like.userId === loginStore.user?.id
    );
    if (index !== -1) {
      moment.likes.splice(index, 1);
    }
    // 取消点赞
    try {
      isDoingLike.value = true;
      const res = await PostLike.deleteLike({
        postId: moment.id,
        userId: loginStore.user?.id,
      });
      if (res.data.code == 1) {
        ElMessage.success("取消点赞成功");
      } else {
        moment.likes.push(likeObj.value);
        ElMessage.error("取消点赞失败");
      }
    } catch (err) {
      moment.likes.push(likeObj.value);
      ElMessage.error("取消点赞失败");
    } finally {
      isDoingLike.value = false;
      currentDoingLikeId.value = null;
    }
  }
};

// 处理评论操作
const handleComment = async (moment: Moment) => {
  ElMessageBox.prompt("请输入评论内容", "评论", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    inputPlaceholder: "说点什么...",
  })
    .then(async ({ value }) => {
      const comment = ref<Comment>({
        id: -1,
        userId: loginStore.user?.id as number,
        content: value,
        createdAt: formatDate(new Date(), "all"),
        user: {
          id: loginStore.user?.id as number,
          username: loginStore.user?.username as string,
          avatar: loginStore.user?.avatar as string,
        },
      });
      moment.comments.push(comment.value);
      const res = await PostComment.addComment({
        postId: moment.id,
        content: value,
        userId: loginStore.user?.id,
      });
      if (res.data.code == 1) {
        ElMessage.success("评论成功");
      } else {
        moment.comments.pop();
        ElMessage.error("评论失败");
      }
    })
    .catch(() => {
      ElMessage.info("取消评论");
      moment.comments.pop();
    });
};

// 发布动态
const showPublishDialog = ref(false);
const publishForm = ref<{
  content: string;
  images: UploadUserFile[];
}>({
  content: "",
  images: [],
});
const upload = ref();
// 处理图片选择
const handleImageChange = (
  file: UploadUserFile,
  fileList: UploadUserFile[]
) => {
  publishForm.value.images = fileList;
};

// 处理图片上传成功
const handleImageSuccess = (
  response: any,
  file: UploadUserFile,
  fileList: UploadUserFile[]
) => {
  const target = fileList.find((f) => f.uid === file.uid);
  if (target) target.url = response.data;
  publishForm.value.images = fileList;
};

// 超出图片上传数量限制
const handleImageExceed = () => {
  ElMessage.warning("最多只能上传9张图片");
};

const handlePublish = async () => {
  const form = publishForm.value;

  // 1. 校验内容
  if (!form.content.trim()) {
    ElMessage.warning("请输入动态内容");
    return;
  }
  isLoading.value = true;

  // 2. 收集所有已上传图片的 url
  const imageUrls = form.images
    .map((f: UploadUserFile) => f.url)
    .filter((url): url is string => Boolean(url)); // 只要有效的字符串

  // 3. 构造新动态
  const newMoment = ref<Moment>({
    id: -1,
    userId: loginStore.user?.id as number,
    content: form.content,
    imageUrls,
    createdAt: formatDate(new Date(), "all"),
    author: {
      id: loginStore.user?.id as number,
      username: loginStore.user?.username as string,
      avatar: loginStore.user?.avatar as string,
    },
    comments: [],
    likes: [],
  });

  // 4. 先插入前端列表（乐观更新）
  postStore.getMonentList.unshift(newMoment.value);
  showPublishDialog.value = false;
  publishForm.value = { content: "", images: [] };
  try {
    const res = await Post.sendPost(newMoment.value);
    if (res.data.code === 1) {
      // 更新后端返回的真实 id
      newMoment.value.id = res.data.data;
      // 重置表单
      ElMessage.success("发布成功");
    } else {
      // 发布失败 → 回滚
      postStore.getMonentList.shift(); // 移除刚插入的第一个
      ElMessage.error("发布失败");
    }
  } catch (e) {
    // 请求异常 → 回滚
    postStore.getMonentList.shift();
    ElMessage.error("发布失败");
  } finally {
    isLoading.value = false;
  }
};
//删除动态
const handleDeleteMoment = (moment: Moment) => {
  if (moment.id === -1) return;
  ElMessageBox.confirm("确定要删除这条动态吗？", "提示", {
    confirmButtonText: "删除",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      // 从列表中移除
      const index = postStore.getMonentList.findIndex(
        (m) => m.id === moment.id
      );
      if (index !== -1) {
        postStore.getMonentList.splice(index, 1);
      }
      try {
        const res = await Post.deletePost(moment.id);
        if (res.data.code === 1) {
          ElMessage.success("删除成功");
        } else {
          ElMessage.error(res.data.msg || "删除失败");
          // 从列表中添加回来
          postStore.getMonentList.push(moment);
        }
      } catch (e) {
        ElMessage.error("删除失败，请重试");
        // 从列表中添加回来
        postStore.getMonentList.push(moment);
      }
    })
    .catch(() => {
      // 从列表中添加回来
      postStore.getMonentList.push(moment);
    });
};
</script>

<style scoped lang="scss">
.moments {
  min-height: 100vh;
  // background-color: #f6d7f7;
  overflow-y: auto;
}

.cover-section {
  position: relative;
  height: 40vh;
}

.cover {
  width: 85%;
  margin: 20px auto;
  height: 40vh;
  border-radius: 20px;
  box-shadow: rgba(100, 100, 111, 0.2) 0px 7px 29px 0px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 20px;
}

.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;

  .cover-icon {
    font-size: 48px;
    margin-bottom: 10px;
  }
}

.publish-btn {
  position: absolute;
  top: 320px;
  right: 20px;
  z-index: 10;
}

.main-content {
  padding: 20px;
  margin: 0 auto;
}

.moments-list {
  width: 87%;
  margin: -10px auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  min-height: 55vh;
  position: relative;
}

.empty-moments {
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

.moments-container {
  padding: 20px;
}

.moment-item {
  display: flex;
  gap: 15px;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.moment-avatar {
  flex-shrink: 0;
}

.moment-content {
  flex: 1;
  min-width: 0;
}

.moment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;

  .username {
    font-weight: 600;
    color: #d995e9;
    font-size: 23px;
  }

  .time {
    color: #909399;
    font-size: 15px;
  }
}

.moment-text {
  margin-bottom: 15px;
  line-height: 1.6;
  color: #555;
  font-size: 18px;
}

.moment-images {
  display: grid;
  grid-template-columns: repeat(3, 130px);
  gap: 2px;
  margin-bottom: 15px;
}

.image-item {
  width: 130px;
  height: 130px;
  position: relative;
  cursor: pointer;
  border-radius: 5px;
  overflow: hidden;
  margin-bottom: 0;
  /* grid布局无需额外下边距 */
}

.image-item .el-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.moment-actions {
  position: relative;

  .action-buttons {
    display: flex;
    gap: 20px;
    margin-bottom: 10px;

    .el-button {
      color: #606266;

      &.liked {
        color: #ffbbff;
      }
    }
  }

  .like-list {
    display: flex;
    align-items: center;
    gap: 10px;
    background-color: #f5f7fa;
    padding: 8px 12px;
    border-radius: 6px;
    margin-bottom: 8px;
    font-size: 13px;
    color: #606266;
    .el-icon {
      color: #409eff;
      margin-right: 5px;
    }
  }

  .comment-list {
    background-color: #f5f7fa;
    border-radius: 6px;
    padding: 8px 12px;

    .comment-item {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 8px;
      font-size: 13px;
      &:last-child {
        margin-bottom: 0;
      }

      .comment-user {
        color: #d995e9;
        font-weight: 500;
        margin-right: 5px;
        font-size: 20px;
      }

      .comment-time {
        color: #909399;
        font-size: 14px;
        flex-shrink: 0;
        margin-left: 10px;
      }

      .comment-content {
        color: #d995e9;
        font-size: 14px;
      }
    }
  }
}

.delete-btn {
  display: none;
}

.delete-icon {
  position: absolute;
  right: 10px;
  top: 10px;
  z-index: 2;
  cursor: pointer;
  font-size: 22px;
  color: #f56c6c;
  transition: color 0.2s;
}

.delete-icon:hover {
  color: #ff0000;
}

.friend-recommend {
  width: 28%;
  min-height: 80vh;
  max-height: 80vh;
  position: fixed;
  top: 10vh;
  right: 3vw;
  border-radius: 20px;
  box-shadow: rgba(0, 0, 0, 0.05) 0px 6px 24px 0px,
    rgba(0, 0, 0, 0.08) 0px 0px 0px 1px;
  background: white;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.recommend-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;

  h3 {
    margin: 0;
    color: #303133;
    font-size: 16px;
  }
}

.recommend-list {
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}

.empty-recommend {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100px;
  color: #909399;

  .empty-icon {
    font-size: 32px;
    margin-bottom: 10px;
  }

  p {
    margin: 0;
    font-size: 14px;
  }
}

.recommend-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  position: relative;

  &:last-child {
    border-bottom: none;
  }
}

.friend-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.friend-details {
  min-width: 0;
}

.friend-name {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
  margin-bottom: 4px;
}

.friend-desc {
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}

.friend-actions-top {
  position: absolute;
  top: 15px;
  right: 20px;
}

.friend-tags-row {
  display: flex;
  align-items: center;
  margin-top: 10px;
  width: 100%;
}

.tags-label {
  font-size: 12px;
  color: #909399;
  margin-right: 8px;
  flex-shrink: 0;
}

.friend-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.friend-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: #fff;
  background: #409eff;
  margin-right: 4px;
  margin-bottom: 2px;
  white-space: nowrap;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
    padding: 10px;
  }

  .friend-recommend {
    width: 100%;
  }

  .moment-images {
    .image-item {
      &.single {
        width: 100%;
        height: 200px;
      }

      &.double {
        width: calc(50% - 2.5px);
        height: 120px;
      }

      &.triple,
      &.quad,
      &.grid {
        width: calc(33.33% - 3.33px);
        height: 100px;
      }
    }
  }
}
</style>
