<template>
  <div class="action-button-group d-flex align-center ga-2">
    <!-- 新增按钮 -->
    <v-btn
      v-if="showAdd"
      color="primary"
      :prepend-icon="addIcon"
      :size="size"
      :variant="variant"
      :loading="loading"
      :disabled="disabled"
      class="btn-hover"
      elevation="2"
      rounded="lg"
      @click="$emit('add')"
    >
      {{ addText || $t('common.add') }}
    </v-btn>

    <!-- 编辑按钮 -->
    <v-btn
      v-if="showEdit"
      color="info"
      :icon="editIcon"
      :size="iconSize"
      variant="text"
      class="btn-hover"
      :disabled="disabled"
      @click="$emit('edit')"
    >
      <v-icon>{{ editIcon }}</v-icon>
      <v-tooltip activator="parent" location="top">
        {{ editText || $t('common.edit') }}
      </v-tooltip>
    </v-btn>

    <!-- 删除按钮 -->
    <v-btn
      v-if="showDelete"
      color="error"
      :icon="deleteIcon"
      :size="iconSize"
      variant="text"
      class="btn-hover"
      :disabled="disabled"
      @click="$emit('delete')"
    >
      <v-icon>{{ deleteIcon }}</v-icon>
      <v-tooltip activator="parent" location="top">
        {{ deleteText || $t('common.delete') }}
      </v-tooltip>
    </v-btn>

    <!-- 查看按钮 -->
    <v-btn
      v-if="showView"
      color="success"
      :icon="viewIcon"
      :size="iconSize"
      variant="text"
      class="btn-hover"
      :disabled="disabled"
      @click="$emit('view')"
    >
      <v-icon>{{ viewIcon }}</v-icon>
      <v-tooltip activator="parent" location="top">
        {{ viewText || $t('common.view') }}
      </v-tooltip>
    </v-btn>

    <!-- 自定义操作按钮 -->
    <template v-for="action in customActions" :key="action.key">
      <v-btn
        :color="action.color || 'primary'"
        :icon="action.icon"
        :size="iconSize"
        :variant="action.variant || 'text'"
        class="btn-hover"
        :disabled="disabled"
        @click="$emit('custom', action.key)"
      >
        <v-icon>{{ action.icon }}</v-icon>
        <v-tooltip activator="parent" location="top">
          {{ action.tooltip }}
        </v-tooltip>
      </v-btn>
    </template>
  </div>
</template>

<script setup lang="ts">
interface CustomAction {
  key: string
  icon: string
  color?: string
  variant?: string
  tooltip: string
}

interface Props {
  showAdd?: boolean
  showEdit?: boolean
  showDelete?: boolean
  showView?: boolean
  addText?: string
  editText?: string
  deleteText?: string
  viewText?: string
  addIcon?: string
  editIcon?: string
  deleteIcon?: string
  viewIcon?: string
  size?: string
  iconSize?: string
  variant?: string
  loading?: boolean
  disabled?: boolean
  customActions?: CustomAction[]
}

interface Emits {
  (e: 'add'): void
  (e: 'edit'): void
  (e: 'delete'): void
  (e: 'view'): void
  (e: 'custom', key: string): void
}

withDefaults(defineProps<Props>(), {
  showAdd: false,
  showEdit: true,
  showDelete: true,
  showView: false,
  addIcon: 'mdi-plus',
  editIcon: 'mdi-pencil',
  deleteIcon: 'mdi-delete',
  viewIcon: 'mdi-eye',
  size: 'default',
  iconSize: 'small',
  variant: 'elevated',
  loading: false,
  disabled: false,
  customActions: () => []
})

defineEmits<Emits>()
</script>

<style scoped>
/* 使用 Vuetify gap utility 和 unified-components.css 中的 btn-hover 类 */

/* 响应式设计 */
@media (max-width: 600px) {
  .action-button-group {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>