<template>
  <div class="tool-cards-container" v-if="isLoading">
    <Loading :show="isLoading" text="好累呀,再等一会会!" :mask="true"></Loading>
  </div>
  <div class="tool-cards-container" v-else>
    <h1 style="margin: 20px 0 0 90px;">工具大全</h1>
    <div class="tool-cards">
      <div 
        v-for="tool in toolStore.getTools" 
        :key="tool.id" 
        class="tool-card" 
        :style="{ backgroundColor: tool.cardColor }"
        @click="openToolUrl(tool.url)"
      >
        <div class="avatar" :style="{ backgroundImage: `url(${tool.avatar})` }"></div>
        <div class="content">
          <p class="name">{{ tool.name }}</p>
          <br>
          <p class="description">{{ tool.description }}</p>
        </div>
      </div>
    </div>
    
    <!-- 添加工具按钮 -->
    <div class="add-tool-button" @click="showAddToolDialog" v-if="userStore.user?.admin===1">
      <el-icon><Plus /></el-icon>
    </div>
    
    <!-- 添加工具对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="添加新工具"
      width="30%"
      :before-close="handleClose"
    >
      <el-form :model="newTool" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="newTool.name" placeholder="请输入工具名称"></el-input>
        </el-form-item>
        <el-form-item label="网址">
          <el-input v-model="newTool.url" placeholder="请输入工具网址"></el-input>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="newTool.description" placeholder="请输入工具简介"></el-input>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload 
            class="avatar-uploader" 
            :headers="headers"
            :action="`${API_BASE_URL}/user/upload`"
            :show-file-list="false" 
            :onSuccess="handleAvatarSuccess" 
            :before-upload="beforeAvatarUpload"
            accept="image/*"
          >
            <el-image 
              v-if="newTool.avatar && !isUploading" 
              :src="newTool.avatar" 
              class="avatar-preview" 
              fit="cover"
            />
            <el-icon v-else-if="!isUploading" class="avatar-uploader-icon"><Plus /></el-icon>
            <el-icon v-else class="avatar-uploader-icon"><Drizzling /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="卡片颜色">
          <el-color-picker v-model="newTool.cardColor" :predefine="['#f0f0f0', '#ffd966', '#6699ff', '#99cc33', '#ff6666']"></el-color-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button :loading="isAdding || isUploading" type="primary" @click="addTool">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
    <SiderBar></SiderBar>

</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import SiderBar from '@/components/SiderBar.vue'
import { getTools, add} from '@/api/tool'
import { useUserStore } from '@/stores/userStore'
const userStore=useUserStore()
import type { Tool } from '@/api/tool'
import { useToolStore } from '@/stores/toolStore'
import Loading from './Loading.vue'

// API 基础 URL
const API_BASE_URL = import.meta.env.VITE_SERVER_URL
//upload的action设置请求头token
const token = localStorage.getItem('token') || ''

const headers = {
  token: `${token}`
}
// 对话框显示状态
const dialogVisible = ref(false)

// 新工具表单数据
const newTool = ref<Omit<Tool, 'id'>>({ 
  name: '',
  url: '',
  description: '',
  avatar: '',
  cardColor: '#f0f0f0'
})
const toolStore=useToolStore()
const isLoading=ref(false)
const fetchTools=async ()=>{
  isLoading.value=true
  const data=await getTools()
  toolStore.setTools(data)
  isLoading.value=false
}
onMounted(async () => {
  if(toolStore.getTools.length===0)
    await fetchTools()
  
})  

// 打开工具网址
const openToolUrl = (url: string) => {
  if (url) {
    window.open(url, '_blank')
  }
}

// 显示添加工具对话框
const showAddToolDialog = () => {
  dialogVisible.value = true
  // 重置表单
  newTool.value = {
    name: '',
    url: '',
    description: '',
    avatar: '',
    cardColor: '#ffffff'
  }
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
}

const isUploading=ref(false)

// 头像上传成功回调
const handleAvatarSuccess = (response: any) => {
  if (response && response.data) {
    newTool.value.avatar = response.data
    ElMessage.success('头像上传成功')
    isUploading.value = false
  } else {
    ElMessage.error('上传失败')
    isUploading.value = false
  }
}

// 头像上传前校验
const beforeAvatarUpload = (file: File) => {
  if(isUploading.value){
    ElMessage.error('请稍后上传')
    return false
  }
  isUploading.value=true
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 10
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过10MB')
  }
  return isImage && isLt2M
}

// 添加新工具
const isAdding=ref(false)
const addTool = async() => {
  isAdding.value=true
  // 表单验证
  if (!newTool.value.name || !newTool.value.url) {
    ElMessage.warning('名称和网址不能为空')
    return
  }
  
  if (!newTool.value.avatar) {
    ElMessage.warning('请上传工具头像')
    return
  }
  //实时更新
  toolStore.getTools.push({
    id: -1,
    ...newTool.value
  })
  const id=await add({
    id:-1,
    ...newTool.value
  })
  toolStore.getTools[toolStore.getTools.length-1].id=id
  isAdding.value=false
  ElNotification({
    title: '成功',
    message: '工具添加成功',
    type: 'success',
    duration: 1500
  })
  // 关闭对话框
  dialogVisible.value = false
  
  // 提示成功
}
</script>

<style scoped>
.tool-cards-container {
  position: relative;
    overflow-y: auto;

}

.tool-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 27px; /* 增加间隔 */
  padding: 30px 30px 30px 120px;
}

.tool-card {
  display: flex;
  align-items: center;
  width: 300px;
  padding: 16px;
  border-radius: 15px;
  overflow: hidden;
  transition: transform 0.7s ease-in-out;
  cursor: pointer;
}

.tool-card:hover .content {
  transform: translateX(-50px);
}

.tool-card:hover .avatar {
  opacity: 0;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-size: cover;
  background-position: center;
  margin-right: 16px;
  transition: opacity 0.7s ease-in-out;
}

.content {
  flex: 1;
  transition: transform 0.7s ease-in-out;
}

.name {
  font-size: 18px;
  font-weight: bold;
}

.description {
  font-size: 14px;
  color: #666;
}

/* 添加工具按钮样式 */
.add-tool-button {
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #1296db;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  z-index: 100;
}

.add-tool-button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
}

/* 对话框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 头像上传样式 */
.avatar-uploader {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.avatar-preview:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}
</style>