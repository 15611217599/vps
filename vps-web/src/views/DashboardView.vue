<template>
  <PageLayout :title="$t('nav.dashboard')">
      <v-container class="py-8">
        <v-card class="mb-6" elevation="2">
          <v-card-text class="text-center py-8">
            <v-avatar size="80" class="mb-4" color="primary">
              <span class="text-h4">{{ authStore.user?.username?.charAt(0).toUpperCase() }}</span>
            </v-avatar>
            
            <h1 class="text-h4 font-weight-bold mb-2">
              {{ $t('dashboard.welcome', { username: authStore.user?.username }) }}
            </h1>
            
            <p class="text-body-1 text-medium-emphasis mb-4">
              {{ $t('dashboard.subtitle') }}
            </p>
            
            <v-chip :color="themeStore.currentColors.success" variant="elevated" class="ma-1">
              <v-icon start>mdi-check-circle</v-icon>
              {{ $t('dashboard.onlineStatus') }}
            </v-chip>
          </v-card-text>
        </v-card>

        <v-row>
          <v-col cols="12" md="6">
            <v-card elevation="2" class="h-100">
              <v-card-title class="d-flex align-center">
                <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-account-details</v-icon>
                {{ $t('dashboard.userInfo') }}
              </v-card-title>
              
              <v-card-text>
                <v-list>
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon :color="themeStore.currentColors.primary">mdi-account</v-icon>
                    </template>
                    <v-list-item-title>{{ $t('dashboard.username') }}</v-list-item-title>
                    <v-list-item-subtitle>{{ authStore.user?.username }}</v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon :color="themeStore.currentColors.info">mdi-email</v-icon>
                    </template>
                    <v-list-item-title>{{ $t('dashboard.email') }}</v-list-item-title>
                    <v-list-item-subtitle>{{ authStore.user?.email }}</v-list-item-subtitle>
                  </v-list-item>
                  
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon :color="themeStore.currentColors.success">mdi-shield-check</v-icon>
                    </template>
                    <v-list-item-title>{{ $t('dashboard.tokenStatus') }}</v-list-item-title>
                    <v-list-item-subtitle>
                      <v-chip :color="themeStore.currentColors.success" size="small" class="mt-1">
                        {{ $t('dashboard.tokenValid') }}
                      </v-chip>
                    </v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-card-text>
            </v-card>
          </v-col>
          
          <v-col cols="12" md="6">
            <v-card elevation="2" class="h-100">
              <v-card-title class="d-flex align-center">
                <v-icon :color="themeStore.currentColors.secondary" class="me-2">mdi-cog</v-icon>
                {{ $t('dashboard.systemFeatures') }}
              </v-card-title>
              
              <v-card-text>
                <v-list>
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon :color="themeStore.currentColors.success">mdi-shield-check</v-icon>
                    </template>
                    <v-list-item-title>{{ $t('dashboard.jwtAuth') }}</v-list-item-title>
                    <template v-slot:append>
                      <v-chip :color="themeStore.currentColors.success" size="small">{{ $t('dashboard.enabled') }}</v-chip>
                    </template>
                  </v-list-item>
                  
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon :color="themeStore.currentColors.info">mdi-clock-check</v-icon>
                    </template>
                    <v-list-item-title>{{ $t('dashboard.tokenNeverExpires') }}</v-list-item-title>
                    <template v-slot:append>
                      <v-chip :color="themeStore.currentColors.info" size="small">{{ $t('dashboard.enabled') }}</v-chip>
                    </template>
                  </v-list-item>
                  
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon :color="themeStore.currentColors.primary">mdi-database</v-icon>
                    </template>
                    <v-list-item-title>{{ $t('dashboard.h2Database') }}</v-list-item-title>
                    <template v-slot:append>
                      <v-chip :color="themeStore.currentColors.primary" size="small">{{ $t('dashboard.enabled') }}</v-chip>
                    </template>
                  </v-list-item>
                  
                  <v-list-item>
                    <template v-slot:prepend>
                      <v-icon :color="themeStore.currentColors.warning">mdi-lock</v-icon>
                    </template>
                    <v-list-item-title>{{ $t('dashboard.passwordEncryption') }}</v-list-item-title>
                    <template v-slot:append>
                      <v-chip :color="themeStore.currentColors.warning" size="small">{{ $t('dashboard.enabled') }}</v-chip>
                    </template>
                  </v-list-item>
                </v-list>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>

        <!-- 快速导航 -->
        <v-row class="mt-6">
          <v-col cols="12">
            <v-card elevation="2">
              <v-card-title class="d-flex align-center">
                <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-view-dashboard</v-icon>
                {{ $t('dashboard.quickNavigation') }}
              </v-card-title>
              
              <v-card-text>
                <v-row>

                  
                  <v-col cols="12" sm="6" md="4">
                    <v-card 
                      elevation="1" 
                      hover 
                      class="text-center pa-4 cursor-pointer"
                      @click="router.push('/groups')"
                    >
                      <v-icon 
                        size="48" 
                        :color="themeStore.currentColors.secondary" 
                        class="mb-2"
                      >
                        mdi-folder-network
                      </v-icon>
                      <div class="text-h6 font-weight-bold">
                        {{ $t('nav.serverGroup') }}
                      </div>
                      <div class="text-body-2 text-medium-emphasis">
                        {{ $t('groups.subtitle') }}
                      </div>
                    </v-card>
                  </v-col>
                  

                  
                  <v-col cols="12" sm="6" md="4">
                    <v-card 
                      elevation="1" 
                      hover 
                      class="text-center pa-4 cursor-pointer"
                      @click="router.push('/profile')"
                    >
                      <v-icon 
                        size="48" 
                        :color="themeStore.currentColors.info" 
                        class="mb-2"
                      >
                        mdi-account-edit
                      </v-icon>
                      <div class="text-h6 font-weight-bold">
                        {{ $t('nav.profile') }}
                      </div>
                      <div class="text-body-2 text-medium-emphasis">
                        {{ $t('dashboard.profileDescription') }}
                      </div>
                    </v-card>
                  </v-col>
                </v-row>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>

      </v-container>
  </PageLayout>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '../stores/auth'
import { useThemeStore } from '@/stores/theme'
import { useRouter } from 'vue-router'
import PageLayout from '@/components/PageLayout.vue'

const { t } = useI18n()
const authStore = useAuthStore()
const themeStore = useThemeStore()
const router = useRouter()
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}

.cursor-pointer:hover {
  transform: translateY(-2px);
  transition: transform 0.2s ease;
}
</style>