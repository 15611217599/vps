<template>
  <div class="brand-showcase pa-6">
    <v-container>
      <v-row>
        <v-col cols="12">
          <h1 class="text-h3 mb-6">RabbitVPS 品牌展示</h1>
        </v-col>
      </v-row>
      
      <!-- Logo展示 -->
      <v-row class="mb-8">
        <v-col cols="12">
          <v-card class="pa-6">
            <h2 class="text-h4 mb-4">品牌Logo</h2>
            
            <!-- 不同尺寸的Logo -->
            <v-row class="mb-6">
              <v-col cols="12" md="4">
                <div class="demo-section">
                  <h3 class="text-h6 mb-3">小尺寸</h3>
                  <div class="logo-demo" style="background: linear-gradient(135deg, #00E5FF 0%, #3F51B5 100%);">
                    <BrandLogo size="small" />
                  </div>
                </div>
              </v-col>
              
              <v-col cols="12" md="4">
                <div class="demo-section">
                  <h3 class="text-h6 mb-3">中等尺寸</h3>
                  <div class="logo-demo" style="background: linear-gradient(135deg, #00E5FF 0%, #3F51B5 100%);">
                    <BrandLogo size="medium" />
                  </div>
                </div>
              </v-col>
              
              <v-col cols="12" md="4">
                <div class="demo-section">
                  <h3 class="text-h6 mb-3">大尺寸</h3>
                  <div class="logo-demo" style="background: linear-gradient(135deg, #00E5FF 0%, #3F51B5 100%);">
                    <BrandLogo size="large" />
                  </div>
                </div>
              </v-col>
            </v-row>
            
            <!-- 仅图标版本 -->
            <v-row>
              <v-col cols="12">
                <h3 class="text-h6 mb-3">仅图标版本</h3>
                <div class="d-flex align-center gap-4">
                  <div class="icon-demo" style="background: linear-gradient(135deg, #00E5FF 0%, #3F51B5 100%);">
                    <BrandIcon size="small" />
                  </div>
                  <div class="icon-demo" style="background: linear-gradient(135deg, #00E5FF 0%, #3F51B5 100%);">
                    <BrandIcon size="medium" />
                  </div>
                  <div class="icon-demo" style="background: linear-gradient(135deg, #00E5FF 0%, #3F51B5 100%);">
                    <BrandIcon size="large" />
                  </div>
                </div>
              </v-col>
            </v-row>
          </v-card>
        </v-col>
      </v-row>
      
      <!-- 颜色系统 -->
      <v-row class="mb-8">
        <v-col cols="12">
          <v-card class="pa-6">
            <h2 class="text-h4 mb-4">品牌色彩</h2>
            
            <v-row>
              <v-col cols="12" md="6">
                <h3 class="text-h6 mb-3">主色调</h3>
                <div class="color-palette">
                  <div class="color-item">
                    <div class="color-swatch" :style="{ backgroundColor: BRAND_COLORS.primary }"></div>
                    <div class="color-info">
                      <div class="color-name">Primary</div>
                      <div class="color-value">{{ BRAND_COLORS.primary }}</div>
                    </div>
                  </div>
                  <div class="color-item">
                    <div class="color-swatch" :style="{ backgroundColor: BRAND_COLORS.primaryDark }"></div>
                    <div class="color-info">
                      <div class="color-name">Primary Dark</div>
                      <div class="color-value">{{ BRAND_COLORS.primaryDark }}</div>
                    </div>
                  </div>
                </div>
              </v-col>
              
              <v-col cols="12" md="6">
                <h3 class="text-h6 mb-3">兔子主题色</h3>
                <div class="color-palette">
                  <div class="color-item" v-for="(color, key) in BRAND_COLORS.rabbit" :key="key">
                    <div class="color-swatch" :style="{ backgroundColor: color }"></div>
                    <div class="color-info">
                      <div class="color-name">Rabbit {{ key }}</div>
                      <div class="color-value">{{ color }}</div>
                    </div>
                  </div>
                </div>
              </v-col>
            </v-row>
            
            <v-row class="mt-4">
              <v-col cols="12">
                <h3 class="text-h6 mb-3">渐变色</h3>
                <div class="gradient-demos">
                  <div class="gradient-item" v-for="(gradient, key) in BRAND_COLORS.gradients" :key="key">
                    <div class="gradient-swatch" :style="{ background: gradient }"></div>
                    <div class="gradient-name">{{ key }}</div>
                  </div>
                </div>
              </v-col>
            </v-row>
          </v-card>
        </v-col>
      </v-row>
      
      <!-- 使用示例 -->
      <v-row>
        <v-col cols="12">
          <v-card class="pa-6">
            <h2 class="text-h4 mb-4">使用示例</h2>
            
            <!-- 模拟顶部栏 -->
            <div class="usage-example mb-4">
              <h3 class="text-h6 mb-3">顶部导航栏</h3>
              <div class="mock-topbar" style="background: linear-gradient(135deg, #00E5FF 0%, #3F51B5 100%);">
                <BrandLogo />
                <v-spacer />
                <v-btn variant="text" color="white">用户菜单</v-btn>
              </div>
            </div>
            
            <!-- 模拟卡片 -->
            <div class="usage-example">
              <h3 class="text-h6 mb-3">品牌卡片</h3>
              <v-card class="brand-card pa-4" style="background: linear-gradient(135deg, #FFB6C1 0%, #87CEEB 100%);">
                <div class="d-flex align-center mb-3">
                  <BrandIcon class="me-3" />
                  <div>
                    <div class="text-h6 text-white">{{ BRAND_CONFIG.fullName }}</div>
                    <div class="text-body-2 text-white opacity-80">{{ BRAND_CONFIG.tagline }}</div>
                  </div>
                </div>
                <p class="text-white mb-0">{{ BRAND_CONFIG.description }}</p>
              </v-card>
            </div>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script setup lang="ts">
import BrandLogo from '@/components/BrandLogo.vue'
import BrandIcon from '@/components/BrandIcon.vue'
import { BRAND_COLORS, BRAND_CONFIG } from '@/constants/brand'
</script>

<style scoped>
.brand-showcase {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.demo-section {
  text-align: center;
}

.logo-demo {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  border-radius: 12px;
  margin-bottom: 1rem;
}

.icon-demo {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  border-radius: 8px;
  width: 80px;
  height: 80px;
}

.color-palette {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.color-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.color-swatch {
  width: 60px;
  height: 40px;
  border-radius: 8px;
  border: 2px solid rgba(0,0,0,0.1);
}

.color-info {
  flex: 1;
}

.color-name {
  font-weight: 600;
  font-size: 0.9rem;
}

.color-value {
  font-family: monospace;
  font-size: 0.8rem;
  color: rgba(0,0,0,0.6);
}

.gradient-demos {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.gradient-item {
  text-align: center;
}

.gradient-swatch {
  width: 120px;
  height: 60px;
  border-radius: 8px;
  margin-bottom: 8px;
  border: 2px solid rgba(0,0,0,0.1);
}

.gradient-name {
  font-weight: 600;
  font-size: 0.9rem;
  text-transform: capitalize;
}

.mock-topbar {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  border-radius: 8px;
  min-height: 64px;
}

.brand-card {
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
}

.usage-example {
  margin-bottom: 24px;
}

.gap-4 {
  gap: 16px;
}
</style>