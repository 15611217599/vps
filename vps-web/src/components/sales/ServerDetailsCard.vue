<template>
  <div class="price-groups-section">
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
                
                <!-- 配置锁定提示 -->
                <div v-if="hasDefaultConfig(priceGroup)" class="mb-4">
                  <v-alert
                    type="info"
                    variant="tonal"
                    density="compact"
                    class="text-caption"
                  >
                    <v-icon size="16" class="me-2">mdi-lock</v-icon>
                    {{ $t('sales.configLocked') }}
                  </v-alert>
                </div>

                <!-- CPU选择 -->
                <div class="config-item mb-4">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-cpu-64-bit</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.cpu') }}</span>
                    <v-chip size="x-small" color="primary" class="ms-2">{{ getSelectedConfig(priceGroup).cpu }}{{ $t('sales.cores') }}</v-chip>
                    <v-icon v-if="hasDefaultConfig(priceGroup)" size="14" class="ms-1" color="grey">mdi-lock</v-icon>
                  </div>
                  <v-slider
                    :model-value="getSelectedConfig(priceGroup).cpu"
                    :min="1"
                    :max="32"
                    :step="1"
                    color="primary"
                    :disabled="hasDefaultConfig(priceGroup)"
                    @update:model-value="(value) => updateConfig(priceGroup, 'cpu', value)"
                  ></v-slider>
                </div>

                <!-- 内存选择 -->
                <div class="config-item mb-4">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-memory</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.memory') }}</span>
                    <v-chip size="x-small" color="success" class="ms-2">{{ getSelectedConfig(priceGroup).memory }}{{ $t('sales.gb') }}</v-chip>
                    <v-icon v-if="hasDefaultConfig(priceGroup)" size="14" class="ms-1" color="grey">mdi-lock</v-icon>
                  </div>
                  <v-slider
                    :model-value="getSelectedConfig(priceGroup).memory"
                    :min="1"
                    :max="128"
                    :step="1"
                    color="success"
                    :disabled="hasDefaultConfig(priceGroup)"
                    @update:model-value="(value) => updateConfig(priceGroup, 'memory', value)"
                  ></v-slider>
                </div>

                <!-- 磁盘选择 -->
                <div class="config-item mb-4">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-harddisk</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.storage') }}</span>
                    <v-chip size="x-small" color="warning" class="ms-2">{{ getSelectedConfig(priceGroup).storage }}{{ $t('sales.gb') }} {{ $t('sales.ssd') }}</v-chip>
                    <v-icon v-if="hasDefaultConfig(priceGroup)" size="14" class="ms-1" color="grey">mdi-lock</v-icon>
                  </div>
                  <v-slider
                    :model-value="getSelectedConfig(priceGroup).storage"
                    :min="20"
                    :max="2000"
                    :step="10"
                    color="warning"
                    :disabled="hasDefaultConfig(priceGroup)"
                    @update:model-value="(value) => updateConfig(priceGroup, 'storage', value)"
                  ></v-slider>
                </div>

                <!-- 带宽选择 -->
                <div class="config-item mb-4">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-speedometer</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.bandwidth') }}</span>
                    <v-chip size="x-small" color="info" class="ms-2">{{ getSelectedConfig(priceGroup).bandwidth }}{{ $t('sales.mbps') }}</v-chip>
                    <v-icon v-if="hasDefaultConfig(priceGroup)" size="14" class="ms-1" color="grey">mdi-lock</v-icon>
                  </div>
                  <v-slider
                    :model-value="getSelectedConfig(priceGroup).bandwidth"
                    :min="1"
                    :max="1000"
                    :step="1"
                    color="info"
                    :disabled="hasDefaultConfig(priceGroup)"
                    @update:model-value="(value) => updateConfig(priceGroup, 'bandwidth', value)"
                  ></v-slider>
                </div>

                <!-- IP地址数选择 -->
                <div class="config-item mb-4">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-ip-network</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.ipCount') }}</span>
                    <v-chip size="x-small" color="purple" class="ms-2">{{ getSelectedConfig(priceGroup).ipCount }}</v-chip>
                    <v-icon v-if="hasDefaultConfig(priceGroup)" size="14" class="ms-1" color="grey">mdi-lock</v-icon>
                  </div>
                  <v-slider
                    :model-value="getSelectedConfig(priceGroup).ipCount"
                    :min="1"
                    :max="10"
                    :step="1"
                    color="purple"
                    :disabled="hasDefaultConfig(priceGroup)"
                    @update:model-value="(value) => updateConfig(priceGroup, 'ipCount', value)"
                  ></v-slider>
                </div>

                <!-- 操作系统选择 -->
                <div v-if="getPriceGroupOperatingSystem(priceGroup)" class="config-item mb-4">
                  <div class="config-label mb-3">
                    <v-icon size="16" class="me-2">mdi-linux</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.osSelection') }}</span>
                  </div>
                  
                  <v-row>
                    <!-- 第一列：系统选择 -->
                    <v-col cols="6">
                      <v-select
                        :model-value="getSelectedOSName(priceGroup)"
                        :items="getOSNameOptions(priceGroup)"
                        item-title="title"
                        item-value="value"
                        :placeholder="$t('sales.selectOS')"
                        variant="outlined"
                        density="compact"
                        :label="$t('sales.systemName')"
                        @update:model-value="(value) => onPriceGroupOSNameChange(priceGroup, value)"
                      >
                      </v-select>
                    </v-col>
                    
                    <!-- 第二列：版本选择 -->
                    <v-col cols="6">
                      <v-select
                        :model-value="getSelectedOSVersion(priceGroup)"
                        :items="getOSVersionOptions(priceGroup)"
                        item-title="title"
                        item-value="value"
                        :placeholder="$t('sales.selectVersion')"
                        :disabled="!getSelectedOSName(priceGroup)"
                        variant="outlined"
                        density="compact"
                        :label="$t('sales.version')"
                        @update:model-value="(value) => onPriceGroupOSVersionChange(priceGroup, value)"
                      >
                      </v-select>
                    </v-col>
                  </v-row>
                  
                  <!-- 选择结果显示 -->
                  <div v-if="getSelectedOSName(priceGroup) && getSelectedOSVersion(priceGroup)" class="mt-3">
                    <v-chip color="success" variant="flat" size="small">
                      <v-icon start size="14">mdi-check-circle</v-icon>
                      {{ $t('sales.selected') }}: {{ getSelectedOSName(priceGroup) }} {{ getSelectedOSVersion(priceGroup) }}
                    </v-chip>
                  </div>
                </div>

                <!-- 初始化密码 -->
                <div class="config-item mb-4">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-key</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.initialPassword') }}</span>
                  </div>
                  <v-text-field
                    :model-value="getSelectedConfig(priceGroup).password"
                    variant="outlined"
                    density="compact"
:label="$t('sales.serverInitialPassword')"
:placeholder="$t('sales.enterInitialPassword')"
                    :type="getPasswordVisibility(priceGroup) ? 'text' : 'password'"
                    @update:model-value="(value) => updateConfigString(priceGroup, 'password', value)"
                  >
                    <template #append-inner>
                      <v-btn
                        :icon="getPasswordVisibility(priceGroup) ? 'mdi-eye-off' : 'mdi-eye'"
                        size="small"
                        variant="text"
                        @click="togglePasswordVisibility(priceGroup)"
                      ></v-btn>
                      <v-btn
                        icon="mdi-refresh"
                        size="small"
                        variant="text"
                        @click="generateRandomPassword(priceGroup)"
                      ></v-btn>
                    </template>
                  </v-text-field>
                </div>

                <!-- SSH端口号 -->
                <div class="config-item mb-4">
                  <div class="config-label mb-2">
                    <v-icon size="16" class="me-2">mdi-network</v-icon>
                    <span class="text-subtitle-2 font-weight-medium">{{ $t('sales.sshPort') }}</span>
                  </div>
                  <v-text-field
                    :model-value="getSelectedConfig(priceGroup).port"
                    variant="outlined"
                    density="compact"
:label="$t('sales.sshPortNumber')"
:placeholder="$t('sales.enterPortNumber')"
                    type="number"
                    :min="1024"
                    :max="65535"
                    @update:model-value="(value) => updateConfigString(priceGroup, 'port', value)"
                  >
                    <template #append-inner>
                      <v-btn
                        icon="mdi-refresh"
                        size="small"
                        variant="text"
                        @click="generateRandomPort(priceGroup)"
                      ></v-btn>
                    </template>
                  </v-text-field>
                </div>

                <!-- 配置摘要 -->
                <v-card class="config-summary mb-4" variant="outlined" elevation="3">
                  <v-card-title class="d-flex align-center pa-4 pb-3 bg-gradient">
                    <v-icon size="22" class="me-2" color="white">mdi-cog</v-icon>
                    <span class="text-h6 font-weight-bold text-white">{{ $t('sales.currentConfig') }}</span>
                  </v-card-title>
                  <v-card-text class="pa-0">
                    <!-- 硬件配置 -->
                    <div class="config-section">
                      <div class="section-header">
                        <v-icon size="18" class="me-2" color="primary">mdi-server</v-icon>
                        <span class="text-subtitle-1 font-weight-bold text-primary">{{ $t('sales.hardwareSpecs') }}</span>
                      </div>
                      <div class="config-grid">
                        <div class="config-item-card">
                          <div class="config-icon">
                            <v-icon size="20" color="primary">mdi-cpu-64-bit</v-icon>
                          </div>
                          <div class="config-content">
                            <div class="config-label">{{ $t('sales.cpu') }}</div>
                            <div class="config-value">{{ getSelectedConfig(priceGroup).cpu }}{{ $t('sales.cores') }}</div>
                          </div>
                        </div>
                        <div class="config-item-card">
                          <div class="config-icon">
                            <v-icon size="20" color="success">mdi-memory</v-icon>
                          </div>
                          <div class="config-content">
                            <div class="config-label">{{ $t('sales.memory') }}</div>
                            <div class="config-value">{{ getSelectedConfig(priceGroup).memory }}{{ $t('sales.gb') }}</div>
                          </div>
                        </div>
                        <div class="config-item-card">
                          <div class="config-icon">
                            <v-icon size="20" color="warning">mdi-harddisk</v-icon>
                          </div>
                          <div class="config-content">
                            <div class="config-label">{{ $t('sales.storage') }}</div>
                            <div class="config-value">{{ getSelectedConfig(priceGroup).storage }}{{ $t('sales.gb') }}</div>
                          </div>
                        </div>
                        <div class="config-item-card">
                          <div class="config-icon">
                            <v-icon size="20" color="info">mdi-speedometer</v-icon>
                          </div>
                          <div class="config-content">
                            <div class="config-label">{{ $t('sales.bandwidth') }}</div>
                            <div class="config-value">{{ getSelectedConfig(priceGroup).bandwidth }}{{ $t('sales.mbps') }}</div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- 网络配置 -->
                    <div class="config-section">
                      <div class="section-header">
                        <v-icon size="18" class="me-2" color="primary">mdi-network</v-icon>
                        <span class="text-subtitle-1 font-weight-bold text-primary">{{ $t('sales.networkConfig') }}</span>
                      </div>
                      <div class="config-grid">
                        <div class="config-item-card">
                          <div class="config-icon">
                            <v-icon size="20" color="purple">mdi-ip-network</v-icon>
                          </div>
                          <div class="config-content">
                            <div class="config-label">{{ $t('sales.ipAddress') }}</div>
                            <div class="config-value">{{ getSelectedConfig(priceGroup).ipCount }} {{ $t('sales.count') }}</div>
                          </div>
                        </div>
                        <div class="config-item-card">
                          <div class="config-icon">
                            <v-icon size="20" color="teal">mdi-lan-connect</v-icon>
                          </div>
                          <div class="config-content">
                            <div class="config-label">{{ $t('sales.sshPort') }}</div>
                            <div class="config-value">{{ getSelectedConfig(priceGroup).port }}</div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- 系统配置 -->
                    <div class="config-section">
                      <div class="section-header">
                        <v-icon size="18" class="me-2" color="primary">mdi-desktop-tower</v-icon>
                        <span class="text-subtitle-1 font-weight-bold text-primary">{{ $t('sales.systemConfig') }}</span>
                      </div>
                      <div class="config-grid">
                        <div v-if="getSelectedOSName(priceGroup) && getSelectedOSVersion(priceGroup)" class="config-item-card">
                          <div class="config-icon">
                            <v-icon size="20" color="orange">mdi-linux</v-icon>
                          </div>
                          <div class="config-content">
                            <div class="config-label">{{ $t('sales.operatingSystem') }}</div>
                            <div class="config-value">{{ getSelectedOSName(priceGroup) }} {{ getSelectedOSVersion(priceGroup) }}</div>
                          </div>
                        </div>
                        <div class="config-item-card">
                          <div class="config-icon">
                            <v-icon size="20" color="red">mdi-key-variant</v-icon>
                          </div>
                          <div class="config-content">
                            <div class="config-label">{{ $t('sales.initialPassword') }}</div>
                            <div class="config-value">{{ getSelectedConfig(priceGroup).password.substring(0, 4) }}****</div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </v-card-text>
                </v-card>

                <!-- 价格选择器 -->
                <v-card class="price-selector mb-4" variant="outlined" elevation="2">
                  <v-card-title class="d-flex align-center pa-4 pb-2">
                    <v-icon size="20" class="me-2" color="success">mdi-currency-usd</v-icon>
                    <span class="text-h6 font-weight-bold">{{ $t('sales.pricingPlans') }}</span>
                  </v-card-title>
                  <v-card-text class="pa-4 pt-2">
                    <div v-if="getPriceOptions(priceGroup).length > 0">
                      <div class="price-options-row">
                        <div 
                          v-for="priceOption in getPriceOptions(priceGroup)" 
                          :key="priceOption.period"
                          class="price-option-compact"
                          :class="{ 'price-selected': getSelectedPricePeriod(priceGroup) === priceOption.period }"
                          @click="selectPricePeriod(priceGroup, priceOption.period)"
                        >
                          <div class="price-period">{{ priceOption.label }}</div>
                          <div class="price-amount">¥{{ priceOption.price }}</div>
                          <div class="price-unit">{{ priceOption.unit }}</div>
                          <v-chip 
                            v-if="priceOption.discount && priceOption.discount > 0"
                            size="x-small" 
                            color="success" 
                            variant="flat"
                            class="price-discount"
                          >
                            {{ $t('sales.save') }}{{ priceOption.discount }}%
                          </v-chip>
                        </div>
                      </div>
                    </div>
                    <div v-else class="text-center py-4">
                      <v-icon size="48" color="grey-lighten-1" class="mb-2">mdi-currency-usd-off</v-icon>
                      <div class="text-body-1 text-medium-emphasis">{{ $t('sales.noPriceInfo') }}</div>
                    </div>
                  </v-card-text>
                </v-card>
              </div>

              
              <!-- 立即购买按钮 -->
              <v-btn
                block
                color="primary"
                variant="flat"
                @click="purchaseNow(priceGroup)"
                class="mt-4"
                :disabled="!getSelectedPricePeriod(priceGroup) || purchasing"
                :loading="purchasing"
              >
                <v-icon start>mdi-cart</v-icon>
                {{ purchasing ? $t('sales.processing') : (getSelectedPricePeriod(priceGroup) ? $t('sales.purchaseNow') : $t('sales.selectPricePlan')) }}
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
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { getLocalizedText } from '@/utils/i18n'
import { orderApi, type CreateOrderRequest } from '@/api/order'
import { useNotification } from '@/composables/useNotification'

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

const { t } = useI18n()
const { showNotification } = useNotification()

// 购买状态
const purchasing = ref(false)

// 价格组选择状态
const priceGroupSelections = ref<Record<number, {
  osName: string | null
  osVersion: string | null
  cpu: number
  memory: number
  storage: number
  bandwidth: number
  ipCount: number
  password: string
  port: number
  selectedPricePeriod: string | null
}>>({})

// 密码可见性状态
const passwordVisibility = ref<Record<number, boolean>>({})  

// 获取分组下的价格组
const getGroupPriceGroups = (groupId: number) => {
  return props.priceGroups.filter(pg => pg.serverGroupId === groupId)
}

// 获取价格组数量
const getPriceGroupCount = (groupId: number) => {
  return getGroupPriceGroups(groupId).length
}

// 获取价格组的操作系统信息
const getPriceGroupOperatingSystem = (priceGroup: any) => {
  try {
    if (priceGroup && priceGroup.salesPageHtml) {
      // 如果 salesPageHtml 是字符串，尝试解析为 JSON
      let data = priceGroup.salesPageHtml
      if (typeof data === 'string') {
        try {
          data = JSON.parse(data)
        } catch (parseError) {
          console.error('JSON解析失败:', parseError)
          return null
        }
      }
      
      // 返回操作系统数据
      if (data && data.operating_system) {
        return data.operating_system
      }
    }
    return null
  } catch (error) {
    console.error('解析操作系统数据失败:', error)
    return null
  }
}

// 获取价格组的操作系统名称选项
const getOSNameOptions = (priceGroup: any) => {
  const osData = getPriceGroupOperatingSystem(priceGroup)
  if (!osData) return []
  
  return osData.map((osInfo: any) => ({
    title: osInfo.name,
    value: osInfo.name
  }))
}

// 获取价格组的操作系统版本选项
const getOSVersionOptions = (priceGroup: any) => {
  const osData = getPriceGroupOperatingSystem(priceGroup)
  const selectedOSName = getSelectedOSName(priceGroup)
  
  if (!osData || !selectedOSName) return []
  
  const osInfo = osData.find((os: any) => os.name === selectedOSName)
  if (!osInfo || !osInfo.versions) return []
  
  return osInfo.versions.map((version: string) => ({
    title: version,
    value: version
  }))
}

// 从JSON数据获取默认配置值
const getDefaultConfigFromJSON = (priceGroup: any) => {
  try {
    if (priceGroup && priceGroup.salesPageHtml) {
      let data = priceGroup.salesPageHtml
      if (typeof data === 'string') {
        try {
          data = JSON.parse(data)
        } catch (parseError) {
          return null
        }
      }
      
      if (data) {
        return {
          cpu: data.cpu_cores || 1,
          memory: data.memory || 1,
          storage: data.disk_space || 20,
          bandwidth: data.network_speed || 1,
          ipCount: data.ip_count || 1
        }
      }
    }
    return null
  } catch (error) {
    return null
  }
}

// 检查是否有默认配置（如果有则禁用修改）
const hasDefaultConfig = (priceGroup: any) => {
  return getDefaultConfigFromJSON(priceGroup) !== null
}

// 初始化价格组选择状态
const initPriceGroupSelection = (priceGroupId: number, priceGroup?: any) => {
  if (!priceGroupSelections.value[priceGroupId]) {
    // 尝试从JSON获取默认值
    const defaultConfig = priceGroup ? getDefaultConfigFromJSON(priceGroup) : null
    
    priceGroupSelections.value[priceGroupId] = {
      osName: null,
      osVersion: null,
      cpu: defaultConfig?.cpu || 1,
      memory: defaultConfig?.memory || 1,
      storage: defaultConfig?.storage || 20,
      bandwidth: defaultConfig?.bandwidth || 1,
      ipCount: defaultConfig?.ipCount || 1,
      password: generateRandomPasswordString(),
      port: generateRandomPortNumber(),
      selectedPricePeriod: null
    }
    
    // 设置默认操作系统
    if (priceGroup) {
      const osData = getPriceGroupOperatingSystem(priceGroup)
      if (osData && osData.length > 0) {
        priceGroupSelections.value[priceGroupId].osName = osData[0].name
        if (osData[0].versions && osData[0].versions.length > 0) {
          // 选择最新版本（数组最后一个元素）
          priceGroupSelections.value[priceGroupId].osVersion = osData[0].versions[osData[0].versions.length - 1]
        }
      }
    }
  }
}

// 生成随机密码
const generateRandomPasswordString = () => {
  const chars = 'ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%^&*'
  let password = ''
  for (let i = 0; i < 12; i++) {
    password += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return password
}

// 生成随机端口号
const generateRandomPortNumber = () => {
  return Math.floor(Math.random() * (65535 - 1024 + 1)) + 1024
}

// 更新字符串配置
const updateConfigString = (priceGroup: any, field: string, value: string) => {
  initPriceGroupSelection(priceGroup.id, priceGroup)
  if (field === 'password') {
    priceGroupSelections.value[priceGroup.id].password = value
  } else if (field === 'port') {
    priceGroupSelections.value[priceGroup.id].port = parseInt(value) || 22
  }
}

// 生成随机密码
const generateRandomPassword = (priceGroup: any) => {
  initPriceGroupSelection(priceGroup.id, priceGroup)
  priceGroupSelections.value[priceGroup.id].password = generateRandomPasswordString()
}

// 生成随机端口
const generateRandomPort = (priceGroup: any) => {
  initPriceGroupSelection(priceGroup.id, priceGroup)
  priceGroupSelections.value[priceGroup.id].port = generateRandomPortNumber()
}

// 获取密码可见性状态
const getPasswordVisibility = (priceGroup: any) => {
  return passwordVisibility.value[priceGroup.id] || false
}

// 切换密码可见性
const togglePasswordVisibility = (priceGroup: any) => {
  passwordVisibility.value[priceGroup.id] = !passwordVisibility.value[priceGroup.id]
}

// 获取价格选项
const getPriceOptions = (priceGroup: any) => {
  const { t } = useI18n()
  const options = []
  
  if (priceGroup.hourlyPrice && priceGroup.hourlyPrice > 0) {
    options.push({
      period: 'hourly',
      label: t('sales.hourly'),
      price: priceGroup.hourlyPrice,
      unit: t('sales.perHour'),
      discount: 0
    })
  }
  
  if (priceGroup.dailyPrice && priceGroup.dailyPrice > 0) {
    options.push({
      period: 'daily',
      label: t('sales.daily'),
      price: priceGroup.dailyPrice,
      unit: t('sales.perDay'),
      discount: 0
    })
  }
  
  if (priceGroup.monthlyPrice && priceGroup.monthlyPrice > 0) {
    options.push({
      period: 'monthly',
      label: t('sales.monthly'),
      price: priceGroup.monthlyPrice,
      unit: t('sales.perMonth'),
      discount: 0
    })
  }
  
  if (priceGroup.quarterlyPrice && priceGroup.quarterlyPrice > 0) {
    const monthlyEquivalent = priceGroup.monthlyPrice ? priceGroup.monthlyPrice * 3 : 0
    const discount = monthlyEquivalent > 0 ? Math.round((1 - priceGroup.quarterlyPrice / monthlyEquivalent) * 100) : 0
    options.push({
      period: 'quarterly',
      label: t('sales.quarterly'),
      price: priceGroup.quarterlyPrice,
      unit: t('sales.perQuarter'),
      discount: discount > 0 ? discount : 0
    })
  }
  
  if (priceGroup.semiAnnualPrice && priceGroup.semiAnnualPrice > 0) {
    const monthlyEquivalent = priceGroup.monthlyPrice ? priceGroup.monthlyPrice * 6 : 0
    const discount = monthlyEquivalent > 0 ? Math.round((1 - priceGroup.semiAnnualPrice / monthlyEquivalent) * 100) : 0
    options.push({
      period: 'semiAnnual',
      label: t('sales.semiAnnual'),
      price: priceGroup.semiAnnualPrice,
      unit: t('sales.perSemiAnnual'),
      discount: discount > 0 ? discount : 0
    })
  }
  
  if (priceGroup.annualPrice && priceGroup.annualPrice > 0) {
    const monthlyEquivalent = priceGroup.monthlyPrice ? priceGroup.monthlyPrice * 12 : 0
    const discount = monthlyEquivalent > 0 ? Math.round((1 - priceGroup.annualPrice / monthlyEquivalent) * 100) : 0
    options.push({
      period: 'annual',
      label: t('sales.annual'),
      price: priceGroup.annualPrice,
      unit: t('sales.perYear'),
      discount: discount > 0 ? discount : 0
    })
  }
  
  return options
}

// 获取选中的价格周期
const getSelectedPricePeriod = (priceGroup: any) => {
  initPriceGroupSelection(priceGroup.id, priceGroup)
  return priceGroupSelections.value[priceGroup.id].selectedPricePeriod
}

// 选择价格周期
const selectPricePeriod = (priceGroup: any, period: string) => {
  initPriceGroupSelection(priceGroup.id, priceGroup)
  priceGroupSelections.value[priceGroup.id].selectedPricePeriod = period
}

// 获取选中的配置
const getSelectedConfig = (priceGroup: any) => {
  initPriceGroupSelection(priceGroup.id, priceGroup)
  return priceGroupSelections.value[priceGroup.id]
}

// 更新配置
const updateConfig = (priceGroup: any, key: string, value: number) => {
  initPriceGroupSelection(priceGroup.id, priceGroup)
  ;(priceGroupSelections.value[priceGroup.id] as any)[key] = value
}

// 获取选中的操作系统名称
const getSelectedOSName = (priceGroup: any) => {
  initPriceGroupSelection(priceGroup.id, priceGroup)
  return priceGroupSelections.value[priceGroup.id].osName
}

// 获取选中的操作系统版本
const getSelectedOSVersion = (priceGroup: any) => {
  initPriceGroupSelection(priceGroup.id, priceGroup)
  return priceGroupSelections.value[priceGroup.id].osVersion
}

// 价格组操作系统名称变化处理
const onPriceGroupOSNameChange = (priceGroup: any, value: string) => {
  initPriceGroupSelection(priceGroup.id, priceGroup)
  priceGroupSelections.value[priceGroup.id].osName = value
  priceGroupSelections.value[priceGroup.id].osVersion = null // 重置版本选择
}

// 价格组操作系统版本变化处理
const onPriceGroupOSVersionChange = (priceGroup: any, value: string) => {
  initPriceGroupSelection(priceGroup.id, priceGroup)
  priceGroupSelections.value[priceGroup.id].osVersion = value
}

// 立即购买
const purchaseNow = async (priceGroup: any) => {
  const selection = priceGroupSelections.value[priceGroup.id]
  if (!selection || !selection.selectedPricePeriod) {
    showNotification(t('sales.selectPriceFirst'), 'warning')
    return
  }

  purchasing.value = true

  try {
    const orderRequest: CreateOrderRequest = {
      priceGroupId: priceGroup.id,
      billingPeriod: selection.selectedPricePeriod,
      cpuCores: selection.cpu,
      memoryGb: selection.memory,
      storageGb: selection.storage,
      bandwidthMbps: selection.bandwidth,
      ipCount: selection.ipCount,
      osName: selection.osName || '',
      osVersion: selection.osVersion || '',
      initialPassword: selection.password,
      sshPort: selection.port
    }

    const response = await orderApi.createOrder(orderRequest)
    
    if (response.success) {
      showNotification(t('sales.orderCreatedSuccess'), 'success')
      console.log('订单创建成功:', response.data)
      
      // 显示订单详情信息
      const orderInfo = `
订单号: ${response.data.orderNumber}
服务器: ${response.data.serverName || 'N/A'}
金额: ¥${response.data.amount}
计费周期: ${response.data.billingPeriod}
状态: ${response.data.status}
过期时间: ${new Date(response.data.expiresAt).toLocaleDateString()}
      `
      console.log('订单详情:', orderInfo)
    } else {
      showNotification(response.message || t('sales.orderCreateFailed'), 'error')
    }
  } catch (error: any) {
    console.error('创建订单失败:', error)
    showNotification(error.message || t('sales.orderCreateFailed'), 'error')
  } finally {
    purchasing.value = false
  }
}

// 联系购买
const contactForPurchase = (priceGroup: any) => {
  emit('contact-purchase', priceGroup)
}

// 联系我们
const contactUs = () => {
  emit('contact-us')
}
</script>

<style scoped>
.config-summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.config-summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.config-summary-label {
  font-weight: 500;
  color: rgb(var(--v-theme-on-surface));
}

.config-summary-value {
  font-weight: 600;
  color: rgb(var(--v-theme-primary));
}

.price-option {
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.price-option:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.price-selected {
  border-color: rgb(var(--v-theme-primary)) !important;
  background-color: rgba(var(--v-theme-primary), 0.05);
}

/* 配置摘要新样式 */
.config-summary {
  border-radius: 12px !important;
  overflow: hidden;
}

.bg-gradient {
  background: linear-gradient(135deg, rgb(var(--v-theme-primary)) 0%, rgb(var(--v-theme-secondary)) 100%);
}

.config-section {
  padding: 20px 24px;
  border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.08);
}

.config-section:last-child {
  border-bottom: none;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid rgba(var(--v-theme-primary), 0.1);
}

.config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.config-item-card {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: rgba(var(--v-theme-surface), 1);
  border: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  border-radius: 8px;
  transition: all 0.2s ease;
}

.config-item-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: rgba(var(--v-theme-primary), 0.3);
}

.config-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: rgba(var(--v-theme-surface-variant), 0.5);
  border-radius: 8px;
  margin-right: 12px;
  flex-shrink: 0;
}

.config-content {
  flex: 1;
  min-width: 0;
}

.config-label {
  font-size: 0.75rem;
  font-weight: 500;
  color: rgba(var(--v-theme-on-surface), 0.7);
  margin-bottom: 2px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.config-value {
  font-size: 0.875rem;
  font-weight: 600;
  color: rgba(var(--v-theme-on-surface), 0.9);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 价格选择器紧凑样式 */
.price-options-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.price-option-compact {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 16px;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.12);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: rgba(var(--v-theme-surface), 1);
  min-width: 100px;
  position: relative;
}

.price-option-compact:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-color: rgba(var(--v-theme-primary), 0.3);
}

.price-option-compact.price-selected {
  border-color: rgb(var(--v-theme-primary)) !important;
  background-color: rgba(var(--v-theme-primary), 0.05);
  box-shadow: 0 2px 8px rgba(var(--v-theme-primary), 0.2);
}

.price-period {
  font-size: 0.75rem;
  font-weight: 500;
  color: rgba(var(--v-theme-on-surface), 0.7);
  margin-bottom: 4px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.price-amount {
  font-size: 1.125rem;
  font-weight: 700;
  color: rgba(var(--v-theme-on-surface), 0.9);
  margin-bottom: 2px;
}

.price-unit {
  font-size: 0.6875rem;
  color: rgba(var(--v-theme-on-surface), 0.6);
  margin-bottom: 6px;
}

.price-discount {
  position: absolute;
  top: -6px;
  right: -6px;
}
</style>
