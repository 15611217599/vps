<template>
  <v-app-bar
    app
    fixed
    :color="themeStore.currentColors.primary"
    :elevation="2"
    height="64"
    class="home-top-nav"
  >
    <template v-slot:default>
      <!-- 左侧品牌标识 -->
      <div class="d-flex align-center">
        <BrandLogo :show-subtitle="true" class="me-4" />
      </div>
      
      <v-spacer />
      
      <!-- 中间导航菜单 -->
      <div class="nav-menu d-none d-lg-flex align-center">
        <template v-for="item in navigationItems" :key="item.title">
          <!-- 普通导航项 -->
          <v-btn
            v-if="!item.items"
            :to="item.to"
            variant="text"
            color="white"
            class="nav-btn me-1"
            size="small"
            :class="{ 'nav-btn--active': route.path === item.to }"
          >
            <v-icon start size="18">{{ item.icon }}</v-icon>
            {{ item.title }}
          </v-btn>

          <!-- 下拉菜单项 -->
          <v-menu v-else offset-y>
            <template v-slot:activator="{ props }">
              <v-btn
                v-bind="props"
                variant="text"
                color="white"
                class="nav-btn me-1"
                size="small"
                :class="{ 'nav-btn--active': isGroupActive(item) }"
              >
                <v-icon start size="18">{{ item.icon }}</v-icon>
                {{ item.title }}
                <v-icon end size="14">mdi-chevron-down</v-icon>
              </v-btn>
            </template>
            
            <v-list class="nav-dropdown">
              <v-list-item
                v-for="subItem in item.items"
                :key="subItem.title"
                :to="subItem.to"
                :class="{ 'nav-dropdown-item--active': route.path === subItem.to }"
                class="nav-dropdown-item"
              >
                <v-list-item-title>{{ subItem.title }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </template>
      </div>
      
      <!-- 右侧控制区域 -->
      <div class="d-flex align-center">
        <!-- 移动端菜单按钮 -->
        <v-btn
          icon
          variant="text"
          color="white"
          class="d-lg-none me-2"
          @click="mobileDrawer = !mobileDrawer"
        >
          <v-icon>mdi-menu</v-icon>
        </v-btn>
        
        <!-- 主题切换器 -->
        <ThemeSwitcher class="me-2" />
        
        <!-- 用户菜单 -->
        <v-menu offset-y v-if="authStore.isAuthenticated && authStore.user">
          <template v-slot:activator="{ props }">
            <v-btn
              v-bind="props"
              prepend-icon="mdi-account-circle"
              variant="text"
              color="white"
              class="me-2"
              height="40"
            >
              {{ authStore.user?.username }}
            </v-btn>
          </template>
          
          <v-list>
            <v-list-item prepend-icon="mdi-account" title="个人资料" @click="handleProfile"></v-list-item>
            <v-divider />
            <v-list-item prepend-icon="mdi-logout" title="退出登录" @click="handleLogout"></v-list-item>
          </v-list>
        </v-menu>
        
        <!-- 未登录时的登录按钮 -->
        <v-btn
          v-else
          variant="outlined"
          color="white"
          class="me-2"
          @click="navigateToAuth"
        >
          <v-icon start>mdi-login</v-icon>
          登录
        </v-btn>
      </div>
    </template>
  </v-app-bar>

  <!-- 移动端导航抽屉 -->
  <v-navigation-drawer
    v-model="mobileDrawer"
    temporary
    location="right"
    width="280"
    class="mobile-nav-drawer"
  >
    <v-list nav class="pa-2">
      <template v-for="item in navigationItems" :key="item.title">
        <!-- 普通导航项 -->
        <v-list-item
          v-if="!item.items"
          :to="item.to"
          :prepend-icon="item.icon"
          :title="item.title"
          :active="route.path === item.to"
          class="mobile-nav-item"
          rounded="lg"
          @click="mobileDrawer = false"
        />

        <!-- 导航组 -->
        <v-list-group 
          v-else 
          :value="item.title"
          :model-value="defaultOpenGroups.includes(item.title)"
        >
          <template v-slot:activator="{ props }">
            <v-list-item
              v-bind="props"
              :prepend-icon="item.icon"
              :title="item.title"
              class="mobile-nav-group-title"
            ></v-list-item>
          </template>

          <v-list-item
            v-for="subItem in item.items"
            :key="subItem.title"
            :to="subItem.to"
            :title="subItem.title"
            :active="route.path === subItem.to"
            class="mobile-nav-sub-item"
            rounded="lg"
            @click="mobileDrawer = false"
          />
        </v-list-group>
      </template>
    </v-list>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'
import ThemeSwitcher from './ThemeSwitcher.vue'
import BrandLogo from './BrandLogo.vue'

const route = useRoute()
const router = useRouter()
const themeStore = useThemeStore()
const authStore = useAuthStore()

// 移动端抽屉状态
const mobileDrawer = ref(false)

// 默认展开的菜单组
const defaultOpenGroups = ref(['服务器管理'])

// 导航菜单项 - 整合了原左侧导航的所有功能
const navigationItems = computed(() => {
  const baseItems = [
    {
      title: '首页',
      icon: 'mdi-home',
      to: '/'
    },
    {
      title: '仪表盘',
      icon: 'mdi-view-dashboard',
      to: '/dashboard'
    },
    {
      title: '产品',
      icon: 'mdi-server',
      to: '/sales'
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
          title: '充值',
          to: '/recharge'
        },
        {
          title: '支付记录',
          to: '/payment/history'
        }
      ]
    }
  ]

  // 仅管理员可见的服务器管理菜单
  if (authStore.isAdmin) {
    baseItems.splice(3, 0, {
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
    })
  }

  // 如果用户已登录，显示相应菜单项
  if (authStore.isAuthenticated) {
    return baseItems
  }
  
  // 如果用户未登录，只显示部分菜单项
  return baseItems.filter(item => 
    ['首页', '产品'].includes(item.title)
  )
})

// 检查导航组是否激活
const isGroupActive = (group: any) => {
  if (!group.items) return false
  return group.items.some((item: any) => route.path === item.to)
}

// 处理用户操作
const handleProfile = () => {
  router.push('/profile')
}

const handleLogout = () => {
  authStore.logout()
  router.push('/auth')
}

const navigateToAuth = () => {
  router.push('/auth')
}
</script>

<style scoped>
.home-top-nav {
  z-index: 2000 !important;
}

/* 导航按钮样式 */
.nav-btn {
  font-weight: 500;
  text-transform: none;
  letter-spacing: 0;
  border-radius: 6px;
  transition: all 0.2s ease;
  min-width: auto;
  padding: 0 12px;
  font-size: 0.875rem;
}

.nav-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-btn--active {
  background-color: rgba(255, 255, 255, 0.15);
  font-weight: 600;
}

/* 下拉菜单样式 */
.nav-dropdown {
  min-width: 200px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.nav-dropdown-item {
  transition: background-color 0.2s ease;
}

.nav-dropdown-item:hover {
  background-color: rgba(var(--v-theme-primary), 0.08);
}

.nav-dropdown-item--active {
  background-color: rgba(var(--v-theme-primary), 0.12);
  color: rgb(var(--v-theme-primary));
  font-weight: 600;
}

/* 移动端导航样式 */
.mobile-nav-drawer {
  border-left: 1px solid rgba(var(--v-theme-on-surface), 0.08);
}

.mobile-nav-item, .mobile-nav-group-title {
  margin-bottom: 4px;
  transition: all 0.2s ease;
  font-weight: 500;
}

.mobile-nav-item:hover, .mobile-nav-group-title:hover {
  background-color: rgba(var(--v-theme-primary), 0.08);
}

.mobile-nav-item.v-list-item--active {
  background-color: rgba(var(--v-theme-primary), 0.12);
  color: rgb(var(--v-theme-primary));
}

.mobile-nav-item.v-list-item--active :deep(.v-icon) {
  color: rgb(var(--v-theme-primary));
}

.mobile-nav-sub-item {
  margin: 0 8px 4px 8px;
  padding-left: 32px !important;
  font-size: 0.875rem;
}

.mobile-nav-sub-item:hover {
  background-color: rgba(var(--v-theme-on-surface), 0.04);
}

.mobile-nav-sub-item.v-list-item--active {
  background-color: transparent;
  color: rgb(var(--v-theme-primary));
  font-weight: bold;
}

/* 确保文字在主色调背景上清晰可见 */
.text-white {
  color: white !important;
}

/* 确保所有内容垂直居中 */
:deep(.v-toolbar__content) {
  align-items: center !important;
}

/* 确保按钮和图标对齐 */
:deep(.v-btn) {
  align-items: center !important;
}

/* 响应式设计 */
@media (max-width: 1264px) {
  .nav-menu {
    display: none !important;
  }
}
</style>