# RabbitVPS 品牌设计指南

## 🐰 品牌概述

RabbitVPS Manager 采用了全新的品牌设计，结合了兔子的可爱元素和VPS云服务的科技感，创造出独特而专业的品牌形象。

## 🎨 设计理念

- **兔子元素**: 代表敏捷、可靠、友好的服务特质
- **科技蓝色**: 体现云服务的专业性和技术实力
- **渐变设计**: 现代化的视觉效果，增强品牌吸引力

## 📦 新增组件

### 1. BrandLogo 组件
主要的品牌logo组件，包含兔子图标和文字标识。

```vue
<BrandLogo 
  :show-subtitle="true" 
  size="medium" 
/>
```

**属性:**
- `showSubtitle`: 是否显示"Manager"副标题
- `size`: 尺寸 ('small' | 'medium' | 'large')

### 2. BrandIcon 组件
仅图标版本，适用于紧凑空间。

```vue
<BrandIcon size="medium" />
```

## 🎨 品牌色彩

### 主色调
- **Primary**: `#00E5FF` - 科技青色
- **Primary Dark**: `#3F51B5` - 深蓝色

### 兔子主题色
- **Light**: `#FFE4E1` - 淡粉色
- **Main**: `#FFB6C1` - 粉色
- **Dark**: `#FF6B9D` - 深粉色

### VPS科技色
- **Light**: `#E0F7FF` - 淡蓝色
- **Main**: `#87CEEB` - 天蓝色
- **Dark**: `#4682B4` - 钢蓝色

### 渐变色
- **Primary**: `linear-gradient(135deg, #00E5FF 0%, #3F51B5 100%)`
- **Secondary**: `linear-gradient(135deg, #FFB6C1 0%, #87CEEB 100%)`
- **Accent**: `linear-gradient(135deg, #FF6B9D 0%, #4682B4 100%)`

## 🛠️ 使用方法

### 1. 在组件中使用
```vue
<template>
  <div>
    <!-- 完整Logo -->
    <BrandLogo />
    
    <!-- 仅图标 -->
    <BrandIcon size="small" />
  </div>
</template>

<script setup>
import BrandLogo from '@/components/BrandLogo.vue'
import BrandIcon from '@/components/BrandIcon.vue'
</script>
```

### 2. 使用品牌CSS类
```vue
<template>
  <div class="brand-gradient-primary pa-4">
    <h1 class="brand-text-rabbit">欢迎使用 RabbitVPS</h1>
    <v-btn class="brand-btn-primary">开始使用</v-btn>
  </div>
</template>
```

### 3. 使用CSS变量
```css
.custom-element {
  background: var(--brand-gradient-primary);
  color: var(--brand-rabbit-main);
  border-radius: var(--brand-border-radius);
}
```

## 📱 响应式设计

品牌组件支持响应式设计：
- **小屏幕**: 自动调整logo尺寸和间距
- **中等屏幕**: 标准显示
- **大屏幕**: 增强的视觉效果

## 🎭 动画效果

### 内置动画
- **悬停效果**: Logo轻微旋转和缩放
- **点击效果**: 弹跳动画
- **加载动画**: 品牌脉冲效果

### 使用动画类
```vue
<div class="brand-fade-in">淡入动画</div>
<div class="brand-pulse">脉冲动画</div>
<div class="brand-spin">旋转动画</div>
```

## 🔧 开发工具

### 品牌展示页面
在开发环境中，访问 `/brand-showcase` 查看所有品牌元素的展示。

### 品牌配置
所有品牌相关配置都在 `src/constants/brand.ts` 中定义。

## 📋 最佳实践

### ✅ 推荐做法
- 在顶部导航栏使用完整的 BrandLogo
- 在侧边栏或紧凑空间使用 BrandIcon
- 使用品牌渐变色作为主要背景
- 保持品牌色彩的一致性

### ❌ 避免做法
- 不要修改logo的比例
- 不要使用非品牌色彩
- 不要在深色背景上使用深色品牌元素
- 不要过度使用动画效果

## 🚀 更新日志

### v1.0.0 (2024-12-30)
- ✨ 新增 BrandLogo 组件
- ✨ 新增 BrandIcon 组件
- 🎨 定义完整的品牌色彩系统
- 📱 实现响应式品牌设计
- 🎭 添加品牌动画效果
- 📚 创建品牌展示页面

## 🤝 贡献指南

如需修改品牌设计：
1. 更新 `src/constants/brand.ts` 中的配置
2. 修改相应的组件文件
3. 更新 `src/styles/brand.css` 中的样式
4. 在品牌展示页面中验证效果

## 📞 联系我们

如有品牌设计相关问题，请联系设计团队。