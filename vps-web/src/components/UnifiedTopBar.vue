<template>
  <v-app-bar
    app
    fixed
    :color="themeStore.currentColors.primary"
    :elevation="2"
    :height="isGuest ? 56 : 64"

  >
    <template v-slot:default>
      <!-- 左侧品牌标识和页面标题 -->
      <div class="d-flex align-center">
        <!-- 菜单按钮 - 只在非访客模式下显示 -->
        <v-btn
          v-if="!isGuest"
          icon
          variant="text"
          color="white"
          class="me-2"
          @click="$emit('toggle-drawer')"
        >
          <v-icon>mdi-menu</v-icon>
        </v-btn>
        
        <BrandLogo :show-subtitle="!isGuest" class="me-4" />
        
        <!-- 只在非访客模式下显示分隔线和页面标题 -->
        <template v-if="!isGuest && title">
          <v-divider vertical class="me-4" color="rgba(255,255,255,0.3)" style="height: 24px;" />
          <span class="text-h6 text-white">{{ title }}</span>
        </template>
      </div>
      
      <v-spacer />
      
      <!-- 右侧控制区域 -->
      <div class="d-flex align-center">

        
        <!-- 主题切换器 -->
        <ThemeSwitcher class="me-2" />
        
        <!-- 用户菜单 - 只在非访客模式下显示 -->
        <v-menu offset-y v-if="!isGuest && authStore.user">
          <template v-slot:activator="{ props }">
            <v-btn
              v-bind="props"
              prepend-icon="mdi-account-circle"
              variant="text"
              color="white"
              class="me-2"
              height="40"
              style="align-self: center;"
            >
              {{ authStore.user?.username }}
            </v-btn>
          </template>
          
          <v-list>
            <v-list-item prepend-icon="mdi-account" :title="TEXTS.nav.profile" @click="handleProfile"></v-list-item>
            <v-divider />
            <v-list-item prepend-icon="mdi-logout" title="退出登录" @click="handleLogout"></v-list-item>
          </v-list>
        </v-menu>
      </div>
    </template>
  </v-app-bar>
</template>

<script setup lang="ts">
import { useThemeStore } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { TEXTS } from '@/constants/texts'

import ThemeSwitcher from './ThemeSwitcher.vue'
import BrandLogo from './BrandLogo.vue'

interface Props {
  title?: string
  isGuest?: boolean
}

withDefaults(defineProps<Props>(), {
  isGuest: false
})

defineEmits<{
  'toggle-drawer': []
}>()

const themeStore = useThemeStore()
const authStore = useAuthStore()
const router = useRouter()

const handleProfile = () => {
  router.push('/profile')
}

const handleLogout = () => {
  authStore.logout()
  router.push('/auth')
}
</script>

<style scoped>
.unified-top-bar {
  z-index: 2000 !important;
}

/* 确保文字在主色调背景上清晰可见 */
.text-white {
  color: white !important;
}

/* 确保所有内容垂直居中 */
:deep(.v-toolbar__content) {
  align-items: center !important;
}

/* 分隔线样式 */
:deep(.v-divider--vertical) {
  height: 24px !important;
  align-self: center !important;
}

/* 确保按钮和图标对齐 */
:deep(.v-btn) {
  align-items: center !important;
}

/* 确保在深色主题下也有正确的边框 */
:deep(.v-theme--dark) .unified-top-bar {
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

:deep(.v-theme--light) .unified-top-bar {
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}
</style>
