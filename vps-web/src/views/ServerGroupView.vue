<template>
    <PageLayout>
      <v-container class="py-6">
        <!-- 页面标题和添加按钮 -->
        <div class="d-flex justify-space-between align-center mb-6">
          <h1 class="text-h4 font-weight-bold">{{ t('groups.title') }}</h1>
          <v-btn
            color="primary"
            prepend-icon="mdi-plus"
            @click="showAddDialog = true"
          >
            {{ t('common.add') }}
          </v-btn>
        </div>
  
        <!-- 分组列表 -->
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="me-2">mdi-folder-network</v-icon>
            {{ t('groups.title') }}
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
            :items="groups"
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
  
            <!-- 类别列 -->
            <template #item.categoryName="{ item }">
              <v-chip
                color="primary"
                size="small"
                variant="tonal"
              >
                {{ getLocalizedText(item.categoryName) || t('groups.uncategorized') }}
              </v-chip>
            </template>
  
            <!-- 地区列 -->
            <template #item.region="{ item }">
              <span v-if="item.region">{{ getLocalizedText(item.region) }}</span>
              <span v-else class="text-medium-emphasis">-</span>
            </template>

            <!-- 国家列 -->
            <template #item.country="{ item }">
              <span v-if="item.country">{{ getLocalizedText(item.country) }}</span>
              <span v-else class="text-medium-emphasis">-</span>
            </template>

            <!-- 城市列 -->
            <template #item.city="{ item }">
              <span v-if="item.city">{{ getLocalizedText(item.city) }}</span>
              <span v-else class="text-medium-emphasis">-</span>
            </template>
  
  
            <!-- 操作列 -->
            <template #item.actions="{ item }">
              <v-btn
                icon="mdi-pencil"
                size="small"
                variant="text"
                @click="editGroup(item)"
              />
              <v-btn
                icon="mdi-delete"
                size="small"
                variant="text"
                color="error"
                @click="deleteGroup(item)"
              />
            </template>
          </v-data-table-server>
        </v-card>
      </v-container>
    </PageLayout>
  
    <!-- 添加/编辑对话框 -->
    <v-dialog 
      v-model="showAddDialog" 
      max-width="700px"
      :scrim="false"
      persistent
      transition="dialog-bottom-transition"
    >
      <v-card elevation="24" class="mx-auto" rounded="xl">
        <v-card-title class="text-h5 bg-gradient-to-r from-primary to-secondary text-white pa-6 d-flex align-center">
          <v-avatar size="40" class="me-3" color="white" variant="flat">
            <v-icon color="primary" size="24">
              {{ editingGroup ? 'mdi-pencil' : 'mdi-plus' }}
            </v-icon>
          </v-avatar>
          <div>
            <div class="text-h5 font-weight-bold">
              {{ editingGroup ? t('groups.editGroup') : t('groups.newGroup') }}
            </div>
            <div class="text-body-2 opacity-90">
              {{ editingGroup ? t('common.editDescription') : t('common.addDescription') }}
            </div>
          </div>
        </v-card-title>
        
        <v-card-text class="pa-8" style="background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);">
          <v-form ref="form" v-model="formValid">
            <!-- 类别选择 -->
            <div class="mb-4">
              <v-select
                v-model="groupForm.categoryId"
                :items="categoryOptions"
                item-title="title"
                item-value="value"
                :label="t('groups.selectCategory')"
                variant="outlined"
                prepend-inner-icon="mdi-folder"
                color="primary"
                :rules="[rules.required]"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </div>
  
            <!-- 名称输入框 -->
            <div class="mb-4">
              <v-text-field
                v-model="groupForm.name"
                :label="t('groups.groupName')"
                :rules="[rules.required]"
                variant="outlined"
                prepend-inner-icon="mdi-folder-network"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </div>
  
            <!-- 描述输入框 -->
            <div class="mb-4">
              <v-textarea
                v-model="groupForm.description"
                :label="t('common.description')"
                variant="outlined"
                prepend-inner-icon="mdi-text"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
                auto-grow
              />
            </div>
  
            <!-- 地区信息 -->
            <v-row class="mb-2">
              <v-col cols="4">
                <v-text-field
                  v-model="groupForm.region"
                  :label="t('groups.region')"
                  variant="outlined"
                  prepend-inner-icon="mdi-earth"
                  color="primary"
                />
              </v-col>
              <v-col cols="4">
                <v-text-field
                  v-model="groupForm.country"
                  :label="t('groups.country')"
                  variant="outlined"
                  prepend-inner-icon="mdi-flag"
                  color="primary"
                />
              </v-col>
              <v-col cols="4">
                <v-text-field
                  v-model="groupForm.city"
                  :label="t('groups.city')"
                  variant="outlined"
                  prepend-inner-icon="mdi-city"
                  color="primary"
                />
              </v-col>
            </v-row>
  
            <!-- 排序 -->
            <div class="mb-4">
              <v-text-field
                v-model.number="groupForm.sortOrder"
                :label="t('common.sortOrder')"
                type="number"
                variant="outlined"
                prepend-inner-icon="mdi-sort-numeric-ascending"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </div>
          </v-form>
        </v-card-text>
  
        <!-- 美化的操作按钮 -->
        <v-card-actions class="pa-6" style="background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%); border-top: 1px solid rgba(0,0,0,0.05);">
          <v-spacer />
          <v-btn 
            variant="outlined" 
            color="grey-darken-1"
            prepend-icon="mdi-close"
            @click="closeDialog"
            class="me-3"
            size="large"
            rounded="lg"
          >
            {{ t('common.cancel') }}
          </v-btn>
          <v-btn
            color="primary"
            :loading="saving"
            :disabled="!formValid"
            prepend-icon="mdi-check"
            @click="saveGroup"
            elevation="4"
            size="large"
            rounded="lg"
            class="px-8"
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
  import { groupAPI, type ServerGroup } from '@/api/group'
  
  const { t } = useI18n()
  
  // 响应式数据
  const groups = ref<ServerGroup[]>([])
  const loading = ref(false)
  const saving = ref(false)
  const showAddDialog = ref(false)
  const formValid = ref(false)
  const searchQuery = ref('')
  const currentPage = ref(1)
  const pageSize = ref(10)
  const totalItems = ref(0)
  const editingGroup = ref<ServerGroup | null>(null)
  const categoryOptions = ref<{title: string, value: number}[]>([])
  
  // 表单数据
  const groupForm = reactive({
    categoryId: 1,
    name: '',
    description: '',
    region: '',
    country: '',
    city: '',
    sortOrder: 0
  })
  
  // 表格头部
  const headers = computed(() => [
    { title: t('groups.groupName'), key: 'name', sortable: false },
    { title: t('groups.selectCategory'), key: 'categoryName', sortable: false, width: 150 },
    { title: t('groups.region'), key: 'region', sortable: false, width: 120 },
    { title: t('groups.country'), key: 'country', sortable: false, width: 120 },
    { title: t('groups.city'), key: 'city', sortable: false, width: 120 },
    { title: t('common.actions'), key: 'actions', sortable: false, width: 120 }
  ])
  
  // 表单验证规则
  const rules = {
    required: (value: any) => !!value || t('validation.required')
  }
  
  // 加载分组数据
  const loadGroups = async () => {
    try {
      loading.value = true
      
      const params = {
        page: currentPage.value - 1, // 后端使用0基索引
        size: pageSize.value,
        ...(searchQuery.value && { search: searchQuery.value })
      }
      
      const response = await groupAPI.getGroups(params)
      
      // 根据后端API响应结构设置数据
      // 后端返回格式: { success: true, data: [...], totalElements: N, totalPages: N, ... }
      groups.value = response.data || []
      totalItems.value = response.totalElements || 0
    } catch (error) {
      console.error('加载分组失败:', error)
      // 显示详细的后端错误信息
      if (error && typeof error === 'object' && 'message' in error) {
        alert(error.message)
      } else {
        alert('加载分组失败')
      }
      groups.value = []
      totalItems.value = 0
    } finally {
      loading.value = false
    }
  }
  
  // 加载类别选项
  const loadCategoryOptions = async () => {
    try {
      const response = await groupAPI.getCategories()
      categoryOptions.value = (response || []).map((cat: any) => ({
        title: getLocalizedText(cat.name),
        value: cat.id
      }))
    } catch (error) {
      console.error('加载类别选项失败:', error)
      categoryOptions.value = []
    }
  }
  
  // 搜索处理
  const handleSearch = () => {
    currentPage.value = 1
    loadGroups()
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
    
    if (page !== currentPage.value) {
      currentPage.value = page
      needsReload = true
    }
    
    if (itemsPerPage !== pageSize.value) {
      pageSize.value = itemsPerPage
      currentPage.value = 1
      needsReload = true
    }
    
    if (needsReload) {
      loadGroups()
    }
  }
  
  // 编辑分组
  const editGroup = (group: ServerGroup) => {
    editingGroup.value = group
    Object.assign(groupForm, {
      categoryId: group.categoryId || 1,
      name: group.name,
      description: group.description || '',
      region: group.region || '',
      country: group.country || '',
      city: group.city || '',
      sortOrder: 0
    })
    showAddDialog.value = true
  }
  
  // 删除分组
  const deleteGroup = async (group: ServerGroup) => {
    if (confirm(t('groups.confirmDeleteTitle') + ': ' + getLocalizedText(group.name))) {
      try {
        await groupAPI.deleteGroup(group.id)
        await loadGroups()
        alert(t('groups.groupDeletedSuccess'))
      } catch (error: any) {
        console.error('删除分组失败:', error)
        let errorMessage = '删除失败'
        
        if (error.response && error.response.data && error.response.data.message) {
          errorMessage = error.response.data.message
        } else if (error.message) {
          errorMessage = error.message
        }
        
        alert(errorMessage)
      }
    }
  }
  
  // 保存分组
  const saveGroup = async () => {
    try {
      saving.value = true
      
      if (editingGroup.value) {
        await groupAPI.updateGroup(editingGroup.value.id, groupForm)
      } else {
        await groupAPI.createGroup(groupForm)
      }
      
      closeDialog()
      await loadGroups()
    } catch (error) {
      console.error('保存分组失败:', error)
    } finally {
      saving.value = false
    }
  }
  
  // 关闭对话框
  const closeDialog = () => {
    showAddDialog.value = false
    editingGroup.value = null
    Object.assign(groupForm, {
      categoryId: 1,
      name: '',
      description: '',
      region: '',
      country: '',
      city: '',
      sortOrder: 0
    })
  }
  
  // 组件挂载时加载数据
  onMounted(() => {
    Promise.all([
      loadGroups(),
      loadCategoryOptions()
    ])
  })
  </script>
  