import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import AuthView from '../views/AuthView.vue'
import DashboardView from '../views/DashboardView.vue'
import ProfileView from '../views/ProfileView.vue'
import ForgotPasswordView from '../views/ForgotPasswordView.vue'
import ServerGroupView from '../views/ServerGroupView.vue'
import ServerCategoryManagementView from '../views/ServerCategoryManagementView.vue'
import ServerView from '../views/ServerView.vue'
import PriceGroupView from '../views/PriceGroupView.vue'
import OrderView from '../views/OrderView.vue'
import SalesView from '../views/SalesView.vue'
import ServerManageView from '../views/ServerManageView.vue'
import TransactionView from '../views/TransactionView.vue'



const routes = [
  {
    path: '/',
    redirect: '/sales'
  },
  {
    path: '/auth',
    name: 'Auth',
    component: AuthView,
    meta: { requiresGuest: true }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardView,
    meta: { requiresAuth: true }
  },

  {
    path: '/groups',
    name: 'ServerGroup',
    component: ServerGroupView,
    meta: { requiresAuth: true }
  },
  {
    path: '/categories',
    name: 'ServerCategories',
    component: ServerCategoryManagementView,
    meta: { requiresAuth: true }
  },
  {
    path: '/servers',
    name: 'Servers',
    component: ServerView,
    meta: { requiresAuth: true }
  },
  {
    path: '/price-groups',
    name: 'PriceGroups',
    component: PriceGroupView,
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: OrderView,
    meta: { requiresAuth: true }
  },
  {
    path: '/sales',
    name: 'Sales',
    component: SalesView
    // 销售页面允许所有用户访问（已登录和未登录）
  },
  {
    path: '/server-manage',
    name: 'ServerManage',
    component: ServerManageView,
    meta: { requiresAuth: true }
  },
  {
    path: '/transactions',
    name: 'Transactions',
    component: TransactionView,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: ProfileView,
    meta: { requiresAuth: true }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: ForgotPasswordView,
    meta: { requiresGuest: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/auth')
  } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router