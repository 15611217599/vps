<template>
  <PageLayout :title="$t('nav.sales')">
    <div class="sales-page">
      <v-container fluid class="pa-0">
        <v-row no-gutters class="fill-height">
          <!-- 左侧菜单栏 -->
          <v-col cols="12" md="4" lg="3" class="sidebar-col">
            <div class="sidebar-card h-100">
              <div class="pa-0">
                <v-list class="category-list" density="compact">
                  <template v-for="category in categories" :key="category.id">
                    <!-- 服务器类别 -->
                    <v-list-item
                      :value="category.id"
                      @click="toggleCategory(category.id)"
                      class="category-item"
                      :class="{ 'category-expanded': expandedCategories.includes(category.id) }"
                    >
                      <template #prepend>
                        <v-avatar size="32" :color="expandedCategories.includes(category.id) ? 'primary' : 'grey-lighten-1'">
                          <v-icon size="18" color="white">{{ getCategoryIcon(category) }}</v-icon>
                        </v-avatar>
                      </template>
                      
                      <v-list-item-title class="font-weight-medium">
                        {{ getLocalizedText(category.name) }}
                      </v-list-item-title>
                      
                      <template #append>
                        <v-chip
                          size="small"
                          :color="getServerGroupCount(category.id) > 0 ? 'success' : 'grey'"
                          variant="flat"
                        >
                          {{ getServerGroupCount(category.id) }}
                        </v-chip>
                        <v-icon class="ms-2">
                          {{ expandedCategories.includes(category.id) ? 'mdi-chevron-down' : 'mdi-chevron-right' }}
                        </v-icon>
                      </template>
                    </v-list-item>
                    
                    <!-- 服务器分组 -->
                    <v-expand-transition>
                      <div v-show="expandedCategories.includes(category.id)">
                        <v-list-item
                          v-for="group in getGroupsByCategory(category.id)"
                          :key="group.id"
                          :value="group.id"
                          @click="selectServerGroup(group)"
                          class="group-item ms-6"
                          :class="{ 'group-selected': selectedGroup?.id === group.id }"
                        >
                          <template #prepend>
                            <v-icon 
                              size="20" 
                              :color="selectedGroup?.id === group.id ? 'primary' : 'grey'"
                            >
                              mdi-server-network
                            </v-icon>
                          </template>
                          
                          <v-list-item-title class="text-body-2">
                            {{ getLocalizedText(group.name) }}
                          </v-list-item-title>
                          
                          <template #append>
                            <v-chip
                              size="x-small"
                              :color="getPriceGroupCount(group.id) > 0 ? 'info' : 'grey'"
                              variant="flat"
                            >
                              {{ getPriceGroupCount(group.id) }}
                            </v-chip>
                          </template>
                        </v-list-item>
                      </div>
                    </v-expand-transition>
                  </template>
                </v-list>
              </div>
            </div>
          </v-col>
          
          <!-- 右侧内容区域 -->
          <v-col cols="12" md="8" lg="9" class="content-col">
            <div class="content-area">
              <!-- 默认欢迎页面 -->
              <div v-if="!selectedGroup" class="welcome-section text-center">
                <v-icon size="120" color="primary" class="mb-6">mdi-rocket-launch</v-icon>
                <h2 class="text-h3 font-weight-bold mb-4">{{ $t('sales.welcome') }}</h2>
                <p class="text-h6 text-medium-emphasis mb-8 max-w-600 mx-auto">
                  {{ $t('sales.selectGroupHint') }}
                </p>
                
                <!-- 快速统计 -->
                <v-row justify="center" class="mb-8">
                  <v-col cols="auto">
                    <v-card class="stats-mini-card" elevation="2">
                      <v-card-text class="pa-4 text-center">
                        <div class="text-h4 font-weight-bold text-primary mb-1">{{ categories.length }}</div>
                        <div class="text-caption text-medium-emphasis">{{ $t('sales.categories') }}</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                  <v-col cols="auto">
                    <v-card class="stats-mini-card" elevation="2">
                      <v-card-text class="pa-4 text-center">
                        <div class="text-h4 font-weight-bold text-success mb-1">{{ serverGroups.length }}</div>
                        <div class="text-caption text-medium-emphasis">{{ $t('sales.groups') }}</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                  <v-col cols="auto">
                    <v-card class="stats-mini-card" elevation="2">
                      <v-card-text class="pa-4 text-center">
                        <div class="text-h4 font-weight-bold text-info mb-1">{{ priceGroups.length }}</div>
                        <div class="text-caption text-medium-emphasis">{{ $t('sales.products') }}</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                </v-row>
                
                <!-- 联系我们按钮 -->
                <v-btn
                  size="large"
                  color="primary"
                  variant="flat"
                  prepend-icon="mdi-phone"
                  @click="contactUs"
                  class="px-8"
                  rounded="xl"
                >
                  {{ $t('sales.contactUs') }}
                </v-btn>
              </div>
              
              <!-- 选中分组的价格组展示 -->
              <div v-else class="price-groups-section">
                <!-- 分组信息头部 -->
                <v-card class="group-header-card mb-6" elevation="2">
                  <v-card-text class="pa-6">
                    <div class="d-flex align-center mb-4">
                      <v-avatar size="48" color="primary" class="me-4">
                        <v-icon size="24" color="white">mdi-server-network</v-icon>
                      </v-avatar>
                      <div>
                        <h2 class="text-h4 font-weight-bold mb-1">{{ getLocalizedText(selectedGroup.name) }}</h2>
                        <p class="text-body-1 text-medium-emphasis mb-0">
                          {{ getLocalizedText(selectedGroup.description) || $t('sales.noDescription') }}
                        </p>
                      </div>
                    </div>
                    
                    <!-- 分组统计信息 -->
                    <v-row>
                      <v-col cols="auto">
                        <v-chip color="info" variant="flat" size="small">
                          <v-icon start size="16">mdi-package-variant</v-icon>
                          {{ getPriceGroupCount(selectedGroup.id) }} {{ $t('sales.products') }}
                        </v-chip>
                      </v-col>
                      <v-col cols="auto">
                        <v-chip color="success" variant="flat" size="small">
                          <v-icon start size="16">mdi-check-circle</v-icon>
                          {{ $t('sales.available') }}
                        </v-chip>
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>
                
                <!-- 价格组列表 -->
                <div v-if="getGroupPriceGroups(selectedGroup.id).length > 0">
                  <v-row>
                    <v-col
                      v-for="priceGroup in getGroupPriceGroups(selectedGroup.id)"
                      :key="priceGroup.id"
                      cols="12"
                      md="6"
                      lg="4"
                    >
                      <v-card class="price-group-card h-100" elevation="3">
                        <v-card-text class="pa-6">
                          <div class="d-flex align-center mb-4">
                            <v-avatar size="40" color="success" class="me-3">
                              <v-icon size="20" color="white">mdi-package-variant</v-icon>
                            </v-avatar>
                            <div>
                              <h3 class="text-h6 font-weight-bold mb-1">{{ getLocalizedText(priceGroup.name) }}</h3>
                              <p class="text-caption text-medium-emphasis mb-0">
                                {{ $t('sales.priceGroup') }}
                              </p>
                            </div>
                          </div>
                          
                          <p class="text-body-2 text-medium-emphasis mb-4">
                            {{ getLocalizedText(priceGroup.description) || $t('sales.noDescription') }}
                          </p>
                          
                          <!-- 价格信息 -->
                          <div class="price-info mb-4">
                            <div class="d-flex align-center justify-space-between">
                              <span class="text-body-2">{{ $t('sales.startingPrice') }}:</span>
                              <span class="text-h6 font-weight-bold text-primary">
                                ¥{{ priceGroup.minPrice || '0' }}/{{ $t('sales.month') }}
                              </span>
                            </div>
                          </div>
                          
                          <!-- 操作按钮 -->
                          <v-btn
                            block
                            color="primary"
                            variant="flat"
                            @click="viewPriceGroup(priceGroup)"
                            class="mt-4"
                          >
                            {{ $t('sales.viewDetails') }}
                          </v-btn>
                        </v-card-text>
                      </v-card>
                    </v-col>
                  </v-row>
                </div>
                
                <!-- 无产品提示 -->
                <div v-else class="no-products-section text-center">
                  <v-card class="no-products-card" elevation="1">
                    <v-card-text class="pa-8">
                      <v-icon size="80" color="grey-lighten-1" class="mb-4">mdi-package-variant-closed</v-icon>
                      <h3 class="text-h5 font-weight-medium mb-3">{{ $t('sales.noProducts') }}</h3>
                      <p class="text-body-1 text-medium-emphasis mb-6">
                        {{ $t('sales.noProductsHint') }}
                      </p>
                      <v-btn
                        color="primary"
                        variant="outlined"
                        prepend-icon="mdi-phone"
                        @click="contactUs"
                      >
                        {{ $t('sales.contactUs') }}
                      </v-btn>
                    </v-card-text>
                  </v-card>
                </div>
              </div>
            </div>
          </v-col>
        </v-row>
      </v-container>
    </div>

    <!-- 联系我们对话框 -->
    <v-dialog v-model="showContactDialog" max-width="500px">
      <v-card>
        <v-card-title class="d-flex align-center pa-6">
          <v-icon class="me-3" color="primary" size="28">mdi-phone</v-icon>
          <span class="text-h5">{{ $t('sales.contactUs') }}</span>
        </v-card-title>
        
        <v-card-text class="pa-6">
          <div class="text-center">
            <h3 class="text-h6 mb-4">{{ $t('sales.contactUs') }}</h3>
            <p class="text-body-1 text-medium-emphasis mb-6">
              {{ $t('sales.contactHint') }}
            </p>
            
            <!-- 联系方式 -->
            <div class="contact-info">
              <div class="contact-item mb-3">
                <v-icon class="me-2" color="primary">mdi-phone</v-icon>
                <span>{{ $t('sales.phone') }}: +86 400-123-4567</span>
              </div>
              <div class="contact-item mb-3">
                <v-icon class="me-2" color="primary">mdi-email</v-icon>
                <span>{{ $t('sales.email') }}: sales@example.com</span>
              </div>
              <div class="contact-item">
                <v-icon class="me-2" color="primary">mdi-wechat</v-icon>
                <span>{{ $t('sales.wechat') }}: VPS_Sales</span>
              </div>
            </div>
          </div>
        </v-card-text>
        
        <v-card-actions class="pa-6">
          <v-spacer />
          <v-btn
            variant="outlined"
            @click="showContactDialog = false"
          >
            {{ $t('common.close') }}
          </v-btn>
          <v-btn
            color="primary"
            variant="flat"
            @click="copyContact"
          >
            {{ $t('sales.copyContact') }}
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
  </PageLayout>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { getLocalizedActiveCategories } from '@/api/category'
import { serverGroupApi } from '@/api/serverGroup'
import { priceGroupApi } from '@/api/priceGroup'
import PageLayout from '@/components/PageLayout.vue'
import NotificationSnackbar from '@/components/NotificationSnackbar.vue'
import { getLocalizedText } from '@/utils/i18n'
import { useNotification } from '@/composables/useNotification'

export default {
  name: 'SalesView',
  components: {
    PageLayout,
    NotificationSnackbar
  },
  setup() {
    const { t } = useI18n()
    const { notificationState, showNotification } = useNotification()

    // 响应式数据
    const loading = ref(false)
    const categories = ref([])
    const serverGroups = ref([])
    const priceGroups = ref([])
    const showContactDialog = ref(false)
    const productsSection = ref(null)
    
    // 菜单状态管理
    const expandedCategories = ref([])
    const selectedGroup = ref(null)

    // 获取服务器类别
    const fetchCategories = async () => {
      try {
        const data = await getLocalizedActiveCategories()
        categories.value = data
      } catch (error) {
        console.error('获取服务器类别失败:', error)
        showNotification(t('sales.loadCategoriesError'), 'error')
      }
    }

    // 获取服务器分组
    const fetchServerGroups = async () => {
      try {
        const response = await serverGroupApi.getAllServerGroups()
        if (response.success) {
          serverGroups.value = response.data
        }
      } catch (error) {
        console.error('获取服务器分组失败:', error)
        showNotification(t('sales.loadGroupsError'), 'error')
      }
    }

    // 获取价格组
    const fetchPriceGroups = async () => {
      try {
        const data = await priceGroupApi.getActivePriceGroups()
        priceGroups.value = data
      } catch (error) {
        console.error('获取价格组失败:', error)
        showNotification(t('sales.loadPriceGroupsError'), 'error')
      }
    }

    // 根据类别ID获取服务器分组
    const getGroupsByCategory = (categoryId) => {
      return serverGroups.value.filter(group => group.categoryId === categoryId)
    }

    // 获取类别下的分组数量
    const getServerGroupCount = (categoryId) => {
      return getGroupsByCategory(categoryId).length
    }
    
    // 获取分组下的价格组数量
    const getPriceGroupCount = (serverGroupId) => {
      return priceGroups.value.filter(pg => pg.serverGroupId === serverGroupId).length
    }
    
    // 获取分组下的价格组
    const getGroupPriceGroups = (serverGroupId) => {
      return priceGroups.value.filter(pg => pg.serverGroupId === serverGroupId)
    }

    // 获取类别图标
    const getCategoryIcon = (category) => {
      const name = getLocalizedText(category.name).toLowerCase()
      if (name.includes('云服务器') || name.includes('cloud')) return 'mdi-cloud'
      if (name.includes('独立服务器') || name.includes('dedicated')) return 'mdi-server'
      if (name.includes('虚拟主机') || name.includes('hosting')) return 'mdi-web'
      return 'mdi-folder'
    }

    // 检查分组是否有产品
    const hasProducts = (serverGroupId) => {
      return priceGroups.value.some(pg => pg.serverGroupId === serverGroupId)
    }

    // 获取可用产品数量
    const getAvailableProductsCount = () => {
      const groupsWithProducts = serverGroups.value.filter(group => hasProducts(group.id))
      return groupsWithProducts.length
    }

    // 滚动到产品区域
    const scrollToProducts = () => {
      if (productsSection.value) {
        productsSection.value.scrollIntoView({ behavior: 'smooth' })
      }
    }

    // 切换类别展开状态
    const toggleCategory = (categoryId) => {
      const index = expandedCategories.value.indexOf(categoryId)
      if (index > -1) {
        expandedCategories.value.splice(index, 1)
      } else {
        expandedCategories.value.push(categoryId)
      }
    }
    
    // 选择服务器分组
    const selectServerGroup = (group) => {
      selectedGroup.value = group
    }
    
    // 查看价格组详情
    const viewPriceGroup = (priceGroup) => {
      console.log('查看价格组详情:', priceGroup)
      // 这里可以添加跳转到价格组详情页面的逻辑
    }

    // 联系我们
    const contactUs = () => {
      showContactDialog.value = true
    }

    // 复制联系方式
    const copyContact = () => {
      const contactInfo = `电话: +86 400-123-4567\n邮箱: sales@example.com\n微信: VPS_Sales`
      navigator.clipboard.writeText(contactInfo).then(() => {
        showNotification(t('sales.contactCopied'), 'success')
        showContactDialog.value = false
      }).catch(() => {
        showNotification(t('sales.copyFailed'), 'error')
      })
    }

    // 组件挂载时获取数据
    onMounted(async () => {
      loading.value = true
      try {
        await Promise.all([
          fetchCategories(),
          fetchServerGroups(),
          fetchPriceGroups()
        ])
      } finally {
        loading.value = false
      }
    })

    return {
      loading,
      categories,
      serverGroups,
      priceGroups,
      showContactDialog,
      productsSection,
      expandedCategories,
      selectedGroup,
      getGroupsByCategory,
      getServerGroupCount,
      getPriceGroupCount,
      getGroupPriceGroups,
      getCategoryIcon,
      hasProducts,
      getAvailableProductsCount,
      scrollToProducts,
      toggleCategory,
      selectServerGroup,
      viewPriceGroup,
      contactUs,
      copyContact,
      getLocalizedText,
      notificationState,
      showNotification
    }
  }
}
</script>

<style scoped>
/* 现代化销售页面样式 */
.sales-page {
  background: rgba(var(--v-theme-background));
  min-height: 100vh;
}

/* 左右分栏布局 */
.sidebar-col {
  border-right: 1px solid rgba(var(--v-theme-on-surface), 0.1);
}

.content-col {
  background: rgba(var(--v-theme-surface));
  padding: 0;
}

/* 侧边栏样式 */
.sidebar-card {
  border-radius: 0;
  background: rgba(var(--v-theme-surface));
  min-height: calc(100vh - 64px);
  box-shadow: none !important;
}

.sidebar-card .v-card-title {
  background: rgb(var(--v-theme-primary)) !important;
  color: white !important;
  border-radius: 0;
}

/* 类别列表样式 */
.category-list {
  background: transparent;
}

.category-item {
  border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
}

.category-item:hover {
  background: rgba(var(--v-theme-primary), 0.05);
}

.category-expanded {
  background: rgba(var(--v-theme-primary), 0.08);
}

.group-item {
  border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.03);
  transition: all 0.3s ease;
  cursor: pointer;
  background: rgba(var(--v-theme-surface), 0.5);
}

.group-item:hover {
  background: rgba(var(--v-theme-primary), 0.08);
}

.group-selected {
  background: rgba(var(--v-theme-primary), 0.12) !important;
  border-left: 3px solid rgb(var(--v-theme-primary));
}

/* 内容区域样式 */
.content-area {
  background: rgba(var(--v-theme-surface));
  padding: 24px;
}

/* 欢迎页面样式 */
.welcome-section {
  padding: 4rem 2rem;
}

.stats-mini-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  min-width: 120px;
}

.stats-mini-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(var(--v-theme-primary), 0.15);
}

/* 分组头部卡片 */
.group-header-card {
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.02) 0%, rgba(var(--v-theme-surface)) 100%);
  border: 1px solid rgba(var(--v-theme-primary), 0.1);
}

/* 价格组卡片样式 */
.price-group-card {
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  background: rgba(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-theme-on-surface), 0.1);
}

.price-group-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(var(--v-theme-primary), 0.15);
  border-color: rgba(var(--v-theme-primary), 0.3);
}

/* 无产品卡片 */
.no-products-card {
  border-radius: 16px;
  background: rgba(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-theme-on-surface), 0.1);
}


/* 价格信息样式 */
.price-info {
  background: rgba(var(--v-theme-primary), 0.05);
  border-radius: 8px;
  padding: 12px;
}

/* 联系信息样式 */
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


/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar-col {
    border-right: none;
    border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.1);
  }
  
  .sidebar-card {
    min-height: auto;
    border-radius: 8px;
    margin-bottom: 16px;
  }
  
  .content-area {
    min-height: auto;
    padding: 16px !important;
  }
  
  .welcome-section {
    padding: 2rem 1rem;
  }
  
  .stats-mini-card {
    min-width: 100px;
  }
  
  .group-header-card .pa-6 {
    padding: 16px !important;
  }
  
  .price-group-card .pa-6 {
    padding: 16px !important;
  }
}

@media (max-width: 600px) {
  .welcome-section .text-h3 {
    font-size: 2rem !important;
  }
  
  .stats-mini-card {
    min-width: 80px;
  }
  
  .stats-mini-card .text-h4 {
    font-size: 1.5rem !important;
  }
}


/* 动画效果 */
.v-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.v-btn {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.v-btn:hover {
  transform: translateY(-2px);
}

.v-chip {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 展开动画 */
.v-expand-transition-enter-active,
.v-expand-transition-leave-active {
  transition: all 0.3s ease;
}

/* 滚动条样式 */
.category-list {
  max-height: calc(100vh - 120px);
  overflow-y: auto;
}

.category-list::-webkit-scrollbar {
  width: 4px;
}

.category-list::-webkit-scrollbar-track {
  background: rgba(var(--v-theme-on-surface), 0.05);
}

.category-list::-webkit-scrollbar-thumb {
  background: rgba(var(--v-theme-primary), 0.3);
  border-radius: 2px;
}

.category-list::-webkit-scrollbar-thumb:hover {
  background: rgba(var(--v-theme-primary), 0.5);
}
</style>