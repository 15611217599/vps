<template>
  <PageLayout>
    <v-container class="py-6">
      <!-- 页面标题和添加按钮 -->
      <div class="d-flex justify-space-between align-center mb-6">
        <h1 class="text-h4 font-weight-bold">{{ t('servers.list') }}</h1>
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
          <v-icon class="me-2">mdi-server-network</v-icon>
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
          <StatusChip
              :status="item.status"
              type="status"
              :custom-texts="{
                online: t('servers.online'),
                offline: t('servers.offline')
              }"
          />
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

  <!-- 添加/编辑服务器对话框 -->
  <UnifiedDialog
      v-model="showAddDialog"
      :title="editingServer ? t('servers.editServer') : t('servers.add')"
      :loading="saving"
      max-width="800px"
      @save="saveServer"
      @cancel="closeDialog"
  >
    <v-form ref="form" v-model="formValid">
      <v-row>
        <!-- IP地址 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.ip"
              type="text"
              :label="t('servers.ip')"
              icon="mdi-ip"
              :rules="[rules.required, rules.ip]"
              required
          />
        </v-col>

        <!-- 端口 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.port"
              type="number"
              :label="t('servers.port')"
              icon="mdi-ethernet"
          />
        </v-col>
      </v-row>

      <v-row>
        <!-- 用户名 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.username"
              type="text"
              :label="t('servers.username')"
              icon="mdi-account"
          />
        </v-col>

        <!-- 密码 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.password"
              type="password"
              :label="t('servers.password')"
              icon="mdi-lock"
          />
        </v-col>
      </v-row>

      <v-row>
        <!-- 分组选择 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.groupId"
              type="select"
              :label="t('servers.selectGroup')"
              icon="mdi-folder-network"
              :items="groupOptions"
              item-title="title"
              item-value="value"
              clearable
          >
            <template #item="{ props, item }">
              <v-list-item v-bind="props" :title="getLocalizedText((item.raw as any).originalName || item.raw.title)">
              </v-list-item>
            </template>
            <template #selection="{ item }">
              {{ getLocalizedText((item.raw as any).originalName || item.raw.title) }}
            </template>
          </UnifiedFormField>
        </v-col>

        <!-- 状态 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.status"
              type="select"
              :label="t('servers.status')"
              icon="mdi-server"
              :items="statusOptions"
          />
        </v-col>
      </v-row>

      <v-row>
        <!-- 操作系统 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.operatingSystem"
              type="text"
              :label="t('servers.operatingSystem')"
              icon="mdi-desktop-classic"
          />
        </v-col>

        <!-- CPU核心数 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.cpuCores"
              type="text"
              :label="t('servers.cpuCores')"
              icon="mdi-cpu-64-bit"
          />
        </v-col>
      </v-row>

      <v-row>
        <!-- 内存 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.memory"
              type="text"
              :label="t('servers.memory')"
              icon="mdi-memory"
          />
        </v-col>

        <!-- 硬盘空间 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.diskSpace"
              type="text"
              :label="t('servers.diskSpace')"
              icon="mdi-harddisk"
          />
        </v-col>
      </v-row>

      <v-row>
        <!-- 硬盘类型 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.diskType"
              type="text"
              :label="t('servers.diskType')"
              icon="mdi-harddisk"
          />
        </v-col>

        <!-- 网络速度 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.networkSpeed"
              type="text"
              :label="t('servers.networkSpeed')"
              icon="mdi-speedometer"
          />
        </v-col>
      </v-row>
    </v-form>
  </UnifiedDialog>

  <!-- 删除确认对话框 -->
  <ConfirmDeleteDialog
    v-model="showDeleteDialog"
    :title="t('servers.deleteWarning')"
    :message="t('servers.confirmDelete', { ip: deletingServer?.ip || '' })"
    :item-name="deletingServer?.ip || ''"
    :loading="deleting"
    @confirm="confirmDelete"
    @cancel="showDeleteDialog = false"
  />
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import PageLayout from '@/components/PageLayout.vue'
import UnifiedFormField from '@/components/UnifiedFormField.vue'
import UnifiedDialog from '@/components/UnifiedDialog.vue'
import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'
import StatusChip from '@/components/StatusChip.vue'
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
const deleting = ref(false)
const showAddDialog = ref(false)
const showDeleteDialog = ref(false)
const deletingServer = ref<Server | null>(null)
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
  { title: t('servers.online'), value: 'ONLINE' },
  { title: t('servers.offline'), value: 'OFFLINE' }
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


// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadServers()
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
    status: server.status || 'ONLINE',
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
const deleteServer = (server: Server) => {
  deletingServer.value = server
  showDeleteDialog.value = true
}

// 确认删除
const confirmDelete = async () => {
  if (!deletingServer.value) return
  
  try {
    deleting.value = true
    await deleteServerApi(deletingServer.value.id)
    showDeleteDialog.value = false
    await loadServers()
  } catch (error) {
    console.error(t('servers.deleteFailed'), error)
    // 显示详细的后端错误信息
    if (error && typeof error === 'object' && 'message' in error) {
      alert(error.message)
    } else {
      alert(t('servers.deleteFailed'))
    }
  } finally {
    deleting.value = false
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
    status: 'ONLINE',
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
