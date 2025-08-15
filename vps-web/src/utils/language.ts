/**
 * 语言处理工具函数
 */

/**
 * 标准化语言代码
 * 将各种格式的语言代码统一为标准格式
 */
export function normalizeLanguageCode(locale: string): string {
  if (!locale) return 'zh-CN'
  
  const lang = locale.toLowerCase().trim()
  
  if (lang.startsWith('en')) {
    return 'en-US'
  } else if (lang.startsWith('zh')) {
    return 'zh-CN'
  }
  
  return 'zh-CN' // 默认中文
}

/**
 * 获取语言的简短代码（用于后端API）
 */
export function getLanguageShortCode(locale: string): string {
  const normalized = normalizeLanguageCode(locale)
  return normalized.split('-')[0] // 'zh-CN' -> 'zh', 'en-US' -> 'en'
}

/**
 * 获取当前存储的语言代码
 */
export function getCurrentLanguage(): string {
  return localStorage.getItem('locale') || 'zh-CN'
}

/**
 * 设置语言代码
 */
export function setCurrentLanguage(locale: string): void {
  const normalized = normalizeLanguageCode(locale)
  localStorage.setItem('locale', normalized)
}

/**
 * 获取用于API请求的Accept-Language头值
 */
export function getAcceptLanguageHeader(): string {
  const currentLang = getCurrentLanguage()
  return normalizeLanguageCode(currentLang)
}

/**
 * 语言显示名称映射
 */
export const LANGUAGE_NAMES = {
  'zh-CN': '简体中文',
  'en-US': 'English'
} as const

/**
 * 语言标志映射
 */
export const LANGUAGE_FLAGS = {
  'zh-CN': '🇨🇳',
  'en-US': '🇺🇸'
} as const

/**
 * 检查是否为中文环境
 */
export function isChineseLanguage(locale?: string): boolean {
  const lang = locale || getCurrentLanguage()
  return lang.startsWith('zh')
}

/**
 * 检查是否为英文环境
 */
export function isEnglishLanguage(locale?: string): boolean {
  const lang = locale || getCurrentLanguage()
  return lang.startsWith('en')
}