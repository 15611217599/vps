import { useI18n as useVueI18n } from 'vue-i18n'

// 扩展useI18n hook，添加一些便利方法
export function useI18n() {
  const i18n = useVueI18n()
  
  // 带参数的翻译函数
  const tWithParams = (key: string, params: Record<string, any>) => {
    return i18n.t(key, params)
  }
  
  // 复数形式翻译
  const tPlural = (key: string, count: number, params?: Record<string, any>) => {
    return i18n.t(key, { count, ...params })
  }
  
  // 检查翻译键是否存在
  const hasTranslation = (key: string) => {
    return i18n.te(key)
  }
  
  // 获取当前语言的翻译对象
  const getCurrentMessages = () => {
    return i18n.messages.value[i18n.locale.value]
  }
  
  return {
    ...i18n,
    tWithParams,
    tPlural,
    hasTranslation,
    getCurrentMessages
  }
}