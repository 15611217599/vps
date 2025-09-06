<template>
  <v-app :theme="themeStore.currentTheme">
    <!-- 首页顶部导航 -->
    <HomeTopNavigation />

    <v-main class="main-content">
      <slot />
    </v-main>

    <!-- 固定底部栏 -->
    <FixedFooter />
  </v-app>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useTheme } from 'vuetify'
import { useThemeStore } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'
import HomeTopNavigation from '@/components/HomeTopNavigation.vue'
import FixedFooter from '@/components/FixedFooter.vue'

const themeStore = useThemeStore()
const authStore = useAuthStore()
const vuetifyTheme = useTheme()

// 应用主题到Vuetify
const applyThemeToVuetify = () => {
  if (typeof (vuetifyTheme as any).change === 'function') {
    ;(vuetifyTheme as any).change(themeStore.currentTheme)
  } else {
    // @ts-ignore legacy fallback for older Vuetify
    vuetifyTheme.global.name.value = themeStore.currentTheme
  }
  
  // 更新主题颜色
  const theme = vuetifyTheme.themes.value[themeStore.currentTheme]
  if (theme) {
    Object.assign(theme.colors, themeStore.currentColors)
  }
}

// 监听主题变化
watch(
  () => [themeStore.currentTheme, themeStore.currentColors],
  applyThemeToVuetify,
  { deep: true, immediate: true }
)

onMounted(() => {
  authStore.getProfile()
  applyThemeToVuetify()
})
</script>

<style scoped>
.main-content {
  /* 为固定顶部栏和底部栏留出空间 */
  padding-top: 64px; /* 64px (HomeTopNavigation) */
  padding-bottom: 60px; /* 60px (FixedFooter) */
  background-color: transparent !important;
}
</style>