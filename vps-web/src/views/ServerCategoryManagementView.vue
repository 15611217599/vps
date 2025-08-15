<template>
  <PageLayout>
    <v-container class="py-6">
      <!-- 页面标题和添加按钮 -->
      <div class="d-flex justify-space-between align-center mb-6">
        <h1 class="text-h4 font-weight-bold">{{ t('serverCategory.title') }}</h1>
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="showAddDialog = true"
        >
          {{ t('common.add') }}
        </v-btn>
      </div>

      <!-- 类别列表 -->
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="me-2">mdi-folder-multiple</v-icon>
          {{ t('serverCategory.list') }}
          <v-spacer />
          <v-text-field
            v-model="searchQuery"
            :placeholder="t('common.search')"
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            hide-details
            clearable
            style="max-width: 300px"
            @input="debouncedSearch"
          />
        </v-card-title>

        <v-data-table-server
          :headers="headers"
          :items="categories"
          :loading="loading"
          :items-length="totalItems"
          :items-per-page="pageSize"
          :items-per-page-options="[5, 10, 20, 50]"
          :items-per-page-text="t('common.itemsPerPage')"
          @update:options="handleOptionsUpdate"
        >
          <!-- 名称列 -->
          <template #item.name="{ item }">
            <div>
              <div class="font-weight-medium">{{ getLocalizedText(item.name) }}</div>
              <div class="text-caption text-medium-emphasis" v-if="item.description">
                {{ getLocalizedText(item.description) }}
              </div>
            </div>
          </template>

          <!-- 状态列 -->
          <template #item.isActive="{ item }">
            <v-chip
              :color="item.isActive ? 'success' : 'error'"
              size="small"
              variant="flat"
            >
              {{ item.isActive ? t('common.enabled') : t('common.disabled') }}
            </v-chip>
          </template>

          <!-- 操作列 -->
          <template #item.actions="{ item }">
            <v-btn
              icon="mdi-pencil"
              size="small"
              variant="text"
              @click="editCategory(item)"
            />
            <v-btn
              icon="mdi-delete"
              size="small"
              variant="text"
              color="error"
              @click="deleteCategory(item)"
            />
          </template>
        </v-data-table-server>
      </v-card>
    </v-container>
  </PageLayout>

  <!-- 添加/编辑对话框 -->
  <v-dialog v-model="showAddDialog" max-width="500px">
    <v-card>
      <v-card-title class="text-h6">
        {{ editingCategory ? t('common.edit') : t('common.add') }}{{ t('serverCategory.category') }}
      </v-card-title>
      
      <v-card-text>
        <v-form ref="form" v-model="formValid">
          <v-text-field
            v-model="categoryForm.name"
            :label="t('serverCategory.name')"
            :rules="[rules.required]"
            variant="outlined"
            density="compact"
          />

          <v-textarea
            v-model="categoryForm.description"
            :label="t('common.description')"
            variant="outlined"
            density="compact"
            rows="2"
          />

          <v-row>
            <v-col cols="6">
              <v-text-field
                v-model.number="categoryForm.sortOrder"
                :label="t('common.sortOrder')"
                type="number"
                variant="outlined"
                density="compact"
              />
            </v-col>
            <v-col cols="6" class="d-flex align-center">
              <v-switch
                v-model="categoryForm.isActive"
                :label="t('common.status')"
                color="primary"
                hide-details
              />
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="closeDialog">
          {{ t('common.cancel') }}
        </v-btn>
        <v-btn
          color="primary"
          :loading="saving"
          :disabled="!formValid"
          @click="saveCategory"
        >
          {{ t('common.save') }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import PageLayout from '@/components/PageLayout.vue'
import { getLocalizedText } from '@/utils/i18n'
import {
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory as deleteCategoryApi,
  type ServerCategory
} from '@/api/category'

const { t } = useI18n()

// 响应式数据
const categories = ref<ServerCategory[]>([])
const loading = ref(false)
const saving = ref(false)
const showAddDialog = ref(false)
const formValid = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const editingCategory = ref<ServerCategory | null>(null)



// 表单数据
const categoryForm = reactive({
  name: '',
  description: '',
  sortOrder: 0,
  isActive: true
})

// 表格头部
const headers = computed(() => [
  { title: t('serverCategory.name'), key: 'name', sortable: false },
  { title: t('common.status'), key: 'isActive', sortable: false, width: 100 },
  { title: t('common.actions'), key: 'actions', sortable: false, width: 120 }
])

// 表单验证规则
const rules = {
  required: (value: string) => !!value || t('validation.required')
}

// 加载类别数据
const loadCategories = async () => {
  try {
    loading.value = true
    
    const params = {
      page: currentPage.value - 1, // 后端使用0基索引
      size: pageSize.value,
      ...(searchQuery.value && { search: searchQuery.value })
    }
    
    const response = await getCategories(params)
    
    // 根据后端API响应结构设置数据
    // 后端返回格式: { success: true, data: [...], totalElements: N, totalPages: N, ... }
    categories.value = response.data || []
    totalItems.value = response.totalElements || 0
  } catch (error) {
    console.error('加载类别失败:', error)
    categories.value = []
    totalItems.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1 // 搜索时重置到第一页
  loadCategories()
}

// 防抖搜索
let searchTimeout: number
const debouncedSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    handleSearch()
  }, 300)
}

// 服务器端数据表选项更新处理
const handleOptionsUpdate = (options: any) => {
  const { page, itemsPerPage } = options
  
  let needsReload = false
  
  // v-data-table-server uses 1-based page indexing
  if (page !== currentPage.value) {
    currentPage.value = page
    needsReload = true
  }
  
  if (itemsPerPage !== pageSize.value) {
    pageSize.value = itemsPerPage
    currentPage.value = 1 // 重置到第一页
    needsReload = true
  }
  
  if (needsReload) {
    loadCategories()
  }
}

// 编辑类别
const editCategory = (category: ServerCategory) => {
  editingCategory.value = category
  // 编辑时使用原始的多语言JSON文本，不使用解析后的文本
  Object.assign(categoryForm, {
    name: category.name, // 保持原始JSON格式，如 {"cn": "中文", "en": "English"}
    description: category.description || '',
    sortOrder: category.sortOrder || 0,
    isActive: category.isActive
  })
  showAddDialog.value = true
}

// 删除类别
const deleteCategory = async (category: ServerCategory) => {
  if (confirm(t('serverCategory.confirmDelete', { name: getLocalizedText(category.name) }))) {
    try {
      await deleteCategoryApi(category.id)
      await loadCategories()
    } catch (error) {
      console.error('删除类别失败:', error)
    }
  }
}

// 保存类别
const saveCategory = async () => {
  try {
    saving.value = true
    
    if (editingCategory.value) {
      await updateCategory(editingCategory.value.id, categoryForm)
    } else {
      await createCategory(categoryForm)
    }
    
    closeDialog()
    await loadCategories()
  } catch (error) {
    console.error('保存类别失败:', error)
  } finally {
    saving.value = false
  }
}

// 关闭对话框
const closeDialog = () => {
  showAddDialog.value = false
  editingCategory.value = null
  Object.assign(categoryForm, {
    name: '',
    description: '',
    sortOrder: 0,
    isActive: true
  })
}


// 组件挂载时加载数据
onMounted(() => {
  loadCategories()
})
</script>
