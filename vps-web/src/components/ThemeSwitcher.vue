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
    
    <v-card min-width="300">
      <v-card-title>{{ $t('theme.title') }}</v-card-title>
      
      <v-card-text>
        <!-- 主题模式选择 -->
        <v-radio-group
          :model-value="themeStore.mode"
          @update:model-value="themeStore.setMode"
          inline
        >
          <v-radio
            :label="$t('theme.light')"
            value="light"
          />
          <v-radio
            :label="$t('theme.dark')"
            value="dark"
          />
          <v-radio
            :label="$t('theme.auto')"
            value="auto"
          />
        </v-radio-group>
        
        <v-divider class="my-4" />
        
        <!-- 主题预设选择 -->
        <div class="mb-4">
          <v-label class="mb-2">{{ $t('theme.customTheme') }}</v-label>
          <v-row>
            <v-col
              v-for="(preset, key) in themePresets"
              :key="key"
              cols="6"
            >
              <v-card
                :variant="themeStore.preset === key ? 'elevated' : 'outlined'"
                :color="themeStore.preset === key ? 'primary' : undefined"
                class="pa-2 cursor-pointer"
                @click="themeStore.setPreset(key)"
              >
                <div class="d-flex align-center">
                  <div class="flex-grow-1">
                    <div class="text-capitalize font-weight-medium">
                      {{ key }}
                    </div>
                  </div>
                  <div class="d-flex">
                    <div
                      v-for="color in ['primary', 'secondary', 'accent']"
                      :key="color"
                      class="color-dot ml-1"
                      :style="{ backgroundColor: preset[themeStore.currentTheme][color] }"
                    />
                  </div>
                </div>
              </v-card>
            </v-col>
          </v-row>
        </div>
        
        <!-- 自定义颜色 -->
        <v-expansion-panels variant="accordion">
          <v-expansion-panel>
            <v-expansion-panel-title>
              自定义颜色
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <v-row>
                <v-col
                  v-for="(color, key) in colorOptions"
                  :key="key"
                  cols="6"
                >
                  <div class="d-flex align-center mb-2">
                    <v-label class="flex-grow-1">{{ $t(`theme.${key}Color`) }}</v-label>
                    <input
                      type="color"
                      :value="themeStore.currentColors[key]"
                      @input="handleColorChange(key, $event)"
                      class="color-input"
                    />
                  </div>
                </v-col>
              </v-row>
              
              <v-btn
                variant="outlined"
                size="small"
                @click="themeStore.resetCustomColors"
                class="mt-2"
              >
                重置颜色
              </v-btn>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-card-text>
    </v-card>
  </v-menu>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useThemeStore, themePresets } from '@/stores/theme'
import type { ThemeColors } from '@/stores/theme'

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

const colorOptions = {
  primary: 'Primary',
  secondary: 'Secondary',
  accent: 'Accent',
  error: 'Error',
  info: 'Info',
  success: 'Success',
  warning: 'Warning'
}

const handleColorChange = (colorKey: keyof ThemeColors, event: Event) => {
  const target = event.target as HTMLInputElement
  themeStore.setCustomColor(colorKey, target.value)
}
</script>

<style scoped>
.color-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 1px solid rgba(0, 0, 0, 0.12);
}

.color-input {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cursor-pointer {
  cursor: pointer;
}
</style>