/**
 * 国际化工具函数
 */

/**
 * 获取当前语言设置
 */
export const getCurrentLanguage = (): string => {
  const locale = localStorage.getItem('locale') || 'zh-CN'
  if (locale.startsWith('en')) return 'en'
  if (locale.startsWith('zh')) return 'zh'
  return 'zh'
}

/**
 * 解析多语言JSON文本
 * @param text 可能是JSON格式的多语言文本或普通文本
 * @returns 解析后的本地化文本
 */
export const parseLocalizedText = (text: string): string => {
  if (!text) return ''
  
  try {
    // 尝试解析JSON格式的多语言文本
    const parsed = JSON.parse(text)
    if (typeof parsed === 'object' && parsed !== null) {
      const currentLang = getCurrentLanguage()
      
      // 根据当前语言返回对应文本
      if (currentLang === 'en') {
        return parsed.en || parsed.cn || parsed.zh || text
      } else {
        return parsed.cn || parsed.zh || parsed.en || text
      }
    }
  } catch (e) {
    // 如果不是JSON格式，直接返回原文本
    return text
  }
  
  return text
}

/**
 * 需要处理国际化的字段名列表
 */
const LOCALIZED_FIELDS = ['name', 'description', 'title', 'label', 'message', 'type', 'location', 'provider']

/**
 * 智能处理对象中的国际化字段（用于显示）
 * @param obj 要处理的对象
 * @returns 处理后的对象
 */
export const processLocalizedFields = (obj: any): any => {
  if (obj === null || obj === undefined) {
    return obj
  }
  
  if (Array.isArray(obj)) {
    return obj.map(item => processLocalizedFields(item))
  }
  
  if (typeof obj === 'object') {
    const processed: any = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        const value = obj[key]
        
        // 如果是需要处理的字段且是字符串，则解析多语言文本
        if (LOCALIZED_FIELDS.includes(key) && typeof value === 'string') {
          processed[key] = parseLocalizedText(value)
        } else if (typeof value === 'object') {
          // 递归处理嵌套对象
          processed[key] = processLocalizedFields(value)
        } else {
          processed[key] = value
        }
      }
    }
    return processed
  }
  
  return obj
}

/**
 * 获取用于显示的本地化文本
 * @param text 多语言文本
 * @returns 本地化后的文本
 */
export const getLocalizedText = (text: string | null | undefined): string => {
  if (!text) return ''
  return parseLocalizedText(text)
}

/**
 * 创建显示用的本地化对象副本
 * @param obj 原始对象
 * @returns 本地化后的对象副本
 */
export const createLocalizedCopy = (obj: any): any => {
  return processLocalizedFields(JSON.parse(JSON.stringify(obj)))
}
