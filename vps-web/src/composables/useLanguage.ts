import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { normalizeLanguageCode, setCurrentLanguage } from '@/utils/language'

// 全局语言变化状态
const languageChangeCounter = ref(0)

export function useLanguage() {
  const { locale } = useI18n()
  
  // 监听语言变化
  const onLanguageChange = (callback: () => void) => {
    watch(locale, () => {
      languageChangeCounter.value++
      callback()
    }, { immediate: false })
    
    // 也监听全局语言变化计数器
    watch(languageChangeCounter, () => {
      callback()
    }, { immediate: false })
  }
  
  // 触发语言变化
  const triggerLanguageChange = () => {
    languageChangeCounter.value++
  }
  
  // 切换语言
  const changeLanguage = (localeCode: string) => {
    const normalizedCode = normalizeLanguageCode(localeCode)
    
    locale.value = normalizedCode
    setCurrentLanguage(normalizedCode)
    triggerLanguageChange()
  }
  
  return {
    locale,
    onLanguageChange,
    changeLanguage,
    triggerLanguageChange
  }
}