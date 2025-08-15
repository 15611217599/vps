<template>
  <v-app :theme="themeStore.currentTheme">
    <!-- 统一顶部栏 - 访客模式 -->
    <UnifiedTopBar :is-guest="true" />
    
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
import UnifiedTopBar from './UnifiedTopBar.vue'
import FixedFooter from './FixedFooter.vue'

const themeStore = useThemeStore()
const vuetifyTheme = useTheme()

// 应用主题到Vuetify
const applyThemeToVuetify = () => {
  vuetifyTheme.global.name.value = themeStore.currentTheme
  
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
  applyThemeToVuetify()
})
</script>

<style scoped>
.main-content {
  /* 为固定顶部栏留出空间，底部栏由Vuetify app属性自动处理 */
  padding-top: 56px;
  /* 移除最小高度设置，让内容自然撑开 */
  background-color: transparent !important;
}
</style>