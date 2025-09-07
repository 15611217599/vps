<template>
  <div v-if="!authStore.isAdmin" class="d-flex justify-center align-center" style="height: 50vh;">
    <v-alert type="error" variant="tonal" class="text-center">
      <v-alert-title>访问被拒绝</v-alert-title>
      <div>您没有权限访问此页面。只有管理员可以管理服务器分组。</div>
    </v-alert>
  </div>
    <HomeLayout v-else>
      <v-container class="py-6">
        <!-- 分组列表 -->
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="me-2">mdi-folder-network</v-icon>
            分组列表
            <v-spacer />
            <div class="d-flex align-center ga-3">
              <v-text-field
                v-model="searchQuery"
                :placeholder="'搜索'"
                prepend-inner-icon="mdi-magnify"
                variant="outlined"
                density="compact"
                hide-details
                clearable
                style="min-width: 250px; max-width: 400px"
                @input="debouncedSearch"
              />
              <v-btn
                color="primary"
                prepend-icon="mdi-plus"
                @click="showAddDialog = true"
              >
                {{ '添加' }}
              </v-btn>
            </div>
          </v-card-title>
  
          <v-data-table-server
            :headers="headers"
            :items="groups"
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
  
            <!-- 类别列 -->
            <template #item.categoryName="{ item }">
              <v-chip
                color="primary"
                size="small"
                variant="tonal"
              >
                {{ item.categoryName || TEXTS.groups.uncategorized }}
              </v-chip>
            </template>
  
            <!-- 地区列 -->
            <template #item.region="{ item }">
              <span v-if="item.region">{{ item.region }}</span>
              <span v-else class="text-medium-emphasis">-</span>
            </template>

            <!-- 国家列 -->
            <template #item.country="{ item }">
              <span v-if="item.country">{{ item.country }}</span>
              <span v-else class="text-medium-emphasis">-</span>
            </template>

            <!-- 城市列 -->
            <template #item.city="{ item }">
              <span v-if="item.city">{{ item.city }}</span>
              <span v-else class="text-medium-emphasis">-</span>
            </template>

            <!-- 激活状态列 -->
            <template #item.isActive="{ item }">
              <v-chip
                :color="item.isActive ? 'success' : 'error'"
                size="small"
                variant="flat"
              >
                {{ item.isActive ? TEXTS.common.active : TEXTS.common.inactive }}
              </v-chip>
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
    </HomeLayout>
  
    <!-- 添加/编辑对话框 -->
  <UnifiedDialog
    v-model="showAddDialog"
    :title="(editingGroup ? '编辑' : '添加') + '服务器分组'"
    :is-edit="!!editingGroup"
    :loading="saving"
    :disabled="!formValid"
    max-width="800px"
    width="85vw"
    :auto-width="false"
    @save="saveGroup"
    @cancel="closeDialog"
  >  
      <v-form ref="form" v-model="formValid">
        <!-- 类别选择 -->
        <div class="mb-4">
          <v-select
            v-model="groupForm.categoryId"
            :items="categoryOptions"
            item-title="title"
            item-value="value"
            :label="TEXTS.groups.selectCategory"
            variant="outlined"
            prepend-inner-icon="mdi-folder"
            color="primary"
            :rules="[rules.required]"
            density="comfortable"
            class="mb-2"
            bg-color="white"
            :menu-props="{ maxHeight: '300px', zIndex: 9999 }"
          />
        </div>

        <!-- 名称输入框 -->
        <div class="mb-4">
          <v-text-field
            v-model="groupForm.name"
            :label="TEXTS.groups.groupName"
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
            :label="'描述'"
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
              :label="TEXTS.groups.region"
              variant="outlined"
              prepend-inner-icon="mdi-earth"
              color="primary"
              density="comfortable"
              bg-color="white"
            />
          </v-col>
          <v-col cols="4">
            <v-text-field
              v-model="groupForm.country"
              :label="TEXTS.groups.country"
              variant="outlined"
              prepend-inner-icon="mdi-flag"
              color="primary"
              density="comfortable"
              bg-color="white"
            />
          </v-col>
          <v-col cols="4">
            <v-text-field
              v-model="groupForm.city"
              :label="TEXTS.groups.city"
              variant="outlined"
              prepend-inner-icon="mdi-city"
              color="primary"
              density="comfortable"
              bg-color="white"
            />
          </v-col>
        </v-row>

        <!-- 排序 -->
        <div class="mb-4">
          <v-text-field
            v-model.number="groupForm.sortOrder"
            :label="TEXTS.common.sortOrder"
            type="number"
            variant="outlined"
            prepend-inner-icon="mdi-sort-numeric-ascending"
            color="primary"
            density="comfortable"
            class="mb-2"
            bg-color="white"
          />
        </div>

        <!-- 激活状态 -->
        <div class="mb-4">
          <v-switch
            v-model="groupForm.isActive"
            :label="TEXTS.common.active"
            color="primary"
            density="comfortable"
            class="mb-2"
          />
        </div>
      </v-form>
    </UnifiedDialog>

    <!-- 删除确认对话框 -->
    <ConfirmDeleteDialog
      v-model="showDeleteDialog"
      :title="TEXTS.groups.deleteWarning"
      :message="`确定要删除分组 '${deletingGroup?.name || ''}' 吗？`"
      :item-name="deletingGroup?.name || ''"
      :loading="deleting"
      @confirm="confirmDelete"
      @cancel="showDeleteDialog = false"
    />
  </template>
  
  <script setup lang="ts">
  import { ref, reactive, onMounted, computed } from 'vue'
  import { useAuthStore } from '@/stores/auth'
  
  import HomeLayout from '@/components/HomeLayout.vue'
  import UnifiedDialog from '@/components/UnifiedDialog.vue'
  import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'
  
  import { groupAPI, type ServerGroup } from '@/api/group'
  import { TEXTS } from '@/constants/texts'

  // Auth store
  const authStore = useAuthStore()
  
  // 响应式数据
  const groups = ref<ServerGroup[]>([])
  const loading = ref(false)
  const saving = ref(false)
  const deleting = ref(false)
  const showAddDialog = ref(false)
  const showDeleteDialog = ref(false)
  const deletingGroup = ref<ServerGroup | null>(null)
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
    sortOrder: 0,
    isActive: true
  })
  
  // 表格头部
  const headers = computed(() => [
    { title: TEXTS.groups.groupName, key: 'name', sortable: false },
    { title: TEXTS.groups.selectCategory, key: 'categoryName', sortable: false, width: 150 },
    { title: TEXTS.groups.region, key: 'region', sortable: false, width: 120 },
    { title: TEXTS.groups.country, key: 'country', sortable: false, width: 120 },
    { title: TEXTS.groups.city, key: 'city', sortable: false, width: 120 },
    { title: '状态', key: 'isActive', sortable: false, width: 100 },
    { title: '操作', key: 'actions', sortable: false, width: 120 }
  ])
  
  // 表单验证规则
  const rules = {
    required: (value: any) => !!value || '此字段为必填项'
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
        title: cat.name,
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
  let searchTimeout: ReturnType<typeof setTimeout>
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
      sortOrder: group.sortOrder || 0,
      isActive: group.isActive !== undefined ? group.isActive : true
    })
    showAddDialog.value = true
  }
  
  // 删除分组
  const deleteGroup = (group: ServerGroup) => {
    deletingGroup.value = group
    showDeleteDialog.value = true
  }

  // 确认删除
  const confirmDelete = async () => {
    if (!deletingGroup.value) return
    
    try {
      deleting.value = true
      await groupAPI.deleteGroup(deletingGroup.value.id)
      showDeleteDialog.value = false
      await loadGroups()
    } catch (error: any) {
      console.error('删除分组失败:', error)
      let errorMessage = '删除失败'
      
      if (error.response && error.response.data && error.response.data.message) {
        errorMessage = error.response.data.message
      } else if (error.message) {
        errorMessage = error.message
      }
      
      alert(errorMessage)
    } finally {
      deleting.value = false
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
      sortOrder: 0,
      isActive: true
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
  