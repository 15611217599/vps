<template>
  <v-dialog 
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    max-width="500px"
    width="90vw"
    scrim="rgba(0, 0, 0, 0.7)"
    persistent
  >
    <v-card rounded="lg" elevation="24" class="mx-auto" style="background: white;">
      <v-card-title class="text-h6 pa-6 d-flex align-center bg-error text-white">
        <v-icon class="me-3" size="28">mdi-alert-circle</v-icon>
        <span>{{ title || $t('common.deleteConfirm') }}</span>
      </v-card-title>
      
      <v-card-text class="pa-6">
        <div class="text-body-1 mb-4">
          {{ message }}
        </div>
        <div v-if="itemName" class="text-body-2 text-medium-emphasis">
          <strong>{{ $t('common.item') }}:</strong> {{ itemName }}
        </div>
      </v-card-text>
      
      <v-card-actions class="pa-6 pt-0">
        <v-spacer />
        <v-btn 
          variant="outlined" 
          color="grey-darken-1"
          @click="$emit('cancel')"
          class="me-3"
          rounded="lg"
        >
          {{ $t('common.cancel') }}
        </v-btn>
        <v-btn
          color="error"
          :loading="loading"
          @click="$emit('confirm')"
          rounded="lg"
          class="px-6"
        >
          {{ $t('common.delete') }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
interface Props {
  modelValue: boolean
  title?: string
  message: string
  itemName?: string
  loading?: boolean
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'confirm'): void
  (e: 'cancel'): void
}

withDefaults(defineProps<Props>(), {
  title: '',
  itemName: '',
  loading: false
})

defineEmits<Emits>()
</script>
