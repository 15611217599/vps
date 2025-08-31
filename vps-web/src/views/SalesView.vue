<template>
  <PageLayout>
    <div class="sales-page">
      <v-container fluid class="pa-0">
        <v-row no-gutters>
          <!-- 左侧菜单栏 -->
          <v-col cols="12" md="3" lg="2" class="sidebar-col">
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
                        {{ category.name }}
                      </v-list-item-title>
                      
                      <template #append>
                        <v-icon>
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
                            {{ group.name }}
                          </v-list-item-title>
                          
                        </v-list-item>
                      </div>
                    </v-expand-transition>
                  </template>
                </v-list>
              </div>
            </div>
          </v-col>
          
          <!-- 右侧内容区域 -->
          <v-col cols="12" md="9" lg="10" class="content-col">
            <div class="content-area">
              <!-- 默认欢迎页面 -->
              <div v-if="!selectedGroup" class="welcome-section">
                <div class="text-center mb-8">
                  <h1 class="text-h3 font-weight-light mb-4">欢迎来到RabbitVPS服务</h1>
                  <p class="text-h6 text-medium-emphasis mb-6">高性能云服务器，助力您的业务发展</p>
                </div>

                <!-- 核心配置 -->
                <div class="mb-8">
                  <h2 class="text-h5 font-weight-medium mb-4 d-flex align-center">
                    <v-icon size="20" color="primary" class="me-2">mdi-rocket-launch</v-icon>
                    核心配置
                  </h2>
                  <div class="simple-grid">
                    <div class="simple-item">
                      <span class="item-icon">💻</span>
                      <div>
                        <div class="item-title">CPU处理器</div>
                        <div class="item-desc">高性能多核处理器，提供强劲计算能力</div>
                      </div>
                    </div>
                    <div class="simple-item">
                      <span class="item-icon">🧠</span>
                      <div>
                        <div class="item-title">内存配置</div>
                        <div class="item-desc">大容量内存，确保应用流畅运行</div>
                      </div>
                    </div>
                    <div class="simple-item">
                      <span class="item-icon">💾</span>
                      <div>
                        <div class="item-title">存储空间</div>
                        <div class="item-desc">高速SSD存储，快速读写性能</div>
                      </div>
                    </div>
                    <div class="simple-item">
                      <span class="item-icon">🌐</span>
                      <div>
                        <div class="item-title">网络带宽</div>
                        <div class="item-desc">高速网络连接，保障数据传输</div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 技术特性 -->
                <div class="mb-8">
                  <h2 class="text-h5 font-weight-medium mb-4 d-flex align-center">
                    <v-icon size="20" color="success" class="me-2">mdi-lightning-bolt</v-icon>
                    技术特性
                  </h2>
                  <div class="simple-grid">
                    <div class="simple-item" v-for="feature in techFeatures" :key="feature.title">
                      <span class="item-icon">{{ feature.icon }}</span>
                      <div>
                        <div class="item-title">{{ feature.title }}</div>
                        <div class="item-desc">{{ feature.desc }}</div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 适用场景 -->
                <div class="mb-8">
                  <h2 class="text-h5 font-weight-medium mb-4 d-flex align-center">
                    <v-icon size="20" color="purple" class="me-2">mdi-target</v-icon>
                    适用场景
                  </h2>
                  <div class="simple-list">
                    <div class="simple-item" v-for="scenario in usageScenarios" :key="scenario.title">
                      <span class="item-icon">{{ scenario.icon }}</span>
                      <div>
                        <div class="item-title">{{ scenario.title }}</div>
                        <div class="item-desc">{{ scenario.desc }}</div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 服务保障 -->
                <div class="mb-8">
                  <h2 class="text-h5 font-weight-medium mb-4 d-flex align-center">
                    <v-icon size="20" color="success" class="me-2">mdi-shield-check</v-icon>
                    服务保障
                  </h2>
                  <div class="simple-grid">
                    <div class="simple-item" v-for="guarantee in serviceGuarantees" :key="guarantee.title">
                      <span class="item-icon">{{ guarantee.icon }}</span>
                      <div>
                        <div class="item-title">{{ guarantee.title }}</div>
                        <div class="item-desc">{{ guarantee.desc }}</div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 联系我们按钮 -->
                <div class="text-center">
                  <v-btn
                    size="large"
                    color="primary"
                    variant="flat"
                    prepend-icon="mdi-phone"
                    @click="contactUs"
                    class="px-8"
                  >
                    联系我们
                  </v-btn>
                </div>
              </div>
              
              <!-- 选中分组的价格组展示 -->
              <ServerDetailsCard
                v-else
                :selected-group="selectedGroup"
                :price-groups="priceGroups"
                :server-specs="serverSpecs"
                @contact-purchase="contactForPurchase"
                @contact-us="contactUs"
              />
            </div>
          </v-col>
        </v-row>
      </v-container>
    </div>

    <!-- 联系我们弹窗 -->
    <ContactDialog v-model="showContactDialog" />
  </PageLayout>
</template>

<script>
import { ref, onMounted, watch, computed } from 'vue'

import { getLocalizedActiveCategories } from '@/api/category'
import { serverGroupApi } from '@/api/serverGroup'
import { priceGroupApi } from '@/api/priceGroup'
import { getServersByGroupId } from '@/api/server'
import PageLayout from '@/components/PageLayout.vue'
import ServerDetailsCard from '@/components/sales/ServerDetailsCard.vue'
import ContactDialog from '@/components/ContactDialog.vue'

export default {
  name: 'SalesView',
  components: {
    PageLayout,
    ServerDetailsCard,
    ContactDialog
  },
  setup() {
    // 移除国际化

    // 响应式数据
    const loading = ref(false)
    const categories = ref([])
    const serverGroups = ref([])
    const priceGroups = ref([])
    const showContactDialog = ref(false)
    const productsSection = ref(null)
    const serverSpecs = ref(null)
    
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
        showNotification('获取服务器类别失败', 'error')
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
        showNotification('获取服务器分组失败', 'error')
      }
    }

    // 获取价格组
    const fetchPriceGroups = async () => {
      try {
        const data = await priceGroupApi.getActivePriceGroups()
        priceGroups.value = data
      } catch (error) {
        console.error('获取价格组失败:', error)
        showNotification('获取价格组失败', 'error')
      }
    }

    // 根据类别ID获取服务器分组
    const getGroupsByCategory = (categoryId) => {
      return serverGroups.value.filter(group => group.categoryId === categoryId)
    }

    

    // 获取类别图标
    const getCategoryIcon = (category) => {
      const name = category.name.toLowerCase()
      if (name.includes('云服务器') || name.includes('cloud')) return 'mdi-cloud'
      if (name.includes('独立服务器') || name.includes('dedicated')) return 'mdi-server'
      if (name.includes('虚拟主机') || name.includes('hosting')) return 'mdi-web'
      return 'mdi-folder'
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
    const selectServerGroup = async (group) => {
      selectedGroup.value = group
      // 获取该分组下的服务器规格数据
      await fetchServerSpecs(group.id)
    }
    
    // 获取服务器规格数据
    const fetchServerSpecs = async (groupId) => {
      try {
        const response = await getServersByGroupId(groupId)
        if (response.data.success && response.data.data.length > 0) {
          // 使用第一个服务器的规格作为模板
          const server = response.data.data[0]
          
          serverSpecs.value = {
            cpu_cores: parseInt(server.cpuCores) || 2,
            memory: parseInt(server.memory) || 4,
            disk_space: parseInt(server.diskSpace) || 40,
            network_speed: parseInt(server.networkSpeed) || 100,
            ip_count: 1
          }
        } else {
          // 如果没有服务器数据，使用默认规格
          serverSpecs.value = {
            cpu_cores: 2,
            memory: 4,
            disk_space: 40,
            network_speed: 100,
            ip_count: 1
          }
        }
      } catch (error) {
        console.error('获取服务器规格失败:', error)
        // 使用默认规格
        serverSpecs.value = {
          cpu_cores: 2,
          memory: 4,
          disk_space: 40,
          network_speed: 100,
          ip_count: 1
        }
      }
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



    // 组件挂载时获取数据
    onMounted(async () => {
      loading.value = true
      try {
        await Promise.all([
          fetchCategories(),
          fetchServerGroups(),
          fetchPriceGroups()
        ])
        // 默认展开第一个分类
        if (categories.value.length > 0) {
          expandedCategories.value.push(categories.value[0].id)
        }
      } finally {
        loading.value = false
      }
    })


    // 技术特性数据
    const techFeatures = ref([
      { icon: '🛡️', title: 'DDoS防护', desc: '专业级DDoS防护，保障服务稳定运行' },
      { icon: '🔄', title: '自动备份', desc: '定期自动备份，数据安全有保障' },
      { icon: '📊', title: '实时监控', desc: '24小时实时监控，及时发现问题' },
      { icon: '🔧', title: '一键部署', desc: '快速部署应用，简化运维工作' },
      { icon: '🌍', title: '全球CDN', desc: '全球CDN加速，提升访问速度' },
      { icon: '🔐', title: 'SSL证书', desc: '免费SSL证书，保障数据传输安全' }
    ])

    // 使用场景数据
    const usageScenarios = ref([
      { icon: '🌐', title: '网站托管', desc: '适合各类网站和Web应用托管' },
      { icon: '⚙️', title: '开发测试', desc: '为开发团队提供灵活的测试环境' },
      { icon: '📊', title: '数据库服务', desc: '高性能数据库服务，支持多种数据库' },
      { icon: '🤖', title: 'AI/ML工作负载', desc: '支持人工智能和机器学习应用' }
    ])

    // 服务保障数据
    const serviceGuarantees = ref([
      { icon: '⏱️', title: 'SLA保障', desc: '99.9%服务可用性保障' },
      { icon: '🎧', title: '7x24技术支持', desc: '全天候专业技术支持服务' },
      { icon: '💰', title: '退款政策', desc: '不满意可申请退款保障' },
      { icon: '🚀', title: '快速交付', desc: '订单确认后快速开通服务' }
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
      serverSpecs,
      techFeatures,
      usageScenarios,
      serviceGuarantees,
      getGroupsByCategory,
      getCategoryIcon,
      scrollToProducts,
      toggleCategory,
      selectServerGroup,
      contactForPurchase,
      contactUs
    }
  }
}
</script>

<style scoped>
/* 简化的销售页面样式 */
.sidebar-col {
  border-right: 1px solid rgba(var(--v-theme-on-surface), 0.1);
}

.category-item {
  cursor: pointer;
}

.category-item:hover {
  background: rgba(var(--v-theme-primary), 0.05);
}

.category-expanded {
  background: rgba(var(--v-theme-primary), 0.08);
}

.group-item {
  cursor: pointer;
}

.group-item:hover {
  background: rgba(var(--v-theme-primary), 0.08);
}

.group-selected {
  background: rgba(var(--v-theme-primary), 0.12) !important;
  border-left: 3px solid rgb(var(--v-theme-primary));
}

.content-area {
  padding: 24px;
}

.welcome-section {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

/* 大气的简单布局 */
.simple-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 16px;
}

.simple-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.simple-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.05);
}

.simple-item:last-child {
  border-bottom: none;
}

.item-icon {
  font-size: 24px;
  line-height: 1;
  flex-shrink: 0;
}

.item-title {
  font-size: 1.1rem;
  font-weight: 500;
  margin-bottom: 4px;
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.item-desc {
  font-size: 0.95rem;
  color: rgba(var(--v-theme-on-surface), 0.6);
  line-height: 1.5;
}

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
  
  .content-area {
    padding: 16px !important;
  }
  
  .welcome-section {
    padding: 1rem;
  }
  
  .simple-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .simple-item {
    gap: 12px;
    padding: 12px 0;
  }
}
</style>