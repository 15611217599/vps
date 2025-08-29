<template>
  <UnifiedDialog
    v-model="show"
    title="安装系统"
    max-width="600"
    @confirm="startInstall"
    :loading="installing"
  >
    <v-form ref="form" v-model="formValid">
      <v-row>
        <v-col cols="12" md="6">
          <v-select
            v-model="installData.osName"
            :items="operatingSystems"
            label="操作系统"
            :rules="[v => !!v || '请选择操作系统']"
            required
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-select
            v-model="installData.osVersion"
            :items="getOsVersions(installData.osName)"
            label="系统版本"
            :disabled="!installData.osName"
          />
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12" md="6">
          <v-text-field
            v-model="installData.rootPassword"
            label="Root密码"
            placeholder="默认: 123@@@"
            type="password"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            v-model="installData.sshPort"
            label="SSH端口"
            placeholder="默认: 22"
            type="number"
          />
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <v-textarea
            v-model="installData.sshKey"
            label="SSH公钥 (可选)"
            placeholder="ssh-rsa AAAAB3NzaC1yc2E... 或 github:username"
            rows="3"
          />
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12" md="6">
          <v-select
            v-model="installData.installType"
            :items="installTypes"
            label="安装类型"
          />
        </v-col>
        <v-col cols="12" md="6" v-if="installData.osName === 'ubuntu'">
          <v-switch
            v-model="installData.minimal"
            label="最小化安装"
            color="primary"
          />
        </v-col>
      </v-row>

      <v-row v-if="installData.installType === 'DD'">
        <v-col cols="12">
          <v-text-field
            v-model="installData.customImageUrl"
            label="自定义镜像URL"
            placeholder="https://example.com/image.xz"
            :rules="installData.installType === 'DD' ? [v => !!v || '请输入镜像URL'] : []"
          />
        </v-col>
      </v-row>
    </v-form>
  </UnifiedDialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { installApi, type ServerInstallRequest } from '@/api/install'
import { useNotification } from '@/composables/useNotification'
import UnifiedDialog from './UnifiedDialog.vue'

interface Props {
  modelValue: boolean
  serverId?: number
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'installed'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()
const { showSuccess, showError } = useNotification()

// 数据
const installing = ref(false)
const formValid = ref(false)
const form = ref()

const installData = ref<ServerInstallRequest>({
  serverId: 0,
  osName: '',
  osVersion: '',
  rootPassword: '123@@@',
  sshKey: '',
  sshPort: 22,
  webPort: 80,
  installType: 'REINSTALL',
  minimal: false
})

// 计算属性
const show = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 操作系统选项
const operatingSystems = [
  'ubuntu', 'centos', 'debian', 'fedora', 'rocky', 'almalinux',
  'oracle', 'opensuse', 'alpine', 'arch', 'gentoo', 'kali'
]

// 安装类型选项
const installTypes = [
  { title: '重装系统', value: 'REINSTALL' },
  { title: 'DD镜像', value: 'DD' },
  { title: 'Alpine Live OS', value: 'ALPINE_LIVE' }
]

// 系统版本映射
const osVersions: Record<string, string[]> = {
  ubuntu: ['16.04', '18.04', '20.04', '22.04', '24.04', '25.04'],
  centos: ['9', '10'],
  debian: ['9', '10', '11', '12', '13'],
  fedora: ['41', '42'],
  rocky: ['8', '9', '10'],
  almalinux: ['8', '9', '10'],
  oracle: ['8', '9'],
  opensuse: ['15.6', 'tumbleweed'],
  alpine: ['3.19', '3.20', '3.21', '3.22']
}

const getOsVersions = (osName: string) => {
  return osVersions[osName] || []
}

// 方法
const startInstall = async () => {
  if (!formValid.value) return

  installing.value = true
  try {
    installData.value.serverId = props.serverId!
    await installApi.startInstall(installData.value)
    
    showSuccess('系统安装已开始')
    show.value = false
    emit('installed')
    
  } catch (error) {
    showError('开始安装失败')
  } finally {
    installing.value = false
  }
}

// 监听服务器ID变化
watch(() => props.serverId, (newId) => {
  if (newId) {
    installData.value.serverId = newId
  }
})

// 重置表单
watch(show, (newShow) => {
  if (newShow) {
    // 重置表单数据
    installData.value = {
      serverId: props.serverId || 0,
      osName: '',
      osVersion: '',
      rootPassword: '123@@@',
      sshKey: '',
      sshPort: 22,
      webPort: 80,
      installType: 'REINSTALL',
      minimal: false
    }
  }
})
</script>