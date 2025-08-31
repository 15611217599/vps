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
    class="dialog-container"
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
            {{ cancelButtonText || TEXTS.common.cancel }}
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
            {{ saveButtonText || TEXTS.common.save }}
          </v-btn>
        </div>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { TEXTS } from '@/constants/texts'

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
/* 简化样式 - 保留必要的功能 */
.dialog-card {
  min-width: 320px;
  width: 100%;
}

/* 自动宽度模式 */
.dialog-container.auto-width .dialog-card {
  width: fit-content;
  max-width: 90vw;
}

/* 非自动宽度模式 - 确保对话框能够使用设定的宽度 */
.dialog-container:not(.auto-width) .dialog-card {
  width: 100%;
  min-width: 400px;
}

.dialog-content {
  max-height: 70vh;
  overflow-y: auto;
}

/* 按钮悬停效果 */
.cancel-btn:hover,
.save-btn:hover {
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 960px) {
  .dialog-content {
    max-height: 60vh;
  }
  
  .dialog-actions {
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
  .dialog-content {
    max-height: 50vh;
  }
}
</style>
