import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 主题类型定义
export interface ThemeColors {
  primary: string
  secondary: string
  accent: string
  error: string
  info: string
  success: string
  warning: string
  background?: string
  surface?: string
  'on-primary'?: string
  'on-secondary'?: string
  'on-background'?: string
  'on-surface'?: string
}

// 主题模式类型
export type ThemeMode = 'light' | 'dark' | 'auto'
export type ThemePreset = 'default' | 'blue' | 'green' | 'purple'

// 预定义主题配色方案
export const themePresets = {
  default: {
    light: {
      primary: '#667eea',
      secondary: '#764ba2',
      accent: '#82B1FF',
      error: '#e74c3c',
      info: '#2196F3',
      success: '#27ae60',
      warning: '#FFC107',
      background: '#ffffff',
      surface: '#f5f5f5',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#667eea',
      secondary: '#764ba2',
      accent: '#82B1FF',
      error: '#e74c3c',
      info: '#2196F3',
      success: '#27ae60',
      warning: '#FFC107',
      background: '#121212',
      surface: '#1e1e1e',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  },
  blue: {
    light: {
      primary: '#1976d2',
      secondary: '#424242',
      accent: '#82B1FF',
      error: '#FF5252',
      info: '#2196F3',
      success: '#4CAF50',
      warning: '#FFC107',
      background: '#ffffff',
      surface: '#f5f5f5',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#2196F3',
      secondary: '#424242',
      accent: '#FF4081',
      error: '#FF5252',
      info: '#2196F3',
      success: '#4CAF50',
      warning: '#FFC107',
      background: '#121212',
      surface: '#1e1e1e',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  },
  green: {
    light: {
      primary: '#4CAF50',
      secondary: '#388E3C',
      accent: '#8BC34A',
      error: '#F44336',
      info: '#2196F3',
      success: '#4CAF50',
      warning: '#FF9800',
      background: '#ffffff',
      surface: '#f5f5f5',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#66BB6A',
      secondary: '#4CAF50',
      accent: '#8BC34A',
      error: '#F44336',
      info: '#2196F3',
      success: '#4CAF50',
      warning: '#FF9800',
      background: '#121212',
      surface: '#1e1e1e',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  },
  purple: {
    light: {
      primary: '#9C27B0',
      secondary: '#7B1FA2',
      accent: '#E91E63',
      error: '#F44336',
      info: '#2196F3',
      success: '#4CAF50',
      warning: '#FF9800',
      background: '#ffffff',
      surface: '#f5f5f5',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#BA68C8',
      secondary: '#9C27B0',
      accent: '#E91E63',
      error: '#F44336',
      info: '#2196F3',
      success: '#4CAF50',
      warning: '#FF9800',
      background: '#121212',
      surface: '#1e1e1e',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  }
}

export const useThemeStore = defineStore('theme', () => {
  // 状态
  const mode = ref<ThemeMode>('light')
  const preset = ref<ThemePreset>('default')
  const customColors = ref<Partial<ThemeColors>>({})
  
  // 计算属性
  const isDark = computed(() => {
    if (mode.value === 'auto') {
      return window.matchMedia('(prefers-color-scheme: dark)').matches
    }
    return mode.value === 'dark'
  })
  
  const currentTheme = computed(() => isDark.value ? 'dark' : 'light')
  
  const currentColors = computed((): ThemeColors => {
    const baseColors = themePresets[preset.value][currentTheme.value]
    return { ...baseColors, ...customColors.value }
  })
  
  // 动作
  const setMode = (newMode: ThemeMode) => {
    mode.value = newMode
    localStorage.setItem('theme-mode', newMode)
  }
  
  const setPreset = (newPreset: ThemePreset) => {
    preset.value = newPreset
    customColors.value = {} // 重置自定义颜色
    localStorage.setItem('theme-preset', newPreset)
  }
  
  const setCustomColor = (colorKey: keyof ThemeColors, color: string) => {
    customColors.value = {
      ...customColors.value,
      [colorKey]: color
    }
    localStorage.setItem('theme-custom-colors', JSON.stringify(customColors.value))
  }
  
  const resetCustomColors = () => {
    customColors.value = {}
    localStorage.removeItem('theme-custom-colors')
  }
  
  const loadSettings = () => {
    const savedMode = localStorage.getItem('theme-mode') as ThemeMode
    const savedPreset = localStorage.getItem('theme-preset') as ThemePreset
    const savedCustomColors = localStorage.getItem('theme-custom-colors')
    
    if (savedMode && ['light', 'dark', 'auto'].includes(savedMode)) {
      mode.value = savedMode
    }
    
    if (savedPreset && themePresets[savedPreset]) {
      preset.value = savedPreset
    }
    
    if (savedCustomColors) {
      try {
        customColors.value = JSON.parse(savedCustomColors)
      } catch (e) {
        console.warn('Failed to parse custom colors from localStorage')
      }
    }
  }
  
  // 初始化时加载设置
  loadSettings()
  
  return {
    // 状态
    mode,
    preset,
    customColors,
    
    // 计算属性
    isDark,
    currentTheme,
    currentColors,
    
    // 动作
    setMode,
    setPreset,
    setCustomColor,
    resetCustomColors,
    loadSettings
  }
})