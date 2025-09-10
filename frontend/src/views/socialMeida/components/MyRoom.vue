chat<template>
  <Nav />
  <SiderBar>  </SiderBar>
  <div class="home">
    <div class="info">
      <el-descriptions direction="vertical" border :column="3">
        <el-descriptions-item :rowspan="4" :colspan="1" label="头像" align="center" width="200px">
          <div class="avatar-container">
            <Avatar :avatar="userInfo?.avatar" :username="userInfo?.username || '用户'"
              :style="{ width: '200px', height: '200px', fontSize: '80px' }" />
          </div>
        </el-descriptions-item>

        <!-- 基本信息 -->
        <el-descriptions-item label="账号">{{ userInfo?.account || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userInfo?.username || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ userInfo?.gender === 1 ? "男" : userInfo?.gender == 2 ? "女 " : "保密"
        }}</el-descriptions-item>

        <el-descriptions-item label="地址">{{ userInfo?.region || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatDate(userInfo?.createdAt) || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="个性签名" :span="2">
          {{ userInfo?.signature || '这个人很懒，什么都没有留下~' }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 封面图片区域 - 在最下面，宽度大，居中 -->
      <div class="cover-section">
        <h3>封面图片</h3>
        <div class="cover-button-container">
          <div class="cover-container">
            <el-image v-if="userInfo?.cover" style="width: 60%; height: 300px; border-radius: 10px;"
              :src="userInfo?.cover" fit="cover" />
            <div v-else class="cover-placeholder">
              <el-icon class="cover-placeholder-icon">
              </el-icon>
              <div class="cover-placeholder-text">请到修改个人信息中上传封面</div>
            </div>
          </div>

          <!-- 修改按钮 -->
          <div class="action-buttons">
            <el-button type="primary" @click="showEditDialog = true">
              <el-icon>
                <Edit />
              </el-icon>
              修改个人信息
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑个人信息弹窗 -->
    <el-dialog v-model="showEditDialog" title="编辑个人信息" width="800px">

      <el-form :model="editForm" label-width="100px">

        <el-row :gutter="20">

          <!-- <el-col :span="12">
            <el-form-item label="账号" prop="account">
              <el-input v-model="editForm.account" placeholder="请输入账号" />
            </el-form-item>
          </el-col> -->

          <el-col :span="12">
            <el-form-item label="昵称" prop="username">
              <el-input v-model="editForm.username" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>

        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="editForm.gender" placeholder=" 请选择性别" style="width: 100%">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
                <el-option label="保密" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地址" prop="region">
              <el-input v-model="editForm.region" placeholder="请输入地址" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="个性签名" prop="signature">
          <el-input v-model="editForm.signature" type="textarea" :rows="3" placeholder="请输入个性签名" />
        </el-form-item>

        <!-- 密码修改区域 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="editForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="editForm.newPassword" type="password" placeholder="请输入新密码" show-password />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="头像">
              <el-upload class="edit-avatar-uploader" :show-file-list="false" :action="`${uploadUrl}/user/upload`"
                :headers="headers" :on-success="handleAvatarUploadSuccess" :before-upload="beforeAvatarUpload">
                <el-image v-if="editForm.avatar" style="width: 100px; height: 100px; border-radius: 50%;"
                  :src="editForm.avatar" fit="cover" />
                <el-icon v-else class="edit-avatar-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="封面">
              <el-upload class="edit-cover-uploader" :show-file-list="false" :action="`${uploadUrl}/user/upload`"
                :headers="headers" :on-success="handleCoverUploadSuccess" :before-upload="beforeCoverUpload">
                <el-image v-if="editForm.cover" style="width: 200px; height: 100px; border-radius: 8px;"
                  :src="editForm.cover" fit="cover" />
                <el-icon v-else class="edit-cover-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>


      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancleEnent">取消</el-button>
          <el-button type="primary" @click="handleSaveEdit">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Plus, Edit } from '@element-plus/icons-vue';
import User from '@/api/User';
import { useUserStore } from '@/stores/userStore';
import { ElNotification } from 'element-plus';
import Upload from '@/api/Upload'
import Avatar from './Avatar.vue';
import Nav from './Nav.vue';
import SiderBar from '@/components/SiderBar.vue';
import formatDate from '@/utils/timeFormate'
const uploadUrl = import.meta.env.VITE_SERVER_URL;


//upload的action设置请求头token
const token = localStorage.getItem('token') || ''

const headers = {
  token: `${token}`
}
const coverOssUrl = ref('')
const avatarOssUrl = ref('')
//upload的请求处理

//上传限制
// 限制文件大小和类型
const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt20M = file.size / 1024 / 1024 < 20

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片')
    return false
  }
  if (!isLt20M) {
    ElMessage.error('上传文件大小不能超过 20MB')
    return false
  }

  return true
}
const handleCoverUploadSuccess = (response, file, fileList) => {
  editForm.value.cover = ref(response.data)
  coverOssUrl.value = response.data.split('/').pop()
}
const handleAvatarUploadSuccess = (response, file, fileList) => {
  editForm.value.avatar = ref(response.data) //响应式
  avatarOssUrl.value = response.data.split('/').pop()
}
//取消
const cancleEnent = () => {
  showEditDialog.value = false;
  //把上传到oss的图片删除
  if (coverOssUrl.value) {
    Upload.deleteUpload(coverOssUrl.value)
    editForm.value.cover = userInfo.value.cover
  }
  if (avatarOssUrl.value) {
    Upload.deleteUpload(avatarOssUrl.value)
    editForm.value.avatar = userInfo.value.avatar
  }

}

onMounted(async () => {
  await fetchUserInfo();
  editForm.value = {
    account: userInfo.value.account,
    username: userInfo.value.username,
    gender: userInfo.value.gender,
    region: userInfo.value.region,
    signature: userInfo.value.signature,
    oldPassword: '',
    newPassword: '',
    avatar: userInfo.value.avatar,
    cover: userInfo.value.cover
  };
  console.log(editForm.value);
});

//store
const loginStore = useUserStore();
// 响应式数据
const showEditDialog = ref(false);
// 用户信息 
const userInfo = ref(null);
const fetchUserInfo = async () => {
  // 获取用户信息
  userInfo.value = loginStore.user;
};

// 编辑表单数据
const editForm = ref();

// 保存编辑信息 - 您可以根据需要实现
const handleSaveEdit = async () => {
  const res = await User.update(editForm.value);
  if (res.data.code == 1) {
    const ress=await User.getById(loginStore.user.id)
    if(ress.data.code==1){
      loginStore.updateUser(ress.data.data)
    }
    userInfo.value = editForm.value;
    ElNotification({
      type: 'success',
      title: 'success',
      message: '修改成功！',
    })
  }
  showEditDialog.value = false;
};
</script>

<style scoped lang="scss">
.home {
  width: 100vw;
  
  height: 92vh;
  padding: 5vh 0 0 0;
  background-color: rgba(24, 144, 255);
}

.info {
  margin: auto;
  width: 80%;
  height: 97%;
  box-shadow: rgba(50, 50, 93, 0.25) 0px 2px 5px -1px, rgba(0, 0, 0, 0.3) 0px 1px 3px -1px;
  border-radius: 30px 30px 0 0;
  background-color: aliceblue;
  padding: 30px;
}

.avatar-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 200px;
  height: 200px;
}

.avatar-placeholder {
  width: 200px;
  height: 200px;
  border: 2px dashed #d9d9d9;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #fafafa;
  color: #8c939d;
}

.avatar-placeholder-icon {
  font-size: 28px;
  margin-bottom: 10px;
}

.avatar-placeholder-text {
  font-size: 14px;
}

.cover-section {
  margin-top: 30px;
  text-align: center;
  // background-color: red;

  h3 {
    margin-right: 150px;
    margin-bottom: 20px;
    color: #333;
    font-size: 18px;
  }
}

.cover-button-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 30px;
}

.cover-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.cover-placeholder {
  width: 60%;
  height: 300px;
  border: 2px dashed #d9d9d9;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #fafafa;
  color: #8c939d;
  margin: 0 auto;
}

.cover-placeholder-icon {
  font-size: 28px;
  margin-bottom: 10px;
}

.cover-placeholder-text {
  font-size: 14px;
}

.action-buttons {

  margin-top: 250px;
  display: flex;
  align-items: center;
  height: 100px;
}

.edit-avatar-uploader {
  width: 100px;
  height: 100px;
  border: 2px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fafafa;

  &:hover {
    border-color: #409eff;
  }
}

.edit-avatar-uploader-icon {
  font-size: 20px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.edit-cover-uploader {
  width: 200px;
  height: 100px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fafafa;

  &:hover {
    border-color: #409eff;
  }
}

.edit-cover-uploader-icon {
  font-size: 20px;
  color: #8c939d;
  width: 200px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 密码字段样式 */
:deep(.el-form-item) {
  .el-input {
    .el-input__wrapper {
      &.is-focus {
        box-shadow: 0 0 0 1px #409eff inset;
      }
    }
  }
}

/* 密码字段组样式 */
.password-section {
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
  margin-top: 20px;
}
</style>