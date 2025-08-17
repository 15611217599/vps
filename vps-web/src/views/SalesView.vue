<template>
  <PageLayout :title="$t('nav.sales')">
    <div class="sales-page">
      <v-container fluid class="pa-0">
        <v-row no-gutters>
          <!-- 左侧菜单栏 -->
          <v-col cols="12" md="4" lg="3" class="sidebar-col">
            <div class="sidebar-card">
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
              <div v-if="!selectedGroup" class="welcome-section">

                <!-- 核心配置规格 -->
                <v-card class="specs-section-card mb-6" elevation="3">
                  <v-card-title class="d-flex align-center pa-6">
                    <v-icon size="28" color="primary" class="me-3">mdi-rocket-launch</v-icon>
                    <span class="text-h5 font-weight-bold">核心配置</span>
                  </v-card-title>
                  <v-card-text class="pa-6 pt-0">
                    <v-row>
                      <v-col cols="12" sm="6" md="3">
                        <v-card class="spec-item-card h-100" elevation="1">
                          <v-card-text class="pa-4 text-center">
                            <div class="spec-icon mb-3">💻</div>
                            <div class="spec-title text-subtitle-1 font-weight-bold mb-2">CPU处理器</div>
                            <div class="spec-value text-h6 text-primary font-weight-bold mb-1">高性能多核处理器</div>
                            <div class="spec-desc text-caption text-medium-emphasis">企业级CPU，稳定高效</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                      <v-col cols="12" sm="6" md="3">
                        <v-card class="spec-item-card h-100" elevation="1">
                          <v-card-text class="pa-4 text-center">
                            <div class="spec-icon mb-3">🧠</div>
                            <div class="spec-title text-subtitle-1 font-weight-bold mb-2">内存配置</div>
                            <div class="spec-value text-h6 text-primary font-weight-bold mb-1">大容量内存</div>
                            <div class="spec-desc text-caption text-medium-emphasis">根据套餐配置提供</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                      <v-col cols="12" sm="6" md="3">
                        <v-card class="spec-item-card h-100" elevation="1">
                          <v-card-text class="pa-4 text-center">
                            <div class="spec-icon mb-3">💾</div>
                            <div class="spec-title text-subtitle-1 font-weight-bold mb-2">存储空间</div>
                            <div class="spec-value text-h6 text-primary font-weight-bold mb-1">高速SSD存储</div>
                            <div class="spec-desc text-caption text-medium-emphasis">快速读写，稳定可靠</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                      <v-col cols="12" sm="6" md="3">
                        <v-card class="spec-item-card h-100" elevation="1">
                          <v-card-text class="pa-4 text-center">
                            <div class="spec-icon mb-3">🌐</div>
                            <div class="spec-title text-subtitle-1 font-weight-bold mb-2">网络带宽</div>
                            <div class="spec-value text-h6 text-primary font-weight-bold mb-1">CN2+BGP网络</div>
                            <div class="spec-desc text-caption text-medium-emphasis">优质线路，低延迟</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>

                <!-- 技术特性 -->
                <v-card class="features-section-card mb-6" elevation="3">
                  <v-card-title class="d-flex align-center pa-6">
                    <v-icon size="28" color="success" class="me-3">mdi-lightning-bolt</v-icon>
                    <span class="text-h5 font-weight-bold">技术特性</span>
                  </v-card-title>
                  <v-card-text class="pa-6 pt-0">
                    <v-row>
                      <v-col cols="12" sm="6" md="4" v-for="feature in techFeatures" :key="feature.title">
                        <v-card class="feature-item-card h-100" elevation="1" hover>
                          <v-card-text class="pa-4 text-center">
                            <div class="feature-icon mb-3">{{ feature.icon }}</div>
                            <div class="feature-title text-subtitle-1 font-weight-bold mb-2">{{ feature.title }}</div>
                            <div class="feature-desc text-body-2 text-medium-emphasis">{{ feature.desc }}</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>

                <!-- 操作系统支持 -->
                <v-card class="os-section-card mb-6" elevation="3">
                  <v-card-title class="d-flex align-center pa-6">
                    <v-icon size="28" color="info" class="me-3">mdi-monitor</v-icon>
                    <span class="text-h5 font-weight-bold">操作系统支持</span>
                  </v-card-title>
                  <v-card-text class="pa-6 pt-0">
                    <v-row>
                      <v-col cols="12" sm="6" md="4" v-for="os in operatingSystems" :key="os.name">
                        <v-card class="os-item-card" elevation="1" hover>
                          <v-card-text class="pa-4 d-flex align-center">
                            <div class="os-logo me-3">{{ os.logo }}</div>
                            <div class="os-name text-body-1 font-weight-medium">{{ os.name }}</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>

                <!-- 适用场景 -->
                <v-card class="scenarios-section-card mb-6" elevation="3">
                  <v-card-title class="d-flex align-center pa-6">
                    <v-icon size="28" color="purple" class="me-3">mdi-target</v-icon>
                    <span class="text-h5 font-weight-bold">适用场景</span>
                  </v-card-title>
                  <v-card-text class="pa-6 pt-0">
                    <v-row>
                      <v-col cols="12" md="6" v-for="scenario in usageScenarios" :key="scenario.title">
                        <v-card class="scenario-item-card h-100" elevation="1" hover>
                          <v-card-text class="pa-4">
                            <div class="d-flex align-center mb-3">
                              <div class="scenario-icon me-3">{{ scenario.icon }}</div>
                              <div class="scenario-title text-h6 font-weight-bold">{{ scenario.title }}</div>
                            </div>
                            <div class="scenario-desc text-body-2 text-medium-emphasis">{{ scenario.desc }}</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>

                <!-- 服务保障 -->
                <v-card class="guarantee-section-card mb-6" elevation="3">
                  <v-card-title class="d-flex align-center pa-6">
                    <v-icon size="28" color="success" class="me-3">mdi-shield-check</v-icon>
                    <span class="text-h5 font-weight-bold">服务保障</span>
                  </v-card-title>
                  <v-card-text class="pa-6 pt-0">
                    <v-row>
                      <v-col cols="12" sm="6" md="3" v-for="guarantee in serviceGuarantees" :key="guarantee.title">
                        <v-card class="guarantee-item-card h-100" elevation="1" hover>
                          <v-card-text class="pa-4 text-center">
                            <div class="guarantee-icon mb-3">{{ guarantee.icon }}</div>
                            <div class="guarantee-title text-subtitle-1 font-weight-bold mb-2">{{ guarantee.title }}</div>
                            <div class="guarantee-desc text-caption text-medium-emphasis">{{ guarantee.desc }}</div>
                          </v-card-text>
                        </v-card>
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>

                
                <!-- 联系我们按钮 -->
                <div class="text-center">
                  <v-btn
                    size="x-large"
                    color="primary"
                    variant="flat"
                    prepend-icon="mdi-phone"
                    @click="contactUs"
                    class="px-12 py-4"
                    rounded="xl"
                    elevation="4"
                  >
                    <span class="text-h6">{{ $t('sales.contactUs') }}</span>
                  </v-btn>
                </div>
              </div>
              
              <!-- 选中分组的价格组展示 -->
              <ServerDetailsCard
                v-else
                :selected-group="selectedGroup"
                :price-groups="priceGroups"
                @contact-purchase="contactForPurchase"
                @contact-us="contactUs"
              />
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
                <v-icon class="me-2" color="primary">mdi-qqchat</v-icon>
                <span>{{ $t('sales.qqGroup') }}: 736757426</span>
              </div>
              <div class="contact-item mb-3">
                <v-icon class="me-2" color="primary">mdi-email</v-icon>
                <span>{{ $t('sales.email') }}: rabbitvps@163.com</span>
              </div>
              <div class="contact-item">
                <v-icon class="me-2" color="primary">mdi-wechat</v-icon>
                <span>{{ $t('sales.wechat') }}: rabbitvps</span>
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
import ServerDetailsCard from '@/components/sales/ServerDetailsCard.vue'
import NotificationSnackbar from '@/components/NotificationSnackbar.vue'
import { getLocalizedText } from '@/utils/i18n'
import { useNotification } from '@/composables/useNotification'

export default {
  name: 'SalesView',
  components: {
    PageLayout,
    ServerDetailsCard,
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
    

    // 联系购买
    const contactForPurchase = (priceGroup) => {
      console.log('联系购买:', priceGroup)
      showContactDialog.value = true
    }

    // 联系我们
    const contactUs = () => {
      showContactDialog.value = true
    }

    // 复制联系方式
    const copyContact = () => {
      const contactInfo = `QQ群: 736757426\n邮箱: rabbitvps@163.com\n微信: rabbitvps`
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

    // 技术特性数据
    const techFeatures = ref([
      { icon: '🛡️', title: 'DDoS防护', desc: '免费提供10Gbps DDoS攻击防护' },
      { icon: '🔄', title: '自动备份', desc: '每日自动备份，保留7天历史数据' },
      { icon: '📊', title: '实时监控', desc: '24/7系统监控，异常自动告警' },
      { icon: '🔧', title: '一键部署', desc: '支持WordPress、Docker等快速部署' },
      { icon: '🌍', title: '全球CDN', desc: '免费提供全球CDN加速服务' },
      { icon: '🔐', title: 'SSL证书', desc: '免费提供Let\'s Encrypt SSL证书' }
    ])

    // 操作系统数据
    const operatingSystems = ref([
      { logo: '🐧', name: 'Ubuntu' },
      { logo: '🎩', name: 'CentOS' },
      { logo: '🔴', name: 'Red Hat Enterprise Linux' },
      { logo: '🟢', name: 'openSUSE' },
      { logo: '🪟', name: 'Windows Server' },
      { logo: '🐋', name: 'Docker预装镜像' }
    ])

    // 使用场景数据
    const usageScenarios = ref([
      { icon: '🌐', title: 'Web应用托管', desc: '适合中小型网站、博客、电商平台等Web应用部署' },
      { icon: '⚙️', title: '开发测试环境', desc: '为开发团队提供稳定的开发、测试和预生产环境' },
      { icon: '📊', title: '数据库服务', desc: 'MySQL、PostgreSQL、MongoDB等数据库服务部署' },
      { icon: '🤖', title: 'AI/ML工作负载', desc: '机器学习模型训练、推理服务等AI应用场景' }
    ])

    // 服务保障数据
    const serviceGuarantees = ref([
      { icon: '⏱️', title: '99.9% SLA保证', desc: '服务可用性保障，不达标按比例赔付' },
      { icon: '🎧', title: '7×24技术支持', desc: '专业技术团队全天候在线支持' },
      { icon: '💰', title: '7天无理由退款', desc: '不满意可在7天内申请全额退款' },
      { icon: '🚀', title: '5分钟快速交付', desc: '支付完成后5分钟内自动开通服务' }
    ])

    return {
      loading,
      categories,
      serverGroups,
      priceGroups,
      showContactDialog,
      productsSection,
      expandedCategories,
      selectedGroup,
      techFeatures,
      operatingSystems,
      usageScenarios,
      serviceGuarantees,
      getGroupsByCategory,
      getServerGroupCount,
      getPriceGroupCount,
      getCategoryIcon,
      hasProducts,
      getAvailableProductsCount,
      scrollToProducts,
      toggleCategory,
      selectServerGroup,
      contactForPurchase,
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
  background: transparent;
}

/* 左右分栏布局 */
.sidebar-col {
  border-right: 1px solid rgba(var(--v-theme-on-surface), 0.1);
}

.content-col {
  padding: 0;
}

/* 侧边栏样式 */
.sidebar-card {
  border-radius: 0;
  background: transparent;
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
  padding: 24px;
}

/* 欢迎页面样式 */
.welcome-section {
  padding: 2rem;
}

/* 产品标题卡片 */
.product-header-card {
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.1) 0%, rgba(var(--v-theme-secondary), 0.1) 100%);
  border: 2px solid rgba(var(--v-theme-primary), 0.15);
  position: relative;
  overflow: hidden;
}

.product-header-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(90deg, rgb(var(--v-theme-primary)) 0%, rgb(var(--v-theme-secondary)) 100%);
}

.product-badges {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
}

/* 规格展示卡片 */
.specs-section-card {
  border-radius: 16px;
  background: rgba(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-theme-primary), 0.1);
}

.spec-item-card {
  border-radius: 12px;
  background: rgba(var(--v-theme-background));
  border: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  transition: all 0.3s ease;
}

.spec-item-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(var(--v-theme-primary), 0.15);
  border-color: rgba(var(--v-theme-primary), 0.3);
}

.spec-icon {
  font-size: 32px;
  line-height: 1;
}

.spec-title {
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.spec-value {
  color: rgb(var(--v-theme-primary));
}

.spec-desc {
  color: rgba(var(--v-theme-on-surface), 0.6);
}

/* 技术特性卡片 */
.features-section-card {
  border-radius: 16px;
  background: rgba(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-theme-success), 0.1);
}

.feature-item-card {
  border-radius: 12px;
  background: rgba(var(--v-theme-background));
  border: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  transition: all 0.3s ease;
}

.feature-item-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(var(--v-theme-success), 0.15);
  border-color: rgba(var(--v-theme-success), 0.3);
}

.feature-icon {
  font-size: 28px;
  line-height: 1;
}

.feature-title {
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.feature-desc {
  color: rgba(var(--v-theme-on-surface), 0.6);
}

/* 操作系统卡片 */
.os-section-card {
  border-radius: 16px;
  background: rgba(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-theme-info), 0.1);
}

.os-item-card {
  border-radius: 12px;
  background: rgba(var(--v-theme-background));
  border: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  transition: all 0.3s ease;
}

.os-item-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(var(--v-theme-info), 0.15);
  border-color: rgba(var(--v-theme-info), 0.3);
}

.os-logo {
  font-size: 24px;
  line-height: 1;
}

.os-name {
  color: rgba(var(--v-theme-on-surface), 0.87);
}

/* 使用场景卡片 */
.scenarios-section-card {
  border-radius: 16px;
  background: rgba(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-theme-purple), 0.1);
}

.scenario-item-card {
  border-radius: 12px;
  background: rgba(var(--v-theme-background));
  border: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  transition: all 0.3s ease;
}

.scenario-item-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(var(--v-theme-purple), 0.15);
  border-color: rgba(var(--v-theme-purple), 0.3);
}

.scenario-icon {
  font-size: 28px;
  line-height: 1;
}

.scenario-title {
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.scenario-desc {
  color: rgba(var(--v-theme-on-surface), 0.6);
}

/* 服务保障卡片 */
.guarantee-section-card {
  border-radius: 16px;
  background: rgba(var(--v-theme-surface));
  border: 1px solid rgba(var(--v-theme-success), 0.1);
}

.guarantee-item-card {
  border-radius: 12px;
  background: rgba(var(--v-theme-background));
  border: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  transition: all 0.3s ease;
}

.guarantee-item-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(var(--v-theme-success), 0.15);
  border-color: rgba(var(--v-theme-success), 0.3);
}

.guarantee-icon {
  font-size: 28px;
  line-height: 1;
}

.guarantee-title {
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.guarantee-desc {
  color: rgba(var(--v-theme-on-surface), 0.6);
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