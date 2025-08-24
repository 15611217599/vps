<template>
  <v-card elevation="2" rounded="lg">
    <!-- 表格标题栏 -->
    <v-card-title class="table-header d-flex align-center pa-6">
      <v-icon v-if="icon" class="me-3" :color="iconColor" size="28">
        {{ icon }}
      </v-icon>
      <div class="flex-grow-1">
        <h2 class="text-h5 font-weight-bold">{{ title }}</h2>
        <p v-if="subtitle" class="text-body-2 text-medium-emphasis mt-1 mb-0">
          {{ subtitle }}
        </p>
      </div>
      
      <!-- 搜索框 -->
      <v-text-field
        v-if="showSearch"
        v-model="searchQuery"
        :placeholder="searchPlaceholder || TEXTS.common.search"
        prepend-inner-icon="mdi-magnify"
        variant="outlined"
        density="compact"
        hide-details
        clearable
        class="search-field"
        style="max-width: 300px"
        @input="handleSearch"
      />
      
      <!-- 操作按钮 -->
      <div v-if="showActions" class="ms-4">
        <ActionButtonGroup
          :show-add="showAdd"
          :add-text="addText"
          :loading="actionLoading"
          @add="$emit('add')"
        />
      </div>
    </v-card-title>

    <!-- 数据表格 -->
    <v-data-table-server
      v-if="serverSide"
      :headers="headers"
      :items="items"
      :loading="loading"
      :items-length="totalItems"
      :items-per-page="itemsPerPage"
      :items-per-page-options="itemsPerPageOptions"
      :items-per-page-text="TEXTS.common.itemsPerPage"
      :loading-text="TEXTS.common.loading"
      :no-data-text="TEXTS.common.noData"
      class="table-content"
      @update:options="$emit('update:options', $event)"
    >
      <!-- 传递所有插槽 -->
      <template v-for="(_, slot) in $slots" #[slot]="scope">
        <slot :name="slot" v-bind="scope" />
      </template>
      
      <!-- 默认操作列 -->
      <template v-if="showDefaultActions" #item.actions="{ item }">
        <ActionButtonGroup
          :show-edit="showEdit"
          :show-delete="showDelete"
          :show-view="showView"
          :custom-actions="customActions"
          :disabled="actionLoading"
          @edit="$emit('edit', item)"
          @delete="$emit('delete', item)"
          @view="$emit('view', item)"
          @custom="$emit('custom', $event, item)"
        />
      </template>
    </v-data-table-server>

    <!-- 客户端分页表格 -->
    <v-data-table
      v-else
      :headers="headers"
      :items="items"
      :loading="loading"
      :items-per-page="itemsPerPage"
      :items-per-page-options="itemsPerPageOptions"
      :items-per-page-text="TEXTS.common.itemsPerPage"
      :loading-text="TEXTS.common.loading"
      :no-data-text="TEXTS.common.noData"
      class="table-content"
    >
      <!-- 传递所有插槽 -->
      <template v-for="(_, slot) in $slots" #[slot]="scope">
        <slot :name="slot" v-bind="scope" />
      </template>
      
      <!-- 默认操作列 -->
      <template v-if="showDefaultActions" #item.actions="{ item }">
        <ActionButtonGroup
          :show-edit="showEdit"
          :show-delete="showDelete"
          :show-view="showView"
          :custom-actions="customActions"
          :disabled="actionLoading"
          @edit="$emit('edit', item)"
          @delete="$emit('delete', item)"
          @view="$emit('view', item)"
          @custom="$emit('custom', $event, item)"
        />
      </template>
    </v-data-table>
  </v-card>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import ActionButtonGroup from './ActionButtonGroup.vue'
import { TEXTS } from '@/constants/texts'

interface Props {
  title: string
  subtitle?: string
  icon?: string
  iconColor?: string
  headers: any[]
  items: any[]
  loading?: boolean
  serverSide?: boolean
  totalItems?: number
  itemsPerPage?: number
  itemsPerPageOptions?: number[]
  showSearch?: boolean
  searchPlaceholder?: string
  showActions?: boolean
  showAdd?: boolean
  addText?: string
  showDefaultActions?: boolean
  showEdit?: boolean
  showDelete?: boolean
  showView?: boolean
  customActions?: any[]
  actionLoading?: boolean
}

interface Emits {
  (e: 'update:options', options: any): void
  (e: 'search', query: string): void
  (e: 'add'): void
  (e: 'edit', item: any): void
  (e: 'delete', item: any): void
  (e: 'view', item: any): void
  (e: 'custom', key: string, item: any): void
}

const props = withDefaults(defineProps<Props>(), {
  subtitle: '',
  icon: '',
  iconColor: 'primary',
  loading: false,
  serverSide: true,
  totalItems: 0,
  itemsPerPage: 10,
  itemsPerPageOptions: () => [5, 10, 20, 50],
  showSearch: true,
  searchPlaceholder: '',
  showActions: true,
  showAdd: true,
  addText: '',
  showDefaultActions: true,
  showEdit: true,
  showDelete: true,
  showView: false,
  customActions: () => [],
  actionLoading: false
})

const emit = defineEmits<Emits>()

const searchQuery = ref('')

// 防抖搜索
let searchTimeout: number
const handleSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    emit('search', searchQuery.value)
  }, 300)
}

// 监听搜索查询变化
watch(searchQuery, (newValue) => {
  if (!newValue) {
    emit('search', '')
  }
})
</script>

<style scoped>
/* 简化样式 - 保留必要的交互效果 */
.table-content :deep(.v-data-table__tr:hover) {
  background-color: rgba(var(--v-theme-primary), 0.04) !important;
}

/* 响应式设计 */
@media (max-width: 960px) {
  .table-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .search-field {
    max-width: none !important;
  }
}
</style>