<template>
  <GuestLayout>
    <div class="auth-main">
      <transition name="form-switch" mode="out-in">
        <LoginForm v-if="mode === 'login'" key="login" @switch-mode="switchMode" />
        <RegisterForm v-else key="register" @switch-mode="switchMode" />
      </transition>
    </div>
  </GuestLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import LoginForm from '../components/LoginForm.vue'
import RegisterForm from '../components/RegisterForm.vue'
import GuestLayout from '../components/GuestLayout.vue'

const mode = ref<'login' | 'register'>('login')

const switchMode = () => {
  mode.value = mode.value === 'login' ? 'register' : 'login'
}
</script><style scoped>
.auth-main {
  /* 让内容自然撑开，不设置固定高度 */
  position: relative;
}

.form-switch-enter-active,
.form-switch-leave-active {
  transition: all 0.3s ease;
}

.form-switch-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.form-switch-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>