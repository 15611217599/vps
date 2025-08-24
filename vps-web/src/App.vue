<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useTheme } from 'vuetify'
import { useThemeStore } from '@/stores/theme'

const vuetifyTheme = useTheme()
const themeStore = useThemeStore()

// 应用主题到Vuetify
const applyThemeToVuetify = () => {
  // Prefer new API; fallback to legacy to avoid runtime break
  if (typeof (vuetifyTheme as any).change === 'function') {
    ;(vuetifyTheme as any).change(themeStore.currentTheme)
  } else {
    // legacy fallback
    // @ts-ignore
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
  applyThemeToVuetify()
})
</script>

<template>
  <v-app :theme="themeStore.currentTheme">
    <router-view />
  </v-app>
</template>

<style>
/* 全局样式 - 让内容自然撑开 */
html, body {
  margin: 0 !important;
  padding: 0 !important;
  overflow-x: hidden !important;
  background-color: transparent !important;
}

#app {
  overflow-x: hidden !important;
  background-color: transparent !important;
}

.v-application {
  overflow-x: hidden !important;
  background-color: transparent !important;
}

/* 自定义滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.5);
}
</style>
