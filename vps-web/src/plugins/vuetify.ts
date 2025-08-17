import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import '@mdi/font/css/materialdesignicons.css'

// 默认主题配色
const defaultColors = {
  primary: '#667eea',
  secondary: '#764ba2',
  accent: '#82B1FF',
  error: '#e74c3c',
  info: '#2196F3',
  success: '#27ae60',
  warning: '#FFC107',
}

export default createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi,
    },
  },
  theme: {
    defaultTheme: 'light',
    variations: {
      colors: ['primary', 'secondary', 'accent'],
      lighten: 2,
      darken: 2,
    },
    themes: {
      light: {
        dark: false,
        colors: {
          ...defaultColors,
          background: 'transparent',
          surface: 'transparent',
          'surface-bright': 'transparent',
          'surface-light': 'transparent',
          'surface-variant': 'transparent',
          'on-surface-variant': 'transparent',
          'primary-darken-1': '#1976D2',
          'secondary-darken-1': '#018786',
          'on-background': '#000000',
          'on-surface': '#000000',
          'on-primary': '#FFFFFF',
          'on-primary-darken-1': '#FFFFFF',
          'on-secondary': '#FFFFFF',
          'on-secondary-darken-1': '#FFFFFF',
        },
      },
      dark: {
        dark: true,
        colors: {
          ...defaultColors,
          background: 'transparent',
          surface: 'transparent',
          'surface-bright': 'transparent',
          'surface-light': 'transparent',
          'surface-variant': 'transparent',
          'on-surface-variant': 'transparent',
          'primary-darken-1': '#3700B3',
          'secondary-darken-1': '#03DAC5',
          'on-background': '#FFFFFF',
          'on-surface': '#FFFFFF',
          'on-primary': '#FFFFFF',
          'on-primary-darken-1': '#FFFFFF',
          'on-secondary': '#000000',
          'on-secondary-darken-1': '#000000',
        },
      },
    },
  },
})