<template>
  <PageLayout>
    <template #header>
      <div class="d-flex justify-space-between align-center">
        <div>
          <h1 class="text-h4 font-weight-bold">{{ $t('servers.category') }}</h1>
          <p class="text-subtitle-1 text-medium-emphasis mt-1">
            {{ $t('servers.title') }}
          </p>
        </div>
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="showAddDialog = true"
        >
          {{ $t('common.add') }}
        </v-btn>
      </div>
    </template>

    <!-- 类别列表 -->
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon class="me-2">mdi-folder-multiple</v-icon>
        {{ $t('servers.category') }}
        <v-spacer />
        <v-text-field
          v-model="searchQuery"
          :placeholder="$t('common.search')"
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          density="compact"
          hide-details
          clearable
          style="max-width: 300px"
          @input="handleSearch"
        />
      </v-card-title>

      <v-data-table
        :headers="headers"
        :items="categories"
        :loading="loading"
        :items-per-page="pageSize"
        :page="currentPage"
        :server-items-length="totalItems"
        @update:page="handlePageChange"
        @update:items-per-page="handlePageSizeChange"
      >
        <!-- 名称列 -->
        <template #item.name="{ item }">
          <div>
            <div class="font-weight-medium">{{ item.name }}</div>
            <div class="text-caption text-medium-emphasis">
              {{ item.description }}
            </div>
          </div>
        </template>

        <!-- 状态列 -->
        <template #item.isActive="{ item }">
          <v-chip
            :color="item.isActive ? 'success' : 'error'"
            size="small"
            variant="flat"
          >
            {{ item.isActive ? $t('common.yes') : $t('common.no') }}
          </v-chip>
        </template>

        <!-- 统计列 -->
        <template #item.stats="{ item }">
          <div class="text-center">
            <div class="text-body-2 font-weight-medium">
              {{ item.groupCount }} / {{ item.serverCount }}
            </div>
            <div class="text-caption text-medium-emphasis">
              {{ $t('categories.groupsServers') }}
            </div>
          </div>
        </template>

        <!-- 操作列 -->
        <template #item.actions="{ item }">
          <v-btn
            icon="mdi-pencil"
            size="small"
            variant="text"
            @click="editCategory(item)"
          />
          <v-btn
            icon="mdi-delete"
            size="small"
            variant="text"
            color="error"
            @click="deleteCategory(item)"
          />
        </template>
      </v-data-table>
    </v-card>

    <!-- 添加/编辑对话框 -->
    <v-dialog v-model="showAddDialog" max-width="600px">
      <v-card>
        <v-card-title>
          {{ editingCategory ? $t('common.edit') : $t('common.add') }}{{ $t('servers.category') }}
        </v-card-title>
        
        <v-card-text>
          <v-form ref="form" v-model="formValid">
            <v-row>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="categoryForm.name"
                  :label="$t('categories.categoryName')"
                  :rules="[rules.required]"
                  variant="outlined"
                  required
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="categoryForm.sortOrder"
                  :label="$t('categories.sort')"
                  type="number"
                  variant="outlined"
                />
              </v-col>
            </v-row>

            <v-textarea
              v-model="categoryForm.description"
              :label="$t('categories.description')"
              variant="outlined"
              rows="3"
            />

            <!-- 国际化字段 -->
            <v-expansion-panels class="mt-4">
              <v-expansion-panel>
                <v-expansion-panel-title>
                  <v-icon class="me-2">mdi-translate</v-icon>
                  {{ $t('categories.multiLanguageSettings') }}
                </v-expansion-panel-title>
                <v-expansion-panel-text>
                  <v-row>
                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="categoryForm.nameZh"
                        :label="$t('categories.chineseName')"
                        variant="outlined"
                      />
                    </v-col>
                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="categoryForm.nameEn"
                        :label="$t('categories.englishName')"
                        variant="outlined"
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col cols="12" md="6">
                      <v-textarea
                        v-model="categoryForm.descriptionZh"
                        :label="$t('categories.chineseDescription')"
                        variant="outlined"
                        rows="2"
                      />
                    </v-col>
                    <v-col cols="12" md="6">
                      <v-textarea
                        v-model="categoryForm.descriptionEn"
                        :label="$t('categories.englishDescription')"
                        variant="outlined"
                        rows="2"
                      />
                    </v-col>
                  </v-row>
                </v-expansion-panel-text>
              </v-expansion-panel>
            </v-expansion-panels>

            <v-switch
              v-model="categoryForm.isActive"
              :label="$t('categories.enableStatus')"
              color="primary"
              class="mt-4"
            />
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn @click="closeDialog">{{ $t('common.cancel') }}</v-btn>
          <v-btn
            color="primary"
            :loading="saving"
            :disabled="!formValid"
            @click="saveCategory"
          >
            {{ $t('common.save') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import PageLayout from '@/components/PageLayout.vue'
import { getLocalizedText } from '@/utils/i18n'
import {
  getLocalizedActiveCategories,
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory as deleteCategoryApi,
  type ServerCategory
} from '@/api/category'

const { t } = useI18n()

// 响应式数据
const categories = ref<ServerCategory[]>([])
const loading = ref(false)
const saving = ref(false)
const showAddDialog = ref(false)
const formValid = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const editingCategory = ref<ServerCategory | null>(null)

// 表单数据
const categoryForm = reactive({
  name: '',
  description: '',
  nameZh: '',
  nameEn: '',
  descriptionZh: '',
  descriptionEn: '',
  sortOrder: 0,
  isActive: true
})

// 表格头部
const headers = computed(() => [
  { title: t('servers.category'), key: 'name', sortable: false },
  { title: t('servers.status'), key: 'isActive', sortable: false, width: 100 },
  { title: t('categories.statistics'), key: 'stats', sortable: false, width: 120 },
  { title: t('servers.actions'), key: 'actions', sortable: false, width: 120 }
])

// 表单验证规则
const rules = {
  required: (value: string) => !!value || t('validation.required')
}

// 加载数据
const loadCategories = async () => {
  try {
    loading.value = true
    
    if (searchQuery.value) {
      // 搜索模式，使用分页API
      const response = await getCategories({
        page: currentPage.value - 1,
        size: pageSize.value,
        search: searchQuery.value
      })
      categories.value = response.data
      totalItems.value = response.totalElements
    } else {
      // 获取本地化的类别数据
      const data = await getLocalizedActiveCategories()
      categories.value = data
      totalItems.value = data.length
    }
  } catch (error) {
    console.error(t('categories.loadCategoriesFailed'), error)
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadCategories()
}

// 分页处理
const handlePageChange = (page: number) => {
  currentPage.value = page
  loadCategories()
}

const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadCategories()
}

// 编辑类别
const editCategory = (category: ServerCategory) => {
  editingCategory.value = category
  Object.assign(categoryForm, {
    name: category.name,
    description: category.description || '',
    nameZh: category.nameZh || '',
    nameEn: category.nameEn || '',
    descriptionZh: category.descriptionZh || '',
    descriptionEn: category.descriptionEn || '',
    sortOrder: category.sortOrder || 0,
    isActive: category.isActive
  })
  showAddDialog.value = true
}

// 删除类别
const deleteCategory = async (category: ServerCategory) => {
  const localizedName = getLocalizedText(category.name)
  if (confirm(t('categories.confirmDeleteCategory', { name: localizedName }))) {
    try {
      await deleteCategoryApi(category.id)
      await loadCategories()
    } catch (error) {
      console.error(t('categories.deleteCategoryFailed'), error)
      // 显示详细的后端错误信息
      if (error && typeof error === 'object' && 'message' in error) {
        alert(error.message)
      }
    }
  }
}

// 保存类别
const saveCategory = async () => {
  try {
    saving.value = true
    
    if (editingCategory.value) {
      await updateCategory(editingCategory.value.id, categoryForm)
    } else {
      await createCategory(categoryForm)
    }
    
    closeDialog()
    await loadCategories()
  } catch (error) {
    console.error(t('categories.saveCategoryFailed'), error)
  } finally {
    saving.value = false
  }
}

// 关闭对话框
const closeDialog = () => {
  showAddDialog.value = false
  editingCategory.value = null
  Object.assign(categoryForm, {
    name: '',
    description: '',
    nameZh: '',
    nameEn: '',
    descriptionZh: '',
    descriptionEn: '',
    sortOrder: 0,
    isActive: true
  })
}

// 组件挂载时加载数据
onMounted(() => {
  loadCategories()
})
</script>