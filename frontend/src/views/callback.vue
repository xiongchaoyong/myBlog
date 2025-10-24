<template>
  <div style="text-align: center; margin-top: 50px">
    <p>正在处理 GitHub 登录，请稍候...</p>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { githubLogin } from "@/api/uuser";
import { useUserStore } from "@/stores/userStore";
import { getUserById } from "@/api/uuser";
import { ElMessage } from "element-plus";
const userStore=useUserStore()
onMounted(async () => {
  // 1. 获取 URL 中的 code 和 state
  const params = new URLSearchParams(window.location.search);
  const code = params.get("code");
  const state = params.get("state");
  const savedState = localStorage.getItem("github_oauth_state");

  // 2. 校验 state 防 CSRF
  if (!code || !state || state !== savedState) {
    alert("非法请求或状态不匹配");
    return;
  }

  try {
    // 3. 发送 code 给后端
    const response = await githubLogin(code);
    console.log(response.data)
    const token = response.data.data.token;
    const id = response.data.data.id;
    localStorage.setItem("token", token);
    const userResponse = await getUserById(id);
    if (userResponse.data.code === 1) {
      
      const userInfo = userResponse.data.data;
      // 使用userStore处理登录成功
      const result = userStore.loginSuccess(userInfo, token);
      if (result.success) {
        ElMessage.success("登录成功");
      } else {
        ElMessage.error("登录失败");
        return;
      }
    } else {
      ElMessage.error("获取用户信息失败");
      return;
    }

    // 5. 清理 state
    localStorage.removeItem("github_oauth_state");

    // 6. 登录成功，跳转首页
    window.location.href = "/";
  } catch (error) {
    console.error("GitHub 登录失败：", error);
    alert("GitHub 登录失败，请重试");
  }
});
</script>
