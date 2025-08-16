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
export type ThemePreset = 'default' | 'blue' | 'green' | 'purple' | 'red' | 'orange' | 'teal' | 'indigo' | 'pink' | 'amber'

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
      background: '#f8f9fa',
      surface: '#ffffff',
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
      background: '#1a1a1a',
      surface: '#2d2d2d',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  },
  blue: {
    light: {
      primary: '#1976d2',
      secondary: '#1565c0',
      accent: '#03a9f4',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#f3f8ff',
      surface: '#ffffff',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#2196f3',
      secondary: '#1976d2',
      accent: '#03a9f4',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#0d1421',
      surface: '#1e2a3a',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  },
  green: {
    light: {
      primary: '#4caf50',
      secondary: '#388e3c',
      accent: '#8bc34a',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#f1f8e9',
      surface: '#ffffff',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#66bb6a',
      secondary: '#4caf50',
      accent: '#8bc34a',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#0f1b0f',
      surface: '#1b2e1b',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  },
  purple: {
    light: {
      primary: '#9c27b0',
      secondary: '#7b1fa2',
      accent: '#e91e63',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#faf4ff',
      surface: '#ffffff',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#ba68c8',
      secondary: '#9c27b0',
      accent: '#e91e63',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#1a0e1f',
      surface: '#2d1b32',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  },
  red: {
    light: {
      primary: '#f44336',
      secondary: '#d32f2f',
      accent: '#ff5722',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#fff5f5',
      surface: '#ffffff',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#ef5350',
      secondary: '#f44336',
      accent: '#ff5722',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#1f0d0d',
      surface: '#321a1a',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  },
  orange: {
    light: {
      primary: '#ff9800',
      secondary: '#f57c00',
      accent: '#ff5722',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#fff8f0',
      surface: '#ffffff',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#ffb74d',
      secondary: '#ff9800',
      accent: '#ff5722',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#1f1509',
      surface: '#322512',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  },
  teal: {
    light: {
      primary: '#009688',
      secondary: '#00796b',
      accent: '#26a69a',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#f0fdfc',
      surface: '#ffffff',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#4db6ac',
      secondary: '#009688',
      accent: '#26a69a',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#0d1f1c',
      surface: '#1a322e',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  },
  indigo: {
    light: {
      primary: '#3f51b5',
      secondary: '#303f9f',
      accent: '#536dfe',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#f5f6ff',
      surface: '#ffffff',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#7986cb',
      secondary: '#3f51b5',
      accent: '#536dfe',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#0f1019',
      surface: '#1c1f2e',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  },
  pink: {
    light: {
      primary: '#e91e63',
      secondary: '#c2185b',
      accent: '#f06292',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#fef7f7',
      surface: '#ffffff',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#f48fb1',
      secondary: '#e91e63',
      accent: '#f06292',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#1f0d14',
      surface: '#321a25',
      'on-primary': '#ffffff',
      'on-secondary': '#ffffff',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  },
  amber: {
    light: {
      primary: '#ffc107',
      secondary: '#ffa000',
      accent: '#ffca28',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#fffbf0',
      surface: '#ffffff',
      'on-primary': '#000000',
      'on-secondary': '#000000',
      'on-background': '#212121',
      'on-surface': '#212121'
    },
    dark: {
      primary: '#ffca28',
      secondary: '#ffc107',
      accent: '#ffca28',
      error: '#f44336',
      info: '#2196f3',
      success: '#4caf50',
      warning: '#ff9800',
      background: '#1f1a0d',
      surface: '#322b1a',
      'on-primary': '#000000',
      'on-secondary': '#000000',
      'on-background': '#ffffff',
      'on-surface': '#ffffff'
    }
  }
}

export const useThemeStore = defineStore('theme', () => {
  // 状态
  const mode = ref<ThemeMode>('light')
  const preset = ref<ThemePreset>('default')
  
  // 计算属性
  const isDark = computed(() => {
    if (mode.value === 'auto') {
      return window.matchMedia('(prefers-color-scheme: dark)').matches
    }
    return mode.value === 'dark'
  })
  
  const currentTheme = computed(() => isDark.value ? 'dark' : 'light')
  
  const currentColors = computed((): ThemeColors => {
    return themePresets[preset.value][currentTheme.value]
  })
  
  // 动作
  const setMode = (newMode: ThemeMode) => {
    mode.value = newMode
    localStorage.setItem('theme-mode', newMode)
  }
  
  const setPreset = (newPreset: ThemePreset) => {
    preset.value = newPreset
    localStorage.setItem('theme-preset', newPreset)
  }
  
  const loadSettings = () => {
    const savedMode = localStorage.getItem('theme-mode') as ThemeMode
    const savedPreset = localStorage.getItem('theme-preset') as ThemePreset
    
    if (savedMode && ['light', 'dark', 'auto'].includes(savedMode)) {
      mode.value = savedMode
    }
    
    if (savedPreset && themePresets[savedPreset]) {
      preset.value = savedPreset
    }
  }
  
  // 初始化时加载设置
  loadSettings()
  
  return {
    // 状态
    mode,
    preset,
    
    // 计算属性
    isDark,
    currentTheme,
    currentColors,
    
    // 动作
    setMode,
    setPreset,
    loadSettings
  }
})