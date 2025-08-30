<template>
  <v-dialog v-model="show" max-width="800px" scrollable>
    <v-card>
      <v-card-title class="d-flex align-center pa-6">
        <v-icon class="me-3" color="primary" size="28">mdi-shield-check</v-icon>
        <span class="text-h5">隐私政策</span>
        <v-spacer />
        <v-btn icon variant="text" @click="close">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      
      <v-card-text class="pa-6" style="max-height: 70vh;">
        <div class="privacy-content">
          <div class="mb-6">
            <p class="text-body-1 mb-4">
              <strong>生效日期：</strong>2025年1月1日<br>
              <strong>最后更新：</strong>{{ new Date().toLocaleDateString('zh-CN') }}
            </p>
            <p class="text-body-1">
              RabbitVPS（以下简称"我们"）非常重视用户的隐私保护。本隐私政策说明了我们如何收集、使用、存储和保护您的个人信息。
            </p>
          </div>

          <v-divider class="my-6" />

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-information</v-icon>
              1. 信息收集
            </h3>
            <div class="ml-8">
              <p class="text-body-2 mb-2"><strong>我们可能收集以下信息：</strong></p>
              <ul class="text-body-2">
                <li>账户信息：用户名、邮箱地址</li>
                <li>服务使用信息：服务器配置、使用记录</li>
                <li>技术信息：IP地址、浏览器类型、操作系统</li>
                <li>交易信息：订单记录、支付信息</li>
              </ul>
            </div>
          </div>

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-cog</v-icon>
              2. 信息使用
            </h3>
            <div class="ml-8">
              <p class="text-body-2 mb-2"><strong>我们使用收集的信息用于：</strong></p>
              <ul class="text-body-2">
                <li>提供和维护我们的服务</li>
                <li>处理您的订单和支付</li>
                <li>改善用户体验和服务质量</li>
                <li>发送重要的服务通知</li>
                <li>防止欺诈和滥用行为</li>
              </ul>
            </div>
          </div>

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-shield-lock</v-icon>
              3. 信息保护
            </h3>
            <div class="ml-8">
              <p class="text-body-2 mb-2"><strong>我们采取以下措施保护您的信息：</strong></p>
              <ul class="text-body-2">
                <li>使用加密技术保护数据传输</li>
                <li>实施严格的访问控制</li>
                <li>定期进行安全审计</li>
                <li>员工签署保密协议</li>
              </ul>
            </div>
          </div>

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-share</v-icon>
              4. 信息共享
            </h3>
            <div class="ml-8">
              <p class="text-body-2 mb-2"><strong>我们不会出售、交易或转让您的个人信息，除非：</strong></p>
              <ul class="text-body-2">
                <li>获得您的明确同意</li>
                <li>法律法规要求</li>
                <li>保护我们的合法权益</li>
                <li>与可信的第三方服务提供商合作（如支付处理）</li>
              </ul>
            </div>
          </div>

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-account-check</v-icon>
              5. 用户权利
            </h3>
            <div class="ml-8">
              <p class="text-body-2 mb-2"><strong>您有权：</strong></p>
              <ul class="text-body-2">
                <li>访问和更新您的个人信息</li>
                <li>删除您的账户和相关数据</li>
                <li>选择退出某些数据收集</li>
                <li>获取数据处理的详细信息</li>
              </ul>
            </div>
          </div>

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-update</v-icon>
              6. 政策更新
            </h3>
            <div class="ml-8">
              <p class="text-body-2">
                我们可能会不时更新本隐私政策。重大变更将通过邮件或网站通知您。
                继续使用我们的服务即表示您接受更新后的政策。
              </p>
            </div>
          </div>

          <v-divider class="my-6" />

          <div class="contact-section text-center">
            <h3 class="text-h6 mb-3">联系我们</h3>
            <p class="text-body-2 mb-4">
              如果您对本隐私政策有任何疑问，请通过以下方式联系我们：
            </p>
            <div class="contact-info">
              <p class="text-body-2">
                <v-icon class="me-2">mdi-email</v-icon>
                邮箱：rabbitvps@163.com
              </p>
              <p class="text-body-2">
                <v-icon class="me-2">mdi-wechat</v-icon>
                微信：rabbitvps
              </p>
            </div>
          </div>
        </div>
      </v-card-text>
      
      <v-card-actions class="pa-6">
        <v-spacer />
        <v-btn color="primary" variant="flat" @click="close">
          我已了解
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  modelValue: boolean
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const show = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const close = () => {
  show.value = false
}
</script>

<style scoped>
.privacy-content {
  line-height: 1.6;
}

.section {
  margin-bottom: 2rem;
}

.section ul {
  padding-left: 1.5rem;
}

.section li {
  margin-bottom: 0.5rem;
}

.contact-info {
  display: flex;
  justify-content: center;
  gap: 2rem;
  flex-wrap: wrap;
}

@media (max-width: 600px) {
  .contact-info {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>