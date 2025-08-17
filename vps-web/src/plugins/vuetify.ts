import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/lib/styles/main.css'

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
          background: '#ffffff',
          surface: '#f5f5f5',
          'surface-bright': '#ffffff',
          'surface-light': '#fafafa',
          'surface-variant': '#e0e0e0',
          'on-surface-variant': '#424242',
          'primary-darken-1': '#1976D2',
          'secondary-darken-1': '#018786',
          'on-background': '#212121',
          'on-surface': '#212121',
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
          background: '#121212',
          surface: '#1e1e1e',
          'surface-bright': '#2c2c2c',
          'surface-light': '#242424',
          'surface-variant': '#424242',
          'on-surface-variant': '#e0e0e0',
          'primary-darken-1': '#3700B3',
          'secondary-darken-1': '#03DAC5',
          'on-background': '#ffffff',
          'on-surface': '#ffffff',
          'on-primary': '#FFFFFF',
          'on-primary-darken-1': '#FFFFFF',
          'on-secondary': '#000000',
          'on-secondary-darken-1': '#000000',
        },
      },
    },
  },
})