<template>
  <div class="brand-icon" :class="`size-${size}`">
    <svg 
      :width="iconSize" 
      :height="iconSize" 
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
      
      <!-- 服务器元素 -->
      <rect x="25" y="12" width="4" height="2" rx="0.5" fill="currentColor" opacity="0.7"/>
      <rect x="25" y="15" width="4" height="2" rx="0.5" fill="currentColor" opacity="0.7"/>
      <rect x="25" y="18" width="4" height="2" rx="0.5" fill="currentColor" opacity="0.7"/>
      
      <!-- 连接线 -->
      <path d="M24 16 L30 16" stroke="currentColor" stroke-width="1" opacity="0.5"/>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  size?: 'small' | 'medium' | 'large'
}

const props = withDefaults(defineProps<Props>(), {
  size: 'medium'
})

const iconSize = computed(() => {
  switch (props.size) {
    case 'small': return 24
    case 'large': return 48
    default: return 32
  }
})
</script>

<style scoped>
.brand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.brand-icon:hover {
  transform: scale(1.1) rotate(-5deg);
}

.rabbit-icon {
  color: white;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
  transition: all 0.3s ease;
}

.brand-icon:active .rabbit-icon {
  animation: bounce 0.6s ease;
}

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
</style>