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
            ¥{{ wallet.balance.toFixed(2) }}
          </div>
          <div class="text-caption text-medium-emphasis">
            当前余额
          </div>
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
import { useRouter } from 'vue-router'
import { walletApi, type Wallet } from '@/api/wallet'

const router = useRouter()

const wallet = ref<Wallet | null>(null)
const loading = ref(false)
const creating = ref(false)

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString()
}

const loadWallet = async () => {
  try {
    loading.value = true
    wallet.value = await walletApi.getWallet()
  } catch (error) {
    console.error('Failed to load wallet:', error)
    wallet.value = null
  } finally {
    loading.value = false
  }
}

// 暴露刷新方法给父组件
defineExpose({
  loadWallet
})

const createWallet = async () => {
  try {
    creating.value = true
    wallet.value = await walletApi.createWallet()
    showSnackbar('钱包创建成功')
  } catch (error) {
    console.error('Failed to create wallet:', error)
    showSnackbar('钱包创建失败')
  } finally {
    creating.value = false
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
