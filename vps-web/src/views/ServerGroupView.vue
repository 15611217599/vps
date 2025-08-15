<template>
  <PageLayout :title="$t('nav.serverGroup')">
    <v-container>
      <!-- 顶部操作栏 -->
      <v-row class="mb-4">
        <v-col cols="12">
          <v-card class="pa-4">
            <div class="d-flex align-center justify-space-between">
              <div>
                <h2 class="text-h5 font-weight-bold mb-2">{{ $t('groups.title') }}</h2>
                <p class="text-subtitle-1 text-medium-emphasis">{{ $t('groups.subtitle') }}</p>
              </div>
              <v-btn
                :color="themeStore.currentColors.primary"
                prepend-icon="mdi-folder-plus"
                @click="openGroupDialog"
              >
                {{ $t('groups.newGroup') }}
              </v-btn>
            </div>
          </v-card>
        </v-col>
      </v-row>

      <!-- 分组列表 -->
      <v-row>
        <v-col
          v-for="group in serverGroups"
          :key="group.id"
          cols="12"
          lg="6"
          xl="4"
        >
          <v-card class="group-card h-100">
            <!-- 分组头部 -->
            <v-card-title class="group-header">
              <div class="d-flex align-center justify-space-between w-100">
                <div class="d-flex align-center">
                  <v-icon 
                    :color="themeStore.currentColors.primary" 
                    size="24" 
                    class="mr-3"
                  >
                    mdi-folder-network
                  </v-icon>
                  <div>
                    <h3 class="text-h6 font-weight-bold">{{ group.name }}</h3>
                    <p class="text-caption text-medium-emphasis mb-0">
                      {{ group.description || $t('groups.noDescription') }}
                    </p>
                  </div>
                </div>
                <v-menu>
                  <template v-slot:activator="{ props }">
                    <v-btn
                      icon
                      variant="text"
                      size="small"
                      v-bind="props"
                    >
                      <v-icon>mdi-dots-vertical</v-icon>
                    </v-btn>
                  </template>
                  <v-list>
                    <v-list-item @click="editGroup(group)">
                      <template v-slot:prepend>
                        <v-icon>mdi-pencil</v-icon>
                      </template>
                      <v-list-item-title>{{ $t('groups.editGroup') }}</v-list-item-title>
                    </v-list-item>
                    <v-list-item @click="deleteGroup(group)">
                      <template v-slot:prepend>
                        <v-icon color="error">mdi-delete</v-icon>
                      </template>
                      <v-list-item-title>{{ $t('groups.deleteGroup') }}</v-list-item-title>
                    </v-list-item>
                  </v-list>
                </v-menu>
              </div>
            </v-card-title>

            <v-divider />

            <!-- 分组详情 -->
            <v-card-text class="pa-0">
              <div class="group-summary pa-4">
                <div class="d-flex align-center justify-space-between">
                  <div>
                    <div class="text-h6 font-weight-bold">
                      {{ $t('groups.groupDetails') }}
                    </div>
                    <div class="text-caption text-medium-emphasis">
                      {{ group.region }} - {{ group.country }}
                    </div>
                  </div>
                  <v-chip
                    color="primary"
                    size="small"
                    variant="tonal"
                  >
                    {{ group.categoryName || $t('groups.uncategorized') }}
                  </v-chip>
                </div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- 分组管理对话框 -->
      <v-dialog v-model="groupDialog" max-width="600px" persistent>
        <v-card class="group-dialog">
          <v-card-title class="dialog-header">
            <div class="d-flex align-center">
              <v-icon 
                :color="themeStore.currentColors.primary" 
                size="28" 
                class="mr-3"
              >
                {{ isEditingGroup ? 'mdi-pencil' : 'mdi-folder-plus' }}
              </v-icon>
              <div>
                <h3 class="text-h5 font-weight-bold">
                  {{ isEditingGroup ? $t('groups.editGroup') : $t('groups.newGroup') }}
                </h3>
                <p class="text-subtitle-2 text-medium-emphasis mb-0">
                  {{ isEditingGroup ? $t('groups.editGroup') : $t('groups.createGroup') }}
                </p>
              </div>
            </div>
            <v-btn
              icon
              variant="text"
              size="small"
              @click="closeGroupDialog"
              class="ml-auto"
            >
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </v-card-title>
          
          <v-divider />
          
          <v-card-text class="pa-6">
            <v-form ref="groupForm" v-model="groupFormValid" lazy-validation>
              <v-row>
                <v-col cols="12">
                  <v-select
                    v-model="newGroup.categoryId"
                    :items="categoryOptions"
                    item-title="title"
                    item-value="value"
                    :label="$t('groups.selectCategory')"
                    variant="outlined"
                    prepend-inner-icon="mdi-folder"
                    :rules="[v => !!v || $t('validation.required')]"
                    required
                  />
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    v-model="newGroup.name"
                    :label="$t('groups.groupName')"
                    variant="outlined"
                    prepend-inner-icon="mdi-folder-network"
                    placeholder="例如：欧洲(德国) 1"
                    :rules="[v => !!v || $t('validation.required')]"
                    required
                  />
                </v-col>
                <v-col cols="12">
                  <v-textarea
                    v-model="newGroup.description"
                    :label="$t('groups.groupDescription')"
                    variant="outlined"
                    prepend-inner-icon="mdi-text"
                    placeholder="例如：德国法兰克福数据中心"
                    rows="3"
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newGroup.region"
                    :label="$t('groups.region')"
                    variant="outlined"
                    prepend-inner-icon="mdi-earth"
                    placeholder="例如：Europe"
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newGroup.country"
                    :label="$t('groups.country')"
                    variant="outlined"
                    prepend-inner-icon="mdi-flag"
                    placeholder="例如：Germany"
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newGroup.city"
                    :label="$t('groups.city')"
                    variant="outlined"
                    prepend-inner-icon="mdi-city"
                    placeholder="例如：Frankfurt"
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newGroup.sortOrder"
                    :label="$t('groups.sortOrder')"
                    variant="outlined"
                    prepend-inner-icon="mdi-sort-numeric-ascending"
                    :placeholder="$t('groups.sortOrderHint')"
                    type="number"
                  />
                </v-col>
              </v-row>
            </v-form>
          </v-card-text>
          
          <v-divider />
          
          <v-card-actions class="pa-6">
            <v-spacer />
            <v-btn
              variant="outlined"
              size="large"
              @click="closeGroupDialog"
              class="mr-3"
            >
              {{ $t('groups.cancelGroup') }}
            </v-btn>
            <v-btn
              :color="themeStore.currentColors.primary"
              variant="elevated"
              size="large"
              @click="saveGroup"
              :disabled="!groupFormValid"
              :loading="loading"
            >
              <v-icon class="mr-2">{{ isEditingGroup ? 'mdi-content-save' : 'mdi-plus' }}</v-icon>
              {{ isEditingGroup ? $t('groups.saveGroup') : $t('groups.createGroup') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>



      <!-- 删除确认对话框 -->
      <v-dialog v-model="deleteDialog" max-width="480px" persistent>
        <v-card class="delete-dialog">
          <v-card-title class="delete-dialog-header">
            <div class="d-flex align-center">
              <v-icon 
                color="error" 
                size="32" 
                class="mr-3"
              >
                mdi-alert-circle
              </v-icon>
              <div>
                <h3 class="text-h5 font-weight-bold">
                  {{ $t('groups.confirmDeleteTitle') }}
                </h3>
                <p class="text-subtitle-2 text-medium-emphasis mb-0">
                  {{ $t('groups.confirmDeleteSubtitle') }}
                </p>
              </div>
            </div>
          </v-card-title>
          
          <v-divider />
          
          <v-card-text class="pa-6">
            <div class="delete-content">
              <v-alert
                type="warning"
                variant="tonal"
                class="mb-4"
                border="start"
                border-color="warning"
              >
                <div class="d-flex align-center">
                  <v-icon class="mr-2">mdi-information</v-icon>
                  <span>{{ $t('groups.deleteWarning') }}</span>
                </div>
              </v-alert>
              
              <div v-if="itemToDelete" class="group-info">
                <h4 class="mb-2">{{ $t('groups.groupToDelete') }}</h4>
                <div class="group-details">
                  <div class="detail-item">
                    <v-icon class="mr-2" size="small">mdi-folder-network</v-icon>
                    <strong>{{ itemToDelete.name }}</strong>
                  </div>
                  <div v-if="itemToDelete.description" class="detail-item">
                    <v-icon class="mr-2" size="small">mdi-text</v-icon>
                    {{ itemToDelete.description }}
                  </div>

                </div>
              </div>
            </div>
          </v-card-text>
          
          <v-divider />
          
          <v-card-actions class="pa-6">
            <v-spacer />
            <v-btn
              variant="outlined"
              size="large"
              @click="deleteDialog = false"
              class="mr-3"
            >
              <v-icon class="mr-2">mdi-close</v-icon>
              {{ $t('common.cancel') }}
            </v-btn>
            <v-btn
              color="error"
              variant="elevated"
              size="large"
              @click="confirmDelete"
              :loading="loading"
            >
              <v-icon class="mr-2">mdi-delete</v-icon>
              {{ $t('groups.confirmDeleteAction') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <!-- 消息提示 -->
      <v-snackbar
        v-model="snackbar"
        :color="snackbarColor"
        :timeout="3000"
      >
        {{ snackbarText }}
        <template v-slot:actions>
          <v-btn
            color="white"
            variant="text"
            @click="snackbar = false"
          >
            {{ $t('groups.close') }}
          </v-btn>
        </template>
      </v-snackbar>
    </v-container>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import PageLayout from '@/components/PageLayout.vue'
import { groupAPI, type ServerGroup, type GroupOption } from '@/api/group'

const { t } = useI18n()
const router = useRouter()
const themeStore = useThemeStore()

// 使用类型定义
// Server, ServerGroup, GroupOption 类型已从 API 模块导入

// 基础状态
const loading = ref(false)

// 分组管理状态
const groupDialog = ref(false)
const groupFormValid = ref(false)
const isEditingGroup = ref(false)
const deleteDialog = ref(false)
const itemToDelete = ref<any>(null)

// 消息提示
const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')

// 表单引用
const groupForm = ref()

// 分组数据
const serverGroups = ref<ServerGroup[]>([
  {
    id: 1,
    name: '欧洲(德国)',
    description: '德国法兰克福数据中心',
    categoryId: 1,
    categoryName: 'VPS服务器'
  },
  {
    id: 2,
    name: '新加坡',
    description: '新加坡数据中心',
    categoryId: 1,
    categoryName: 'VPS服务器'
  },
  {
    id: 3,
    name: '中国香港',
    description: '香港数据中心',
    categoryId: 1,
    categoryName: 'VPS服务器'
  },
  {
    id: 4,
    name: '日本东京',
    description: '东京数据中心',
    categoryId: 1,
    categoryName: 'VPS服务器'
  },
  {
    id: 5,
    name: '个人服务器',
    description: '个人使用的服务器',
    categoryId: 2,
    categoryName: '专用服务器'
  },
  {
    id: 6,
    name: '美国硅谷',
    description: '硅谷数据中心',
    categoryId: 3,
    categoryName: '云服务器'
  },
  {
    id: 7,
    name: '美国洛杉矶',
    description: '洛杉矶数据中心',
    categoryId: 3,
    categoryName: '云服务器'
  }
])

// 分组选项
const groupOptions = ref<GroupOption[]>([])

// 类别选项
const categoryOptions = ref([])

// 获取类别选项
const fetchCategoryOptions = async () => {
  try {
    const categories = await groupAPI.getCategories()
    
    if (categories && categories.length > 0) {
      categoryOptions.value = categories.map(cat => ({
        title: cat.name,
        value: cat.id
      }))
    } else {
      // 使用默认选项
      categoryOptions.value = [
        { title: 'VPS服务器', value: 1 },
        { title: '专用服务器', value: 2 },
        { title: '云服务器', value: 3 }
      ]
    }
  } catch (error: any) {
    console.error('获取类别选项失败:', error)
    showMessage(t('groups.getCategoriesFailed'), 'error')
    // 使用默认选项
    categoryOptions.value = [
      { title: 'VPS服务器', value: 1 },
      { title: '专用服务器', value: 2 },
      { title: '云服务器', value: 3 }
    ]
  }
}

// 新分组对象
const newGroup = ref({
  id: null,
  categoryId: null,
  name: '',
  description: '',
  region: '',
  country: '',
  city: '',
  sortOrder: 0
})

// 获取分组选项
const fetchGroupOptions = async () => {
  try {
    groupOptions.value = await groupAPI.getGroupOptions()
  } catch (error: any) {
    console.error('获取分组选项失败:', error)
    showMessage(error.message || t('groups.getGroupsFailed'), 'error')
  }
}





// 打开分组对话框
const openGroupDialog = () => {
  newGroup.value = {
    id: null,
    categoryId: null,
    name: '',
    description: '',
    region: '',
    country: '',
    city: '',
    sortOrder: 0
  }
  isEditingGroup.value = false
  groupDialog.value = true
  nextTick(() => {
    if (groupForm.value) {
      groupForm.value.resetValidation()
    }
  })
}

// 关闭分组对话框
const closeGroupDialog = () => {
  groupDialog.value = false
  newGroup.value = {
    id: null,
    categoryId: null,
    name: '',
    description: '',
    region: '',
    country: '',
    city: '',
    sortOrder: 0
  }
}

// 保存分组
const saveGroup = async () => {
  if (!groupForm.value) return

  const { valid } = await groupForm.value.validate()
  if (!valid) return

  loading.value = true

  try {
    if (isEditingGroup.value) {
      // 编辑分组
      await groupAPI.updateGroup(newGroup.value.id!, {
        categoryId: newGroup.value.categoryId!,
        name: newGroup.value.name,
        description: newGroup.value.description,
        region: newGroup.value.region,
        country: newGroup.value.country,
        city: newGroup.value.city,
        sortOrder: newGroup.value.sortOrder
      })
      showMessage(t('groups.groupUpdatedSuccess'), 'success')
    } else {
      // 创建分组
      await groupAPI.createGroup({
        categoryId: newGroup.value.categoryId!,
        name: newGroup.value.name,
        description: newGroup.value.description,
        region: newGroup.value.region,
        country: newGroup.value.country,
        city: newGroup.value.city,
        sortOrder: newGroup.value.sortOrder
      })
      showMessage(t('groups.groupCreatedSuccess'), 'success')
    }

    closeGroupDialog()
    // 刷新数据
    await Promise.all([
      fetchServerGroups(),
      fetchGroupOptions()
    ])
  } catch (error: any) {
    console.error('保存分组失败:', error)
    showMessage(error.message || t('groups.groupCreateFailed'), 'error')
  } finally {
    loading.value = false
  }
}



// 编辑分组
const editGroup = (group: ServerGroup) => {
  newGroup.value = {
    id: group.id,
    categoryId: group.categoryId || null,
    name: group.name,
    description: group.description || '',
    region: group.region || '',
    country: group.country || '',
    city: group.city || '',
    sortOrder: 0 // 这里可以从group获取，如果有的话
  }
  isEditingGroup.value = true
  groupDialog.value = true
  nextTick(() => {
    if (groupForm.value) {
      groupForm.value.resetValidation()
    }
  })
}

// 删除分组
const deleteGroup = (group: ServerGroup) => {
  itemToDelete.value = group
  deleteDialog.value = true
}

// 确认删除
const confirmDelete = async () => {
  if (itemToDelete.value) {
    try {
      loading.value = true
      await groupAPI.deleteGroup(itemToDelete.value.id)
      showMessage(t('groups.groupDeletedSuccess'), 'success')
      // 刷新数据
      await Promise.all([
        fetchServerGroups(),
        fetchGroupOptions()
      ])
    } catch (error: any) {
      console.error('删除分组失败:', error)
      showMessage(error.message || t('groups.groupDeleteFailed'), 'error')
    } finally {
      loading.value = false
    }
  }
  deleteDialog.value = false
  itemToDelete.value = null
}



// 获取服务器分组数据
const fetchServerGroups = async () => {
  try {
    loading.value = true
    serverGroups.value = await groupAPI.getGroupedServers()
  } catch (error: any) {
    console.error('获取服务器分组失败:', error)
    showMessage(error.message || t('groups.getServerGroupsFailed'), 'error')
  } finally {
    loading.value = false
  }
}

// 显示消息
const showMessage = (message: string, color: string = 'success') => {
  snackbarText.value = message
  snackbarColor.value = color
  snackbar.value = true
}

// 组件初始化
onMounted(async () => {
  await Promise.all([
    fetchServerGroups(),
    fetchGroupOptions(),
    fetchCategoryOptions()
  ])
})
</script>

<style scoped>
.group-card {
  border-radius: 16px !important;
  transition: all 0.3s ease;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.08);
}

.group-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.group-header {
  background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.05) 0%, rgba(var(--v-theme-primary), 0.02) 100%);
  border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  padding: 20px !important;
}

/* 服务器相关样式已移除 */

.empty-state {
  background: rgba(var(--v-theme-surface), 0.3);
  border-radius: 8px;
  margin: 16px;
}

/* 服务器对话框样式已移除 */

.dialog-header {
  background: linear-gradient(135deg, rgba(var(--v-theme-success), 0.05) 0%, rgba(var(--v-theme-success), 0.02) 100%);
  border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  padding: 24px !important;
}

:deep(.v-text-field .v-field) {
  border-radius: 8px;
}

:deep(.v-select .v-field) {
  border-radius: 8px;
}

.v-btn {
  border-radius: 8px !important;
  text-transform: none;
  font-weight: 500;
}

/* 删除对话框美化样式 */
.delete-dialog {
  border-radius: 16px !important;
  overflow: hidden;
}

.delete-dialog-header {
  background: linear-gradient(135deg, rgba(var(--v-theme-error), 0.05) 0%, rgba(var(--v-theme-error), 0.02) 100%);
  border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  padding: 24px !important;
}

.delete-content {
  text-align: left;
}

.group-info {
  background: rgba(var(--v-theme-surface), 0.5);
  border-radius: 8px;
  padding: 16px;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.08);
}

.group-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  color: rgba(var(--v-theme-on-surface), 0.87);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .dialog-header {
    padding: 16px !important;
  }
  
  .delete-dialog-header {
    padding: 16px !important;
  }
  
  .v-card-actions {
    padding: 16px !important;
  }
}
</style>