// RabbitVPS 品牌配置
export const BRAND_COLORS = {
  // 主品牌色 - 科技蓝到青色的渐变
  primary: '#00E5FF',
  primaryDark: '#3F51B5',
  
  // 兔子主题色 - 温暖的粉色系
  rabbit: {
    light: '#FFE4E1',
    main: '#FFB6C1',
    dark: '#FF6B9D'
  },
  
  // VPS科技色 - 冷色调蓝色系
  vps: {
    light: '#E0F7FF',
    main: '#87CEEB',
    dark: '#4682B4'
  },
  
  // 渐变色
  gradients: {
    primary: 'linear-gradient(135deg, #00E5FF 0%, #3F51B5 100%)',
    secondary: 'linear-gradient(135deg, #FFB6C1 0%, #87CEEB 100%)',
    accent: 'linear-gradient(135deg, #FF6B9D 0%, #4682B4 100%)'
  }
}

export const BRAND_CONFIG = {
  name: 'RabbitVPS',
  fullName: 'RabbitVPS Manager',
  tagline: '云服务器管理平台',
  description: '专业的VPS云服务器管理解决方案',
  
  // Logo配置
  logo: {
    showSubtitle: true,
    defaultSize: 'medium' as const,
    animationEnabled: true
  },
  
  // 主题配置
  theme: {
    borderRadius: 8,
    elevation: 2,
    fontFamily: '"Roboto", "Noto Sans SC", sans-serif'
  }
}

// 品牌相关的CSS变量
export const BRAND_CSS_VARS = {
  '--brand-primary': BRAND_COLORS.primary,
  '--brand-primary-dark': BRAND_COLORS.primaryDark,
  '--brand-rabbit-main': BRAND_COLORS.rabbit.main,
  '--brand-vps-main': BRAND_COLORS.vps.main,
  '--brand-gradient-primary': BRAND_COLORS.gradients.primary,
  '--brand-gradient-secondary': BRAND_COLORS.gradients.secondary
}