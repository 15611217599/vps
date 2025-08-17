<template>
  <v-menu
    :close-on-content-click="false"
    location="bottom end"
    transition="slide-y-transition"
    offset="8"
  >
    <template v-slot:activator="{ props }">
      <v-btn
        v-bind="props"
        variant="text"
        icon
        :title="$t('theme.title')"
        height="40"
        width="40"
        color="white"
        rounded
        class="theme-activator"
      >
        <v-icon>{{ themeIcon }}</v-icon>
      </v-btn>
    </template>
    
    <v-card 
      class="theme-popup"
      width="480"
      elevation="12"
    >
      <!-- 头部 -->
      <div class="theme-header">
        <div class="theme-header-content">
          <div class="theme-header-icon">
            <v-icon size="24" color="primary">mdi-palette</v-icon>
          </div>
          <div>
            <h3 class="theme-title">主题设置</h3>
            <p class="theme-subtitle">个性化您的界面外观</p>
          </div>
        </div>
      </div>
      
      <v-card-text class="theme-content">
        <!-- 主题模式选择 -->
        <div class="theme-section">
          <div class="section-header">
            <v-icon size="20" class="section-icon">mdi-brightness-6</v-icon>
            <span class="section-title">显示模式</span>
          </div>
          
          <div class="mode-selector">
            <div
              v-for="mode in modes"
              :key="mode.value"
              class="mode-option"
              :class="{ active: themeStore.mode === mode.value }"
              @click="themeStore.setMode(mode.value as any)"
            >
              <div class="mode-icon">
                <v-icon :color="themeStore.mode === mode.value ? 'primary' : 'grey'">
                  {{ mode.icon }}
                </v-icon>
              </div>
              <span class="mode-label">{{ mode.label }}</span>
            </div>
          </div>
        </div>
        
        <!-- 主题预设选择 -->
        <div class="theme-section">
          <div class="section-header">
            <v-icon size="20" class="section-icon">mdi-format-color-fill</v-icon>
            <span class="section-title">主题配色</span>
          </div>
          
          <div class="preset-grid">
            <div
              v-for="(preset, key) in themePresets"
              :key="key"
              class="preset-item"
              :class="{ active: themeStore.preset === key }"
              @click="themeStore.setPreset(key)"
            >
              <div class="preset-colors">
                <div
                  v-for="(color, index) in ['primary', 'secondary', 'accent']"
                  :key="color"
                  class="color-dot"
                  :class="`color-${index + 1}`"
                  :style="{ backgroundColor: preset[themeStore.currentTheme][color as keyof typeof preset[typeof themeStore.currentTheme]] }"
                />
              </div>
              <span class="preset-name">{{ getThemeName(key) }}</span>
              <div v-if="themeStore.preset === key" class="preset-check">
                <v-icon size="16" color="primary">mdi-check</v-icon>
              </div>
            </div>
          </div>
        </div>
      </v-card-text>
    </v-card>
  </v-menu>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useThemeStore, themePresets } from '@/stores/theme'

const themeStore = useThemeStore()

const themeIcon = computed(() => {
  switch (themeStore.mode) {
    case 'light':
      return 'mdi-weather-sunny'
    case 'dark':
      return 'mdi-weather-night'
    case 'auto':
      return 'mdi-theme-light-dark'
    default:
      return 'mdi-palette'
  }
})

const modes = [
  { value: 'light', label: '浅色', icon: 'mdi-weather-sunny' },
  { value: 'dark', label: '深色', icon: 'mdi-weather-night' },
  { value: 'auto', label: '自动', icon: 'mdi-theme-light-dark' }
]

const themeNames: Record<string, string> = {
  default: '默认',
  blue: '蓝色',
  green: '绿色',
  purple: '紫色',
  red: '红色',
  orange: '橙色',
  teal: '青色',
  indigo: '靛蓝',
  pink: '粉色',
  amber: '琥珀'
}

const getThemeName = (key: string) => {
  return themeNames[key] || key
}
</script>

<style scoped>
/* 弹出窗口主体 */
.theme-popup {
  border-radius: 16px !important;
  overflow: hidden !important;
  backdrop-filter: blur(20px);
  background: rgba(255, 255, 255, 0.98) !important;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1), 0 8px 24px rgba(0, 0, 0, 0.08) !important;
}

.theme-popup .v-card {
  border-radius: 16px !important;
  overflow: hidden !important;
}

/* 头部样式 */
.theme-header {
  background: linear-gradient(135deg, rgb(var(--v-theme-primary)) 0%, rgba(var(--v-theme-primary), 0.8) 100%);
  padding: 20px 24px;
  position: relative;
  border-radius: 16px 16px 0 0 !important;
  margin: 0;
  overflow: hidden;
}

.theme-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Ccircle cx='7' cy='7' r='1'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E") repeat;
  opacity: 0.3;
  border-radius: 16px 16px 0 0;
}

.theme-header-content {
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.theme-header-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.theme-title {
  color: white;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  line-height: 1.2;
}

.theme-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  margin: 4px 0 0 0;
  line-height: 1.2;
}

/* 内容区域 */
.theme-content {
  padding: 24px !important;
}

.theme-section {
  margin-bottom: 32px;
}

.theme-section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.section-icon {
  color: rgb(var(--v-theme-primary));
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: rgb(var(--v-theme-on-surface));
}

/* 模式选择器 */
.mode-selector {
  display: flex;
  gap: 12px;
}

.mode-option {
  flex: 1;
  padding: 16px 12px;
  border-radius: 12px;
  border: 2px solid rgba(var(--v-theme-outline), 0.2);
  background: rgba(var(--v-theme-surface), 0.5);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.mode-option::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(var(--v-theme-primary), 0.1), transparent);
  transition: left 0.5s;
}

.mode-option:hover::before {
  left: 100%;
}

.mode-option:hover {
  transform: translateY(-2px);
  border-color: rgba(var(--v-theme-primary), 0.3);
  box-shadow: 0 8px 24px rgba(var(--v-theme-primary), 0.15);
}

.mode-option.active {
  border-color: rgb(var(--v-theme-primary));
  background: rgba(var(--v-theme-primary), 0.08);
  box-shadow: 0 4px 16px rgba(var(--v-theme-primary), 0.2);
}

.mode-icon {
  margin-bottom: 8px;
}

.mode-label {
  font-size: 14px;
  font-weight: 500;
  color: rgb(var(--v-theme-on-surface));
}

/* 预设网格 */
.preset-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.preset-item {
  padding: 16px 12px;
  border-radius: 12px;
  border: 2px solid rgba(var(--v-theme-outline), 0.2);
  background: rgba(var(--v-theme-surface), 0.5);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.preset-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s;
}

.preset-item:hover::before {
  left: 100%;
}

.preset-item:hover {
  transform: translateY(-3px);
  border-color: rgba(var(--v-theme-primary), 0.3);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.preset-item.active {
  border-color: rgb(var(--v-theme-primary));
  background: rgba(var(--v-theme-primary), 0.08);
  box-shadow: 0 8px 24px rgba(var(--v-theme-primary), 0.2);
}

.preset-colors {
  display: flex;
  justify-content: center;
  gap: 4px;
  margin-bottom: 12px;
}

.color-dot {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.preset-item:hover .color-dot {
  transform: scale(1.1);
}

.color-dot.color-1 {
  z-index: 3;
}

.color-dot.color-2 {
  z-index: 2;
  margin-left: -6px;
}

.color-dot.color-3 {
  z-index: 1;
  margin-left: -6px;
}

.preset-name {
  font-size: 14px;
  font-weight: 500;
  color: rgb(var(--v-theme-on-surface));
  display: block;
}

.preset-check {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgb(var(--v-theme-primary));
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(var(--v-theme-primary), 0.3);
}

/* 激活按钮样式 */
.theme-activator {
  transition: all 0.3s ease;
}

.theme-activator:hover {
  transform: scale(1.05);
  background: rgba(255, 255, 255, 0.1) !important;
}

/* 深色主题适配 */
.v-theme--dark .theme-popup {
  background: rgba(18, 18, 18, 0.98) !important;
}

.v-theme--dark .mode-option,
.v-theme--dark .preset-item {
  background: rgba(var(--v-theme-surface), 0.3);
}

.v-theme--dark .preset-item::before {
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.05), transparent);
}

/* 全局修复：确保所有 Vuetify 弹出框都没有白边 */
:deep(.v-overlay__content) {
  border-radius: 16px !important;
  overflow: hidden !important;
}

:deep(.v-card) {
  border-radius: 16px !important;
  overflow: hidden !important;
}

:deep(.v-menu > .v-overlay__content > .v-card) {
  border-radius: 16px !important;
  overflow: hidden !important;
}
</style>