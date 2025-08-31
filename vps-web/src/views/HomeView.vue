<template>
  <GuestLayout>
    <div class="homepage">
      <!-- Hero Section -->
      <section class="hero-section">
        <v-container class="hero-container">
          <v-row align="center" class="min-height-screen">
            <v-col cols="12" md="6" class="hero-content">
              <div class="hero-text">
                <h1 class="hero-title">
                  专业云服务器解决方案
                  <span class="hero-highlight">为您的业务赋能</span>
                </h1>
                <p class="hero-subtitle">
                  高性能、高可靠、高安全的云服务器服务。99.9%可用性保障，7x24技术支持，助力您的数字化转型。
                </p>
                <div class="hero-features">
                  <div class="feature-item">
                    <v-icon color="success" size="20">mdi-check-circle</v-icon>
                    <span>99.9% SLA保障</span>
                  </div>
                  <div class="feature-item">
                    <v-icon color="success" size="20">mdi-check-circle</v-icon>
                    <span>秒级部署</span>
                  </div>
                  <div class="feature-item">
                    <v-icon color="success" size="20">mdi-check-circle</v-icon>
                    <span>全球数据中心</span>
                  </div>
                </div>
                <div class="hero-actions">
                  <v-btn
                    size="large"
                    color="primary"
                    variant="flat"
                    class="me-4 hero-btn-primary"
                    @click="scrollToPlans"
                  >
                    <v-icon start>mdi-rocket-launch</v-icon>
                    立即开始
                  </v-btn>
                  <v-btn
                    size="large"
                    variant="outlined"
                    color="primary"
                    class="hero-btn-secondary"
                    @click="goToSales"
                  >
                    <v-icon start>mdi-view-grid</v-icon>
                    查看产品
                  </v-btn>
                </div>
              </div>
            </v-col>
            <v-col cols="12" md="6" class="hero-visual">
              <div class="server-illustration">
                <v-card class="server-card" elevation="8">
                  <div class="server-visual">
                    <div class="server-rack">
                      <div class="server-unit" v-for="i in 4" :key="i">
                        <div class="server-lights">
                          <div class="light active"></div>
                          <div class="light active"></div>
                          <div class="light"></div>
                        </div>
                        <div class="server-label">Server {{ i }}</div>
                      </div>
                    </div>
                    <div class="stats-overlay">
                      <div class="stat-item">
                        <v-icon color="success">mdi-server</v-icon>
                        <span>99.9% 在线</span>
                      </div>
                      <div class="stat-item">
                        <v-icon color="primary">mdi-speedometer</v-icon>
                        <span>< 50ms 延迟</span>
                      </div>
                    </div>
                  </div>
                </v-card>
              </div>
            </v-col>
          </v-row>
        </v-container>
      </section>

      <!-- Features Section -->
      <section class="features-section">
        <v-container>
          <div class="section-header text-center mb-12">
            <h2 class="section-title">为什么选择我们？</h2>
            <p class="section-subtitle">专业的技术实力，可靠的服务保障</p>
          </div>
          <v-row>
            <v-col cols="12" md="4" v-for="feature in keyFeatures" :key="feature.title">
              <v-card class="feature-card h-100" elevation="2" hover>
                <v-card-text class="text-center pa-6">
                  <div class="feature-icon mb-4">
                    <v-avatar size="64" :color="feature.color" variant="tonal">
                      <v-icon size="32" :color="feature.color">{{ feature.icon }}</v-icon>
                    </v-avatar>
                  </div>
                  <h3 class="feature-title mb-3">{{ feature.title }}</h3>
                  <p class="feature-description">{{ feature.description }}</p>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </section>


      <!-- Stats Section -->
      <section class="stats-section">
        <v-container>
          <v-row>
            <v-col cols="6" md="3" v-for="stat in stats" :key="stat.label">
              <div class="stat-card text-center">
                <div class="stat-number">{{ stat.number }}</div>
                <div class="stat-label">{{ stat.label }}</div>
              </div>
            </v-col>
          </v-row>
        </v-container>
      </section>

      <!-- Technology Section -->
      <section class="technology-section">
        <v-container>
          <div class="section-header text-center mb-12">
            <h2 class="section-title">先进的技术架构</h2>
            <p class="section-subtitle">采用最新技术，确保服务稳定可靠</p>
          </div>
          <v-row>
            <v-col cols="12" md="6" v-for="tech in technologies" :key="tech.title">
              <div class="tech-item">
                <div class="tech-icon">
                  <v-icon :color="tech.color" size="40">{{ tech.icon }}</v-icon>
                </div>
                <div class="tech-content">
                  <h4 class="tech-title">{{ tech.title }}</h4>
                  <p class="tech-description">{{ tech.description }}</p>
                </div>
              </div>
            </v-col>
          </v-row>
        </v-container>
      </section>

      <!-- Testimonials Section -->
      <section class="testimonials-section">
        <v-container>
          <div class="section-header text-center mb-12">
            <h2 class="section-title">客户见证</h2>
            <p class="section-subtitle">超过10,000+企业的信赖选择</p>
          </div>
          <v-row>
            <v-col cols="12" md="4" v-for="testimonial in testimonials" :key="testimonial.name">
              <v-card class="testimonial-card h-100" elevation="2">
                <v-card-text class="pa-6">
                  <div class="testimonial-content mb-4">
                    <v-icon color="primary" size="24" class="quote-icon">mdi-format-quote-open</v-icon>
                    <p class="testimonial-text">{{ testimonial.content }}</p>
                  </div>
                  <div class="testimonial-author">
                    <v-avatar size="48" :color="testimonial.avatarColor" class="me-3">
                      <span class="text-h6">{{ testimonial.name.charAt(0) }}</span>
                    </v-avatar>
                    <div>
                      <div class="author-name">{{ testimonial.name }}</div>
                      <div class="author-title">{{ testimonial.title }}</div>
                    </div>
                  </div>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </section>

      <!-- CTA Section -->
      <section class="cta-section">
        <v-container>
          <v-card class="cta-card" elevation="8">
            <v-card-text class="text-center pa-12">
              <h2 class="cta-title mb-4">准备开始您的云端之旅？</h2>
              <p class="cta-subtitle mb-6">立即体验专业的云服务器服务，享受7天免费试用</p>
              <div class="cta-actions">
                <v-btn
                  size="x-large"
                  color="primary"
                  variant="flat"
                  class="me-4 cta-btn-primary"
                  @click="startFreeTrial"
                >
                  <v-icon start>mdi-gift</v-icon>
                  免费试用
                </v-btn>
                <v-btn
                  size="x-large"
                  variant="outlined"
                  color="primary"
                  class="cta-btn-secondary"
                  @click="contactSales"
                >
                  <v-icon start>mdi-phone</v-icon>
                  联系销售
                </v-btn>
              </div>
            </v-card-text>
          </v-card>
        </v-container>
      </section>
    </div>

    <!-- Contact Dialog -->
    <ContactDialog v-model="showContactDialog" />
  </GuestLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import GuestLayout from '@/components/GuestLayout.vue'
import ContactDialog from '@/components/ContactDialog.vue'

const router = useRouter()
const showContactDialog = ref(false)

// Key Features Data
const keyFeatures = ref([
  {
    icon: 'mdi-lightning-bolt',
    color: 'primary',
    title: '极速部署',
    description: '30秒内完成服务器部署，即开即用，无需等待。'
  },
  {
    icon: 'mdi-shield-check',
    color: 'success',
    title: '安全可靠',
    description: '企业级安全防护，DDoS防护，数据加密传输。'
  },
  {
    icon: 'mdi-chart-line',
    color: 'info',
    title: '弹性扩展',
    description: '根据业务需求随时调整配置，支持灵活升级。'
  },
  {
    icon: 'mdi-headset',
    color: 'warning',
    title: '专业支持',
    description: '7x24小时技术支持，平均响应时间5分钟。'
  },
  {
    icon: 'mdi-earth',
    color: 'purple',
    title: '全球覆盖',
    description: '遍布全球的数据中心，就近接入，低延迟访问。'
  },
  {
    icon: 'mdi-currency-cny',
    color: 'teal',
    title: '性价比高',
    description: '透明定价，按需付费，无隐藏费用。'
  }
])


// Statistics Data
const stats = ref([
  { number: '10,000+', label: '活跃用户' },
  { number: '99.9%', label: '服务可用性' },
  { number: '24/7', label: '技术支持' },
  { number: '15+', label: '全球数据中心' }
])

// Technology Data
const technologies = ref([
  {
    icon: 'mdi-cloud',
    color: 'primary',
    title: '云原生架构',
    description: '基于Kubernetes的容器化部署，支持微服务架构。'
  },
  {
    icon: 'mdi-security',
    color: 'success',
    title: '多层安全防护',
    description: 'WAF防火墙、DDoS防护、入侵检测系统。'
  },
  {
    icon: 'mdi-backup-restore',
    color: 'info',
    title: '智能备份',
    description: '自动化备份策略，支持增量备份和快照恢复。'
  },
  {
    icon: 'mdi-monitor-dashboard',
    color: 'warning',
    title: '实时监控',
    description: '全面的性能监控和告警系统。'
  }
])

// Testimonials Data
const testimonials = ref([
  {
    name: '张总',
    title: '科技公司CEO',
    content: '使用RabbitVPS已经两年了，服务稳定可靠，技术支持响应及时，是我们业务发展的重要支撑。',
    avatarColor: 'primary'
  },
  {
    name: '李工程师',
    title: '技术负责人',
    content: '部署速度很快，配置灵活，监控功能强大。特别是自动扩容功能，帮我们节省了很多运维成本。',
    avatarColor: 'success'
  },
  {
    name: '王经理',
    title: '电商平台运营',
    content: '在双11期间，服务器表现非常稳定，没有出现任何宕机情况，为我们的业务保驾护航。',
    avatarColor: 'info'
  }
])

// Methods
const scrollToPlans = () => {
  // Scroll to stats section instead since pricing section was removed
  const statsSection = document.querySelector('.stats-section')
  if (statsSection) {
    statsSection.scrollIntoView({ behavior: 'smooth' })
  }
}

const goToSales = () => {
  router.push('/sales')
}

const startFreeTrial = () => {
  router.push('/auth')
}

const contactSales = () => {
  showContactDialog.value = true
}
</script>

<style scoped>
.homepage {
  overflow-x: hidden;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.1) 0%, rgba(var(--v-theme-secondary), 0.05) 100%);
  position: relative;
  overflow: hidden;
}

.hero-container {
  position: relative;
  z-index: 1;
}

.min-height-screen {
  min-height: 80vh;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 1.5rem;
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.hero-highlight {
  display: block;
  background: linear-gradient(45deg, rgb(var(--v-theme-primary)), rgb(var(--v-theme-secondary)));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.25rem;
  color: rgba(var(--v-theme-on-surface), 0.7);
  margin-bottom: 2rem;
  line-height: 1.6;
}

.hero-features {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  margin-bottom: 2.5rem;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.hero-btn-primary, .hero-btn-secondary {
  padding: 0 2rem;
  font-weight: 600;
}

/* Server Illustration */
.server-illustration {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.server-card {
  width: 300px;
  background: rgba(var(--v-theme-surface), 0.95);
  backdrop-filter: blur(10px);
}

.server-visual {
  padding: 2rem;
  position: relative;
}

.server-rack {
  background: #2c3e50;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1rem;
}

.server-unit {
  background: #34495e;
  margin-bottom: 0.5rem;
  padding: 0.75rem;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.server-unit:last-child {
  margin-bottom: 0;
}

.server-lights {
  display: flex;
  gap: 0.25rem;
}

.light {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #7f8c8d;
}

.light.active {
  background: #2ecc71;
  box-shadow: 0 0 8px rgba(46, 204, 113, 0.6);
}

.server-label {
  color: #ecf0f1;
  font-size: 0.75rem;
  font-weight: 500;
}

.stats-overlay {
  display: flex;
  justify-content: space-between;
  background: rgba(var(--v-theme-surface), 0.9);
  border-radius: 6px;
  padding: 0.75rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
}

/* Sections */
.features-section, .technology-section {
  padding: 5rem 0;
}

.stats-section {
  padding: 3rem 0;
  background: rgba(var(--v-theme-primary), 0.05);
}

.cta-section {
  padding: 5rem 0;
  background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.1) 0%, rgba(var(--v-theme-secondary), 0.05) 100%);
}

/* Section Headers */
.section-header {
  max-width: 600px;
  margin: 0 auto;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.section-subtitle {
  font-size: 1.125rem;
  color: rgba(var(--v-theme-on-surface), 0.7);
  line-height: 1.6;
}

/* Feature Cards */
.feature-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.1);
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(var(--v-theme-on-surface), 0.15) !important;
}

.feature-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.feature-description {
  color: rgba(var(--v-theme-on-surface), 0.7);
  line-height: 1.6;
}


/* Stats */
.stat-card {
  padding: 1.5rem;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  color: rgb(var(--v-theme-primary));
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 1rem;
  color: rgba(var(--v-theme-on-surface), 0.7);
  font-weight: 500;
}

/* Technology */
.tech-item {
  display: flex;
  align-items: flex-start;
  gap: 1.5rem;
  padding: 1.5rem 0;
}

.tech-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.tech-description {
  color: rgba(var(--v-theme-on-surface), 0.7);
  line-height: 1.6;
  margin: 0;
}

/* Testimonials */
.testimonials-section {
  padding: 5rem 0;
  background: rgba(var(--v-theme-surface), 0.3);
}

.testimonial-card {
  border: 1px solid rgba(var(--v-theme-on-surface), 0.1);
  transition: transform 0.3s ease;
}

.testimonial-card:hover {
  transform: translateY(-4px);
}

.testimonial-text {
  font-style: italic;
  color: rgba(var(--v-theme-on-surface), 0.8);
  line-height: 1.6;
  margin: 0;
}

.quote-icon {
  opacity: 0.3;
  margin-bottom: 1rem;
}

.testimonial-author {
  display: flex;
  align-items: center;
}

.author-name {
  font-weight: 600;
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.author-title {
  font-size: 0.875rem;
  color: rgba(var(--v-theme-on-surface), 0.6);
}

/* CTA Section */
.cta-card {
  background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.05) 0%, rgba(var(--v-theme-secondary), 0.02) 100%);
  border: 1px solid rgba(var(--v-theme-primary), 0.2);
}

.cta-title {
  font-size: 2.25rem;
  font-weight: 700;
  color: rgba(var(--v-theme-on-surface), 0.87);
}

.cta-subtitle {
  font-size: 1.125rem;
  color: rgba(var(--v-theme-on-surface), 0.7);
  line-height: 1.6;
}

.cta-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.cta-btn-primary, .cta-btn-secondary {
  padding: 0 2.5rem;
  font-weight: 600;
}

/* Responsive Design */
@media (max-width: 960px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .hero-actions {
    justify-content: center;
  }
  
  .cta-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .cta-btn-primary, .cta-btn-secondary {
    width: 100%;
    max-width: 300px;
  }
}

@media (max-width: 600px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-features {
    justify-content: center;
  }
  
  .server-card {
    width: 250px;
  }
}
</style>
