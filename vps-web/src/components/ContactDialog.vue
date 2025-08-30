<template>
  <v-dialog v-model="show" max-width="500px">
    <v-card>
      <v-card-title class="d-flex align-center pa-6">
        <v-icon class="me-3" color="primary" size="28">mdi-phone</v-icon>
        <span class="text-h5">联系我们</span>
      </v-card-title>
      
      <v-card-text class="pa-6">
        <div class="text-center">
          <h3 class="text-h6 mb-4">联系我们</h3>
          <p class="text-body-1 text-medium-emphasis mb-6">
            我们提供7x24小时技术支持，随时为您服务
          </p>
          
          <!-- 联系方式 -->
          <div class="contact-info">
            <div class="contact-item mb-3">
              <v-icon class="me-2" color="primary">mdi-qqchat</v-icon>
              <span>QQ群: 736757426</span>
            </div>
            <div class="contact-item mb-3">
              <v-icon class="me-2" color="primary">mdi-email</v-icon>
              <span>邮箱: rabbitvps@163.com</span>
            </div>
            <div class="contact-item">
              <v-icon class="me-2" color="primary">mdi-wechat</v-icon>
              <span>微信: rabbitvps</span>
            </div>
          </div>
        </div>
      </v-card-text>
      
      <v-card-actions class="pa-6">
        <v-spacer />
        <v-btn
          variant="outlined"
          @click="close"
        >
          关闭
        </v-btn>
        <v-btn
          color="primary"
          variant="flat"
          @click="copyContact"
        >
          复制联系方式
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- 通知组件 -->
  <NotificationSnackbar
    v-model="notificationState.show"
    :message="notificationState.message"
    :type="notificationState.type"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useNotification } from '@/composables/useNotification'
import NotificationSnackbar from './NotificationSnackbar.vue'

interface Props {
  modelValue: boolean
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const { notificationState, showNotification } = useNotification()

const show = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const close = () => {
  show.value = false
}

const copyContact = () => {
  const contactInfo = `QQ群: 736757426\n邮箱: rabbitvps@163.com\n微信: rabbitvps`
  navigator.clipboard.writeText(contactInfo).then(() => {
    showNotification('联系方式已复制', 'success')
    close()
  }).catch(() => {
    showNotification('复制失败', 'error')
  })
}
</script>

<style scoped>
.contact-info {
  text-align: left;
  max-width: 300px;
  margin: 0 auto;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  font-size: 1rem;
}
</style>