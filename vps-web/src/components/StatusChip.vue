<template>
  <v-chip
    :color="chipColor"
    :variant="variant"
    :size="size"
    :prepend-icon="icon"
    class="chip-hover"
  >
    <span class="chip-text">{{ displayText }}</span>
  </v-chip>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { TEXTS } from '@/constants/texts'

interface Props {
  status: string | boolean | number
  type?: 'status' | 'boolean' | 'priority' | 'category' | 'custom'
  size?: 'x-small' | 'small' | 'default' | 'large' | 'x-large'
  variant?: 'flat' | 'tonal' | 'outlined' | 'text' | 'elevated'
  customColors?: Record<string, string>
  customIcons?: Record<string, string>
  customTexts?: Record<string, string>
  showIcon?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'status',
  size: 'small',
  variant: 'flat',
  customColors: () => ({}),
  customIcons: () => ({}),
  customTexts: () => ({}),
  showIcon: true
})

// 预定义的状态配置
const statusConfigs = {
  status: {
    online: { color: 'success', icon: 'mdi-check-circle', text: '在线' },
    offline: { color: 'error', icon: 'mdi-close-circle', text: '离线' },
    pending: { color: 'warning', icon: 'mdi-clock', text: '待处理' },
    active: { color: 'success', icon: 'mdi-check-circle', text: '启用' },
    inactive: { color: 'error', icon: 'mdi-close-circle', text: '禁用' },
    running: { color: 'success', icon: 'mdi-play-circle', text: '运行中' },
    stopped: { color: 'error', icon: 'mdi-stop-circle', text: '已停止' },
    paused: { color: 'warning', icon: 'mdi-pause-circle', text: '暂停' }
  },
  boolean: {
    true: { color: 'success', icon: 'mdi-check', text: TEXTS.common.yes },
    false: { color: 'error', icon: 'mdi-close', text: TEXTS.common.no }
  },
  priority: {
    high: { color: 'error', icon: 'mdi-arrow-up-bold', text: '高' },
    medium: { color: 'warning', icon: 'mdi-minus', text: '中' },
    low: { color: 'success', icon: 'mdi-arrow-down-bold', text: '低' }
  },
  category: {
    server: { color: 'primary', icon: 'mdi-server', text: '服务器' },
    network: { color: 'info', icon: 'mdi-network', text: '网络' },
    storage: { color: 'purple', icon: 'mdi-harddisk', text: '存储' },
    security: { color: 'orange', icon: 'mdi-shield', text: '安全' }
  }
}

// 计算芯片颜色
const chipColor = computed(() => {
  const statusKey = String(props.status).toLowerCase()
  
  // 优先使用自定义颜色
  if (props.customColors[statusKey]) {
    return props.customColors[statusKey]
  }
  
  // 使用预定义配置
  const config = statusConfigs[props.type]?.[statusKey]
  if (config) {
    return config.color
  }
  
  // 默认颜色
  return 'grey'
})

// 计算图标
const icon = computed(() => {
  if (!props.showIcon) return undefined
  
  const statusKey = String(props.status).toLowerCase()
  
  // 优先使用自定义图标
  if (props.customIcons[statusKey]) {
    return props.customIcons[statusKey]
  }
  
  // 使用预定义配置
  const config = statusConfigs[props.type]?.[statusKey]
  if (config) {
    return config.icon
  }
  
  return undefined
})

// 计算显示文本
const displayText = computed(() => {
  const statusKey = String(props.status).toLowerCase()
  
  // 优先使用自定义文本
  if (props.customTexts[statusKey]) {
    return props.customTexts[statusKey]
  }
  
  // 使用预定义配置的文本
  const config = statusConfigs[props.type]?.[statusKey]
  if (config) {
    return config.text
  }
  
  // 默认显示原始状态值
  return String(props.status)
})

// 计算芯片样式类
const chipClass = computed(() => {
  return [
    `status-chip--${props.type}`,
    `status-chip--${String(props.status).toLowerCase()}`,
    `status-chip--${chipColor.value}`
  ]
})
</script>

<style scoped>
/* 使用 unified-components.css 中的 chip-hover 类 */

/* 响应式设计 */
@media (max-width: 600px) {
  .chip-text {
    display: none;
  }
  
  :deep(.v-chip__prepend) {
    margin-inline-end: 0;
  }
}
</style>