import { createI18n } from 'vue-i18n'
import zhCN from './zh-CN'
import enUS from './en-US'

// 支持的语言列表
export const supportedLocales = [
  { code: 'zh-CN', name: '简体中文', flag: '🇨🇳' },
  { code: 'en-US', name: 'English', flag: '🇺🇸' }
]

// 获取浏览器默认语言
const getDefaultLocale = (): string => {
  const stored = localStorage.getItem('locale')
  if (stored && supportedLocales.some(locale => locale.code === stored)) {
    return stored
  }
  
  const browserLang = navigator.language
  const supportedCodes = supportedLocales.map(locale => locale.code)
  
  // 精确匹配
  if (supportedCodes.includes(browserLang)) {
    return browserLang
  }
  
  // 语言前缀匹配
  const langPrefix = browserLang.split('-')[0]
  const matchedLocale = supportedCodes.find(code => code.startsWith(langPrefix))
  
  return matchedLocale || 'zh-CN' // 默认中文
}

// 创建i18n实例
const i18n = createI18n({
  legacy: false, // 使用 Composition API
  locale: getDefaultLocale(),
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS
  }
})

export default i18n