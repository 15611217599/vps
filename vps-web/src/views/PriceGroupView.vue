<template>
  <PageLayout :title="TEXTS.priceGroup.title">
    <v-container class="py-6">
      <!-- 价格组列表 -->
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon class="me-2">mdi-format-list-bulleted</v-icon>
        {{ TEXTS.priceGroup.list }}
        <v-spacer />
        <div class="d-flex align-center ga-3">
          <v-text-field
            v-model="searchQuery"
            :placeholder="TEXTS.priceGroup.searchPlaceholder"
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            hide-details
            clearable
            style="min-width: 250px; max-width: 400px"
            @input="debouncedSearch"
          />
          <v-btn
            color="primary"
            prepend-icon="mdi-plus"
            @click="showAddDialog = true"
          >
            {{ TEXTS.priceGroup.add }}
          </v-btn>
        </div>
      </v-card-title>

      <v-data-table-server
        :headers="headers"
        :items="priceGroups"
        :loading="loading"
        :items-length="totalItems"
        :items-per-page="pageSize"
        :items-per-page-options="[5, 10, 20, 50]"
        :items-per-page-text="TEXTS.common.itemsPerPage"
        class="price-group-table"
        @update:options="handleOptionsUpdate"
      >
        <!-- 名称列 -->
        <template #item.name="{ item }">
          <div>
            <div class="font-weight-medium">{{ item.name }}</div>
            <div class="text-caption text-medium-emphasis" v-if="item.description">
              {{ item.description }}
            </div>
          </div>
        </template>

        <!-- 价格列 -->
        <template #item.prices="{ item }">
          <div class="price-display-grid">
            <div class="price-grid">
              <!-- 第一行：短期价格 -->
              <div class="price-item" :class="{ 'has-discount': item.hasDiscount }">
                <div class="price-label">时</div>
                <div class="price-value">
                  <span class="current-price">¥{{ item.hasDiscount && item.discountPercentage ? calculateDiscountedPrice(item.hourlyPrice, item.discountPercentage) : item.hourlyPrice }}</span>
                  <span v-if="item.hasDiscount && item.discountPercentage" class="original-price-small">
                    ¥{{ item.hourlyPrice }}
                  </span>
                </div>
              </div>
              
              <div class="price-item" :class="{ 'has-discount': item.hasDiscount }">
                <div class="price-label">日</div>
                <div class="price-value">
                  <span class="current-price">¥{{ item.hasDiscount && item.discountPercentage ? calculateDiscountedPrice(item.dailyPrice, item.discountPercentage) : item.dailyPrice }}</span>
                  <span v-if="item.hasDiscount && item.discountPercentage" class="original-price-small">
                    ¥{{ item.dailyPrice }}
                  </span>
                </div>
              </div>
              
              <div class="price-item" :class="{ 'has-discount': item.hasDiscount }">
                <div class="price-label">月</div>
                <div class="price-value">
                  <span class="current-price">¥{{ item.hasDiscount && item.discountPercentage ? calculateDiscountedPrice(item.monthlyPrice, item.discountPercentage) : item.monthlyPrice }}</span>
                  <span v-if="item.hasDiscount && item.discountPercentage" class="original-price-small">
                    ¥{{ item.monthlyPrice }}
                  </span>
                </div>
              </div>
              
              <!-- 第二行：长期价格 -->
              <div class="price-item" :class="{ 'has-discount': item.hasDiscount }">
                <div class="price-label">季</div>
                <div class="price-value">
                  <span class="current-price">¥{{ item.hasDiscount && item.discountPercentage ? calculateDiscountedPrice(item.quarterlyPrice, item.discountPercentage) : item.quarterlyPrice }}</span>
                  <span v-if="item.hasDiscount && item.discountPercentage" class="original-price-small">
                    ¥{{ item.quarterlyPrice }}
                  </span>
                </div>
              </div>
              
              <div class="price-item" :class="{ 'has-discount': item.hasDiscount }">
                <div class="price-label">半年</div>
                <div class="price-value">
                  <span class="current-price">¥{{ item.hasDiscount && item.discountPercentage ? calculateDiscountedPrice(item.semiAnnualPrice, item.discountPercentage) : item.semiAnnualPrice }}</span>
                  <span v-if="item.hasDiscount && item.discountPercentage" class="original-price-small">
                    ¥{{ item.semiAnnualPrice }}
                  </span>
                </div>
              </div>
              
              <div class="price-item" :class="{ 'has-discount': item.hasDiscount }">
                <div class="price-label">年</div>
                <div class="price-value">
                  <span class="current-price">¥{{ item.hasDiscount && item.discountPercentage ? calculateDiscountedPrice(item.annualPrice, item.discountPercentage) : item.annualPrice }}</span>
                  <span v-if="item.hasDiscount && item.discountPercentage" class="original-price-small">
                    ¥{{ item.annualPrice }}
                  </span>
                </div>
              </div>
            </div>
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
            {{ TEXTS.priceGroup.noServerGroup }}
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
              {{ TEXTS.priceGroup.preview }}
            </v-btn>
          </div>
          <div v-else class="text-caption text-medium-emphasis">
            {{ TEXTS.priceGroup.noSalesPage }}
          </div>
        </template>

        <!-- 状态列 -->
        <template #item.isActive="{ item }">
          <v-chip
            :color="item.isActive ? 'success' : 'error'"
            size="small"
            variant="flat"
          >
            {{ item.isActive ? TEXTS.priceGroup.active : TEXTS.priceGroup.inactive }}
          </v-chip>
        </template>

        <!-- 操作列 -->
        <template #item.actions="{ item }">
          <div class="d-flex align-center ga-1">
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
          </div>
        </template>
      </v-data-table-server>
    </v-card>

    <!-- 添加/编辑对话框 -->
    <UnifiedDialog
      v-model="showAddDialog"
      :title="editingItem ? TEXTS.priceGroup.editDialog : TEXTS.priceGroup.addDialog"
      :is-edit="!!editingItem"
      :loading="saving"
      :disabled="!formValid"
      max-width="800px"
      width="85vw"
      :auto-width="false"
      @save="savePriceGroup"
      @cancel="closeDialog"
    >
      <v-form ref="form" v-model="formValid">
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.name"
              :label="TEXTS.priceGroup.name"
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
              :label="TEXTS.priceGroup.sortOrder"
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
              :label="TEXTS.priceGroup.description"
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
              :label="TEXTS.priceGroup.hourlyPrice + ' (' + TEXTS.priceGroup.priceUnit + ')'"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positiveNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-currency-cny"
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
              :label="TEXTS.priceGroup.dailyPrice + ' (' + TEXTS.priceGroup.priceUnit + ')'"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positiveNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-currency-cny"
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
              :label="TEXTS.priceGroup.monthlyPrice + ' (' + TEXTS.priceGroup.priceUnit + ')'"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positiveNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-currency-cny"
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
              :label="TEXTS.priceGroup.quarterlyPrice + ' (' + TEXTS.priceGroup.priceUnit + ')'"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positiveNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-currency-cny"
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
              :label="TEXTS.priceGroup.semiAnnualPrice + ' (' + TEXTS.priceGroup.priceUnit + ')'"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positiveNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-currency-cny"
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
              :label="TEXTS.priceGroup.annualPrice + ' (' + TEXTS.priceGroup.priceUnit + ')'"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positiveNumber]"
              variant="outlined"
              prepend-inner-icon="mdi-currency-cny"
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
              :label="TEXTS.priceGroup.serverGroup"
              :item-title="(item) => item.originalName || item.name"
              item-value="id"
              variant="outlined"
              prepend-inner-icon="mdi-server-network"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              clearable
              :menu-props="{ maxHeight: '300px', zIndex: 9999 }"
            >
              <template #item="{ props, item }">
                <v-list-item v-bind="props" :title="item.raw.originalName || item.raw.name">
                </v-list-item>
              </template>
              <template #selection="{ item }">
                {{ item.raw.originalName || item.raw.name }}
              </template>
            </v-select>
          </v-col>
          <v-col cols="12" md="6">
            <v-switch
              v-model="formData.isActive"
              :label="'状态'"
              color="primary"
              class="mt-4"
            />
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12">
            <v-textarea
              v-model="formData.salesPageHtml"
              :label="TEXTS.priceGroup.salesPageHtml"
              variant="outlined"
              prepend-inner-icon="mdi-code-tags"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
              rows="6"
              auto-grow
              :placeholder="TEXTS.priceGroup.salesPageHtmlPlaceholder"
            />
          </v-col>
        </v-row>

        <!-- 折扣设置部分 -->
        <v-divider class="my-4" />
        <v-row>
          <v-col cols="12">
            <h3 class="text-h6 mb-3">
              <v-icon class="me-2">mdi-percent</v-icon>
              折扣设置
            </h3>
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12" md="6">
            <v-switch
              v-model="formData.hasDiscount"
              label="启用折扣"
              color="primary"
              class="mt-2"
            />
          </v-col>
        </v-row>

        <v-row v-if="formData.hasDiscount">
          <v-col cols="12" md="4">
            <v-text-field
              v-model="formData.discountPercentage"
              label="折扣百分比 (%)"
              type="number"
              step="0.1"
              :rules="discountPercentageRules"
              variant="outlined"
              prepend-inner-icon="mdi-percent"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="formData.discountStartTime"
              label="开始时间 (可选)"
              type="datetime-local"
              variant="outlined"
              prepend-inner-icon="mdi-calendar-start"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="formData.discountEndTime"
              label="结束时间 (可选)"
              type="datetime-local"
              variant="outlined"
              prepend-inner-icon="mdi-calendar-end"
              color="primary"
              density="comfortable"
              class="mb-2"
              bg-color="white"
            />
          </v-col>
        </v-row>

        <!-- 折扣预览 -->
        <v-row v-if="formData.hasDiscount && formData.discountPercentage">
          <v-col cols="12">
            <v-card variant="outlined" class="discount-preview-card">
              <v-card-title class="text-h6 pb-3 bg-orange-lighten-5">
                <v-icon class="me-2" color="orange">mdi-sale</v-icon>
                折扣预览 ({{ formData.discountPercentage }}% 折扣)
              </v-card-title>
              <v-card-text class="pa-4">
                <v-row>
                  <!-- 短期价格 -->
                  <v-col cols="12" md="6">
                    <div class="preview-section">
                      <h4 class="text-subtitle-1 mb-3 text-blue-darken-2">
                        <v-icon size="18" class="me-1">mdi-clock-fast</v-icon>
                        短期价格
                      </h4>
                      <div class="preview-item">
                        <div class="price-label">小时价格</div>
                        <div class="price-comparison">
                          <span class="original-price">¥{{ formData.hourlyPrice }}</span>
                          <v-icon size="14" class="mx-2 text-grey">mdi-arrow-right</v-icon>
                          <span class="discounted-price">¥{{ calculateFormDiscountedPrice(formData.hourlyPrice) }}</span>
                        </div>
                      </div>
                      <div class="preview-item">
                        <div class="price-label">日价格</div>
                        <div class="price-comparison">
                          <span class="original-price">¥{{ formData.dailyPrice }}</span>
                          <v-icon size="14" class="mx-2 text-grey">mdi-arrow-right</v-icon>
                          <span class="discounted-price">¥{{ calculateFormDiscountedPrice(formData.dailyPrice) }}</span>
                        </div>
                      </div>
                      <div class="preview-item">
                        <div class="price-label">月价格</div>
                        <div class="price-comparison">
                          <span class="original-price">¥{{ formData.monthlyPrice }}</span>
                          <v-icon size="14" class="mx-2 text-grey">mdi-arrow-right</v-icon>
                          <span class="discounted-price">¥{{ calculateFormDiscountedPrice(formData.monthlyPrice) }}</span>
                        </div>
                      </div>
                    </div>
                  </v-col>
                  
                  <!-- 长期价格 -->
                  <v-col cols="12" md="6">
                    <div class="preview-section">
                      <h4 class="text-subtitle-1 mb-3 text-green-darken-2">
                        <v-icon size="18" class="me-1">mdi-calendar-range</v-icon>
                        长期价格
                      </h4>
                      <div class="preview-item">
                        <div class="price-label">季度价格</div>
                        <div class="price-comparison">
                          <span class="original-price">¥{{ formData.quarterlyPrice }}</span>
                          <v-icon size="14" class="mx-2 text-grey">mdi-arrow-right</v-icon>
                          <span class="discounted-price">¥{{ calculateFormDiscountedPrice(formData.quarterlyPrice) }}</span>
                        </div>
                      </div>
                      <div class="preview-item">
                        <div class="price-label">半年价格</div>
                        <div class="price-comparison">
                          <span class="original-price">¥{{ formData.semiAnnualPrice }}</span>
                          <v-icon size="14" class="mx-2 text-grey">mdi-arrow-right</v-icon>
                          <span class="discounted-price">¥{{ calculateFormDiscountedPrice(formData.semiAnnualPrice) }}</span>
                        </div>
                      </div>
                      <div class="preview-item">
                        <div class="price-label">年价格</div>
                        <div class="price-comparison">
                          <span class="original-price">¥{{ formData.annualPrice }}</span>
                          <v-icon size="14" class="mx-2 text-grey">mdi-arrow-right</v-icon>
                          <span class="discounted-price">¥{{ calculateFormDiscountedPrice(formData.annualPrice) }}</span>
                        </div>
                      </div>
                    </div>
                  </v-col>
                </v-row>
                
                <!-- 折扣信息提示 -->
                <v-divider class="my-3" />
                <div class="discount-info text-center">
                  <v-chip size="small" color="success" variant="flat" class="me-2">
                    <v-icon start size="16">mdi-check-circle</v-icon>
                    节省 {{ formData.discountPercentage }}%
                  </v-chip>
                  <span class="text-caption text-grey-darken-1">
                    以上为折扣后的实际价格
                  </span>
                </div>
              </v-card-text>
            </v-card>
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
          {{ TEXTS.priceGroup.salesPagePreview }}
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
            {{ '关闭' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>



    <!-- 删除确认对话框 -->
    <ConfirmDeleteDialog
      v-model="showDeleteDialog"
      :title="TEXTS.priceGroup.deleteWarning"
      :message="`确定要删除价格组 '${deletingItem?.name || ''}' 吗？`"
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

import { priceGroupApi } from '@/api/priceGroup'
import { serverGroupApi } from '@/api/serverGroup'
import PageLayout from '@/components/PageLayout.vue'
import UnifiedDialog from '@/components/UnifiedDialog.vue'
import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'
import NotificationSnackbar from '@/components/NotificationSnackbar.vue'

import { useNotification } from '@/composables/useNotification'
import { TEXTS } from '@/constants/texts'

export default {
  name: 'PriceGroupView',
  components: {
    PageLayout,
    UnifiedDialog,
    ConfirmDeleteDialog,
    NotificationSnackbar
  },
  setup() {
    // 移除国际化
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
      { title: TEXTS.priceGroup.name, key: 'name', sortable: false, width: 200 },
      { title: TEXTS.priceGroup.prices, key: 'prices', sortable: false, width: 350 },
      { title: TEXTS.priceGroup.serverGroup, key: 'serverGroup', sortable: false, width: 120 },
      { title: '销售页面', key: 'salesPage', sortable: false, width: 100 },
      { title: '状态', key: 'isActive', sortable: false, width: 80 },
      { title: '操作', key: 'actions', sortable: false, width: 100 }
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
      salesPageHtml: '',
      // 折扣相关字段
      hasDiscount: false,
      discountPercentage: '',
      discountStartTime: '',
      discountEndTime: ''
    })


    
    // 验证规则
    const rules = {
      required: value => (value !== null && value !== undefined && value !== '') || TEXTS.priceGroup.required,
      positiveNumber: value => (value > 0) || TEXTS.priceGroup.positiveNumber,
      nonNegativeNumber: value => (value >= 0) || TEXTS.priceGroup.nonNegativeNumber
    }

    // 折扣验证规则（只有启用折扣时才验证）
    const discountPercentageRules = computed(() => {
      if (!formData.hasDiscount) return []
      return [
        value => (value !== null && value !== undefined && value !== '') || '折扣百分比不能为空',
        value => (value > 0) || '折扣百分比必须大于0',
        value => (value <= 100) || '折扣百分比不能超过100%'
      ]
    })
    
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
          showNotification(response.message || TEXTS.priceGroup.loadError, 'error')
        }
      } catch (error) {
        console.error('获取价格组列表失败:', error)
        showNotification(TEXTS.priceGroup.loadError, 'error')
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
        salesPageHtml: '',
        // 重置折扣字段
        hasDiscount: false,
        discountPercentage: '',
        discountStartTime: '',
        discountEndTime: ''
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
        salesPageHtml: item.salesPageHtml || '',
        // 填充折扣数据
        hasDiscount: item.hasDiscount || false,
        discountPercentage: item.discountPercentage || '',
        discountStartTime: item.discountStartTime ? item.discountStartTime.slice(0, 16) : '',
        discountEndTime: item.discountEndTime ? item.discountEndTime.slice(0, 16) : ''
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
          salesPageHtml: formData.salesPageHtml,
          // 折扣相关数据
          hasDiscount: formData.hasDiscount,
          discountPercentage: formData.hasDiscount && formData.discountPercentage ? parseFloat(formData.discountPercentage) : null,
          discountStartTime: formData.hasDiscount && formData.discountStartTime ? new Date(formData.discountStartTime).toISOString() : null,
          discountEndTime: formData.hasDiscount && formData.discountEndTime ? new Date(formData.discountEndTime).toISOString() : null
        }
        
        let response
        if (editingItem.value) {
          response = await priceGroupApi.updatePriceGroup(editingItem.value.id, data)
        } else {
          response = await priceGroupApi.createPriceGroup(data)
        }
        
        if (response.success) {
          showNotification(editingItem.value ? TEXTS.priceGroup.updateSuccess : TEXTS.priceGroup.createSuccess, 'success')
          closeDialog()
          fetchPriceGroups()
        } else {
          showNotification(response.message || TEXTS.priceGroup.saveError, 'error')
        }
      } catch (error) {
        console.error('保存价格组失败:', error)
        showNotification(TEXTS.priceGroup.saveError, 'error')
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
          showNotification(TEXTS.priceGroup.deleteSuccess, 'success')
          showDeleteDialog.value = false
          fetchPriceGroups()
        } else {
          showNotification(response.message || TEXTS.priceGroup.deleteError, 'error')
        }
      } catch (error) {
        console.error('删除价格组失败:', error)
        showNotification(TEXTS.priceGroup.deleteError, 'error')
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
        const localizedName = group.originalName || group.name
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



    // 计算折扣后价格（配置的是原价）
    const calculateDiscountedPrice = (originalPrice, discountPercentage) => {
      if (!originalPrice || !discountPercentage) return originalPrice
      
      const discountRate = parseFloat(discountPercentage) / 100
      const discountedPrice = originalPrice * (1 - discountRate)
      return discountedPrice.toFixed(2)
    }

    // 计算表单中的折扣价格（用于预览）
    const calculateFormDiscountedPrice = (originalPrice) => {
      if (!formData.discountPercentage || !originalPrice) return originalPrice
      
      const discountPercentage = parseFloat(formData.discountPercentage)
      return (originalPrice * (1 - discountPercentage / 100)).toFixed(2)
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

      serverGroups,
      fetchServerGroups,
      showPreviewDialog,
      previewContent,
      getServerGroupName,
      previewSalesPage,

      // 折扣管理相关
      discountPercentageRules,
      calculateDiscountedPrice,
      calculateFormDiscountedPrice,
      
      // 添加文本常量
      TEXTS
    }
  }
}
</script>

<style scoped>
.price-display-grid {
  min-width: 320px;
  max-width: 320px;
}

.price-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  padding: 10px;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.02) 0%, rgba(0, 0, 0, 0.01) 100%);
  border-radius: 10px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.price-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 6px 4px;
  background: white;
  border-radius: 6px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  min-height: 45px;
  justify-content: center;
  transition: all 0.2s ease;
  position: relative;
}

.price-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-color: #1976d2;
}

.price-item:nth-child(1) { border-left: 3px solid #2196f3; }
.price-item:nth-child(2) { border-left: 3px solid #4caf50; }
.price-item:nth-child(3) { border-left: 3px solid #ff9800; }
.price-item:nth-child(4) { border-left: 3px solid #9c27b0; }
.price-item:nth-child(5) { border-left: 3px solid #00bcd4; }
.price-item:nth-child(6) { border-left: 3px solid #f44336; }

.price-label {
  font-size: 0.7rem;
  font-weight: 600;
  color: #666;
  margin-bottom: 2px;
  text-align: center;
  min-height: 12px;
}

.price-value {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.current-price {
  font-size: 0.75rem;
  font-weight: 700;
  color: #1976d2;
  line-height: 1.2;
}

.original-price-small {
  font-size: 0.6rem;
  color: #999;
  text-decoration: line-through;
  margin-top: 1px;
  line-height: 1;
}

/* 有折扣时的价格样式 */
.price-item.has-discount {
  background: linear-gradient(135deg, #fff3e0 0%, #ffffff 100%);
  border-color: #ff9800;
}

.price-item.has-discount .current-price {
  color: #d32f2f;
  font-weight: 800;
}

.price-item.has-discount::after {
  content: '折';
  position: absolute;
  top: -2px;
  right: -2px;
  background: #ff5722;
  color: white;
  font-size: 0.5rem;
  font-weight: bold;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

/* 折扣相关样式已移除，保持简洁 */



.original-price {
  font-size: 0.5rem;
  color: #666;
  text-decoration: line-through;
  margin-left: 4px;
}

/* 折扣预览样式 */
.discount-preview-card {
  border: 2px solid #ff9800 !important;
  border-radius: 12px !important;
  overflow: hidden;
}

.discount-preview-card :deep(.v-card-title) {
  border-bottom: 1px solid rgba(255, 152, 0, 0.2);
  font-weight: 600;
}

.preview-section {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  padding: 16px;
  border: 1px solid rgba(0, 0, 0, 0.08);
}

.preview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.preview-item:last-child {
  border-bottom: none;
}

.price-label {
  font-weight: 500;
  color: #424242;
  min-width: 80px;
}

.price-comparison {
  display: flex;
  align-items: center;
  gap: 8px;
}

.original-price {
  color: #757575;
  text-decoration: line-through;
  font-size: 0.9rem;
}

.discounted-price {
  color: #d32f2f;
  font-weight: 700;
  font-size: 1rem;
}

.discount-info {
  background: rgba(76, 175, 80, 0.1);
  border-radius: 8px;
  padding: 12px;
  margin-top: 8px;
}

/* 确保价格列有足够的宽度和垂直对齐 */
:deep(.v-data-table__td) {
  vertical-align: top;
  padding: 8px 12px;
}

/* 价格列特殊处理 */
:deep(.v-data-table__td:nth-child(2)) {
  min-width: 350px;
  max-width: 350px;
  padding: 12px 8px;
}

/* 名称列处理 */
:deep(.v-data-table__td:nth-child(1)) {
  min-width: 200px;
  max-width: 200px;
}

/* 表格整体样式优化 */
.price-group-table :deep(.v-data-table) {
  font-size: 0.875rem;
}

.price-group-table :deep(.v-data-table__th) {
  font-weight: 600;
  font-size: 0.875rem;
  white-space: nowrap;
}

.price-group-table :deep(.v-data-table__wrapper) {
  overflow-x: auto;
}

.price-group-table :deep(.v-data-table__tr:hover) {
  background-color: rgba(0, 0, 0, 0.02);
}

.price-group-table :deep(.v-data-table__td) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .price-display-grid {
    min-width: 300px;
    max-width: 300px;
  }
  
  .price-grid {
    gap: 6px;
    padding: 6px;
  }
  
  .price-item {
    min-height: 42px;
    padding: 5px 3px;
  }
  
  .current-price {
    font-size: 0.7rem;
  }
  
  :deep(.v-data-table__td:nth-child(2)) {
    min-width: 300px;
    max-width: 300px;
  }
}

@media (max-width: 768px) {
  .price-display-grid {
    min-width: 280px;
    max-width: 280px;
  }
  
  .price-grid {
    gap: 6px;
    padding: 6px;
  }
  
  .price-item {
    padding: 4px 2px;
    min-height: 40px;
  }
  
  .price-label {
    font-size: 0.65rem;
  }
  
  .current-price {
    font-size: 0.7rem;
  }
  
  .original-price-small {
    font-size: 0.55rem;
  }
  
  :deep(.v-data-table__td:nth-child(2)) {
    min-width: 280px;
    max-width: 280px;
  }
  
  :deep(.v-data-table__td:nth-child(1)) {
    min-width: 150px;
    max-width: 150px;
  }
  
  /* 折扣预览移动端样式 */
  .preview-section {
    padding: 12px;
    margin-bottom: 16px;
  }
  
  .preview-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
    padding: 6px 0;
  }
  
  .price-comparison {
    width: 100%;
    justify-content: space-between;
  }
  
  .price-label {
    font-size: 0.9rem;
    min-width: auto;
  }
  
  .original-price {
    font-size: 0.8rem;
  }
  
  .discounted-price {
    font-size: 0.95rem;
  }
  
  .discount-info {
    padding: 8px;
  }
}
</style>
