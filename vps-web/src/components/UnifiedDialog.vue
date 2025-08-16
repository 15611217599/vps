<template>
  <v-dialog 
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    :max-width="autoWidth ? 'none' : maxWidth"
    :width="autoWidth ? 'auto' : width"
    :fullscreen="fullscreen"
    :scrim="scrim"
    :persistent="persistent"
    :transition="transition"
    class="unified-dialog"
    :class="{ 'auto-width': autoWidth }"
  >
    <v-card 
      :elevation="elevation" 
      class="mx-auto dialog-card" 
      :rounded="rounded"
      :class="cardClass"
    >
      <!-- 统一的标题栏 -->
      <v-card-title 
        v-if="showHeader"
        class="dialog-header pa-4 d-flex align-center"
      >
        <v-avatar 
          :size="avatarSize" 
          class="me-3 dialog-avatar" 
          :color="avatarBgColor" 
          variant="flat"
        >
          <v-icon :color="avatarIconColor" :size="avatarIconSize">
            {{ headerIcon }}
          </v-icon>
        </v-avatar>
        <div class="flex-grow-1">
          <div class="dialog-title text-h5 font-weight-bold">
            {{ title }}
          </div>
          <div v-if="subtitle" class="dialog-subtitle text-body-2 opacity-90 mt-1">
            {{ subtitle }}
          </div>
        </div>
        
        <!-- 关闭按钮 -->
        <v-btn
          v-if="showCloseButton"
          icon="mdi-close"
          variant="text"
          size="small"
          class="dialog-close-btn"
          @click="$emit('cancel')"
        />
      </v-card-title>
      
      <!-- 无头部模式的关闭按钮 -->
      <div 
        v-if="!showHeader && showCloseButton" 
        class="headerless-close-btn"
      >
        <v-btn
          icon="mdi-close"
          variant="text"
          size="small"
          class="dialog-close-btn"
          @click="$emit('cancel')"
        />
      </div>
      
      <!-- 内容区域 -->
      <v-card-text 
        class="dialog-content" 
        :class="[contentClass, compact ? 'pa-4' : 'pa-6']"
        :style="contentStyle"
      >
        <slot />
      </v-card-text>

      <!-- 统一的操作按钮 -->
      <v-card-actions 
        class="dialog-actions" 
        :class="[actionsClass, compact ? 'pa-3' : 'pa-4']"
        :style="actionsStyle"
      >
        <!-- 左侧自定义按钮 -->
        <div v-if="$slots.leftActions" class="left-actions">
          <slot name="leftActions" />
        </div>
        
        <v-spacer />
        
        <!-- 右侧主要操作按钮 -->
        <div class="right-actions d-flex align-center ga-3">
          <!-- 取消按钮 -->
          <v-btn 
            v-if="showCancelButton"
            :variant="cancelButtonVariant" 
            :color="cancelButtonColor"
            :prepend-icon="cancelButtonIcon"
            :size="buttonSize"
            :rounded="buttonRounded"
            :disabled="loading"
            class="cancel-btn"
            @click="$emit('cancel')"
          >
            {{ cancelButtonText || $t('common.cancel') }}
          </v-btn>
          
          <!-- 自定义中间按钮 -->
          <slot name="middleActions" />
          
          <!-- 保存/确认按钮 -->
          <v-btn
            v-if="showSaveButton"
            :color="saveButtonColor"
            :loading="loading"
            :disabled="disabled"
            :prepend-icon="saveButtonIcon"
            :size="buttonSize"
            :elevation="saveButtonElevation"
            :rounded="buttonRounded"
            :variant="saveButtonVariant"
            class="save-btn px-8"
            @click="$emit('save')"
          >
            {{ saveButtonText || $t('common.save') }}
          </v-btn>
        </div>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  modelValue: boolean
  title: string
  subtitle?: string
  isEdit?: boolean
  loading?: boolean
  disabled?: boolean
  maxWidth?: string
  width?: string
  fullscreen?: boolean
  persistent?: boolean
  scrim?: string
  transition?: string
  elevation?: number
  rounded?: string | number | boolean
  autoWidth?: boolean
  
  // 头部配置
  showHeader?: boolean
  headerIcon?: string
  avatarSize?: number
  avatarBgColor?: string
  avatarIconColor?: string
  avatarIconSize?: number
  showCloseButton?: boolean
  
  // 按钮配置
  showCancelButton?: boolean
  showSaveButton?: boolean
  cancelButtonText?: string
  saveButtonText?: string
  cancelButtonColor?: string
  saveButtonColor?: string
  cancelButtonIcon?: string
  saveButtonIcon?: string
  cancelButtonVariant?: 'outlined' | 'elevated' | 'flat' | 'text' | 'tonal' | 'plain'
  saveButtonVariant?: 'outlined' | 'elevated' | 'flat' | 'text' | 'tonal' | 'plain'
  saveButtonElevation?: number
  buttonSize?: string
  buttonRounded?: string
  
  // 样式配置
  cardClass?: string
  contentClass?: string
  actionsClass?: string
  contentStyle?: string
  actionsStyle?: string
  compact?: boolean
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'save'): void
  (e: 'cancel'): void
}

const props = withDefaults(defineProps<Props>(), {
  subtitle: '',
  isEdit: false,
  loading: false,
  disabled: false,
  maxWidth: '90vw',
  width: 'auto',
  fullscreen: false,
  persistent: true,
  scrim: 'rgba(0, 0, 0, 0.5)',
  transition: 'dialog-bottom-transition',
  elevation: 24,
  rounded: 'xl',
  autoWidth: true,
  
  // 头部默认配置
  showHeader: true,
  headerIcon: '',
  avatarSize: 40,
  avatarBgColor: 'white',
  avatarIconColor: 'primary',
  avatarIconSize: 24,
  showCloseButton: true,
  
  // 按钮默认配置
  showCancelButton: true,
  showSaveButton: true,
  cancelButtonText: '',
  saveButtonText: '',
  cancelButtonColor: 'grey-darken-1',
  saveButtonColor: 'primary',
  cancelButtonIcon: 'mdi-close',
  saveButtonIcon: 'mdi-check',
  cancelButtonVariant: 'outlined',
  saveButtonVariant: 'elevated',
  saveButtonElevation: 4,
  buttonSize: 'large',
  buttonRounded: 'lg',
  
  // 样式默认配置
  cardClass: '',
  contentClass: '',
  actionsClass: '',
  contentStyle: '',
  actionsStyle: '',
  compact: false
})

defineEmits<Emits>()

// 计算头部图标
const headerIcon = computed(() => {
  if (props.headerIcon) {
    return props.headerIcon
  }
  return props.isEdit ? 'mdi-pencil' : 'mdi-plus'
})
</script>

<style scoped>
.unified-dialog {
  z-index: 2000;
}

/* 修复对话框四角白边问题 */
.unified-dialog :deep(.v-overlay__content) {
  background: transparent !important;
}

.unified-dialog :deep(.v-card) {
  background: rgb(var(--v-theme-surface)) !important;
  border-radius: 24px !important;
  overflow: hidden !important;
}

/* 确保对话框容器也是透明的 */
.unified-dialog :deep(.v-dialog) {
  background: transparent !important;
}

.dialog-card {
  background: rgb(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
  overflow: hidden;
  min-width: 320px; /* 最小宽度确保可用性 */
  border-radius: 24px;
}

/* 自动宽度模式 */
.unified-dialog.auto-width .dialog-card {
  width: fit-content;
  max-width: 90vw;
}

.unified-dialog.auto-width .dialog-content {
  width: auto;
  min-width: 300px;
}

.unified-dialog.auto-width .dialog-actions {
  width: auto;
}

/* 对话框头部样式 */
.dialog-header {
  background: linear-gradient(135deg, 
    rgba(var(--v-theme-primary), 0.1) 0%, 
    rgba(var(--v-theme-primary), 0.05) 50%,
    rgba(var(--v-theme-surface), 1) 100%);
  border-bottom: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
  position: relative;
}

.dialog-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, 
    rgb(var(--v-theme-primary)) 0%, 
    rgb(var(--v-theme-secondary)) 100%);
}

.dialog-avatar {
  box-shadow: 0 2px 8px rgba(var(--v-theme-primary), 0.3);
  border: 2px solid rgba(var(--v-theme-primary), 0.1);
}

.dialog-title {
  color: rgb(var(--v-theme-on-surface));
  line-height: 1.2;
}

.dialog-subtitle {
  color: rgba(var(--v-theme-on-surface), 0.7);
  line-height: 1.4;
}

.dialog-close-btn {
  opacity: 0.7;
  transition: opacity 0.2s ease;
}

.dialog-close-btn:hover {
  opacity: 1;
}

/* 对话框内容样式 */
.dialog-content {
  background: linear-gradient(135deg, 
    rgba(var(--v-theme-surface-variant), 0.1) 0%, 
    rgb(var(--v-theme-surface)) 100%);
  min-height: 100px;
  max-height: 70vh;
  overflow-y: auto;
}

.dialog-content::-webkit-scrollbar {
  width: 6px;
}

.dialog-content::-webkit-scrollbar-track {
  background: rgba(var(--v-theme-surface-variant), 0.1);
  border-radius: 3px;
}

.dialog-content::-webkit-scrollbar-thumb {
  background: rgba(var(--v-theme-primary), 0.3);
  border-radius: 3px;
}

.dialog-content::-webkit-scrollbar-thumb:hover {
  background: rgba(var(--v-theme-primary), 0.5);
}

/* 对话框操作区域样式 */
.dialog-actions {
  background: linear-gradient(135deg, 
    rgba(var(--v-theme-surface-variant), 0.05) 0%, 
    rgb(var(--v-theme-surface)) 100%);
  border-top: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
}

.left-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 按钮样式 */
.cancel-btn {
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.save-btn {
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.2s ease;
  background: linear-gradient(135deg, 
    rgb(var(--v-theme-primary)) 0%, 
    rgba(var(--v-theme-primary), 0.9) 100%);
}

.save-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(var(--v-theme-primary), 0.4);
}

.save-btn:active {
  transform: translateY(0);
}

/* 响应式设计 */
@media (max-width: 960px) {
  .unified-dialog :deep(.v-dialog) {
    margin: 16px;
  }
  
  .dialog-header {
    padding: 16px;
  }
  
  .dialog-content {
    padding: 16px;
    max-height: 60vh;
  }
  
  .dialog-actions {
    padding: 16px;
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .right-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .cancel-btn,
  .save-btn {
    flex: 1;
    max-width: 48%;
  }
}

@media (max-width: 600px) {
  .unified-dialog :deep(.v-dialog) {
    margin: 8px;
  }
  
  .dialog-header {
    padding: 12px;
  }
  
  .dialog-content {
    padding: 12px;
    max-height: 50vh;
  }
  
  .dialog-actions {
    padding: 12px;
  }
  
  .dialog-title {
    font-size: 1.25rem;
  }
  
  .dialog-avatar {
    width: 32px;
    height: 32px;
  }
}

/* 全屏模式样式 */
.unified-dialog :deep(.v-dialog--fullscreen .dialog-card) {
  border-radius: 0;
  height: 100vh;
}

.unified-dialog :deep(.v-dialog--fullscreen .dialog-content) {
  max-height: calc(100vh - 200px);
}

/* 动画效果 */
.dialog-card {
  animation: dialogSlideIn 0.3s ease-out;
}

@keyframes dialogSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 深色主题适配 */
.v-theme--dark .dialog-header {
  background: linear-gradient(135deg, 
    rgba(var(--v-theme-primary), 0.15) 0%, 
    rgba(var(--v-theme-primary), 0.08) 50%,
    rgba(var(--v-theme-surface), 1) 100%);
}

.v-theme--dark .dialog-content {
  background: linear-gradient(135deg, 
    rgba(var(--v-theme-surface-variant), 0.05) 0%, 
    rgb(var(--v-theme-surface)) 100%);
}

.v-theme--dark .dialog-actions {
  background: linear-gradient(135deg, 
    rgba(var(--v-theme-surface-variant), 0.03) 0%, 
    rgb(var(--v-theme-surface)) 100%);
}
</style>
