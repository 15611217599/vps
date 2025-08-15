<template>
  <v-menu>
    <template v-slot:activator="{ props }">
      <v-btn
        v-bind="props"
        variant="text"
        class="text-none px-3"
        height="40"
        color="white"
        rounded
        style="align-self: center;"
      >
        <span class="text-h6 mr-1">{{ currentLocale.flag }}</span>
        {{ currentLocale.name }}
        <v-icon end size="small">mdi-chevron-down</v-icon>
      </v-btn>
    </template>
    
    <v-list>
      <v-list-item
        v-for="locale in supportedLocales"
        :key="locale.code"
        :active="locale.code === currentLocale.code"
        @click="handleLanguageChange(locale.code)"
      >
        <template v-slot:prepend>
          <span class="text-h6 mr-2">{{ locale.flag }}</span>
        </template>
        <v-list-item-title>{{ locale.name }}</v-list-item-title>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { supportedLocales } from '@/locales'
import { useLanguage } from '@/composables/useLanguage'
import { normalizeLanguageCode } from '@/utils/language'

const { locale, changeLanguage } = useLanguage()

const currentLocale = computed(() => {
  return supportedLocales.find(l => l.code === locale.value) || supportedLocales[0]
})

// 重写changeLanguage函数以使用标准化
const handleLanguageChange = (localeCode: string) => {
  const normalizedCode = normalizeLanguageCode(localeCode)
  console.log('Language change:', localeCode, '->', normalizedCode)
  changeLanguage(normalizedCode)
}
</script>