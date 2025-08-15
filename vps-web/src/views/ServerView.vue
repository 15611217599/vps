<template>
  <PageLayout>
    <v-container class="py-6">
      <!-- 页面标题和添加按钮 -->
      <div class="d-flex justify-space-between align-center mb-6">
        <h1 class="text-h4 font-weight-bold">{{ t('servers.title') }}</h1>
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="showAddDialog = true"
        >
          {{ t('common.add') }}
        </v-btn>
      </div>

      <!-- 服务器列表 -->
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="me-2">mdi-server</v-icon>
          {{ t('servers.list') }}
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
          :items="servers"
          :loading="loading"
          :items-length="totalItems"
          :items-per-page="pageSize"
          :items-per-page-options="[5, 10, 20, 50]"
          :items-per-page-text="t('common.itemsPerPage')"
          @update:options="handleOptionsUpdate"
        >
          <!-- IP地址列 -->
          <template #item.ip="{ item }">
            <span class="font-weight-medium">{{ item.ip }}</span>
          </template>

          <!-- 端口列 -->
          <template #item.port="{ item }">
            <span>{{ item.port || '-' }}</span>
          </template>

          <!-- 状态列 -->
          <template #item.status="{ item }">
            <v-chip
              :color="item.status === 'online' ? 'success' : 'error'"
              size="small"
              variant="flat"
            >
              {{ item.status === 'online' ? t('servers.online') : t('servers.offline') }}
            </v-chip>
          </template>

          <!-- 分组列 -->
          <template #item.groupName="{ item }">
            <v-chip
              v-if="item.groupName"
              color="primary"
              size="small"
              variant="tonal"
            >
              {{ getLocalizedText(item.groupName) }}
            </v-chip>
            <span v-else class="text-medium-emphasis">{{ t('servers.ungrouped') }}</span>
          </template>

          <!-- 操作系统列 -->
          <template #item.operatingSystem="{ item }">
            <span>{{ item.operatingSystem || '-' }}</span>
          </template>

          <!-- CPU核心数列 -->
          <template #item.cpuCores="{ item }">
            <span>{{ item.cpuCores || '-' }}</span>
          </template>

          <!-- 内存列 -->
          <template #item.memory="{ item }">
            <span>{{ item.memory || '-' }}</span>
          </template>

          <!-- 硬盘空间列 -->
          <template #item.diskSpace="{ item }">
            <span>{{ item.diskSpace || '-' }}</span>
          </template>

          <!-- 硬盘类型列 -->
          <template #item.diskType="{ item }">
            <span>{{ item.diskType || '-' }}</span>
          </template>

          <!-- 网络速度列 -->
          <template #item.networkSpeed="{ item }">
            <span>{{ item.networkSpeed || '-' }}</span>
          </template>

          <!-- 操作列 -->
          <template #item.actions="{ item }">
            <v-btn
              icon="mdi-pencil"
              size="small"
              variant="text"
              @click="editServer(item)"
            />
            <v-btn
              icon="mdi-delete"
              size="small"
              variant="text"
              color="error"
              @click="deleteServer(item)"
            />
          </template>
        </v-data-table-server>
      </v-card>
    </v-container>
  </PageLayout>

  <!-- 添加/编辑对话框 -->
  <v-dialog 
    v-model="showAddDialog" 
    max-width="900px"
    :scrim="false"
    persistent
    transition="dialog-bottom-transition"
  >
    <v-card elevation="24" class="mx-auto" rounded="xl">
      <v-card-title class="text-h5 bg-gradient-to-r from-primary to-secondary text-white pa-6 d-flex align-center">
        <v-avatar size="40" class="me-3" color="white" variant="flat">
          <v-icon color="primary" size="24">
            {{ editingServer ? 'mdi-pencil' : 'mdi-plus' }}
          </v-icon>
        </v-avatar>
        <div>
          <div class="text-h5 font-weight-bold">
            {{ editingServer ? t('servers.editServer') : t('servers.newServer') }}
          </div>
          <div class="text-body-2 opacity-90">
            {{ editingServer ? t('common.editDescription') : t('common.addDescription') }}
          </div>
        </div>
      </v-card-title>
      
      <v-card-text class="pa-8" style="background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);">
        <v-form ref="form" v-model="formValid">
          <v-row>
            <!-- IP地址 -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="serverForm.ip"
                :label="t('servers.ip')"
                :rules="[rules.required, rules.ip]"
                variant="outlined"
                prepend-inner-icon="mdi-ip"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </v-col>
            
            <!-- 端口 -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model.number="serverForm.port"
                :label="t('servers.port')"
                type="number"
                variant="outlined"
                prepend-inner-icon="mdi-ethernet"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </v-col>
          </v-row>

          <v-row>
            <!-- 用户名 -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="serverForm.username"
                :label="t('servers.username')"
                variant="outlined"
                prepend-inner-icon="mdi-account"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </v-col>
            
            <!-- 密码 -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="serverForm.password"
                :label="t('servers.password')"
                type="password"
                variant="outlined"
                prepend-inner-icon="mdi-lock"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </v-col>
          </v-row>

          <v-row>
            <!-- 分组选择 -->
            <v-col cols="12" md="6">
              <v-select
                v-model="serverForm.groupId"
                :items="groupOptions"
                item-title="title"
                item-value="value"
                :label="t('servers.selectGroup')"
                variant="outlined"
                prepend-inner-icon="mdi-folder-network"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
                clearable
              >
                <template #item="{ props, item }">
                  <v-list-item v-bind="props" :title="getLocalizedText((item.raw as any).originalName || item.raw.title)">
                  </v-list-item>
                </template>
                <template #selection="{ item }">
                  {{ getLocalizedText((item.raw as any).originalName || item.raw.title) }}
                </template>
              </v-select>
            </v-col>
            
            <!-- 状态 -->
            <v-col cols="12" md="6">
              <v-select
                v-model="serverForm.status"
                :items="statusOptions"
                :label="t('servers.status')"
                variant="outlined"
                prepend-inner-icon="mdi-server"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </v-col>
          </v-row>

          <v-row>
            <!-- 操作系统 -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="serverForm.operatingSystem"
                :label="t('servers.operatingSystem')"
                variant="outlined"
                prepend-inner-icon="mdi-desktop-classic"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </v-col>
            
            <!-- CPU核心数 -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="serverForm.cpuCores"
                :label="t('servers.cpuCores')"
                variant="outlined"
                prepend-inner-icon="mdi-cpu-64-bit"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </v-col>
          </v-row>

          <v-row>
            <!-- 内存 -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="serverForm.memory"
                :label="t('servers.memory')"
                variant="outlined"
                prepend-inner-icon="mdi-memory"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </v-col>
            
            <!-- 硬盘空间 -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="serverForm.diskSpace"
                :label="t('servers.diskSpace')"
                variant="outlined"
                prepend-inner-icon="mdi-harddisk"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </v-col>
          </v-row>

          <v-row>
            <!-- 硬盘类型 -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="serverForm.diskType"
                :label="t('servers.diskType')"
                variant="outlined"
                prepend-inner-icon="mdi-harddisk"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </v-col>
            
            <!-- 网络速度 -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="serverForm.networkSpeed"
                :label="t('servers.networkSpeed')"
                variant="outlined"
                prepend-inner-icon="mdi-speedometer"
                color="primary"
                density="comfortable"
                class="mb-2"
                bg-color="white"
              />
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>

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
          @click="saveServer"
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
import {
  getServers,
  createServer,
  updateServer,
  deleteServer as deleteServerApi,
  type Server,
  type ServerRequest
} from '../api/server'
import { groupAPI } from '../api/group'

const { t } = useI18n()

// 响应式数据
const servers = ref<Server[]>([])
const loading = ref(false)
const saving = ref(false)
const showAddDialog = ref(false)
const formValid = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const editingServer = ref<Server | null>(null)
const groupOptions = ref<{title: string, value: number}[]>([])

// 表单数据
const serverForm = reactive({
  ip: '',
  port: undefined as number | undefined,
  username: '',
  password: '',
  status: 'ONLINE',
  groupId: undefined as number | undefined,
  operatingSystem: '',
  cpuCores: '',
  memory: '',
  diskSpace: '',
  diskType: '',
  networkSpeed: ''
})

// 状态选项
const statusOptions = computed(() => [
  { title: t('servers.online'), value: 'online' },
  { title: t('servers.offline'), value: 'offline' }
])

// 表格头部
const headers = computed(() => [
  { title: t('servers.group'), key: 'groupName', sortable: false, width: 120 },
  { title: t('servers.ip'), key: 'ip', sortable: false, width: 140 },
  { title: t('servers.port'), key: 'port', sortable: false, width: 80 },
  { title: t('servers.status'), key: 'status', sortable: false, width: 80 },
  { title: t('servers.operatingSystem'), key: 'operatingSystem', sortable: false, width: 160 },
  { title: t('servers.cpuCores'), key: 'cpuCores', sortable: false, width: 100 },
  { title: t('servers.memory'), key: 'memory', sortable: false, width: 80 },
  { title: t('servers.diskSpace'), key: 'diskSpace', sortable: false, width: 100 },
  { title: t('servers.diskType'), key: 'diskType', sortable: false, width: 100 },
  { title: t('servers.networkSpeed'), key: 'networkSpeed', sortable: false, width: 120 },
  { title: t('common.actions'), key: 'actions', sortable: false, width: 120 }
])

// 表单验证规则
const rules = {
  required: (value: string) => !!value || t('validation.required'),
  ip: (value: string) => {
    if (!value) return true
    const ipRegex = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/
    return ipRegex.test(value) || t('validation.invalidIp')
  }
}

// 加载服务器数据
const loadServers = async () => {
  try {
    loading.value = true
    
    const params = {
      page: currentPage.value - 1, // 后端使用0基索引
      size: pageSize.value,
      ...(searchQuery.value && { search: searchQuery.value })
    }
    
    const response = await getServers(params)
    
    // 根据后端API响应结构设置数据
    if (response && response.data && response.data.content) {
      // 处理服务器数据
      const processedServers = response.data.content.map((server: any) => {
        return {
          ...server,
          groupName: server.groupName, // 保持原始格式，让getLocalizedText处理
          status: server.status || 'offline'
        }
      })
      
      servers.value = processedServers
      totalItems.value = response.data.totalElements || 0
    } else {
      servers.value = []
      totalItems.value = 0
    }
  } catch (error) {
    console.error(t('servers.loadFailed'), error)
    // 显示详细的后端错误信息
    if (error && typeof error === 'object' && 'message' in error) {
      alert(error.message)
    } else {
      alert(t('servers.loadFailed'))
    }
    servers.value = []
    totalItems.value = 0
  } finally {
    loading.value = false
  }
}

// 加载分组选项
const loadGroupOptions = async () => {
  try {
    // 使用正确的分组API获取分组选项
    const response = await groupAPI.getGroupOptions()
    if (response) {
      groupOptions.value = response.map((group: any) => ({
        title: getLocalizedText(group.name || group.title),
        value: group.id || group.value,
        originalName: group.name || group.title // 保存原始名称用于国际化
      }))
    }
  } catch (error) {
    console.error('加载分组选项失败:', error)
    // 尝试备用API
      groupOptions.value = []
  }
}


// 防抖搜索
let searchTimeout: number
const debouncedSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 1 // 搜索时重置到第一页
    loadServers()
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
    loadServers()
  }
}

// 编辑服务器
const editServer = (server: Server) => {
  editingServer.value = server
  Object.assign(serverForm, {
    ip: server.ip,
    port: server.port,
    username: server.username || '',
    password: server.password || '',
    status: server.status || 'online',
    groupId: server.groupId,
    operatingSystem: server.operatingSystem || '',
    cpuCores: server.cpuCores || '',
    memory: server.memory || '',
    diskSpace: server.diskSpace || '',
    diskType: server.diskType || '',
    networkSpeed: server.networkSpeed || ''
  })
  showAddDialog.value = true
}

// 删除服务器
const deleteServer = async (server: Server) => {
  if (confirm(t('servers.confirmDelete', { ip: server.ip }))) {
    try {
      await deleteServerApi(server.id)
      await loadServers()
    } catch (error) {
      console.error(t('servers.deleteFailed'), error)
      // 显示详细的后端错误信息
      if (error && typeof error === 'object' && 'message' in error) {
        alert(error.message)
      } else {
        alert(t('servers.deleteFailed'))
      }
    }
  }
}

// 保存服务器
const saveServer = async () => {
  try {
    saving.value = true
    
    const serverData: ServerRequest = {
      ip: serverForm.ip,
      port: serverForm.port,
      username: serverForm.username,
      password: serverForm.password,
      status: serverForm.status,
      groupId: serverForm.groupId,
      operatingSystem: serverForm.operatingSystem,
      cpuCores: serverForm.cpuCores,
      memory: serverForm.memory,
      diskSpace: serverForm.diskSpace,
      diskType: serverForm.diskType,
      networkSpeed: serverForm.networkSpeed
    }
    
    if (editingServer.value) {
      await updateServer(editingServer.value.id, serverData)
    } else {
      await createServer(serverData)
    }
    
    closeDialog()
    await loadServers()
  } catch (error) {
    console.error(t('servers.saveFailed'), error)
    // 显示详细的后端错误信息
    if (error && typeof error === 'object' && 'message' in error) {
      alert(error.message)
    } else {
      alert(t('servers.saveFailed'))
    }
  } finally {
    saving.value = false
  }
}

// 关闭对话框
const closeDialog = () => {
  showAddDialog.value = false
  editingServer.value = null
  Object.assign(serverForm, {
    ip: '',
    port: undefined,
    username: '',
    password: '',
    status: 'online',
    groupId: undefined,
    operatingSystem: '',
    cpuCores: '',
    memory: '',
    diskSpace: '',
    diskType: '',
    networkSpeed: ''
  })
}

// 组件挂载时加载数据
onMounted(() => {
  Promise.all([
    loadServers(),
    loadGroupOptions()
  ])
})
</script>
