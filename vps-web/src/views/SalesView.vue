<template>
  <PageLayout :title="$t('nav.sales')">
    <div class="sales-page">
      <!-- 顶部标题区域 -->
      <div class="hero-section">
        <v-container>
          <div class="text-center py-8">
            <v-icon size="80" color="primary" class="mb-4">mdi-storefront</v-icon>
            <h1 class="text-h3 font-weight-bold mb-2">{{ $t('nav.sales') }}</h1>
            <p class="text-h6 text-medium-emphasis">{{ $t('sales.subtitle') }}</p>
          </div>
        </v-container>
      </div>

      <v-container fluid class="pa-0">
        <v-row no-gutters>
          <!-- 左侧菜单 -->
          <v-col cols="12" md="3" lg="3">
            <v-card class="sales-nav-card" variant="outlined">
              <v-card-title class="text-subtitle-1 py-3">
                <v-icon class="me-2" size="small">mdi-format-list-bulleted</v-icon>
                {{ $t('sales.productCategories') }}
              </v-card-title>
              <v-divider />
              <v-list nav class="pa-2">
                <!-- 一级菜单：服务器类别 -->
                <template v-for="category in categories" :key="category.id">
                  <v-list-group
                    :value="category.id"
                  >
                    <template #activator="{ props }">
                      <v-list-item
                        v-bind="props"
                        :title="getLocalizedText(category.name)"
                        :subtitle="getServerGroupCount(category.id) + ' ' + $t('sales.groups')"
                        :prepend-icon="getCategoryIcon(category)"
                        rounded="lg"
                        class="mb-1"
                      />
                    </template>

                    <!-- 二级菜单：服务器分组 -->
                    <v-list-item
                      v-for="group in getGroupsByCategory(category.id)"
                      :key="group.id"
                      :title="getLocalizedText(group.name)"
                      :value="group.id"
                      :active="selectedGroupId === group.id"
                      @click="selectGroup(group)"
                      class="ms-4 mb-1"
                      rounded="lg"
                    >
                      <template #prepend>
                        <v-icon size="small">mdi-server-network</v-icon>
                      </template>
                      
                      <template #append v-if="hasProducts(group.id)">
                        <v-chip size="x-small" color="success" variant="flat">
                          {{ $t('sales.hasProducts') }}
                        </v-chip>
                      </template>
                    </v-list-item>
                  </v-list-group>
                </template>
              </v-list>
            </v-card>
          </v-col>

          <!-- 右侧内容区域 -->
          <v-col cols="12" md="9" lg="9">
            <div class="sales-content ps-4">
              <!-- 未选择分组时的欢迎页面 -->
              <div v-if="!selectedGroup" class="welcome-section">
                <v-card class="welcome-card" elevation="0">
                  <v-card-text class="text-center py-12">
                    <v-icon size="100" color="primary" class="mb-6">
                      mdi-package-variant
                    </v-icon>
                    <h2 class="text-h4 font-weight-bold mb-4">
                      {{ $t('sales.welcome') }}
                    </h2>
                    <p class="text-body-1 text-medium-emphasis mb-8 mx-auto" style="max-width: 500px;">
                      {{ $t('sales.selectGroupHint') }}
                    </p>
                    
                    <!-- 快速统计 -->
                    <v-row justify="center" class="mb-6">
                      <v-col cols="12" sm="4" md="3">
                        <v-card class="stats-card" variant="flat" color="primary">
                          <v-card-text class="text-center pa-4">
                            <div class="text-h3 font-weight-bold text-white">{{ categories.length }}</div>
                            <div class="text-body-2 text-white opacity-90">{{ $t('sales.totalCategories') }}</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                      <v-col cols="12" sm="4" md="3">
                        <v-card class="stats-card" variant="flat" color="success">
                          <v-card-text class="text-center pa-4">
                            <div class="text-h3 font-weight-bold text-white">{{ serverGroups.length }}</div>
                            <div class="text-body-2 text-white opacity-90">{{ $t('sales.totalGroups') }}</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                      <v-col cols="12" sm="4" md="3">
                        <v-card class="stats-card" variant="flat" color="info">
                          <v-card-text class="text-center pa-4">
                            <div class="text-h3 font-weight-bold text-white">{{ getAvailableProductsCount() }}</div>
                            <div class="text-body-2 text-white opacity-90">{{ $t('sales.availableProducts') }}</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>
              </div>

              <!-- 选中分组后的产品展示页面 -->
              <div v-else class="product-section">
                <!-- 产品信息头部 -->
                <v-card class="product-header-card mb-6" elevation="2">
                  <v-card-text class="pa-6">
                    <div class="d-flex align-center mb-4">
                      <v-avatar size="48" color="primary" class="me-4">
                        <v-icon size="28" color="white">mdi-package-variant</v-icon>
                      </v-avatar>
                      <div class="flex-grow-1">
                        <h2 class="text-h5 font-weight-bold mb-1">{{ getLocalizedText(selectedGroup.name) }}</h2>
                        <div class="d-flex align-center">
                          <v-chip
                            :color="hasProducts(selectedGroup.id) ? 'success' : 'warning'"
                            variant="flat"
                            size="small"
                            class="me-2"
                          >
                            {{ hasProducts(selectedGroup.id) ? $t('sales.productsAvailable') : $t('sales.noProducts') }}
                          </v-chip>
                        </div>
                      </div>
                    </div>
                    
                    <v-divider class="mb-4" />
                    
                    <div v-if="selectedGroup.description" class="mb-4">
                      <p class="text-body-1">{{ getLocalizedText(selectedGroup.description) }}</p>
                    </div>

                    <!-- 地理位置信息 -->
                    <div v-if="selectedGroup.region || selectedGroup.country || selectedGroup.city" class="location-info">
                      <div class="d-flex align-center flex-wrap ga-2">
                        <v-icon size="20" class="me-2" color="primary">mdi-map-marker</v-icon>
                        <v-chip v-if="selectedGroup.region" size="small" variant="outlined" color="primary">
                          {{ getLocalizedText(selectedGroup.region) }}
                        </v-chip>
                        <v-chip v-if="selectedGroup.country" size="small" variant="outlined" color="primary">
                          {{ getLocalizedText(selectedGroup.country) }}
                        </v-chip>
                        <v-chip v-if="selectedGroup.city" size="small" variant="outlined" color="primary">
                          {{ getLocalizedText(selectedGroup.city) }}
                        </v-chip>
                      </div>
                    </div>
                  </v-card-text>
                </v-card>

                <!-- 产品信息展示 -->
                <v-card v-if="hasProducts(selectedGroup.id)" class="product-info-card mb-6" elevation="1">
                  <v-card-title class="d-flex align-center pa-6">
                    <v-icon class="me-3" color="success" size="28">mdi-package-variant-closed</v-icon>
                    <span class="text-h5">{{ $t('sales.productInfo') }}</span>
                  </v-card-title>
                  
                  <v-card-text class="pa-6">
                    <!-- 产品特性展示 -->
                    <v-row class="mb-6">
                      <v-col cols="12" md="6">
                        <div class="feature-item">
                          <v-icon color="success" class="me-2">mdi-check-circle</v-icon>
                          <span>{{ $t('sales.highPerformance') }}</span>
                        </div>
                      </v-col>
                      <v-col cols="12" md="6">
                        <div class="feature-item">
                          <v-icon color="success" class="me-2">mdi-check-circle</v-icon>
                          <span>{{ $t('sales.reliableService') }}</span>
                        </div>
                      </v-col>
                      <v-col cols="12" md="6">
                        <div class="feature-item">
                          <v-icon color="success" class="me-2">mdi-check-circle</v-icon>
                          <span>{{ $t('sales.support247') }}</span>
                        </div>
                      </v-col>
                      <v-col cols="12" md="6">
                        <div class="feature-item">
                          <v-icon color="success" class="me-2">mdi-check-circle</v-icon>
                          <span>{{ $t('sales.flexibleConfig') }}</span>
                        </div>
                      </v-col>
                    </v-row>
                    
                    <!-- 联系按钮 -->
                    <div class="text-center">
                      <v-btn
                        size="large"
                        color="primary"
                        variant="flat"
                        prepend-icon="mdi-phone"
                        @click="contactUs"
                        class="px-8"
                      >
                        {{ $t('sales.contactUs') }}
                      </v-btn>
                    </div>
                  </v-card-text>
                </v-card>

                <!-- 无产品时的提示 -->
                <v-card v-else class="no-product-card" elevation="1">
                  <v-card-text class="text-center py-12">
                    <v-icon size="80" color="grey-lighten-1" class="mb-4">
                      mdi-package-variant-remove
                    </v-icon>
                    <h3 class="text-h5 mb-3 text-medium-emphasis">{{ $t('sales.noProducts') }}</h3>
                    <p class="text-body-1 text-medium-emphasis">
                      {{ $t('sales.noProductsHint') }}
                    </p>
                  </v-card-text>
                </v-card>
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
            <h3 class="text-h6 mb-4">{{ getLocalizedText(selectedGroup?.name || '') }}</h3>
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
import { ref, reactive, onMounted, computed } from 'vue'
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
    const selectedGroupId = ref(null)
    const selectedGroup = ref(null)
    const selectedPriceGroup = ref(null)
    const showContactDialog = ref(false)

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
        // 使用激活的价格组API
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

    // 获取类别图标
    const getCategoryIcon = (category) => {
      // 可以根据类别名称返回不同的图标
      const name = getLocalizedText(category.name).toLowerCase()
      if (name.includes('云服务器') || name.includes('cloud')) return 'mdi-cloud'
      if (name.includes('独立服务器') || name.includes('dedicated')) return 'mdi-server'
      if (name.includes('虚拟主机') || name.includes('hosting')) return 'mdi-web'
      return 'mdi-folder'
    }

    // 检查分组是否有产品（基于是否有价格组配置）
    const hasProducts = (serverGroupId) => {
      return priceGroups.value.some(pg => pg.serverGroupId === serverGroupId)
    }

    // 获取可用产品数量
    const getAvailableProductsCount = () => {
      const groupsWithProducts = serverGroups.value.filter(group => hasProducts(group.id))
      return groupsWithProducts.length
    }

    // 选择分组
    const selectGroup = (group) => {
      selectedGroupId.value = group.id
      selectedGroup.value = group
      selectedPriceGroup.value = priceGroups.value.find(pg => pg.serverGroupId === group.id)
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
      selectedGroupId,
      selectedGroup,
      selectedPriceGroup,
      showContactDialog,
      getGroupsByCategory,
      getServerGroupCount,
      getCategoryIcon,
      hasProducts,
      getAvailableProductsCount,
      selectGroup,
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
.sales-page {
background-color: transparent;
}

/* 顶部标题区域 */
.hero-section {
background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.1) 0%, rgba(var(--v-theme-secondary), 0.05) 100%);
border-radius: 16px;
margin-bottom: 32px;
}

/* 左侧导航卡片 */
.sales-nav-card {
position: sticky;
top: 24px;
max-height: calc(100vh - 200px);
overflow-y: auto;
border-radius: 16px;
box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.sales-content {
min-height: calc(100vh - 200px);
}

/* 欢迎页面样式 */
.welcome-card {
border-radius: 20px;
background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.05) 0%, rgba(var(--v-theme-surface)) 100%);
border: 1px solid rgba(var(--v-theme-primary), 0.1);
}

.stats-card {
border-radius: 16px;
transition: all 0.3s ease;
box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stats-card:hover {
transform: translateY(-4px);
box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

/* 产品展示区域 */
.product-section {
max-width: 1000px;
}

.product-header-card {
border-radius: 20px;
background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.02) 0%, rgba(var(--v-theme-surface)) 100%);
border: 1px solid rgba(var(--v-theme-primary), 0.1);
}

.product-info-card {
border-radius: 16px;
border: 1px solid rgba(var(--v-theme-success), 0.2);
background: linear-gradient(135deg, rgba(var(--v-theme-success), 0.02) 0%, rgba(var(--v-theme-surface)) 100%);
}

.no-product-card {
border-radius: 16px;
background: linear-gradient(135deg, rgba(var(--v-theme-surface)) 0%, rgba(var(--v-theme-surface), 0.5) 100%);
border: 1px solid rgba(var(--v-theme-on-surface), 0.1);
}

/* 产品特性列表 */
.feature-item {
display: flex;
align-items: center;
padding: 12px 0;
font-size: 1rem;
font-weight: 500;
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

/* 位置信息 */
.location-info {
background: rgba(var(--v-theme-primary), 0.05);
border-radius: 12px;
padding: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
.hero-section {
margin-bottom: 16px;
}
  
.sales-nav-card {
position: static;
max-height: none;
margin-bottom: 16px;
}
  
.sales-content {
padding-left: 0 !important;
}
  
.product-header-card .pa-6 {
padding: 16px !important;
}
  
.product-info-card .pa-6 {
padding: 16px !important;
}
}

/* 动画效果 */
.v-card {
transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.v-btn {
transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.v-chip {
transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
</style>