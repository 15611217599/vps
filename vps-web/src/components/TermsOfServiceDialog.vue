<template>
  <v-dialog v-model="show" max-width="800px" scrollable>
    <v-card>
      <v-card-title class="d-flex align-center pa-6">
        <v-icon class="me-3" color="primary" size="28">mdi-file-document</v-icon>
        <span class="text-h5">服务条款</span>
        <v-spacer />
        <v-btn icon variant="text" @click="close">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      
      <v-card-text class="pa-6" style="max-height: 70vh;">
        <div class="terms-content">
          <div class="mb-6">
            <p class="text-body-1 mb-4">
              <strong>生效日期：</strong>2025年1月1日<br>
              <strong>最后更新：</strong>{{ new Date().toLocaleDateString('zh-CN') }}
            </p>
            <p class="text-body-1">
              欢迎使用RabbitVPS服务。通过访问或使用我们的服务，您同意遵守以下服务条款。
            </p>
          </div>

          <v-divider class="my-6" />

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-handshake</v-icon>
              1. 服务协议
            </h3>
            <div class="ml-8">
              <p class="text-body-2 mb-2"><strong>服务内容：</strong></p>
              <ul class="text-body-2">
                <li>提供云服务器租赁服务</li>
                <li>提供技术支持和维护服务</li>
                <li>提供相关的网络基础设施</li>
                <li>提供用户管理平台</li>
              </ul>
            </div>
          </div>

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-account-circle</v-icon>
              2. 用户责任
            </h3>
            <div class="ml-8">
              <p class="text-body-2 mb-2"><strong>用户承诺：</strong></p>
              <ul class="text-body-2">
                <li>提供真实、准确的注册信息</li>
                <li>妥善保管账户信息和密码</li>
                <li>合法使用服务，不从事违法活动</li>
                <li>不滥用服务器资源</li>
                <li>及时支付服务费用</li>
                <li>遵守相关法律法规</li>
              </ul>
            </div>
          </div>

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-cancel</v-icon>
              3. 禁止行为
            </h3>
            <div class="ml-8">
              <p class="text-body-2 mb-2"><strong>严禁以下行为：</strong></p>
              <ul class="text-body-2">
                <li>发送垃圾邮件或进行网络攻击</li>
                <li>传播病毒、恶意软件</li>
                <li>侵犯他人知识产权</li>
                <li>从事非法交易或欺诈活动</li>
                <li>违反当地法律法规的内容</li>
                <li>未经授权访问他人系统</li>
              </ul>
            </div>
          </div>

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-credit-card</v-icon>
              4. 付费与退款
            </h3>
            <div class="ml-8">
              <p class="text-body-2 mb-2"><strong>付费政策：</strong></p>
              <ul class="text-body-2">
                <li>服务费用按照选择的计费周期收取</li>
                <li>支持多种支付方式</li>
                <li>逾期未付费可能导致服务暂停</li>
              </ul>
              <p class="text-body-2 mb-2 mt-4"><strong>退款政策：</strong></p>
              <ul class="text-body-2">
                <li>服务开通后7天内可申请退款</li>
                <li>因违规行为导致的服务终止不予退款</li>
                <li>退款将在5-10个工作日内处理</li>
              </ul>
            </div>
          </div>

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-server</v-icon>
              5. 服务可用性
            </h3>
            <div class="ml-8">
              <p class="text-body-2 mb-2"><strong>服务保障：</strong></p>
              <ul class="text-body-2">
                <li>我们承诺99.9%的服务可用性</li>
                <li>定期进行系统维护和升级</li>
                <li>提供7x24小时技术支持</li>
                <li>不可抗力因素除外</li>
              </ul>
            </div>
          </div>

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-backup-restore</v-icon>
              6. 数据备份
            </h3>
            <div class="ml-8">
              <p class="text-body-2 mb-2"><strong>备份责任：</strong></p>
              <ul class="text-body-2">
                <li>用户有责任备份重要数据</li>
                <li>我们提供基础的系统备份服务</li>
                <li>不保证数据100%不丢失</li>
                <li>建议用户定期下载重要数据</li>
              </ul>
            </div>
          </div>

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-gavel</v-icon>
              7. 责任限制
            </h3>
            <div class="ml-8">
              <p class="text-body-2 mb-2"><strong>免责声明：</strong></p>
              <ul class="text-body-2">
                <li>我们不对用户的间接损失承担责任</li>
                <li>服务中断造成的损失由用户自行承担</li>
                <li>第三方服务问题不在我们责任范围内</li>
                <li>用户违规使用导致的后果由用户承担</li>
              </ul>
            </div>
          </div>

          <div class="section mb-6">
            <h3 class="text-h6 mb-3 d-flex align-center">
              <v-icon class="me-2" color="primary">mdi-update</v-icon>
              8. 条款修改
            </h3>
            <div class="ml-8">
              <p class="text-body-2">
                我们保留随时修改本服务条款的权利。重要修改将提前通知用户。
                继续使用服务即表示接受修改后的条款。
              </p>
            </div>
          </div>

          <v-divider class="my-6" />

          <div class="contact-section text-center">
            <h3 class="text-h6 mb-3">联系我们</h3>
            <p class="text-body-2 mb-4">
              如果您对本服务条款有任何疑问，请联系我们：
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
.terms-content {
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