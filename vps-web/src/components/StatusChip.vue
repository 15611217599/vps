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
    online: { color: 'success', icon: 'mdi-check-circle', text: 'common.online' },
    offline: { color: 'error', icon: 'mdi-close-circle', text: 'common.offline' },
    pending: { color: 'warning', icon: 'mdi-clock', text: 'common.pending' },
    active: { color: 'success', icon: 'mdi-check-circle', text: 'common.active' },
    inactive: { color: 'error', icon: 'mdi-close-circle', text: 'common.inactive' },
    running: { color: 'success', icon: 'mdi-play-circle', text: 'common.running' },
    stopped: { color: 'error', icon: 'mdi-stop-circle', text: 'common.stopped' },
    paused: { color: 'warning', icon: 'mdi-pause-circle', text: 'common.paused' }
  },
  boolean: {
    true: { color: 'success', icon: 'mdi-check', text: 'common.yes' },
    false: { color: 'error', icon: 'mdi-close', text: 'common.no' }
  },
  priority: {
    high: { color: 'error', icon: 'mdi-arrow-up-bold', text: 'common.high' },
    medium: { color: 'warning', icon: 'mdi-minus', text: 'common.medium' },
    low: { color: 'success', icon: 'mdi-arrow-down-bold', text: 'common.low' }
  },
  category: {
    server: { color: 'primary', icon: 'mdi-server', text: 'common.server' },
    network: { color: 'info', icon: 'mdi-network', text: 'common.network' },
    storage: { color: 'purple', icon: 'mdi-harddisk', text: 'common.storage' },
    security: { color: 'orange', icon: 'mdi-shield', text: 'common.security' }
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
  
  // 使用预定义配置的国际化键
  const config = statusConfigs[props.type]?.[statusKey]
  if (config) {
    // 这里应该使用 $t() 函数，但在 computed 中需要特殊处理
    // 暂时返回原始状态值，在实际使用时可以通过 i18n 处理
    return String(props.status)
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