<template>
  <v-card class="mb-6" elevation="2" rounded="lg">
    <div class="header-content d-flex justify-space-between align-center">
      <!-- 左侧标题区域 -->
      <div class="title-section">
        <div class="d-flex align-center mb-2">
          <v-icon 
            v-if="icon" 
            :color="iconColor" 
            :size="iconSize" 
            class="me-3"
          >
            {{ icon }}
          </v-icon>
          <h1 class="page-title text-h4 font-weight-bold">
            {{ title }}
          </h1>
          <v-chip
            v-if="badge"
            :color="badgeColor"
            :variant="badgeVariant"
            size="small"
            class="ms-3"
          >
            {{ badge }}
          </v-chip>
        </div>
        <p 
          v-if="subtitle" 
          class="page-subtitle text-subtitle-1 text-medium-emphasis mt-1 mb-0"
        >
          {{ subtitle }}
        </p>
        
        <!-- 面包屑导航 -->
        <v-breadcrumbs
          v-if="breadcrumbs && breadcrumbs.length > 0"
          :items="breadcrumbs"
          class="pa-0 mt-2"
          density="compact"
        >
          <template #prepend>
            <v-icon size="small" icon="mdi-home" />
          </template>
          <template #divider>
            <v-icon size="small" icon="mdi-chevron-right" />
          </template>
        </v-breadcrumbs>
      </div>

      <!-- 右侧操作区域 -->
      <div class="actions-section d-flex align-center ga-3">
        <!-- 统计信息 -->
        <div v-if="stats && stats.length > 0" class="stats-container d-flex ga-4 me-4">
          <div 
            v-for="stat in stats" 
            :key="stat.key"
            class="stat-item text-center"
          >
            <div class="stat-value text-h6 font-weight-bold" :style="{ color: stat.color }">
              {{ stat.value }}
            </div>
            <div class="stat-label text-caption text-medium-emphasis">
              {{ stat.label }}
            </div>
          </div>
        </div>

        <!-- 自定义操作按钮 -->
        <slot name="actions" />

        <!-- 默认添加按钮 -->
        <ActionButtonGroup
          v-if="showDefaultActions"
          :show-add="showAdd"
          :add-text="addText"
          :add-icon="addIcon"
          :loading="actionLoading"
          :custom-actions="customActions"
          @add="$emit('add')"
          @custom="$emit('custom', $event)"
        />
      </div>
    </div>

    <!-- 扩展内容区域 -->
    <div v-if="$slots.extended" class="extended-content mt-4">
      <slot name="extended" />
    </div>
  </v-card>
</template>

<script setup lang="ts">
import ActionButtonGroup from './ActionButtonGroup.vue'

interface Stat {
  key: string
  label: string
  value: string | number
  color?: string
}

interface Breadcrumb {
  title: string
  disabled?: boolean
  href?: string
  to?: string
}

interface CustomAction {
  key: string
  icon: string
  color?: string
  variant?: string
  tooltip: string
}

interface Props {
  title: string
  subtitle?: string
  icon?: string
  iconColor?: string
  iconSize?: string | number
  badge?: string
  badgeColor?: string
  badgeVariant?: string
  breadcrumbs?: Breadcrumb[]
  stats?: Stat[]
  showDefaultActions?: boolean
  showAdd?: boolean
  addText?: string
  addIcon?: string
  actionLoading?: boolean
  customActions?: CustomAction[]
}

interface Emits {
  (e: 'add'): void
  (e: 'custom', key: string): void
}

withDefaults(defineProps<Props>(), {
  subtitle: '',
  icon: '',
  iconColor: 'primary',
  iconSize: 32,
  badge: '',
  badgeColor: 'primary',
  badgeVariant: 'tonal',
  breadcrumbs: () => [],
  stats: () => [],
  showDefaultActions: true,
  showAdd: true,
  addText: '',
  addIcon: 'mdi-plus',
  actionLoading: false,
  customActions: () => []
})

defineEmits<Emits>()
</script>

<style scoped>
/* 简化样式 - 使用 Vuetify 卡片样式 */
.title-section {
  flex: 1;
  min-width: 0;
}

.page-subtitle {
  max-width: 600px;
}

.actions-section {
  flex-shrink: 0;
}

.stat-item {
  min-width: 60px;
}

/* 响应式设计 */
@media (max-width: 960px) {
  .header-content {
    flex-direction: column;
    align-items: stretch;
    gap: 20px;
  }
  
  .actions-section {
    justify-content: space-between;
    width: 100%;
  }
  
  .stats-container {
    order: -1;
    justify-content: space-around;
    width: 100%;
  }
}

@media (max-width: 600px) {
  .page-title {
    font-size: 1.5rem;
  }
}
</style>