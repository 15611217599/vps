<template>
  <v-app :theme="themeStore.currentTheme">
    <!-- 统一顶部栏 -->
    <UnifiedTopBar :title="title" :is-guest="false" @toggle-drawer="drawer = !drawer" />
    
    <!-- 侧边导航栏 -->
    <v-navigation-drawer
      v-model="drawer"
      app
      :permanent="$vuetify.display.lgAndUp"
      :temporary="!$vuetify.display.lgAndUp"
      width="280"
      class="navigation-drawer"
    >
      <v-list nav class="pa-2">
        <v-list-item
          v-for="item in navigationItems"
          :key="item.title"
          :to="item.to"
          :prepend-icon="item.icon"
          :title="item.title"
          :active="$route.path === item.to"
          class="nav-item"
          rounded="lg"
        />
      </v-list>
    </v-navigation-drawer>
    
    <v-main class="main-content">
      <slot />
    </v-main>
    
    <!-- 固定底部栏 -->
    <FixedFooter />
  </v-app>
</template>

<script setup lang="ts">
import { onMounted, watch, ref, computed } from 'vue'
import { useTheme } from 'vuetify'
import { useThemeStore } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from 'vue-i18n'
import UnifiedTopBar from './UnifiedTopBar.vue'
import FixedFooter from './FixedFooter.vue'

interface Props {
  title?: string
}

defineProps<Props>()

const themeStore = useThemeStore()
const authStore = useAuthStore()
const vuetifyTheme = useTheme()
const { t } = useI18n()

// 侧边栏状态
const drawer = ref(true)

// 导航菜单项
const navigationItems = computed(() => [
  {
    title: t('nav.dashboard'),
    icon: 'mdi-view-dashboard',
    to: '/dashboard'
  },
  {
    title: t('nav.serverCategory'),
    icon: 'mdi-folder-multiple',
    to: '/categories'
  },
  {
    title: t('nav.serverGroup'),
    icon: 'mdi-folder-network',
    to: '/groups'
  },
  {
    title: t('nav.servers'),
    icon: 'mdi-server',
    to: '/servers'
  },
  {
    title: t('nav.priceGroups'),
    icon: 'mdi-currency-usd',
    to: '/price-groups'
  },
  {
    title: t('nav.sales'),
    icon: 'mdi-storefront',
    to: '/sales'
  },
  {
    title: t('nav.profile'),
    icon: 'mdi-account',
    to: '/profile'
  }
])

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
  authStore.getProfile()
  applyThemeToVuetify()
})
</script>

<style scoped>
.main-content {
  /* 为固定顶部栏和底部栏留出空间 */
  padding-top: 64px; /* 64px (UnifiedTopBar) */
  padding-bottom: 60px; /* 60px (FixedFooter) */
  /* 移除最小高度设置，让内容自然撑开 */
  background-color: transparent !important;
}

.navigation-drawer {
  border-right: 1px solid rgba(var(--v-theme-on-surface), 0.08);
}

.nav-item {
  margin-bottom: 4px;
  transition: all 0.2s ease;
}

.nav-item:hover {
  background-color: rgba(var(--v-theme-primary), 0.08);
}

.nav-item.v-list-item--active {
  background-color: rgba(var(--v-theme-primary), 0.12);
  color: rgb(var(--v-theme-primary));
}

.nav-item.v-list-item--active :deep(.v-icon) {
  color: rgb(var(--v-theme-primary));
}
</style>