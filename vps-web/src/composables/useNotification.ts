import { ref } from 'vue'

interface NotificationState {
  show: boolean
  message: string
  type: 'success' | 'error' | 'warning' | 'info'
}

const notificationState = ref<NotificationState>({
  show: false,
  message: '',
  type: 'info'
})

export function useNotification() {
  const showNotification = (message: string, type: 'success' | 'error' | 'warning' | 'info' = 'info') => {
    notificationState.value = {
      show: true,
      message,
      type
    }
  }

  const hideNotification = () => {
    notificationState.value.show = false
  }

  return {
    notificationState,
    showNotification,
    hideNotification
  }
}
