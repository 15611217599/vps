<template>
  <v-card class="wallet-card" elevation="2">
    <v-card-title class="d-flex align-center">
      <v-icon class="me-2" color="primary">mdi-wallet</v-icon>
      钱包
    </v-card-title>
    
    <v-card-text>
      <div v-if="loading" class="text-center py-4">
        <v-progress-circular indeterminate color="primary"></v-progress-circular>
      </div>
      
      <div v-else-if="wallet">
        <div class="balance-section mb-4">
          <div class="text-h4 font-weight-bold text-primary">
            {{ formatCurrency(wallet.balance, wallet.currency) }}
          </div>
          <div class="text-caption text-medium-emphasis">
            当前余额
          </div>
        </div>
        
        <v-row>
          <v-col cols="12" sm="6">
            <v-text-field
              v-model="newBalance"
              label="新余额"
              type="number"
              step="0.01"
              variant="outlined"
              density="compact"
              :disabled="updating"
            />
          </v-col>
          <v-col cols="12" sm="6">
            <v-select
              v-model="selectedCurrency"
              :items="currencies"
              label="货币"
              variant="outlined"
              density="compact"
              :disabled="updating"
            />
          </v-col>
        </v-row>
        
        <div class="d-flex gap-2 mt-3">
          <v-btn
            color="primary"
            :loading="updating"
            @click="updateBalance"
            :disabled="!newBalance || newBalance === wallet.balance"
          >
            更新余额
          </v-btn>
          
          <v-btn
            color="secondary"
            variant="outlined"
            :loading="updating"
            @click="updateCurrency"
            :disabled="selectedCurrency === wallet.currency"
          >
            更新货币
          </v-btn>
        </div>
        
        <div class="mt-4 text-caption text-medium-emphasis">
          最后更新: {{ formatDate(wallet.updatedAt) }}
        </div>
      </div>
      
      <div v-else class="text-center py-4">
        <v-icon size="48" color="grey" class="mb-2">mdi-wallet-outline</v-icon>
        <div class="text-body-1 mb-3">暂无钱包</div>
        <v-btn color="primary" @click="createWallet" :loading="creating">
          创建钱包
        </v-btn>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { walletApi, type Wallet } from '@/api/wallet'

const wallet = ref<Wallet | null>(null)
const loading = ref(false)
const updating = ref(false)
const creating = ref(false)
const newBalance = ref<number | null>(null)
const selectedCurrency = ref('USD')

const currencies = [
  { title: 'USD - 美元', value: 'USD' },
  { title: 'CNY - 人民币', value: 'CNY' },
  { title: 'EUR - 欧元', value: 'EUR' },
  { title: 'JPY - 日元', value: 'JPY' },
  { title: 'GBP - 英镑', value: 'GBP' }
]

const formatCurrency = (amount: number, currency: string) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: currency
  }).format(amount)
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString()
}

const loadWallet = async () => {
  try {
    loading.value = true
    wallet.value = await walletApi.getWallet()
    newBalance.value = wallet.value.balance
    selectedCurrency.value = wallet.value.currency
  } catch (error) {
    console.error('Failed to load wallet:', error)
    wallet.value = null
  } finally {
    loading.value = false
  }
}

const createWallet = async () => {
  try {
    creating.value = true
    wallet.value = await walletApi.createWallet(selectedCurrency.value)
    newBalance.value = wallet.value.balance
    selectedCurrency.value = wallet.value.currency
    showSnackbar('钱包创建成功')
  } catch (error) {
    console.error('Failed to create wallet:', error)
    showSnackbar('钱包创建失败')
  } finally {
    creating.value = false
  }
}

const updateBalance = async () => {
  if (!newBalance.value || !wallet.value) return
  
  try {
    updating.value = true
    wallet.value = await walletApi.updateBalance(newBalance.value)
    showSnackbar('余额更新成功')
  } catch (error) {
    console.error('Failed to update balance:', error)
    showSnackbar('更新失败')
  } finally {
    updating.value = false
  }
}

const updateCurrency = async () => {
  if (!wallet.value) return
  
  try {
    updating.value = true
    wallet.value = await walletApi.updateCurrency(selectedCurrency.value)
    showSnackbar('货币更新成功')
  } catch (error) {
    console.error('Failed to update currency:', error)
    showSnackbar('更新失败')
  } finally {
    updating.value = false
  }
}

const showSnackbar = (message: string) => {
  console.log(message) // 临时使用console.log，实际应该使用全局通知组件
}

onMounted(() => {
  loadWallet()
})
</script>

<style scoped>
.wallet-card {
  background: rgb(var(--v-theme-surface));
}

.balance-section {
  text-align: center;
  padding: 16px;
  background: rgba(var(--v-theme-primary), 0.1);
  border-radius: 8px;
}
</style>
