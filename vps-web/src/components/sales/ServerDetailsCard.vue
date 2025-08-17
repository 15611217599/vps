<template>
  <div class="price-groups-section">
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
      <div class="price-groups-container">
        <div
          v-for="priceGroup in getGroupPriceGroups(selectedGroup.id)"
          :key="priceGroup.id"
          class="price-group-wrapper"
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
              
              <!-- 商品详情HTML展示 -->
              <div v-if="priceGroup.salesPageHtml" class="product-details mb-4">
                <div 
                  class="product-html-content"
                  v-html="priceGroup.salesPageHtml"
                ></div>
              </div>
              
              <!-- 配置选择器 -->
              <div class="config-selector mb-4">
                <!-- 参数选择标题 -->
                <div class="config-title mb-4">
                  <h4 class="text-h6 font-weight-bold d-flex align-center">
                    <v-icon size="20" class="me-2" color="primary">mdi-tune</v-icon>
                    {{ $t('sales.configureParameters') }}
                  </h4>
                  <p class="text-body-2 text-medium-emphasis mb-0">
                    {{ $t('sales.customizeServerConfig') }}
                  </p>
                </div>
                
                <!-- CPU选择 -->
                <div class="config-item mb-4">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-cpu-64-bit</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.cpu') }}</span>
                    <v-chip size="x-small" color="primary" class="ms-2">{{ selectedConfig.cpu }}{{ $t('sales.cores') }}</v-chip>
                  </div>
                  <v-slider
                    v-model="selectedConfig.cpu"
                    :min="1"
                    :max="32"
                    :step="1"
                    :disabled="isConfigMatched"
                    color="primary"
                  ></v-slider>
                </div>

                <!-- 内存选择 -->
                <div class="config-item mb-4">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-memory</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.memory') }}</span>
                    <v-chip size="x-small" color="success" class="ms-2">{{ selectedConfig.memory }}{{ $t('sales.gb') }}</v-chip>
                  </div>
                  <v-slider
                    v-model="selectedConfig.memory"
                    :min="1"
                    :max="128"
                    :step="1"
                    :disabled="isConfigMatched"
                    color="success"
                  ></v-slider>
                </div>

                <!-- 磁盘选择 -->
                <div class="config-item mb-4">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-harddisk</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.storage') }}</span>
                    <v-chip size="x-small" color="warning" class="ms-2">{{ selectedConfig.storage }}{{ $t('sales.gb') }} {{ $t('sales.ssd') }}</v-chip>
                  </div>
                  <v-slider
                    v-model="selectedConfig.storage"
                    :min="20"
                    :max="2000"
                    :step="10"
                    :disabled="isConfigMatched"
                    color="warning"
                  ></v-slider>
                </div>

                <!-- 带宽选择 -->
                <div class="config-item mb-4">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-speedometer</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.bandwidth') }}</span>
                    <v-chip size="x-small" color="info" class="ms-2">{{ selectedConfig.bandwidth }}{{ $t('sales.mbps') }}</v-chip>
                  </div>
                  <v-slider
                    v-model="selectedConfig.bandwidth"
                    :min="1"
                    :max="1000"
                    :step="1"
                    :disabled="isConfigMatched"
                    color="info"
                  ></v-slider>
                </div>

                <!-- IP地址数选择 -->
                <div class="config-item mb-4">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-ip-network</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.ipCount') }}</span>
                    <v-chip size="x-small" color="purple" class="ms-2">{{ selectedConfig.ipCount }}</v-chip>
                  </div>
                  <v-slider
                    v-model="selectedConfig.ipCount"
                    :min="1"
                    :max="10"
                    :step="1"
                    :disabled="isConfigMatched"
                    color="purple"
                  ></v-slider>
                </div>

                <!-- 操作系统选择 - 动态生成 -->
                <div class="config-item mb-4" v-if="operatingSystemOptions.length > 0">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-linux</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.operatingSystem') }}</span>
                  </div>
                  <v-select
                    v-model="selectedConfig.operatingSystem"
                    :items="operatingSystemOptions"
                    :placeholder="$t('sales.selectOS')"
                    :disabled="false"
                    variant="outlined"
                    density="compact"
                    class="config-select"
                  >
                    <template #item="{ props, item }">
                      <v-list-item v-bind="props">
                        <template #prepend>
                          <v-icon size="20" :icon="getOSIcon(item.raw.os)"></v-icon>
                        </template>
                        <v-list-item-title>{{ item.raw.title }}</v-list-item-title>
                        <v-list-item-subtitle>{{ item.raw.subtitle }}</v-list-item-subtitle>
                      </v-list-item>
                    </template>
                    <template #selection="{ item }">
                      <div class="d-flex align-center">
                        <v-icon size="16" class="me-2" :icon="getOSIcon(item.raw.os)"></v-icon>
                        <span>{{ item.raw.title }}</span>
                      </div>
                    </template>
                  </v-select>
                </div>

                <!-- 配置摘要 -->
                <v-card class="config-summary mb-4" variant="tonal" color="primary">
                  <v-card-text class="pa-4">
                    <div class="d-flex align-center mb-2">
                      <v-icon size="20" class="me-2">mdi-server</v-icon>
                      <span class="text-subtitle-2 font-weight-bold">{{ $t('sales.currentConfig') }}</span>
                    </div>
                    <div class="config-summary-grid">
                      <div class="config-summary-item">
                        <span class="config-summary-label">{{ $t('sales.cpu') }}:</span>
                        <span class="config-summary-value">{{ selectedConfig.cpu }}{{ $t('sales.cores') }}</span>
                      </div>
                      <div class="config-summary-item">
                        <span class="config-summary-label">{{ $t('sales.memory') }}:</span>
                        <span class="config-summary-value">{{ selectedConfig.memory }}{{ $t('sales.gb') }}</span>
                      </div>
                      <div class="config-summary-item">
                        <span class="config-summary-label">{{ $t('sales.storage') }}:</span>
                        <span class="config-summary-value">{{ selectedConfig.storage }}{{ $t('sales.gb') }}</span>
                      </div>
                      <div class="config-summary-item">
                        <span class="config-summary-label">{{ $t('sales.bandwidth') }}:</span>
                        <span class="config-summary-value">{{ selectedConfig.bandwidth }}{{ $t('sales.mbps') }}</span>
                      </div>
                      <div class="config-summary-item">
                        <span class="config-summary-label">{{ $t('sales.ipCount') }}:</span>
                        <span class="config-summary-value">{{ selectedConfig.ipCount }}</span>
                      </div>
                      <div class="config-summary-item" v-if="selectedConfig.operatingSystem">
                        <span class="config-summary-label">{{ $t('sales.operatingSystem') }}:</span>
                        <span class="config-summary-value">{{ getSelectedOSDisplay() }}</span>
                      </div>
                    </div>
                  </v-card-text>
                </v-card>
              </div>

              
              <!-- 联系购买按钮 -->
              <v-btn
                block
                color="primary"
                variant="flat"
                @click="contactForPurchase(priceGroup)"
                class="mt-4"
              >
                {{ $t('sales.contactToPurchase') }}
              </v-btn>
            </v-card-text>
          </v-card>
        </div>
      </div>
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
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
// import { useI18n } from 'vue-i18n' // Removed unused import
import { getLocalizedText } from '@/utils/i18n'

interface Props {
  selectedGroup: any
  priceGroups: any[]
}

interface Emits {
  (e: 'contact-purchase', priceGroup: any): void
  (e: 'contact-us'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()
// const { t } = useI18n() // Removed unused import

// 配置选择器状态
const selectedConfig = ref({
  cpu: 2,
  memory: 4,
  storage: 50,
  bandwidth: 10,
  ipCount: 1,
  operatingSystem: null as string | null
})

// 服务器规格匹配状态
const isConfigMatched = ref(false)
const serverSpecs = ref<any>(null)


// 操作系统选项 - 动态生成
const operatingSystemOptions = ref<Array<{
  title: string
  subtitle: string
  value: string
  os: string
}>>([])


// 获取分组下的价格组
const getGroupPriceGroups = (groupId: number) => {
  return props.priceGroups.filter(pg => pg.serverGroupId === groupId)
}

// 获取价格组数量
const getPriceGroupCount = (groupId: number) => {
  return getGroupPriceGroups(groupId).length
}



// 联系购买
const contactForPurchase = (priceGroup: any) => {
  emit('contact-purchase', priceGroup)
}

// 联系我们
const contactUs = () => {
  emit('contact-us')
}

// 获取操作系统图标
const getOSIcon = (osName: string) => {
  const iconMap: Record<string, string> = {
    ubuntu: 'mdi-ubuntu',
    centos: 'mdi-centos',
    rocky: 'mdi-linux',
    almalinux: 'mdi-linux',
    debian: 'mdi-debian',
    fedora: 'mdi-fedora',
    opensuse: 'mdi-suse',
    alpine: 'mdi-alpine-linux',
    oracle: 'mdi-oracle',
    anolis: 'mdi-linux',
    openeuler: 'mdi-linux',
    nixos: 'mdi-nixos'
  }
  return iconMap[osName] || 'mdi-linux'
}

// 获取选中的操作系统显示文本
const getSelectedOSDisplay = () => {
  if (!selectedConfig.value.operatingSystem) return ''
  const selected = operatingSystemOptions.value.find(opt => opt.value === selectedConfig.value.operatingSystem)
  return selected ? selected.title : ''
}


// 生成操作系统选项
const generateOperatingSystemOptions = (osData: any[]) => {
  const options: Array<{
    title: string
    subtitle: string
    value: string
    os: string
  }> = []
  
  if (!osData || osData.length === 0) {
    return
  }
  
  osData.forEach(osInfo => {
    const osName = osInfo.name?.toLowerCase() || ''
    const versions = osInfo.versions || []
    
    versions.forEach((version: string) => {
      const displayName = getOSDisplayName(osName, version)
      const subtitle = getOSSubtitle(osName, version)
      
      options.push({
        title: displayName,
        subtitle: subtitle,
        value: `${osName}-${version}`,
        os: osName
      })
    })
  })
  
  operatingSystemOptions.value = options.length > 0 ? options : []
}

// 获取操作系统显示名称
const getOSDisplayName = (osName: string, version: string) => {
  const nameMap: Record<string, string> = {
    ubuntu: 'Ubuntu',
    centos: 'CentOS',
    debian: 'Debian',
    fedora: 'Fedora',
    rocky: 'Rocky Linux',
    almalinux: 'AlmaLinux',
    opensuse: 'openSUSE Leap',
    alpine: 'Alpine Linux',
    oracle: 'Oracle Linux',
    anolis: 'Anolis OS',
    openeuler: 'OpenEuler',
    nixos: 'NixOS'
  }
  
  const displayName = nameMap[osName] || osName.charAt(0).toUpperCase() + osName.slice(1)
  return `${displayName} ${version}`
}

// 获取操作系统副标题
const getOSSubtitle = (osName: string, version: string) => {
  // 根据操作系统和版本生成合适的副标题
  if (osName === 'ubuntu' && version.includes('24.04')) return 'Latest LTS'
  if (osName === 'ubuntu' && version.includes('22.04')) return 'Stable LTS'
  if (osName === 'ubuntu' && version.includes('20.04')) return 'Legacy LTS'
  if (osName === 'centos') return 'Stream'
  if (osName === 'rocky' || osName === 'almalinux') return 'RHEL Compatible'
  if (osName === 'debian' && version === '12') return 'Bookworm'
  if (osName === 'debian' && version === '11') return 'Bullseye'
  if (osName === 'fedora') return 'Latest'
  if (osName === 'opensuse') return 'Stable'
  if (osName === 'alpine') return 'Lightweight'
  if (osName === 'oracle') return 'Enterprise'
  if (osName === 'anolis') return 'OpenAnolis'
  if (osName === 'openeuler') return 'Huawei'
  if (osName === 'nixos') return 'Functional'
  
  return 'Available'
}

// 自动匹配服务器规格
const autoMatchServerSpecs = (specs: any) => {
  if (!specs) return
  
  serverSpecs.value = specs
  selectedConfig.value = {
    cpu: specs.cpu_cores || 2,
    memory: specs.memory || 4,
    storage: specs.disk_space || 50,
    bandwidth: specs.network_speed || 10,
    ipCount: specs.ip_count || 1,
    operatingSystem: null
  }
  
  // 根据API返回的操作系统数据生成选项
  if (specs.operating_system) {
    generateOperatingSystemOptions(specs.operating_system)
    
    // 设置默认操作系统（选择第一个可用的）
    if (operatingSystemOptions.value.length > 0) {
      selectedConfig.value.operatingSystem = operatingSystemOptions.value[0].value
    }
  } else {
    // 如果没有操作系统数据，使用默认选项
  }
  
  isConfigMatched.value = true
}

// 监听价格组变化，检查是否有匹配的服务器规格
const checkForServerSpecs = () => {
  const groupPriceGroups = getGroupPriceGroups(props.selectedGroup.id)
  
  // 这里应该从API获取服务器规格数据
  // 暂时使用示例数据进行演示
  const exampleSpecs = {
    cpu_cores: 32,
    memory: 32,
    disk_space: 200,
    network_speed: 25,
    ip_count: 1,
    operating_system: [
      { name: 'ubuntu', versions: ['24.04', '22.04', '20.04'] },
      { name: 'centos', versions: ['9', '10'] },
      { name: 'debian', versions: ['11', '12'] }
    ]
  }
  
  // 如果有匹配的规格，自动设置配置
  if (groupPriceGroups.length > 0) {
    autoMatchServerSpecs(exampleSpecs)
  }
}

// 组件挂载时检查服务器规格
onMounted(() => {
  // 初始化默认操作系统选项
  checkForServerSpecs()
})

// 监听选中分组变化
watch(() => props.selectedGroup, () => {
  checkForServerSpecs()
}, { deep: true })




</script>

<style scoped>
/* 保留必要的样式，移除复杂的自定义样式 */
</style>
