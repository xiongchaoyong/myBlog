<template>
  <transition name="fade">
  <div class="login-box" v-if="visible">
    <el-icon class="close-btn" @click="handleClose">
      <Close />
    </el-icon>
    <div class="login-header">
      <h2>{{ isLogin ? "登录" : "注册" }}</h2>
    </div>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      class="login-form"
      @submit.prevent="handleSubmit"
    >
      <el-form-item v-if="!isLogin" prop="username">
        <el-input
          v-model="formData.username"
          placeholder="用户名"
          prefix-icon="User"
          size="large"
        />
      </el-form-item>
      <el-form-item v-if="!isLogin" prop="email">
        <el-input
          v-model="formData.email"
          placeholder="邮箱"
          prefix-icon="Message"
          size="large"
        />
      </el-form-item>
      <el-form-item v-if="isLogin" prop="emailOraccount">
        <el-input
          v-model="formData.emailOraccount"
          placeholder="邮箱或账号"
          prefix-icon="User"
          size="large"
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="formData.password"
          type="password"
          placeholder="密码"
          prefix-icon="Lock"
          size="large"
          show-password
        />
      </el-form-item>
      <el-form-item v-if="!isLogin" prop="confirmPassword">
        <el-input
          v-model="formData.confirmPassword"
          type="password"
          placeholder="确认密码"
          prefix-icon="Lock"
          size="large"
          show-password
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          size="large"
          class="submit-btn"
          :loading="loading"
          native-type="submit"
        >
          {{ isLogin ? "登录" : "注册" }}
        </el-button>
      </el-form-item>
    </el-form>

    
    <div class="otherLogin" v-if="isLogin">
      <p>其它登录方式</p>
      <div class="icons">
        <svg style="width: 1.3rem; height: 1.3rem">
          <use xlink:href="#icon-QQ" fill="#1296db"></use>
        </svg>
        <svg style="width: 1.3rem; height: 1.3rem" @click="loginWithGithub">
          <use xlink:href="#icon-github" fill="#1296db"></use>
        </svg>
        <svg style="width: 1.3rem; height: 1.3rem">
          <use xlink:href="#icon-shoujihaoma" fill="#1296db"></use>
        </svg>
      </div>
    </div>

    <div class="login-footer">
      <el-button type="text" @click="toggleMode">
        {{ isLogin ? "没有账户？立即注册" : "已有账户？立即登录" }}
      </el-button>
    </div>
  </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from "vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { User, Lock, Close } from "@element-plus/icons-vue";
import { login, register, getUserById } from "@/api/uuser";
import { useUserStore } from "@/stores/userStore";

const props = defineProps<{ visible: boolean }>();

const emit = defineEmits<{ (e: "close"): void; (e: "login-success"): void }>();

const visible = ref(props.visible);

watch(
  () => props.visible,
  (v) => (visible.value = v)
);


const formRef = ref<FormInstance>();
const isLogin = ref(true);
const loading = ref(false);
const formData = reactive({
  email: "",
  username: "",
  password: "",
  confirmPassword: "",
  emailOraccount: "",
});

// 状态管理
const userStore = useUserStore();
const formRules: FormRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 1,
      max: 20,
      message: "用户名长度在 1 到 20 个字符",
      trigger: "blur",
    },
  ],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱格式", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 3, max: 20, message: "密码长度在 3 到 20 个字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== formData.password) {
          callback(new Error("两次输入密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
};
const toggleMode = () => {
  isLogin.value = !isLogin.value;
  formData.username = "";
  formData.email = "";
  formData.password = "";
  formData.confirmPassword = "";
  formRef.value?.clearValidate();
};
const handleClose = () => {
  visible.value = false;
  setTimeout(() => {
  emit("close");
  }, 300);
};


//后端逻辑
//qq邮箱注册
//github、qq、手机号、密码登录
const handleSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
    loading.value = true;

    if (isLogin.value) {
      //密码登录 (qq邮箱、账号)
      let account = "";
      let email = "";
      try {
        //先判断emailOraccount是否是邮箱
        if (formData.emailOraccount.includes("@")) {
          //是邮箱
          email = formData.emailOraccount;
        } else {
          //是账号
          account = formData.emailOraccount;
        }
        
        const response = await login({
          email: email,
          account: account, 
          password: formData.password,
        });

        if (response.data.code === 1) {
          const { id, token: tokenValue } = response.data.data;
          // 登录成功，将token存储到localStorage
          localStorage.setItem("token", tokenValue);
          try {
            // 获取完整的用户信息
            const userResponse = await getUserById(id);

            if (userResponse.data.code === 1) {
              const userInfo = userResponse.data.data;

              // 使用userStore处理登录成功
              const result = userStore.loginSuccess(userInfo, tokenValue);

              if (result.success) {
                ElMessage.success("登录成功");
                emit("login-success");
              } else {
                ElMessage.error("登录失败");
                return;
              }
            } else {
              ElMessage.error("获取用户信息失败");
              return;
            }
          } catch (userError) {
            console.error("获取用户信息失败:", userError);
            ElMessage.error("获取用户信息失败");
            return;
          }
        } else {
          ElMessage.error(response.data.msg || "登录失败");
          return;
        }
      } catch (error) {
        console.error("登录失败:", error);
        ElMessage.error("登录失败，请检查网络连接");
        return;
      }
    } else {
      // 注册
      try {
        const response = await register({
          username: formData.username,
          email: formData.email,
          password: formData.password,
        });

        if (response.data.code === 1) {
          ElMessage.success("注册成功，请登录");
          isLogin.value = true;
          formData.username = "";
          formData.email = "";
          formData.password = "";
          formData.confirmPassword = "";
          formRef.value?.clearValidate();
          return;
        } else {
          ElMessage.error(response.data.msg || "注册失败");
          return;
        }
      } catch (error) {
        console.error("注册失败:", error);
        ElMessage.error("注册失败，请检查网络连接");
        return;
      }
    }

    visible.value = false;
  } catch (error) {
    console.error("表单验证失败:", error);
  } finally {
    loading.value = false;
  }
};


//github登录
const loginWithGithub=()=> {
      const clientId = 'Ov23liekxBjOJPcC1piu';
      const redirectUri = 'http://localhost:5173/callback';
      const state = Math.random().toString(36).substring(2); // 随机字符串防 CSRF
      localStorage.setItem('github_oauth_state', state);
      const url = `https://github.com/login/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&scope=user&state=${state}`;
      window.location.href = url; // 跳转到 GitHub 授权
    }
</script>

<style scoped lang="scss">
.login-box {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px 20px 20px 20px;
  width: 100%;
  max-width: 400px;
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 9999;
  animation: slideIn    0.3s ease-out;
}


/* transition动画定义 */
.fade-enter-from,
.fade-leave-to {
  opacity: 0;  /* 开始和结束透明 */
  transform: translate(-50%, -55%); /* 可加轻微位移效果 */
}

.fade-enter-to,
.fade-leave-from {
  opacity: 1;
  transform: translate(-50%, -50%);
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.close-btn {
  position: absolute;
  right: 16px;
  top: 16px;
  font-size: 22px;
  color: #888;
  cursor: pointer;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #f56c6c;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  h2 {
    margin: 0 0 8px 0;
    color: #333;
    font-size: 24px;
    font-weight: 600;
  }

  p {
    margin: 0;
    color: #666;
    font-size: 14px;
  }
}

.login-form .submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
}

.login-footer {
  text-align: center;
  margin-top: 20px;

  .el-button {
    color: #409eff;
    font-size: 14px;

    &:hover {
      color: #66b1ff;
    }
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translate(-50%, -60%);
  }

  to {
    opacity: 1;
    transform: translate(-50%, -50%);
  }
}

.otherLogin {
  display: flex;
  flex-direction: column;
  align-items: center;
  p{
    margin-bottom: 12px;
    color: #888;
  }
  .icons{
    display: flex;
    gap: 16px;
    svg{
      cursor: pointer;
    }
  }
}
</style>
