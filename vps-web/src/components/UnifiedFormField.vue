<template>
  <div class="form-field-wrapper">
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
/* 简化样式 - 使用 Vuetify 默认样式 */
.form-field-wrapper {
  margin-bottom: 8px;
}

/* 响应式设计 */
@media (max-width: 600px) {
  .form-field :deep(.v-field__input) {
    font-size: 16px; /* 防止iOS缩放 */
  }
}
</style>