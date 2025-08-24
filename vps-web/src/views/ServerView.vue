<template>
  <PageLayout>
    <v-container class="py-6">
      <!-- 页面标题和添加按钮 -->
      <div class="d-flex justify-space-between align-center mb-6">
        <h1 class="text-h4 font-weight-bold">服务器列表</h1>
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="showAddDialog = true"
        >
          添加
        </v-btn>
      </div>

      <!-- 服务器列表 -->
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="me-2">mdi-server-network</v-icon>
          服务器列表
          <v-spacer />
          <v-text-field
            v-model="searchQuery"
            placeholder="搜索"
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
          items-per-page-text="每页条数"
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
                online: '在线',
                offline: '离线'
              }"
          />
        </template>

        <!-- 销售状态列 -->
        <template #item.isSold="{ item }">
          <v-chip
            :color="item.isSold ? 'error' : 'success'"
            size="small"
            variant="tonal"
          >
            {{ item.isSold ? '已售出' : '可用' }}
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
              {{ item.groupName }}
            </v-chip>
            <span v-else class="text-medium-emphasis">未分组</span>
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
      :title="editingServer ? '编辑服务器' : '添加服务器'"
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
              label="IP地址"
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
              label="端口"
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
              label="用户名"
              icon="mdi-account"
          />
        </v-col>

        <!-- 密码 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.password"
              type="password"
              label="密码"
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
              label="选择分组"
              icon="mdi-folder-network"
              :items="groupOptions"
              item-title="title"
              item-value="value"
              clearable
          >
            <template #item="{ props, item }">
              <v-list-item v-bind="props" :title="(item.raw as any).originalName || item.raw.title">
              </v-list-item>
            </template>
            <template #selection="{ item }">
              {{ (item.raw as any).originalName || item.raw.title }}
            </template>
          </UnifiedFormField>
        </v-col>

        <!-- 状态 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.status"
              type="select"
              label="状态"
              icon="mdi-server"
              :items="statusOptions"
          />
        </v-col>
      </v-row>

      <v-row>
        <!-- 销售状态 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.isSold"
              type="select"
              label="销售状态"
              icon="mdi-tag"
              :items="soldStatusOptions"
          />
        </v-col>
      </v-row>

      <v-row>
        <!-- 操作系统 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.operatingSystem"
              type="text"
              label="操作系统"
              icon="mdi-desktop-classic"
          />
        </v-col>

        <!-- CPU核心数 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.cpuCores"
              type="text"
              label="CPU核心数"
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
              label="内存"
              icon="mdi-memory"
          />
        </v-col>

        <!-- 硬盘空间 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.diskSpace"
              type="text"
              label="硬盘空间"
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
              label="硬盘类型"
              icon="mdi-harddisk"
          />
        </v-col>

        <!-- 网络速度 -->
        <v-col cols="12" md="6">
          <UnifiedFormField
              v-model="serverForm.networkSpeed"
              type="text"
              label="网络速度"
              icon="mdi-speedometer"
          />
        </v-col>
      </v-row>
    </v-form>
  </UnifiedDialog>

  <!-- 删除确认对话框 -->
  <ConfirmDeleteDialog
    v-model="showDeleteDialog"
    title="删除警告"
    :message="`确定要删除服务器 ${deletingServer?.ip || ''} 吗？`"
    :item-name="deletingServer?.ip || ''"
    :loading="deleting"
    @confirm="confirmDelete"
    @cancel="showDeleteDialog = false"
  />
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import PageLayout from '@/components/PageLayout.vue'
import UnifiedFormField from '@/components/UnifiedFormField.vue'
import UnifiedDialog from '@/components/UnifiedDialog.vue'
import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'
import StatusChip from '@/components/StatusChip.vue'
import {
  getServers,
  createServer,
  updateServer,
  deleteServer as deleteServerApi,
  type Server,
  type ServerRequest
} from '../api/server'
import { groupAPI } from '../api/group'

// 移除国际化

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
  isSold: false,
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
  { title: '在线', value: 'ONLINE' },
  { title: '离线', value: 'OFFLINE' }
])

// 销售状态选项
const soldStatusOptions = computed(() => [
  { title: '可用', value: false },
  { title: '已售出', value: true }
])


// 表格头部
const headers = computed(() => [
  { title: '分组', key: 'groupName', sortable: false, width: 120 },
  { title: 'IP地址', key: 'ip', sortable: false, width: 140 },
  { title: '端口', key: 'port', sortable: false, width: 80 },
  { title: '状态', key: 'status', sortable: false, width: 80 },
  { title: '销售状态', key: 'isSold', sortable: false, width: 100 },
  { title: '操作系统', key: 'operatingSystem', sortable: false, width: 160 },
  { title: 'CPU核心数', key: 'cpuCores', sortable: false, width: 100 },
  { title: '内存', key: 'memory', sortable: false, width: 80 },
  { title: '硬盘空间', key: 'diskSpace', sortable: false, width: 100 },
  { title: '网络速度', key: 'networkSpeed', sortable: false, width: 120 },
  { title: '操作', key: 'actions', sortable: false, width: 120 }
])

// 表单验证规则
const rules = {
  required: (value: string) => !!value || '此字段为必填项',
  ip: (value: string) => {
    if (!value) return true
    const ipRegex = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/
    return ipRegex.test(value) || 'IP地址格式不正确'
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
          groupName: server.groupName,
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
    console.error('加载服务器列表失败', error)
    // 显示详细的后端错误信息
    if (error && typeof error === 'object' && 'message' in error) {
      alert(error.message)
    } else {
      alert('加载服务器列表失败')
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
        title: group.name || group.title,
        value: group.id || group.value,
        originalName: group.name || group.title
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
    isSold: server.isSold || false,
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
    console.error('删除服务器失败', error)
    // 显示详细的后端错误信息
    if (error && typeof error === 'object' && 'message' in error) {
      alert(error.message)
    } else {
      alert('删除服务器失败')
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
      isSold: serverForm.isSold,
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
    console.error('保存服务器失败', error)
    // 显示详细的后端错误信息
    if (error && typeof error === 'object' && 'message' in error) {
      alert(error.message)
    } else {
      alert('保存服务器失败')
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
    isSold: false,
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
