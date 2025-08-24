<template>
  <v-footer
    :color="themeStore.currentColors.primary"
    :elevation="1"
    class="fixed-footer"
  >
    <v-container fluid class="pa-2">
      <v-row no-gutters align="center" class="fill-height">
        <!-- 左侧公司信息 -->
        <v-col cols="12" md="4" class="text-center text-md-left mb-1 mb-md-0">
          <div class="d-flex align-center justify-center justify-md-start">
            <v-icon color="white" size="small" class="me-1">
              mdi-copyright
            </v-icon>
            <span class="text-caption text-white">
              {{ new Date().getFullYear() }} {{ TEXTS.footer.company }}
            </span>
          </div>
        </v-col>
        
        <!-- 中间版本信息 -->
        <v-col cols="12" md="4" class="text-center mb-1 mb-md-0">
          <div class="d-flex align-center justify-center">
            <v-chip
              size="x-small"
              color="white"
              variant="outlined"
              class="me-2"
            >
              v1.0.0
            </v-chip>
            <span class="text-caption text-white">
              {{ TEXTS.footer.poweredBy }}
            </span>
          </div>
        </v-col>
        
        <!-- 右侧链接 -->
        <v-col cols="12" md="4" class="text-center text-md-right">
          <div class="d-flex align-center justify-center justify-md-end flex-wrap">
            <v-btn
              variant="text"
              size="small"
              color="white"
              class="text-caption px-2"
              @click="openLink('privacy')"
            >
              {{ TEXTS.footer.privacy }}
            </v-btn>
            
            <v-divider vertical class="mx-1" color="rgba(255,255,255,0.3)" />
            
            <v-btn
              variant="text"
              size="small"
              color="white"
              class="text-caption px-2"
              @click="openLink('terms')"
            >
              {{ TEXTS.footer.terms }}
            </v-btn>
            
            <v-divider vertical class="mx-1" color="rgba(255,255,255,0.3)" />
            
            <v-btn
              variant="text"
              size="small"
              color="white"
              class="text-caption px-2"
              @click="openLink('support')"
            >
              {{ TEXTS.footer.support }}
            </v-btn>
          </div>
        </v-col>
      </v-row>
    </v-container>
  </v-footer>
</template>

<script setup lang="ts">
import { useThemeStore } from '@/stores/theme'
import { TEXTS } from '@/constants/texts'

const themeStore = useThemeStore()

const openLink = (type: string) => {
  // 这里可以根据需要打开相应的链接
  switch (type) {
    case 'privacy':
      // 打开隐私政策页面
      window.open('/privacy', '_blank')
      break
    case 'terms':
      // 打开服务条款页面
      window.open('/terms', '_blank')
      break
    case 'support':
      // 打开技术支持邮箱
      window.open('mailto:support@vpsmanager.com')
      break
  }
}
</script>

<style scoped>
.fixed-footer {
  position: fixed !important;
  bottom: 0 !important;
  left: 0 !important;
  right: 0 !important;
  width: 100% !important;
  z-index: 1000 !important;
  border-top: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
  height: 60px !important; /* 固定底部栏高度 */
  min-height: 60px !important;
  max-height: 60px !important;
}

/* 确保在深色主题下也有正确的边框 */
:deep(.v-theme--dark) .fixed-footer {
  border-top: 1px solid rgba(255, 255, 255, 0.12);
}

:deep(.v-theme--light) .fixed-footer {
  border-top: 1px solid rgba(0, 0, 0, 0.12);
}

/* 移动端样式调整 */
@media (max-width: 960px) {
  .fixed-footer :deep(.v-col) {
    padding: 2px 0;
  }
  
  .fixed-footer {
    height: 80px !important; /* 移动端稍微高一点 */
    min-height: 80px !important;
    max-height: 80px !important;
  }
}
</style>