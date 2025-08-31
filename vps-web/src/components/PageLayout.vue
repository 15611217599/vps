<template>
  <v-app :theme="themeStore.currentTheme">
    <!-- 统一顶部栏 -->
    <UnifiedTopBar :title="currentTitle" :is-guest="false" @toggle-drawer="drawer = !drawer" />

    <!-- 侧边导航栏 -->
    <v-navigation-drawer
      v-model="drawer"
      app
      :permanent="lgAndUp"
      :temporary="!lgAndUp"
      width="280"
      class="navigation-drawer"
    >

      <v-list nav class="pa-2">
        <template v-for="item in navigationItems" :key="item.title">
          <!-- Regular Nav Item -->
          <v-list-item
            v-if="!item.items"
            :to="item.to"
            :prepend-icon="item.icon"
            :title="item.title"
            :active="route.path === item.to"
            class="nav-item"
            rounded="lg"
          />

          <!-- Nav Group -->
          <v-list-group v-else :value="item.title">
            <template v-slot:activator="{ props }">
              <v-list-item
                v-bind="props"
                :prepend-icon="item.icon"
                :title="item.title"
                class="nav-group-title"
              ></v-list-item>
            </template>

            <v-list-item
              v-for="subItem in item.items"
              :key="subItem.title"
              :to="subItem.to"
              :title="subItem.title"
              :active="route.path === subItem.to"
              class="nav-sub-item"
              rounded="lg"
            />
          </v-list-group>
        </template>
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
import { useTheme, useDisplay } from 'vuetify'
import { useRoute } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'
import UnifiedTopBar from './UnifiedTopBar.vue'
import FixedFooter from './FixedFooter.vue'

interface Props {
  title?: string
}

defineProps<Props>()

const themeStore = useThemeStore()
const authStore = useAuthStore()
const vuetifyTheme = useTheme()
const { lgAndUp } = useDisplay()
const route = useRoute()

// 侧边栏状态
const drawer = ref(true)

// 根据当前路由自动获取页面标题
const currentTitle = computed(() => {
  const findTitleByPath = (items: any[], path: string): string | null => {
    for (const item of items) {
      if (item.to === path) {
        return item.title
      }
      if (item.items) {
        const subTitle = findTitleByPath(item.items, path)
        if (subTitle) return subTitle
      }
    }
    return null
  }
  
  return findTitleByPath(navigationItems.value, route.path) || '页面'
})

// 导航菜单项
const navigationItems = computed(() => [
  {
    title: '仪表盘',
    icon: 'mdi-view-dashboard',
    to: '/dashboard'
  },
  {
    title: '购买服务器',
    icon: 'mdi-cart-outline',
    to: '/sales'
  },
  {
    title: '服务器管理',
    icon: 'mdi-server-network',
    items: [
      {
        title: '服务器分类',
        to: '/categories'
      },
      {
        title: '服务器组',
        to: '/groups'
      },
      {
        title: '服务器',
        to: '/servers'
      },
      {
        title: '价格组',
        to: '/price-groups'
      }
    ]
  },
  {
    title: '财务管理',
    icon: 'mdi-finance',
    items: [
      {
        title: '订单管理',
        to: '/orders'
      },
      {
        title: '交易流水',
        to: '/transactions'
      },
      {
        title: '支付记录',
        to: '/payment/history'
      }
    ]
  },
  {
    title: '个人资料',
    icon: 'mdi-account',
    to: '/profile'
  }
])

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
  padding-top: 64px; /* 64px (UnifiedTopBar) */
  padding-bottom: 60px; /* 60px (FixedFooter) */
  /* 移除最小高度设置，让内容自然撑开 */
  background-color: transparent !important;
}

.navigation-drawer {
  border-right: 1px solid rgba(var(--v-theme-on-surface), 0.08);
}

.nav-item, .nav-group-title {
  margin-bottom: 4px;
  transition: all 0.2s ease;
  font-weight: 500;
}

.nav-item:hover, .v-list-group:hover .nav-group-title {
  background-color: rgba(var(--v-theme-primary), 0.08);
}

.nav-item.v-list-item--active {
  background-color: rgba(var(--v-theme-primary), 0.12);
  color: rgb(var(--v-theme-primary));
}

.nav-item.v-list-item--active :deep(.v-icon) {
  color: rgb(var(--v-theme-primary));
}

.nav-sub-item {
  margin: 0 8px 4px 8px;
  padding-left: 32px !important; /* Indent sub-items */
  font-size: 0.875rem;
}

.nav-sub-item:hover {
  background-color: rgba(var(--v-theme-on-surface), 0.04);
}

.nav-sub-item.v-list-item--active {
  background-color: transparent;
  color: rgb(var(--v-theme-primary));
  font-weight: bold;
}
</style>