<template>
  <div class="brand-logo d-flex align-center">
    <!-- 兔子图标 SVG -->
    <div class="logo-icon me-3">
      <svg 
        width="32" 
        height="32" 
        viewBox="0 0 32 32" 
        fill="none" 
        xmlns="http://www.w3.org/2000/svg"
        class="rabbit-icon"
      >
        <!-- 兔子耳朵 -->
        <ellipse cx="10" cy="8" rx="2.5" ry="6" fill="currentColor" opacity="0.9"/>
        <ellipse cx="22" cy="8" rx="2.5" ry="6" fill="currentColor" opacity="0.9"/>
        
        <!-- 兔子头部 -->
        <circle cx="16" cy="16" r="8" fill="currentColor"/>
        
        <!-- 兔子眼睛 -->
        <circle cx="13" cy="14" r="1.5" fill="white"/>
        <circle cx="19" cy="14" r="1.5" fill="white"/>
        
        <!-- 兔子鼻子 -->
        <ellipse cx="16" cy="17" rx="1" ry="0.5" fill="white" opacity="0.8"/>
        
        <!-- 兔子嘴巴 -->
        <path d="M14 19 Q16 20 18 19" stroke="white" stroke-width="1" fill="none" opacity="0.8"/>
        
        <!-- 服务器元素 - 小方块代表数据中心 -->
        <rect x="25" y="12" width="4" height="2" rx="0.5" fill="currentColor" opacity="0.7"/>
        <rect x="25" y="15" width="4" height="2" rx="0.5" fill="currentColor" opacity="0.7"/>
        <rect x="25" y="18" width="4" height="2" rx="0.5" fill="currentColor" opacity="0.7"/>
        
        <!-- 连接线 -->
        <path d="M24 16 L30 16" stroke="currentColor" stroke-width="1" opacity="0.5"/>
      </svg>
    </div>
    
    <!-- 品牌文字 -->
    <div class="brand-text">
      <div class="brand-name">
        <span class="rabbit-text">Rabbit</span><span class="vps-text">VPS</span>
      </div>
      <div class="brand-subtitle" v-if="showSubtitle">Manager</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { BRAND_CONFIG, BRAND_COLORS } from '@/constants/brand'

interface Props {
  showSubtitle?: boolean
  size?: 'small' | 'medium' | 'large'
}

withDefaults(defineProps<Props>(), {
  showSubtitle: BRAND_CONFIG.logo.showSubtitle,
  size: BRAND_CONFIG.logo.defaultSize
})
</script>

<style scoped>
.brand-logo {
  user-select: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.brand-logo:hover {
  transform: scale(1.02);
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.rabbit-icon {
  color: white;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
  transition: all 0.3s ease;
}

.brand-logo:hover .rabbit-icon {
  transform: rotate(-5deg);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1;
}

.brand-name {
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
  letter-spacing: -0.5px;
}

.rabbit-text {
  color: var(--brand-rabbit-light, #FFE4E1);
}

.vps-text {
  color: var(--brand-vps-light, #E0F7FF);
  font-weight: 800;
}

.brand-subtitle {
  font-size: 0.75rem;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.8);
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-top: -2px;
}

/* 响应式尺寸 */
.brand-logo.size-small .brand-name {
  font-size: 1.2rem;
}

.brand-logo.size-small .brand-subtitle {
  font-size: 0.65rem;
}

.brand-logo.size-large .brand-name {
  font-size: 1.8rem;
}

.brand-logo.size-large .brand-subtitle {
  font-size: 0.85rem;
}

/* 动画效果 */
@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-3px);
  }
  60% {
    transform: translateY(-1px);
  }
}

.brand-logo:active .rabbit-icon {
  animation: bounce 0.6s ease;
}

/* 深色主题适配 */
.v-theme--dark .rabbit-text {
  color: #FFB6C1; /* 深色主题下的粉色 */
}

.v-theme--dark .vps-text {
  color: #87CEEB; /* 深色主题下的蓝色 */
}
</style>