<template>
  <HeaderWave text="发表文章" />
  <div class="publish-page">
    <!-- 全局加载组件 -->
    <Loading class="page-loading" :show="isPublishing" text="正在发表文章..." :mask="true"/>
    <!-- <Loading class="page-loading" :show="hh" text="正在发表文章..." :mask="true"/> -->

    <SiderBar />
    <div class="publish-container">
      <div class="publish-form">
        <!-- 文章基本信息 -->
        <div class="form-section">
          <h3 class="section-title">文章信息</h3>
          <div class="form-row">
            <div class="form-item">
              <label class="form-label">文章标题 *</label>
              <el-input v-model="articleForm.title" placeholder="请输入文章标题" size="large" maxlength="100"
                show-word-limit />
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label class="form-label">封面图片</label>
              <el-upload class="cover-uploader cover-upload-box" :action="`${API_BASE_URL}/user/upload`"
                :headers="headers"
                :show-file-list="false" :on-success="handleCoverSuccess" :before-upload="beforeCoverUpload"
                accept="image/*">
                <el-image v-if="articleForm.cover" :src="articleForm.cover" class="cover-preview" fit="contain"
                  style="width: 180px; height: 120px; border-radius: 8px;" />
                <el-icon v-else>
                  <Plus />
                </el-icon>
                <div class="el-upload__text" v-if="!articleForm.cover">点击上传封面</div>
              </el-upload>
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label class="form-label">文章分类 *</label>
              <div class="category-header">
                <div class="category-tags">
                  <el-tag v-for="category in categories" :key="category.id"
                    :class="{ 'category-tag-active': articleForm.categoryId === category.id }" :style="{
                      backgroundColor: articleForm.categoryId === category.id ? category.color : '#f5f5f5',
                      color: articleForm.categoryId === category.id ? '#fff' : '#666',
                      borderColor: category.color
                    }" @click="articleForm.categoryId = category.id" class="category-tag">
                    {{ category.name }}
                  </el-tag>
                </div>
                <el-button size="small" class="create-category-btn" @click="showCreateCategoryDialog">
                  <el-icon>
                    <Plus />
                  </el-icon>
                  创建分类
                </el-button>
              </div>
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label class="form-label">文章摘要</label>
              <el-input v-model="articleForm.summary" type="textarea" placeholder="请输入文章摘要" :rows="3" maxlength="200"
                show-word-limit />
            </div>
          </div>
        </div>


        <!-- Markdown编辑器 -->
        <div class="form-section">
          <h3 class="section-title">文章内容 *</h3>
          <div class="editor-container">
            <MdEditor v-if="!isPublishing && articleForm.content !== undefined" v-model="articleForm.content"
              @on-change="handleEditorChange" @on-upload-img="handleUploadImg" placeholder="开始编写你的文章..."
              :toolbars="toolbars" style="height: 500px">

              <template #defToolbars>
                <Emoji>
                  <template #trigger> <svg style="width:1.3rem;height:1.3rem;">
                      <use xlink:href="#icon-biaoqing" fill="#1296db"></use>
                    </svg></template>
                </Emoji>
              </template>

            </MdEditor>
          </div>
        </div>



        <!-- 操作按钮 -->
        <div class="form-actions">
          <div class="draft-info">
            <el-button type="info" size="small" @click="openDraftDialog" :disabled="draftCount === 0">
              草稿管理 ({{ draftCount }})
            </el-button>
          </div>
          <div class="action-buttons">
            <el-button size="large" @click="handlePreview">预览</el-button>
            <el-button size="large" @click="handleSaveDraft">保存草稿</el-button>
            <el-button type="primary" size="large" @click="handlePublish" :loading="isPublishing"
              :disabled="isPublishing">
              {{ isPublishing ? (isEditMode ? '修改中...' : '发表中...') : (isEditMode ? '修改文章' : '发表文章') }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="文章预览" width="80%" :before-close="handlePreviewClose">
      <div class="preview-content" v-if="previewVisible && articleForm.content">
        <h1>{{ articleForm.title }}</h1>
        <div class="preview-meta">
          <span v-if="articleForm.categoryId">
            分类：
            <el-tag :color="getCategoryColor(articleForm.categoryId)" size="small">
              {{ getCategoryName(articleForm.categoryId) }}
            </el-tag>
          </span>

          <span>发布时间：{{ formatShortDate([new Date().getFullYear(), new Date().getMonth() + 1, new Date().getDate(), new
            Date().getHours(), new Date().getMinutes()]) }}</span>
        </div>
        <div class="preview-body">
          <div v-html="marked.parse(articleForm.content)" class="markdown-content"></div>
        </div>
      </div>
    </el-dialog>

    <!-- 草稿管理对话框 -->
    <el-dialog v-model="showDraftDialog" title="草稿管理" width="800px" :close-on-click-modal="false">
      <div v-if="drafts.length === 0" class="empty-drafts">
        <el-empty description="暂无草稿" />
      </div>

      <div v-else class="drafts-list">
        <div v-for="draft in drafts" :key="draft.id" class="draft-item">
          <div class="draft-info">
            <h4 class="draft-title">{{ draft.title }}</h4>
            <p class="draft-summary">{{ draft.summary || '暂无摘要' }}</p>
            <div class="draft-meta">
              <span class="draft-time">创建时间：{{ formatLocalDate(draft.createTime) }}</span>

            </div>
          </div>
          <div class="draft-actions">
            <el-button type="primary" size="small" @click="loadDraftToEditor(draft)">
              加载
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteDraft(draft.id)">
              删除
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 创建分类弹窗 -->
    <el-dialog v-model="createCategoryVisible" title="创建分类" width="400px" :before-close="handleCreateCategoryClose">
      <div class="create-category-form">
        <div class="form-item">
          <label class="form-label">分类名称 *</label>
          <el-input v-model="newCategoryForm.name" placeholder="请输入分类名称" maxlength="100" show-word-limit />
        </div>
        <div class="form-item">
          <label class="form-label">标签颜色</label>
          <el-color-picker v-model="newCategoryForm.color" show-alpha />
          <span class="color-tip">选择标签的背景颜色</span>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCreateCategoryClose">取消</el-button>
          <el-button type="primary" @click="handleCreateCategory">创建</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { MdEditor, MdPreview } from 'md-editor-v3'
import type { ToolbarNames } from 'md-editor-v3'
import 'md-editor-v3/lib/preview.css';
import 'md-editor-v3/lib/style.css'



import SiderBar from '@/components/SiderBar.vue'
import { publishArticle, updateArticle, getArticleById,aiJudge, type ArticleForm, getLocalDrafts, deleteLocalDraft, type Draft } from '@/api/article'
import { getCategories, type Category, createCategory } from '@/api/category'
import Loading from '@/components/Loading.vue'
import { formatLocalDate, formatShortDate } from '@/utils/dateUtils'
import { marked } from 'marked'
import { useArticleStore } from '@/stores/articleStore'
// import { useCategoryStore } from '@/stores/categoryStore'
import HeaderWave from '@/components/HeaderWave.vue'
import { Emoji } from '@vavt/v3-extension';
import '@vavt/v3-extension/lib/asset/Emoji.css';
import { Plus } from '@element-plus/icons-vue'

const hh=ref(true)
// 配置marked解析器
marked.setOptions({
  breaks: true,
  gfm: true
})

//upload的action设置请求头token
const token = localStorage.getItem("token") || "";

const headers = {
  token: `${token}`,
};

const router = useRouter()
const route = useRoute()

const articleForm = reactive({
  title: '',
  cover: '',
  categoryId: undefined as number | undefined,
  summary: '',
  content: ''
})

const categories = ref<Category[]>([])
const draftCount = ref(0)
const showDraftDialog = ref(false)
const drafts = ref<Draft[]>([])
// 删除不再需要的变量

const previewVisible = ref(false)

// 创建分类相关
const createCategoryVisible = ref(false)
const newCategoryForm = reactive({
  name: '',
  color: '#409EFF'
})

// 状态管理
const articleStore = useArticleStore()
// const categoryStore = useCategoryStore()

// 本地加载状态
const isPublishing = ref(false)

const isEditMode = computed(() => !!route.params.id)

const API_BASE_URL = import.meta.env.VITE_SERVER_URL

// 编辑器工具栏配置
const toolbars = ref<ToolbarNames[]>([
  'bold', 'underline', 'italic', 'strikeThrough', 'title', 'sub', 'sup', 'quote',
  'unorderedList', 'orderedList', 'task', 'codeRow', 'code', 'link', 'image', 'table',
  'mermaid', 'katex', 'revoke', 'next', 'save', 'prettier', 'pageFullscreen', 'fullscreen',
  'preview', 'previewOnly', 'htmlPreview', 'catalog', 'github', 0
])


/**
 * 获取分类列表
 */
const isLoadingCategories = ref(false)

const loadCategories = async () => {
  try {
    isLoadingCategories.value = true
    // categories.value = await categoryStore.fetchCategories()
  } catch (error) {
    console.error('获取分类列表失败:', error)
    categories.value = []
  } finally {
    isLoadingCategories.value = false
  }
}

/**
 * 显示创建分类弹窗
 */
const showCreateCategoryDialog = () => {
  createCategoryVisible.value = true
}

/**
 * 创建分类
 */
const handleCreateCategory = async () => {
  if (!newCategoryForm.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }

  try {
    const newCategory = await createCategory({
      name: newCategoryForm.name.trim(),
      color: newCategoryForm.color
    })

    // 添加到分类列表
    categories.value.push({
      id: newCategory.id,
      name: newCategory.name,
      color: newCategory.color || '#409EFF'
    })

    // 强制刷新分类store缓存
    // await Promise.all([
    //   categoryStore.refreshCategories(),
    //   categoryStore.refreshCategoriesWithCount()
    // ])

    // 自动选择新创建的分类
    articleForm.categoryId = newCategory.id

    // 关闭弹窗并重置表单
    createCategoryVisible.value = false
    newCategoryForm.name = ''
    newCategoryForm.color = '#409EFF'

    ElMessage.success('分类创建成功')
  } catch (error) {
    console.error('创建分类失败:', error)
    ElMessage.error('创建分类失败')
  }
}

/**
 * 关闭创建分类弹窗
 */
const handleCreateCategoryClose = () => {
  createCategoryVisible.value = false
  newCategoryForm.name = ''
  newCategoryForm.color = '#409EFF'
}
/**
* 获取草稿数量
*/
const getDraftCount = () => {
  try {
    const drafts = JSON.parse(localStorage.getItem('article_drafts') || '[]')
    draftCount.value = drafts.length
  } catch (error) {
    console.error('获取草稿数量失败:', error)
    draftCount.value = 0
  }
}

/**
 * 打开草稿管理对话框
 */
const openDraftDialog = () => {
  drafts.value = getLocalDrafts()
  showDraftDialog.value = true
}

/**
 * 删除草稿
 */
const handleDeleteDraft = (draftId: number) => {
  deleteLocalDraft(draftId)
  drafts.value = getLocalDrafts()
  getDraftCount()
  ElMessage.success('草稿删除成功')
}

/**
 * 加载草稿到编辑器
 */
const loadDraftToEditor = (draft: Draft) => {
  articleForm.title = draft.title
  articleForm.content = draft.content
  articleForm.cover = draft.cover
  articleForm.summary = draft.summary
  articleForm.categoryId = draft.categoryId
  showDraftDialog.value = false
  ElMessage.success('草稿已加载到编辑器')
}

/**
 * 根据路由参数id加载文章详情（编辑模式）
 * @returns {Promise<void>}
 */
// const loadArticleDetail = async () => {
//   const id = route.params.id
//   if (!id) return
//   try {
//     isPublishing.value = true
//     // const detail =  articleStore.getArticleById(Number(id))
//     articleForm.title = detail.title
//     articleForm.cover = detail.cover
//     articleForm.categoryId = detail.categoryId
//     articleForm.summary = detail.summary
//     articleForm.content = detail.content
//   } catch (e) {
//     console.error('加载文章详情失败:', e)
//     ElMessage.error('加载文章详情失败')
//   } finally {
//     isPublishing.value = false
//   }
// }

/**
 * 组件挂载时获取分类列表和草稿数量
 */
onMounted(() => {
  loadCategories()
  getDraftCount()
  if (route.params.id) {
    // loadArticleDetail()
  }
})

/**
 * 根据分类ID获取分类名称
 * @param categoryId 分类ID
 * @returns 分类名称
 */
const getCategoryName = (categoryId: number): string => {
  const category = categories.value.find(cat => cat.id === categoryId)
  return category ? category.name : '未知分类'
}

/**
 * 根据分类ID获取分类颜色
 * @param categoryId 分类ID
 * @returns 分类颜色
 */
const getCategoryColor = (categoryId: number): string => {
  const category = categories.value.find(cat => cat.id === categoryId)
  return category?.color || '#409EFF'
}



const handleEditorChange = (val: string) => {
  articleForm.content = val
}



const handleUploadImg = async (files: FileList, callback: (urls: string[]) => void) => {
  /**
   * 编辑器图片上传，调用后端接口，返回图片url数组（每次只上传一张，url在res.data）
   */
  try {
    const uploadPromises = Array.from(files).map(file => {
      const formData = new FormData();
      formData.append('file', file);
      return fetch(`${API_BASE_URL}/user/upload`, {
        method: 'POST',
        headers: headers,
        body: formData
      }).then(res => res.json());
    });
    const results = await Promise.all(uploadPromises);
    // 取每个返回的 data 字段作为图片url
    const urls = results.map(res => res.data);
    callback(urls);
  } catch (e) {
    ElMessage.error('图片上传失败');
    callback([]);
  }
}

// 上传成功回调
const handleCoverSuccess = (response: any) => {
  if (response && response.data) {
    articleForm.cover = response.data
    ElMessage.success('封面上传成功')
    loading.value = false
  } else {
    ElMessage.error('上传失败')
    loading.value = false
  }
}

// 上传前校验（可选）
const loading = ref(false)
const beforeCoverUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    loading.value = false
    ElMessage.error('只能上传图片文件')
  }
  if (!isLt2M) {
    loading.value = false
    ElMessage.error('图片大小不能超过2MB')
  }
    loading.value = false
  return isImage && isLt2M
}

const handlePreview = () => {
  if (!articleForm.title.trim()) {
    ElMessage.warning('请先输入文章标题')
    return
  }
  if (!articleForm.content.trim()) {
    ElMessage.warning('请先输入文章内容')
    return
  }
  previewVisible.value = true
}

const handlePreviewClose = () => {
  previewVisible.value = false
}

const handleSaveDraft = () => {
  try {
    // 验证必填字段
    if (!articleForm.title.trim()) {
      ElMessage.warning('请输入文章标题')
      return
    }
    if (!articleForm.content.trim()) {
      ElMessage.warning('请输入文章内容')
      return
    }

    // 构建草稿数据
    const draftData = {
      id: Date.now(), // 使用时间戳作为草稿ID
      title: articleForm.title.trim(),
      content: articleForm.content.trim(),
      cover: articleForm.cover.trim() || '',
      summary: articleForm.summary.trim() || '',
      categoryId: articleForm.categoryId,
      createTime: new Date().toISOString(),
      updateTime: new Date().toISOString()
    }

    // 保存到本地存储
    const drafts = JSON.parse(localStorage.getItem('article_drafts') || '[]')
    drafts.push(draftData)
    localStorage.setItem('article_drafts', JSON.stringify(drafts))

    // 更新草稿数量
    getDraftCount()

    ElMessage.success('草稿保存成功')
  } catch (error) {
    console.error('保存草稿失败:', error)
    ElMessage.error('保存失败，请重试')
  }
}

/**
 * 发表/更新文章
 * @description 有id时为更新，无id时为新建
 */
const handlePublish = async () => {
  if (!(articleForm.title ?? '').trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  if (!(articleForm.content ?? '').trim()) {
    ElMessage.warning('请输入文章内容')
    return
  }
  try {
    await ElMessageBox.confirm(
      route.params.id ? '确定要更新这篇文章吗？' : '确定要发表这篇文章吗？发表后将无法修改。',
      route.params.id ? '确认更新' : '确认发表',
      {
        confirmButtonText: route.params.id ? '确定更新' : '确定发表',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // 设置发布状态为true
    isPublishing.value = true

    const articleData: ArticleForm = {
      title: (articleForm.title ?? '').trim(),
      content: (articleForm.content ?? '').trim() || '',
      cover: (articleForm.cover ?? '').trim() || undefined,
      summary: (articleForm.summary ?? '').trim() || undefined,
      categoryId: articleForm.categoryId,
    }

 

    let result
    try {
      if (route.params.id) {
        // 编辑模式，调用更新接口
        const articleId = Number(route.params.id)
        result = await updateArticle(articleId, articleData)
        // articleStore.updateArticle(result)
        ElMessage.success('文章更新成功！')
      } else {

           // 调用AI判断接口
    const aiResult = await aiJudge(articleData)
    ElNotification({
      title: 'AI判断结果',
      message: aiResult.data.data.reason,
      type: aiResult.data.data.approved? 'success' : 'error',
      duration: 0
    })
    ElMessage({
      message: aiResult.data.data.reason,
      type: aiResult.data.data.approved? 'success' : 'error',
      duration: 2000
    })
    if(!aiResult.data.data.approved){
            isPublishing.value = false
    return
    }

        // 新建模式
        result = await publishArticle(articleData)
        ElMessage.success('文章发表成功！')
        // articleStore.addArticle(result)
      }
      // 清空表单
      Object.assign(articleForm, {
        title: '',
        cover: '',
        categoryId: undefined,
        summary: '',
        content: ''
      })
      // 新增文章
      // 跳转到文章详情页面
      router.push(`/article/${result.id}`)
    } catch (error) {
      console.error(route.params.id ? '更新文章失败:' : '发表文章失败:', error)
      ElMessage.error(route.params.id ? '更新失败，请重试' : '发表失败，请重试')
    } 
  } catch (error) {
    // 这里捕获的是ElMessageBox.confirm的取消操作
    if (error !== 'cancel') {
      console.error('确认对话框错误:', error)
    }
  }
}


</script>

<style scoped>
.publish-page {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
  position: relative;
}

.publish-container {
  flex: 1;
  margin-left: 64px;
  padding: 20px;
  width: 96%;
}

.publish-header {
  margin-bottom: 30px;
  text-align: center;
}

.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 10px;
}

.page-desc {
  font-size: 1.1rem;
  color: #7f8c8d;
}

.publish-form {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.form-section {
  padding: 30px;
  border-bottom: 1px solid #f0f0f0;
}

.form-section:last-child {
  border-bottom: none;
}

.section-title {
  font-size: 1.3rem;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: #3498db;
  margin-right: 10px;
  border-radius: 2px;
}

.form-row {
  margin-bottom: 20px;
}

.form-row:last-child {
  margin-bottom: 0;
}

.form-item {
  width: 100%;
}

.form-label {
  display: block;
  font-weight: 500;
  color: #34495e;
  margin-bottom: 8px;
  font-size: 0.95rem;
}

.editor-container {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  /* overflow: hidden; */
  width: 96%;
}

.form-actions {
  padding: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafafa;
}

.draft-info {
  display: flex;
  align-items: center;
}

.draft-count {
  color: #666;
  font-size: 0.9rem;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

/* 草稿管理对话框样式 */
.drafts-list {
  max-height: 400px;
  overflow-y: auto;
}

.draft-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  margin-bottom: 10px;
  background: #fafafa;
}

.draft-item:last-child {
  margin-bottom: 0;
}

.draft-info {
  flex: 1;
  margin-right: 15px;
}

.draft-title {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
  color: #2c3e50;
}

.draft-summary {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
}

.draft-meta {
  display: flex;
  gap: 15px;
  font-size: 0.8rem;
  color: #999;
}

.draft-actions {
  display: flex;
  gap: 8px;
}

.empty-drafts {
  text-align: center;
  padding: 40px 0;
}

.preview-content {
  max-height: 70vh;
  overflow-y: auto;
}

.preview-content h1 {
  font-size: 2rem;
  color: #2c3e50;
  margin-bottom: 20px;
  text-align: center;
}

.preview-meta {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 30px;
  color: #7f8c8d;
  font-size: 0.9rem;
}

.preview-body {
  line-height: 1.8;
  color: #333;
}

/* Markdown内容样式 */
.markdown-content {
  max-width: 800px;
  margin: 0 auto;
  line-height: 1.8;
  color: #333;
}

.markdown-content h1 {
  font-size: 2rem;
  margin: 30px 0 20px 0;
  color: #2c3e50;
  border-bottom: 2px solid #3498db;
  padding-bottom: 10px;
}

.markdown-content h2 {
  font-size: 1.5rem;
  margin: 25px 0 15px 0;
  color: #34495e;
}

.markdown-content h3 {
  font-size: 1.2rem;
  margin: 20px 0 10px 0;
  color: #7f8c8d;
}

.markdown-content p {
  margin: 15px 0;
  font-size: 1rem;
}

.markdown-content pre {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 20px 0;
  border-left: 4px solid #3498db;
}

.markdown-content code {
  background: #f1f2f6;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
}

.markdown-content ul {
  margin: 15px 0;
  padding-left: 20px;
}

.markdown-content li {
  margin: 8px 0;
}

.markdown-content blockquote {
  border-left: 4px solid #3498db;
  padding-left: 15px;
  margin: 20px 0;
  color: #7f8c8d;
  font-style: italic;
}

.markdown-content strong {
  font-weight: bold;
  color: #2c3e50;
}

.markdown-content em {
  font-style: italic;
  color: #34495e;
}

.category-option {
  display: flex;
  align-items: center;
}

/* 分类标签样式 */
.category-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
}

.category-tag {
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
}

.category-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.category-tag-active {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.cover-uploader {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.cover-preview {
  width: 180px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 8px;
  border: 1px solid #eee;
}

.el-upload__text {
  color: #999;
  font-size: 0.95rem;
}

.cover-upload-box {
  width: 200px;
  height: 140px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafbfc;
  cursor: pointer;
  transition: border-color 0.3s;
  margin-bottom: 8px;
}

.cover-upload-box:hover {
  border-color: #409eff;
}

.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 8px;
}

.md-editor-icon {
  vertical-align: middle;
  cursor: pointer;
}

.md-editor-icon {
  fill: #606266;
  transition: fill 0.2s ease;
}

.md-editor-icon:hover {
  fill: #409eff;
}



/* 创建分类相关样式 */
.create-category-btn {
  margin-left: 10px;
  display: flex;
  align-items: center;
  gap: 5px;
  background-color: paleturquoise;
}

.category-header {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.create-category-form {
  padding: 10px;
}

.create-category-form .form-item {
  margin-bottom: 20px;
}

.create-category-form .form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.create-category-form .color-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 0.9rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .publish-container {
    margin-left: 0;
    padding: 15px;
  }

  .page-title {
    font-size: 2rem;
  }

  .form-section {
    padding: 20px 15px;
  }

  .form-actions {
    padding: 20px 15px;
    flex-direction: column;
  }

  .form-actions .el-button {
    width: 100%;
  }
}
</style>