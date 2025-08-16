<template>
  <v-menu>
    <template v-slot:activator="{ props }">
      <v-btn
        v-bind="props"
        variant="text"
        icon
        :title="$t('theme.title')"
        height="40"
        width="40"
        color="white"
        rounded
        style="align-self: center;"
      >
        <v-icon>{{ themeIcon }}</v-icon>
      </v-btn>
    </template>
    
    <v-card min-width="400" max-width="500">
      <v-card-title class="d-flex align-center pa-4">
        <v-icon class="mr-2">mdi-palette</v-icon>
        {{ $t('theme.title') }}
      </v-card-title>
      
      <v-card-text class="pa-4">
        <!-- 主题模式选择 -->
        <div class="mb-6">
          <v-label class="text-subtitle-2 font-weight-medium mb-3 d-block">
            <v-icon size="small" class="mr-1">mdi-brightness-6</v-icon>
            显示模式
          </v-label>
          <v-btn-toggle
            :model-value="themeStore.mode"
            @update:model-value="themeStore.setMode"
            mandatory
            variant="outlined"
            divided
            class="w-100"
          >
            <v-btn value="light" class="flex-grow-1">
              <v-icon class="mr-1">mdi-weather-sunny</v-icon>
              {{ $t('theme.light') }}
            </v-btn>
            <v-btn value="dark" class="flex-grow-1">
              <v-icon class="mr-1">mdi-weather-night</v-icon>
              {{ $t('theme.dark') }}
            </v-btn>
            <v-btn value="auto" class="flex-grow-1">
              <v-icon class="mr-1">mdi-theme-light-dark</v-icon>
              {{ $t('theme.auto') }}
            </v-btn>
          </v-btn-toggle>
        </div>
        
        <v-divider class="my-4" />
        
        <!-- 主题预设选择 -->
        <div>
          <v-label class="text-subtitle-2 font-weight-medium mb-3 d-block">
            <v-icon size="small" class="mr-1">mdi-format-color-fill</v-icon>
            主题配色
          </v-label>
          <v-row>
            <v-col
              v-for="(preset, key) in themePresets"
              :key="key"
              cols="4"
              class="pa-1"
            >
              <v-card
                :variant="themeStore.preset === key ? 'elevated' : 'outlined'"
                :color="themeStore.preset === key ? 'primary' : undefined"
                class="pa-3 cursor-pointer theme-card"
                @click="themeStore.setPreset(key)"
                :elevation="themeStore.preset === key ? 8 : 1"
              >
                <div class="text-center">
                  <div class="d-flex justify-center mb-2">
                    <div
                      v-for="color in ['primary', 'secondary', 'accent']"
                      :key="color"
                      class="color-preview"
                      :style="{ backgroundColor: preset[themeStore.currentTheme][color as keyof typeof preset[typeof themeStore.currentTheme]] }"
                    />
                  </div>
                  <div class="text-caption font-weight-medium text-capitalize">
                    {{ getThemeName(key) }}
                  </div>
                </div>
              </v-card>
            </v-col>
          </v-row>
        </div>
      </v-card-text>
    </v-card>
  </v-menu>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useThemeStore, themePresets } from '@/stores/theme'

const themeStore = useThemeStore()

const themeIcon = computed(() => {
  switch (themeStore.mode) {
    case 'light':
      return 'mdi-weather-sunny'
    case 'dark':
      return 'mdi-weather-night'
    case 'auto':
      return 'mdi-theme-light-dark'
    default:
      return 'mdi-palette'
  }
})

const themeNames: Record<string, string> = {
  default: '默认',
  blue: '蓝色',
  green: '绿色',
  purple: '紫色',
  red: '红色',
  orange: '橙色',
  teal: '青色',
  indigo: '靛蓝',
  pink: '粉色',
  amber: '琥珀'
}

const getThemeName = (key: string) => {
  return themeNames[key] || key
}
</script>

<style scoped>
.color-preview {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.8);
  margin: 0 1px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.theme-card {
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 12px !important;
}

.theme-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

.v-btn-toggle {
  border-radius: 8px;
}

.v-btn-toggle .v-btn {
  border-radius: 8px;
}
</style>