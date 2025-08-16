<template>
  <div class="unified-form-field">
    <!-- 文本输入框 -->
    <v-text-field
      v-if="type === 'text' || type === 'email' || type === 'password' || type === 'number'"
      :model-value="modelValue"
      :label="label"
      :type="type"
      :rules="rules"
      :required="required"
      :disabled="disabled"
      :readonly="readonly"
      :placeholder="placeholder"
      :prepend-inner-icon="icon"
      :append-inner-icon="appendIcon"
      :hint="hint"
      :persistent-hint="!!hint"
      :step="step"
      variant="outlined"
      color="primary"
      density="comfortable"
      class="form-field"
      :class="fieldClass"
      bg-color="surface"
      @update:model-value="$emit('update:modelValue', $event)"
      @click:append-inner="$emit('click:append')"
    />

    <!-- 文本域 -->
    <v-textarea
      v-else-if="type === 'textarea'"
      :model-value="modelValue"
      :label="label"
      :rules="rules"
      :required="required"
      :disabled="disabled"
      :readonly="readonly"
      :placeholder="placeholder"
      :prepend-inner-icon="icon"
      :hint="hint"
      :persistent-hint="!!hint"
      :rows="rows"
      :auto-grow="autoGrow"
      variant="outlined"
      color="primary"
      density="comfortable"
      class="form-field"
      :class="fieldClass"
      bg-color="surface"
      @update:model-value="$emit('update:modelValue', $event)"
    />

    <!-- 选择框 -->
    <v-select
      v-else-if="type === 'select'"
      :model-value="modelValue"
      :items="items"
      :item-title="itemTitle"
      :item-value="itemValue"
      :label="label"
      :rules="rules"
      :required="required"
      :disabled="disabled"
      :readonly="readonly"
      :placeholder="placeholder"
      :prepend-inner-icon="icon"
      :hint="hint"
      :persistent-hint="!!hint"
      :multiple="multiple"
      :clearable="clearable"
      variant="outlined"
      color="primary"
      density="comfortable"
      class="form-field"
      :class="fieldClass"
      bg-color="surface"
      @update:model-value="$emit('update:modelValue', $event)"
    >
      <!-- 传递选择项插槽 -->
      <template v-if="$slots.item" #item="scope">
        <slot name="item" v-bind="scope" />
      </template>
      
      <!-- 传递选中项插槽 -->
      <template v-if="$slots.selection" #selection="scope">
        <slot name="selection" v-bind="scope" />
      </template>
    </v-select>

    <!-- 自动完成 -->
    <v-autocomplete
      v-else-if="type === 'autocomplete'"
      :model-value="modelValue"
      :items="items"
      :item-title="itemTitle"
      :item-value="itemValue"
      :label="label"
      :rules="rules"
      :required="required"
      :disabled="disabled"
      :readonly="readonly"
      :placeholder="placeholder"
      :prepend-inner-icon="icon"
      :hint="hint"
      :persistent-hint="!!hint"
      :multiple="multiple"
      :clearable="clearable"
      variant="outlined"
      color="primary"
      density="comfortable"
      class="form-field"
      :class="fieldClass"
      bg-color="surface"
      @update:model-value="$emit('update:modelValue', $event)"
    >
      <!-- 传递选择项插槽 -->
      <template v-if="$slots.item" #item="scope">
        <slot name="item" v-bind="scope" />
      </template>
      
      <!-- 传递选中项插槽 -->
      <template v-if="$slots.selection" #selection="scope">
        <slot name="selection" v-bind="scope" />
      </template>
    </v-autocomplete>

    <!-- 开关 -->
    <v-switch
      v-else-if="type === 'switch'"
      :model-value="modelValue"
      :label="label"
      :disabled="disabled"
      :readonly="readonly"
      :hint="hint"
      :persistent-hint="!!hint"
      color="primary"
      class="form-field switch-field"
      :class="fieldClass"
      @update:model-value="$emit('update:modelValue', $event)"
    />

    <!-- 复选框 -->
    <v-checkbox
      v-else-if="type === 'checkbox'"
      :model-value="modelValue"
      :label="label"
      :disabled="disabled"
      :readonly="readonly"
      :hint="hint"
      :persistent-hint="!!hint"
      color="primary"
      class="form-field checkbox-field"
      :class="fieldClass"
      @update:model-value="$emit('update:modelValue', $event)"
    />

    <!-- 单选框组 -->
    <v-radio-group
      v-else-if="type === 'radio'"
      :model-value="modelValue"
      :disabled="disabled"
      :readonly="readonly"
      :hint="hint"
      :persistent-hint="!!hint"
      color="primary"
      class="form-field radio-field"
      :class="fieldClass"
      @update:model-value="$emit('update:modelValue', $event)"
    >
      <template #label>
        <span class="text-subtitle-1 font-weight-medium">{{ label }}</span>
      </template>
      <v-radio
        v-for="item in items"
        :key="item.value"
        :label="item.title"
        :value="item.value"
      />
    </v-radio-group>

    <!-- 日期选择器 -->
    <v-text-field
      v-else-if="type === 'date'"
      :model-value="modelValue"
      :label="label"
      :rules="rules"
      :required="required"
      :disabled="disabled"
      :readonly="readonly"
      :placeholder="placeholder"
      :prepend-inner-icon="icon || 'mdi-calendar'"
      :hint="hint"
      :persistent-hint="!!hint"
      type="date"
      variant="outlined"
      color="primary"
      density="comfortable"
      class="form-field"
      :class="fieldClass"
      bg-color="surface"
      @update:model-value="$emit('update:modelValue', $event)"
    />

    <!-- 时间选择器 -->
    <v-text-field
      v-else-if="type === 'time'"
      :model-value="modelValue"
      :label="label"
      :rules="rules"
      :required="required"
      :disabled="disabled"
      :readonly="readonly"
      :placeholder="placeholder"
      :prepend-inner-icon="icon || 'mdi-clock'"
      :hint="hint"
      :persistent-hint="!!hint"
      type="time"
      variant="outlined"
      color="primary"
      density="comfortable"
      class="form-field"
      :class="fieldClass"
      bg-color="surface"
      @update:model-value="$emit('update:modelValue', $event)"
    />
  </div>
</template>

<script setup lang="ts">
interface Props {
  modelValue: any
  type: 'text' | 'email' | 'password' | 'number' | 'textarea' | 'select' | 'autocomplete' | 'switch' | 'checkbox' | 'radio' | 'date' | 'time'
  label: string
  rules?: any[]
  required?: boolean
  disabled?: boolean
  readonly?: boolean
  placeholder?: string
  icon?: string
  appendIcon?: string
  hint?: string
  step?: string | number
  rows?: number
  autoGrow?: boolean
  items?: any[]
  itemTitle?: string
  itemValue?: string
  multiple?: boolean
  clearable?: boolean
  fieldClass?: string
}

interface Emits {
  (e: 'update:modelValue', value: any): void
  (e: 'click:append'): void
}

withDefaults(defineProps<Props>(), {
  rules: () => [],
  required: false,
  disabled: false,
  readonly: false,
  placeholder: '',
  icon: '',
  appendIcon: '',
  hint: '',
  step: 'any',
  rows: 3,
  autoGrow: false,
  items: () => [],
  itemTitle: 'title',
  itemValue: 'value',
  multiple: false,
  clearable: false,
  fieldClass: ''
})

defineEmits<Emits>()
</script>

<style scoped>
.unified-form-field {
  margin-bottom: 8px;
}

.form-field {
  transition: all 0.3s ease;
}

.form-field :deep(.v-field) {
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.form-field :deep(.v-field:hover) {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.form-field :deep(.v-field--focused) {
  box-shadow: 0 0 0 2px rgba(var(--v-theme-primary), 0.2);
}

.form-field :deep(.v-field__input) {
  padding: 12px 16px;
  font-size: 14px;
  line-height: 1.5;
}

.form-field :deep(.v-label) {
  font-weight: 500;
  color: rgba(var(--v-theme-on-surface), 0.7);
}

.form-field :deep(.v-field--focused .v-label) {
  color: rgb(var(--v-theme-primary));
}

/* 开关字段样式 */
.switch-field {
  margin-top: 16px;
}

.switch-field :deep(.v-switch__track) {
  border-radius: 12px;
}

.switch-field :deep(.v-switch__thumb) {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 复选框字段样式 */
.checkbox-field {
  margin-top: 8px;
}

.checkbox-field :deep(.v-checkbox .v-selection-control__wrapper) {
  margin-right: 12px;
}

/* 单选框组样式 */
.radio-field {
  margin-top: 8px;
}

.radio-field :deep(.v-radio) {
  margin-bottom: 8px;
}

.radio-field :deep(.v-selection-control__wrapper) {
  margin-right: 12px;
}

/* 错误状态样式 */
.form-field :deep(.v-field--error) {
  border-color: rgb(var(--v-theme-error));
  box-shadow: 0 0 0 2px rgba(var(--v-theme-error), 0.2);
}

/* 禁用状态样式 */
.form-field :deep(.v-field--disabled) {
  opacity: 0.6;
  background-color: rgba(var(--v-theme-surface-variant), 0.3);
}

/* 只读状态样式 */
.form-field :deep(.v-field--readonly) {
  background-color: rgba(var(--v-theme-surface-variant), 0.1);
}

/* 响应式设计 */
@media (max-width: 600px) {
  .form-field :deep(.v-field__input) {
    padding: 10px 14px;
    font-size: 16px; /* 防止iOS缩放 */
  }
}
</style>