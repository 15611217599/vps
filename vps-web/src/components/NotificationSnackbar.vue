<template>
  <v-snackbar
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    :color="color"
    :timeout="timeout"
    location="top right"
    variant="elevated"
    elevation="8"
    rounded="lg"
  >
    <div class="d-flex align-center">
      <v-icon class="me-3" size="20">
        {{ icon }}
      </v-icon>
      <span class="text-body-2 font-weight-medium">{{ message }}</span>
    </div>
    
    <template #actions>
      <v-btn
        icon="mdi-close"
        size="small"
        variant="text"
        @click="$emit('update:modelValue', false)"
      />
    </template>
  </v-snackbar>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  modelValue: boolean
  message: string
  type?: 'success' | 'error' | 'warning' | 'info'
  timeout?: number
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
}

const props = withDefaults(defineProps<Props>(), {
  type: 'info',
  timeout: 4000
})

defineEmits<Emits>()

const color = computed(() => {
  switch (props.type) {
    case 'success': return 'success'
    case 'error': return 'error'
    case 'warning': return 'warning'
    default: return 'info'
  }
})

const icon = computed(() => {
  switch (props.type) {
    case 'success': return 'mdi-check-circle'
    case 'error': return 'mdi-alert-circle'
    case 'warning': return 'mdi-alert'
    default: return 'mdi-information'
  }
})
</script>
