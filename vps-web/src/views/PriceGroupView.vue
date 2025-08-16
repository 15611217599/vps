<template>
  <PageLayout>
    <v-container class="py-6">
      <!-- 页面标题和添加按钮 -->
      <div class="d-flex justify-space-between align-center mb-6">
        <div>
          <h1 class="text-h4 font-weight-bold">{{ $t('priceGroup.title') }}</h1>
          <p class="text-subtitle-1 text-medium-emphasis mt-1">
            {{ $t('priceGroup.subtitle') }}
          </p>
        </div>
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="showAddDialog = true"
        >
          {{ $t('priceGroup.add') }}
        </v-btn>
      </div>

      <!-- 价格组列表 -->
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon class="me-2">mdi-currency-usd</v-icon>
        {{ $t('priceGroup.list') }}
        <v-spacer />
        <v-text-field
          v-model="searchQuery"
          :placeholder="$t('priceGroup.searchPlaceholder')"
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          density="compact"
          hide-details
          clearable
          style="max-width: 300px"
          @input="debouncedSearch"
        />
      </v-card-title>

      <v-data-table-server
        :headers="headers"
        :items="priceGroups"
        :loading="loading"
        :items-length="totalItems"
        :items-per-page="pageSize"
        :items-per-page-options="[5, 10, 20, 50]"
        :items-per-page-text="$t('common.itemsPerPage')"
        @update:options="handleOptionsUpdate"
      >
        <!-- 名称列 -->
        <template #item.name="{ item }">
          <div>
            <div class="font-weight-medium">{{ getLocalizedText(item.name) }}</div>
            <div class="text-caption text-medium-emphasis" v-if="item.description">
              {{ getLocalizedText(item.description) }}
            </div>
          </div>
        </template>

        <!-- 价格列 -->
        <template #item.prices="{ item }">
          <div class="text-body-2 d-flex flex-wrap ga-2">
            <v-chip size="small" variant="outlined" color="primary">
              {{ $t('priceGroup.hour') }}: {{ $t('priceGroup.priceUnit') }}{{ item.hourlyPrice }}
            </v-chip>
            <v-chip size="small" variant="outlined" color="success">
              {{ $t('priceGroup.day') }}: {{ $t('priceGroup.priceUnit') }}{{ item.dailyPrice }}
            </v-chip>
            <v-chip size="small" variant="outlined" color="info">
              {{ $t('priceGroup.month') }}: {{ $t('priceGroup.priceUnit') }}{{ item.monthlyPrice }}
            </v-chip>
            <v-chip size="small" variant="outlined" color="warning">
              {{ $t('priceGroup.quarter') }}: {{ $t('priceGroup.priceUnit') }}{{ item.quarterlyPrice }}
            </v-chip>
            <v-chip size="small" variant="outlined" color="orange">
              {{ $t('priceGroup.halfYear') }}: {{ $t('priceGroup.priceUnit') }}{{ item.semiAnnualPrice }}
            </v-chip>
            <v-chip size="small" variant="outlined" color="deep-purple">
              {{ $t('priceGroup.year') }}: {{ $t('priceGroup.priceUnit') }}{{ item.annualPrice }}
            </v-chip>
          </div>
        </template>

        <!-- 服务器分组列 -->
        <template #item.serverGroup="{ item }">
          <div v-if="item.serverGroupId">
            <v-chip size="small" variant="outlined" color="info">
              {{ getServerGroupName(item.serverGroupId) }}
            </v-chip>
          </div>
          <div v-else class="text-caption text-medium-emphasis">
            {{ $t('priceGroup.noServerGroup') }}
          </div>
        </template>

        <!-- 销售页面列 -->
        <template #item.salesPage="{ item }">
          <div v-if="item.salesPageHtml">
            <v-btn
              size="small"
              variant="outlined"
              color="primary"
              prepend-icon="mdi-code-tags"
              @click="previewSalesPage(item)"
            >
              {{ $t('priceGroup.preview') }}
            </v-btn>
          </div>
          <div v-else class="text-caption text-medium-emphasis">
            {{ $t('priceGroup.noSalesPage') }}
          </div>
        </template>

        <!-- 状态列 -->
        <template #item.isActive="{ item }">
          <v-chip
            :color="item.isActive ? 'success' : 'error'"
            size="small"
            variant="flat"
          >
            {{ item.isActive ? $t('priceGroup.active') : $t('priceGroup.inactive') }}
          </v-chip>
        </template>

        <!-- 操作列 -->
        <template #item.actions="{ item }">
          <v-btn
            icon="mdi-pencil"
            size="small"
            variant="text"
            @click="editPriceGroup(item)"
          />
          <v-btn
            icon="mdi-delete"
            size="small"
            variant="text"
            color="error"
            @click="deletePriceGroup(item)"
          />
        </template>
      </v-data-table-server>
    </v-card>

    <!-- 添加/编辑对话框 -->
    <UnifiedDialog
      v-model="showAddDialog"
      :title="editingItem ? $t('priceGroup.editDialog') : $t('priceGroup.addDialog')"
      :is-edit="!!editingItem"
      :loading="saving"
      :disabled="!formValid"
      max-width="650px"
      width="95vw"
      @save="savePriceGroup"
      @cancel="closeDialog"
    >
      <v-form ref="form" v-model="formValid">
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.name"
              :label="$t('priceGroup.name')"
              :rules="[rules.required]"
              variant="outlined"
              prepend-inner-icon="mdi-tag"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              required
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.sortOrder"
              :label="$t('priceGroup.sortOrder')"
              type="number"
              :rules="[rules.nonNegativeNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-sort-numeric-ascending"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              required
            />
          </v-col>
        </v-row>
        
        <v-row>
          <v-col cols="12">
            <v-textarea
              v-model="formData.description"
              :label="$t('priceGroup.description')"
              variant="outlined"
              prepend-inner-icon="mdi-text"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              rows="2"
              auto-grow
            />
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.hourlyPrice"
              :label="$t('priceGroup.hourlyPrice') + ' (' + $t('priceGroup.priceUnit') + ')'"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positiveNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-currency-usd"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              required
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.dailyPrice"
              :label="$t('priceGroup.dailyPrice') + ' (' + $t('priceGroup.priceUnit') + ')'"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positiveNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-currency-usd"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              required
            />
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.monthlyPrice"
              :label="$t('priceGroup.monthlyPrice') + ' (' + $t('priceGroup.priceUnit') + ')'"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positiveNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-currency-usd"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              required
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.quarterlyPrice"
              :label="$t('priceGroup.quarterlyPrice') + ' (' + $t('priceGroup.priceUnit') + ')'"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positiveNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-currency-usd"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              required
            />
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.semiAnnualPrice"
              :label="$t('priceGroup.semiAnnualPrice') + ' (' + $t('priceGroup.priceUnit') + ')'"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positiveNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-currency-usd"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              required
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.annualPrice"
              :label="$t('priceGroup.annualPrice') + ' (' + $t('priceGroup.priceUnit') + ')'"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positiveNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-currency-usd"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              required
            />
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12" md="6">
            <v-select
              v-model="formData.serverGroupId"
              :items="serverGroups"
              :label="$t('priceGroup.serverGroup')"
              :item-title="(item) => getLocalizedText(item.originalName || item.name)"
              item-value="id"
              variant="outlined"
              prepend-inner-icon="mdi-server-network"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              clearable
            >
              <template #item="{ props, item }">
                <v-list-item v-bind="props" :title="getLocalizedText(item.raw.originalName || item.raw.name)">
                </v-list-item>
              </template>
              <template #selection="{ item }">
                {{ getLocalizedText(item.raw.originalName || item.raw.name) }}
              </template>
            </v-select>
          </v-col>
          <v-col cols="12" md="6">
            <v-switch
              v-model="formData.isActive"
              :label="$t('common.status')"
              color="primary"
              class="mt-4"
            />
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12">
            <v-textarea
              v-model="formData.salesPageHtml"
              :label="$t('priceGroup.salesPageHtml')"
              variant="outlined"
              prepend-inner-icon="mdi-code-tags"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              rows="6"
              auto-grow
              :placeholder="$t('priceGroup.salesPageHtmlPlaceholder')"
            />
          </v-col>
        </v-row>
      </v-form>
    </UnifiedDialog>

    <!-- 销售页面预览对话框 -->
    <v-dialog
      v-model="showPreviewDialog"
      max-width="800px"
      scrollable
    >
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="me-2">mdi-eye</v-icon>
          {{ $t('priceGroup.salesPagePreview') }}
          <v-spacer />
          <v-btn
            icon="mdi-close"
            variant="text"
            @click="showPreviewDialog = false"
          />
        </v-card-title>
        <v-divider />
        <v-card-text class="pa-0">
          <div
            v-if="previewContent"
            v-html="previewContent"
            class="pa-4"
            style="min-height: 300px;"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="primary"
            variant="text"
            @click="showPreviewDialog = false"
          >
            {{ $t('common.close') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 删除确认对话框 -->
    <ConfirmDeleteDialog
      v-model="showDeleteDialog"
      :title="$t('priceGroup.deleteWarning')"
      :message="$t('priceGroup.confirmDelete', { name: deletingItem?.name })"
      :item-name="deletingItem?.name"
      :loading="deleting"
      @confirm="confirmDelete"
      @cancel="showDeleteDialog = false"
    />

    <!-- 通知组件 -->
    <NotificationSnackbar
      v-model="notificationState.show"
      :message="notificationState.message"
      :type="notificationState.type"
    />
    </v-container>
  </PageLayout>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { priceGroupApi } from '@/api/priceGroup'
import { serverGroupApi } from '@/api/serverGroup'
import PageLayout from '@/components/PageLayout.vue'
import UnifiedDialog from '@/components/UnifiedDialog.vue'
import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'
import NotificationSnackbar from '@/components/NotificationSnackbar.vue'
import { getLocalizedText } from '@/utils/i18n'
import { useNotification } from '@/composables/useNotification'

export default {
  name: 'PriceGroupView',
  components: {
    PageLayout,
    UnifiedDialog,
    ConfirmDeleteDialog,
    NotificationSnackbar
  },
  setup() {
    const { t } = useI18n()
    const { notificationState, showNotification } = useNotification()

    // 响应式数据
    const loading = ref(false)
    const saving = ref(false)
    const deleting = ref(false)
    const priceGroups = ref([])
    const serverGroups = ref([])
    const searchQuery = ref('')
    const showAddDialog = ref(false)
    const showDeleteDialog = ref(false)
    const showPreviewDialog = ref(false)
    const formValid = ref(false)
    const editingItem = ref(null)
    const deletingItem = ref(null)
    const previewContent = ref('')
    
    // 分页数据
    const currentPage = ref(1)
    const pageSize = ref(10)
    const totalItems = ref(0)
    
    // 表格头部
    const headers = [
      { title: computed(() => t('priceGroup.name')), key: 'name', sortable: false },
      { title: computed(() => t('priceGroup.prices')), key: 'prices', sortable: false },
      { title: computed(() => t('priceGroup.serverGroup')), key: 'serverGroup', sortable: false },
      { title: computed(() => t('priceGroup.salesPage')), key: 'salesPage', sortable: false },
      { title: computed(() => t('common.status')), key: 'isActive', sortable: false },
      { title: computed(() => t('priceGroup.sortOrder')), key: 'sortOrder', sortable: false },
      { title: computed(() => t('common.actions')), key: 'actions', sortable: false, width: 120 }
    ]
    
    // 表单数据
    const formData = reactive({
      name: '',
      description: '',
      hourlyPrice: '',
      dailyPrice: '',
      monthlyPrice: '',
      quarterlyPrice: '',
      semiAnnualPrice: '',
      annualPrice: '',
      sortOrder: 1,
      isActive: true,
      serverGroupId: null,
      salesPageHtml: ''
    })
    
    // 验证规则
    const rules = {
      required: value => (value !== null && value !== undefined && value !== '') || t('priceGroup.required'),
      positiveNumber: value => (value > 0) || t('priceGroup.positiveNumber'),
      nonNegativeNumber: value => (value >= 0) || t('priceGroup.nonNegativeNumber')
    }
    
    // 获取价格组列表
    const fetchPriceGroups = async () => {
      try {
        loading.value = true
        const params = {
          page: currentPage.value - 1,
          size: pageSize.value
        }
        
        if (searchQuery.value) {
          params.search = searchQuery.value
        }
        
        const response = await priceGroupApi.getPriceGroups(params)
        
        if (response.success) {
          priceGroups.value = response.data
          totalItems.value = response.totalElements
        } else {
          showNotification(response.message || t('priceGroup.loadError'), 'error')
        }
      } catch (error) {
        console.error('获取价格组列表失败:', error)
        showNotification(t('priceGroup.loadError'), 'error')
      } finally {
        loading.value = false
      }
    }
    
    // 获取服务器分组列表
    const fetchServerGroups = async () => {
      try {
        // 直接调用后端的服务器分组API
        const response = await serverGroupApi.getAllServerGroups()
        if (response.success) {
          serverGroups.value = response.data.map(group => ({
            id: group.id,
            name: group.name,
            originalName: group.name // 保存原始的多语言JSON数据
          }))
          console.log('服务器分组数据:', serverGroups.value)
        }
      } catch (error) {
        console.error('获取服务器分组列表失败:', error)
      }
    }
    
    // 搜索处理
    const handleSearch = () => {
      currentPage.value = 1
      fetchPriceGroups()
    }

    // 防抖搜索
    let searchTimeout
    const debouncedSearch = () => {
      clearTimeout(searchTimeout)
      searchTimeout = setTimeout(() => {
        handleSearch()
      }, 300)
    }
    
    // 服务器端数据表选项更新处理
    const handleOptionsUpdate = (options) => {
      const { page, itemsPerPage } = options
      
      let needsReload = false
      
      if (page !== currentPage.value) {
        currentPage.value = page
        needsReload = true
      }
      
      if (itemsPerPage !== pageSize.value) {
        pageSize.value = itemsPerPage
        currentPage.value = 1
        needsReload = true
      }
      
      if (needsReload) {
        fetchPriceGroups()
      }
    }
    
    // 重置表单
    const resetForm = () => {
      Object.assign(formData, {
        name: '',
        description: '',
        hourlyPrice: '',
        dailyPrice: '',
        monthlyPrice: '',
        quarterlyPrice: '',
        semiAnnualPrice: '',
        annualPrice: '',
        sortOrder: 1,
        isActive: true,
        serverGroupId: null,
        salesPageHtml: ''
      })
      editingItem.value = null
    }
    
    // 编辑价格组
    const editPriceGroup = (item) => {
      editingItem.value = item
      Object.assign(formData, {
        name: item.name,
        description: item.description || '',
        hourlyPrice: item.hourlyPrice,
        dailyPrice: item.dailyPrice,
        monthlyPrice: item.monthlyPrice,
        quarterlyPrice: item.quarterlyPrice,
        semiAnnualPrice: item.semiAnnualPrice,
        annualPrice: item.annualPrice,
        sortOrder: item.sortOrder,
        isActive: item.isActive,
        serverGroupId: item.serverGroupId || null,
        salesPageHtml: item.salesPageHtml || ''
      })
      showAddDialog.value = true
    }
    
    // 保存价格组
    const savePriceGroup = async () => {
      try {
        saving.value = true
        
        const data = {
          name: formData.name,
          description: formData.description,
          hourlyPrice: parseFloat(formData.hourlyPrice),
          dailyPrice: parseFloat(formData.dailyPrice),
          monthlyPrice: parseFloat(formData.monthlyPrice),
          quarterlyPrice: parseFloat(formData.quarterlyPrice),
          semiAnnualPrice: parseFloat(formData.semiAnnualPrice),
          annualPrice: parseFloat(formData.annualPrice),
          sortOrder: parseInt(formData.sortOrder),
          isActive: formData.isActive,
          serverGroupId: formData.serverGroupId,
          salesPageHtml: formData.salesPageHtml
        }
        
        let response
        if (editingItem.value) {
          response = await priceGroupApi.updatePriceGroup(editingItem.value.id, data)
        } else {
          response = await priceGroupApi.createPriceGroup(data)
        }
        
        if (response.success) {
          showNotification(editingItem.value ? t('priceGroup.updateSuccess') : t('priceGroup.createSuccess'), 'success')
          closeDialog()
          fetchPriceGroups()
        } else {
          showNotification(response.message || t('priceGroup.saveError'), 'error')
        }
      } catch (error) {
        console.error('保存价格组失败:', error)
        showNotification(t('priceGroup.saveError'), 'error')
      } finally {
        saving.value = false
      }
    }
    
    // 删除价格组
    const deletePriceGroup = (item) => {
      deletingItem.value = item
      showDeleteDialog.value = true
    }
    
    const confirmDelete = async () => {
      try {
        deleting.value = true
        const response = await priceGroupApi.deletePriceGroup(deletingItem.value.id)
        
        if (response.success) {
          showNotification(t('priceGroup.deleteSuccess'), 'success')
          showDeleteDialog.value = false
          fetchPriceGroups()
        } else {
          showNotification(response.message || t('priceGroup.deleteError'), 'error')
        }
      } catch (error) {
        console.error('删除价格组失败:', error)
        showNotification(t('priceGroup.deleteError'), 'error')
      } finally {
        deleting.value = false
      }
    }
    
    // 关闭对话框
    const closeDialog = () => {
      showAddDialog.value = false
      resetForm()
    }
    
    // 获取服务器分组名称
    const getServerGroupName = (serverGroupId) => {
      console.log('查找服务器分组ID:', serverGroupId)
      console.log('可用的服务器分组:', serverGroups.value)
      const group = serverGroups.value.find(g => g.id === serverGroupId)
      console.log('找到的分组:', group)
      if (group) {
        const localizedName = getLocalizedText(group.originalName || group.name)
        console.log('本地化名称:', localizedName)
        return localizedName
      }
      return `ID: ${serverGroupId}`
    }
    
    // 预览销售页面
    const previewSalesPage = (item) => {
      previewContent.value = item.salesPageHtml
      showPreviewDialog.value = true
    }
    
    
    // 组件挂载时获取数据
    onMounted(() => {
      fetchPriceGroups()
      fetchServerGroups()
    })
    
    return {
      loading,
      saving,
      deleting,
      priceGroups,
      searchQuery,
      showAddDialog,
      showDeleteDialog,
      formValid,
      editingItem,
      deletingItem,
      currentPage,
      pageSize,
      totalItems,
      headers,
      formData,
      rules,
      fetchPriceGroups,
      handleSearch,
      debouncedSearch,
      handleOptionsUpdate,
      editPriceGroup,
      savePriceGroup,
      deletePriceGroup,
      confirmDelete,
      closeDialog,
      notificationState,
      showNotification,
      getLocalizedText,
      serverGroups,
      fetchServerGroups,
      showPreviewDialog,
      previewContent,
      getServerGroupName,
      previewSalesPage
    }
  }
}
</script>

<style scoped>
.v-data-table {
  background: transparent;
}
</style>
