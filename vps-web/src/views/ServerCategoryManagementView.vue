<template>
  <PageLayout>
    <v-container class="py-6">
      <!-- 页面标题和添加按钮 -->
      <div class="d-flex justify-space-between align-center mb-6">
        <h1 class="text-h4 font-weight-bold">{{ TEXTS.serverCategory.title }}</h1>
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="showAddDialog = true"
        >
          {{ TEXTS.common.add }}
        </v-btn>
      </div>

      <!-- 类别列表 -->
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="me-2">mdi-folder-multiple</v-icon>
          {{ TEXTS.serverCategory.title }}
          <v-spacer />
          <v-text-field
            v-model="searchQuery"
            :placeholder="TEXTS.common.search"
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
          :items-per-page-text="TEXTS.common.itemsPerPage"
          @update:options="handleOptionsUpdate"
        >
          <!-- 名称列 -->
          <template #item.name="{ item }">
            <div>
              <div class="font-weight-medium">{{ item.name }}</div>
              <div class="text-caption text-medium-emphasis" v-if="item.description">
                {{ item.description }}
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
              {{ item.isActive ? TEXTS.common.enabled : TEXTS.common.disabled }}
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
  <UnifiedDialog
    v-model="showAddDialog"
    :title="(editingCategory ? TEXTS.common.edit : TEXTS.common.add) + TEXTS.serverCategory.title"
    :is-edit="!!editingCategory"
    :loading="saving"
    :disabled="!formValid"
    max-width="800px"
    width="80vw"
    @save="saveCategory"
    @cancel="closeDialog"
  >
    <v-form ref="form" v-model="formValid">
      <v-text-field
        v-model="categoryForm.name"
        :label="TEXTS.serverCategory.name"
        :rules="[rules.required]"
        variant="outlined"
        prepend-inner-icon="mdi-folder"
        color="primary"
        density="comfortable"
        class="mb-2"
        bg-color="white"
      />

      <v-textarea
        v-model="categoryForm.description"
        :label="TEXTS.common.description"
        variant="outlined"
        prepend-inner-icon="mdi-text"
        color="primary"
        density="comfortable"
        class="mb-2"
        bg-color="white"
        rows="2"
        auto-grow
      />

      <v-row>
        <v-col cols="6">
          <v-text-field
            v-model.number="categoryForm.sortOrder"
            :label="TEXTS.common.sortOrder"
            type="number"
            variant="outlined"
            prepend-inner-icon="mdi-sort-numeric-ascending"
            color="primary"
            density="comfortable"
            class="mb-2"
            bg-color="white"
          />
        </v-col>
        <v-col cols="6" class="d-flex align-center">
          <v-switch
            v-model="categoryForm.isActive"
            :label="TEXTS.common.status"
            color="primary"
            class="mt-4"
          />
        </v-col>
      </v-row>
    </v-form>
  </UnifiedDialog>

  <!-- 删除确认对话框 -->
  <ConfirmDeleteDialog
    v-model="showDeleteDialog"
    :title="TEXTS.serverCategory.deleteWarning"
    :message="`确定要删除分类 '${deletingCategory?.name || ''}' 吗？`"
    :item-name="deletingCategory?.name || ''"
    :loading="deleting"
    @confirm="confirmDelete"
    @cancel="showDeleteDialog = false"
  />
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { TEXTS } from '@/constants/texts'

import PageLayout from '@/components/PageLayout.vue'
import UnifiedDialog from '@/components/UnifiedDialog.vue'
import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'

import {
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory as deleteCategoryApi,
  type ServerCategory
} from '@/api/category'

// 移除国际化

// 响应式数据
const categories = ref<ServerCategory[]>([])  
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const showAddDialog = ref(false)
const showDeleteDialog = ref(false)
const deletingCategory = ref<ServerCategory | null>(null)
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
  { title: TEXTS.serverCategory.name, key: 'name', sortable: false },
  { title: TEXTS.common.status, key: 'isActive', sortable: false, width: 100 },
  { title: TEXTS.common.actions, key: 'actions', sortable: false, width: 120 }
])

// 表单验证规则
const rules = {
  required: (value: string) => !!value || '此字段为必填项'
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
    console.error(TEXTS.serverCategory.loadFailed, error)
    // 显示详细的后端错误信息
    if (error && typeof error === 'object' && 'message' in error) {
      alert(error.message)
    } else {
      alert(TEXTS.serverCategory.loadFailed)
    }
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
  Object.assign(categoryForm, {
    name: category.name,
    description: category.description || '',
    sortOrder: category.sortOrder || 0,
    isActive: category.isActive
  })
  showAddDialog.value = true
}

// 删除类别
const deleteCategory = (category: ServerCategory) => {
  deletingCategory.value = category
  showDeleteDialog.value = true
}

// 确认删除
const confirmDelete = async () => {
  if (!deletingCategory.value) return
  
  try {
    deleting.value = true
    await deleteCategoryApi(deletingCategory.value.id)
    showDeleteDialog.value = false
    await loadCategories()
  } catch (error) {
    console.error(TEXTS.serverCategory.deleteFailed, error)
    // 显示详细的后端错误信息
    if (error && typeof error === 'object' && 'message' in error) {
      alert(error.message)
    } else {
      alert(TEXTS.serverCategory.deleteFailed)
    }
  } finally {
    deleting.value = false
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
    console.error(TEXTS.serverCategory.saveFailed, error)
    // 显示详细的后端错误信息
    if (error && typeof error === 'object' && 'message' in error) {
      alert(error.message)
    } else {
      alert(TEXTS.serverCategory.saveFailed)
    }
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
