# 统一组件样式系统使用指南

## 概述

本项目实现了一套完整的统一组件样式系统，旨在提供一致的用户界面体验和高效的开发流程。系统包含以下核心组件：

## 核心组件

### 1. ActionButtonGroup - 统一操作按钮组

用于标准化所有页面的新增、编辑、删除等操作按钮。

```vue
<ActionButtonGroup
  :show-add="true"
  :show-edit="true"
  :show-delete="true"
  :show-view="false"
  add-text="添加服务器"
  :loading="actionLoading"
  :custom-actions="[
    { key: 'restart', icon: 'mdi-restart', color: 'warning', tooltip: '重启' }
  ]"
  @add="handleAdd"
  @edit="handleEdit"
  @delete="handleDelete"
  @custom="handleCustomAction"
/>
```

**属性说明：**
- `show-add/edit/delete/view`: 控制按钮显示
- `add-text/edit-text/delete-text`: 自定义按钮文本
- `loading/disabled`: 控制按钮状态
- `custom-actions`: 自定义操作按钮数组

### 2. UnifiedDataTable - 统一数据表格

标准化所有数据表格的外观和交互。

```vue
<UnifiedDataTable
  title="服务器列表"
  subtitle="管理所有VPS服务器实例"
  icon="mdi-server"
  :headers="headers"
  :items="items"
  :loading="loading"
  :server-side="true"
  :total-items="totalItems"
  @update:options="handleOptionsUpdate"
  @search="handleSearch"
  @add="handleAdd"
  @edit="handleEdit"
  @delete="handleDelete"
>
  <!-- 自定义列内容 -->
  <template #item.status="{ item }">
    <StatusChip :status="item.status" type="status" />
  </template>
</UnifiedDataTable>
```

**特性：**
- 内置搜索功能
- 统一的表格样式
- 支持服务端和客户端分页
- 可自定义列内容
- 集成操作按钮

### 3. UnifiedDialog - 增强对话框

统一所有对话框的样式和交互模式。

```vue
<UnifiedDialog
  v-model="showDialog"
  title="编辑服务器"
  subtitle="修改服务器配置信息"
  :is-edit="true"
  :loading="saving"
  :disabled="!formValid"
  @save="handleSave"
  @cancel="handleCancel"
>
  <!-- 表单内容 -->
  <v-form v-model="formValid">
    <!-- 表单字段 -->
  </v-form>
  
  <!-- 自定义左侧按钮 -->
  <template #leftActions>
    <v-btn variant="text">重置</v-btn>
  </template>
</UnifiedDialog>
```

**新增特性：**
- **自动宽度** - 默认根据内容自动调整宽度，无需设置固定尺寸
- 可配置的头部图标和颜色
- 支持自定义按钮区域
- 响应式设计
- 增强的视觉效果

**宽度控制：**
- `autoWidth: true` (默认) - 根据内容自动调整宽度
- `autoWidth: false` - 使用传统的固定宽度模式
- `maxWidth` - 设置最大宽度限制 (默认: 90vw)
- `width` - 设置固定宽度 (仅在 autoWidth: false 时生效)

### 4. UnifiedFormField - 统一表单字段

标准化所有表单输入控件。

```vue
<UnifiedFormField
  v-model="formData.name"
  type="text"
  label="服务器名称"
  icon="mdi-server"
  :rules="[rules.required]"
  required
  hint="请输入唯一的服务器名称"
/>

<UnifiedFormField
  v-model="formData.status"
  type="select"
  label="状态"
  :items="statusOptions"
  item-title="title"
  item-value="value"
/>

<UnifiedFormField
  v-model="formData.autoStart"
  type="switch"
  label="自动启动"
/>
```

**支持的字段类型：**
- `text`, `email`, `password`, `number`
- `textarea`
- `select`, `autocomplete`
- `switch`, `checkbox`, `radio`
- `date`, `time`

### 5. StatusChip - 状态芯片

统一的状态显示组件。

```vue
<StatusChip
  :status="item.status"
  type="status"
  :custom-texts="{
    online: '在线',
    offline: '离线'
  }"
  :custom-colors="{
    online: 'success',
    offline: 'error'
  }"
/>
```

**预定义类型：**
- `status`: 在线/离线状态
- `boolean`: 是/否状态
- `priority`: 优先级状态
- `category`: 分类状态

### 6. UnifiedPageHeader - 页面头部

标准化页面头部布局。

```vue
<UnifiedPageHeader
  title="服务器管理"
  subtitle="管理和监控您的VPS服务器"
  icon="mdi-server"
  :breadcrumbs="breadcrumbs"
  :stats="stats"
  @add="handleAdd"
>
  <template #actions>
    <v-btn color="secondary" prepend-icon="mdi-export">
      导出数据
    </v-btn>
  </template>
</UnifiedPageHeader>
```

**特性：**
- 统一的标题样式
- 面包屑导航
- 统计信息显示
- 自定义操作区域

## 样式系统

### CSS 变量

系统定义了一套完整的设计令牌：

```css
:root {
  /* 间距系统 */
  --unified-spacing-xs: 4px;
  --unified-spacing-sm: 8px;
  --unified-spacing-md: 16px;
  --unified-spacing-lg: 24px;
  --unified-spacing-xl: 32px;

  /* 圆角系统 */
  --unified-radius-sm: 8px;
  --unified-radius-md: 12px;
  --unified-radius-lg: 16px;

  /* 阴影系统 */
  --unified-shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
  --unified-shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1);
  --unified-shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.1);
}
```

### 统一样式类

```css
/* 卡片样式 */
.unified-card {
  background: rgb(var(--v-theme-surface));
  border-radius: var(--unified-radius-lg);
  box-shadow: var(--unified-shadow-sm);
}

/* 按钮样式 */
.unified-btn--primary {
  background: linear-gradient(135deg, 
    rgb(var(--v-theme-primary)) 0%, 
    rgba(var(--v-theme-primary), 0.9) 100%);
}

/* 输入框样式 */
.unified-input .v-field {
  border-radius: var(--unified-radius-md);
  box-shadow: var(--unified-shadow-sm);
}
```

## 使用最佳实践

### 1. 页面结构标准化

```vue
<template>
  <PageLayout>
    <v-container class="py-6">
      <!-- 页面头部 -->
      <UnifiedPageHeader
        :title="pageTitle"
        :subtitle="pageSubtitle"
        :icon="pageIcon"
        @add="handleAdd"
      />

      <!-- 数据表格 -->
      <UnifiedDataTable
        :title="tableTitle"
        :headers="headers"
        :items="items"
        :loading="loading"
        @edit="handleEdit"
        @delete="handleDelete"
      />
    </v-container>
  </PageLayout>

  <!-- 对话框 -->
  <UnifiedDialog
    v-model="showDialog"
    :title="dialogTitle"
    @save="handleSave"
    @cancel="handleCancel"
  >
    <!-- 表单内容 -->
  </UnifiedDialog>
</template>
```

### 2. 表单设计标准化

```vue
<v-form ref="form" v-model="formValid">
  <v-row>
    <v-col cols="12" md="6">
      <UnifiedFormField
        v-model="formData.field1"
        type="text"
        label="字段1"
        :rules="[rules.required]"
        required
      />
    </v-col>
    <v-col cols="12" md="6">
      <UnifiedFormField
        v-model="formData.field2"
        type="select"
        label="字段2"
        :items="options"
      />
    </v-col>
  </v-row>
</v-form>
```

### 3. 状态显示标准化

```vue
<!-- 使用 StatusChip 替代原始的 v-chip -->
<StatusChip :status="item.status" type="status" />

<!-- 使用 ActionButtonGroup 替代单独的按钮 -->
<ActionButtonGroup
  :show-edit="true"
  :show-delete="true"
  @edit="handleEdit(item)"
  @delete="handleDelete(item)"
/>
```

## 响应式设计

所有组件都内置了响应式设计：

- **桌面端 (>960px)**: 完整功能和布局
- **平板端 (600px-960px)**: 适配的布局和交互
- **移动端 (<600px)**: 优化的移动体验

## 主题适配

系统完全支持 Vuetify 的主题系统：

- 自动适配浅色/深色主题
- 支持自定义主题颜色
- 高对比度模式支持
- 打印样式优化

## 国际化支持

所有组件都支持 Vue I18n：

```vue
<UnifiedDialog
  :title="$t('dialog.edit.title')"
  :subtitle="$t('dialog.edit.subtitle')"
/>
```

## 迁移指南

### 从现有组件迁移

1. **替换数据表格**：
```vue
<!-- 旧的方式 -->
<v-card>
  <v-card-title>标题</v-card-title>
  <v-data-table :headers="headers" :items="items" />
</v-card>

<!-- 新的方式 -->
<UnifiedDataTable
  title="标题"
  :headers="headers"
  :items="items"
/>
```

2. **替换对话框**：
```vue
<!-- 旧的方式 -->
<v-dialog v-model="dialog">
  <v-card>
    <v-card-title>标题</v-card-title>
    <v-card-text>内容</v-card-text>
    <v-card-actions>
      <v-btn @click="save">保存</v-btn>
    </v-card-actions>
  </v-card>
</v-dialog>

<!-- 新的方式 -->
<UnifiedDialog
  v-model="dialog"
  title="标题"
  @save="save"
>
  内容
</UnifiedDialog>
```

3. **替换表单字段**：
```vue
<!-- 旧的方式 -->
<v-text-field
  v-model="value"
  label="标签"
  variant="outlined"
  prepend-inner-icon="mdi-icon"
/>

<!-- 新的方式 -->
<UnifiedFormField
  v-model="value"
  type="text"
  label="标签"
  icon="mdi-icon"
/>
```

## 性能优化

- 所有组件都使用了 `<script setup>` 语法
- 合理使用 `computed` 和 `watch`
- 支持按需加载和代码分割
- 优化的 CSS 和动画性能

## 维护和扩展

### 添加新的状态类型

```typescript
// 在 StatusChip 组件中添加新的状态配置
const statusConfigs = {
  // 现有配置...
  newType: {
    active: { color: 'success', icon: 'mdi-check', text: 'common.active' },
    inactive: { color: 'error', icon: 'mdi-close', text: 'common.inactive' }
  }
}
```

### 扩展表单字段类型

```vue
<!-- 在 UnifiedFormField 组件中添加新的字段类型 -->
<v-color-picker
  v-else-if="type === 'color'"
  v-model="modelValue"
  @update:model-value="$emit('update:modelValue', $event)"
/>
```

这套统一组件系统提供了完整的解决方案，确保整个应用的一致性和可维护性。通过使用这些组件，可以大大提高开发效率并保证用户体验的一致性。