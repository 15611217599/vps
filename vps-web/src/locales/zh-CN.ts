export default {
  // 通用
  common: {
    confirm: '确认',
    cancel: '取消',
    save: '保存',
    delete: '删除',
    edit: '编辑',
    add: '添加',
    search: '搜索',
    loading: '加载中...',
    success: '操作成功',
    error: '操作失败',
    warning: '警告',
    info: '提示',
    yes: '是',
    no: '否',
    back: '返回',
    next: '下一步',
    previous: '上一步',
    submit: '提交',
    reset: '重置',
    refresh: '刷新',
    close: '关闭',
    open: '打开',
    view: '查看',
    download: '下载',
    upload: '上传',
    export: '导出',
    import: '导入',
    confirmDelete: '确认删除',
    actions: '操作',
    status: '状态',
    enabled: '启用',
    disabled: '禁用',
    description: '描述',
    sortOrder: '排序',
    showing: '显示',
    of: '共',
    itemsPerPage: '每页显示',
    noData: '暂无数据',
    noSearchResults: '未找到匹配的结果'
  },
  
  // 导航菜单
  nav: {
    dashboard: '仪表板',
    servers: '服务器管理',
    serverCategory: '服务器类别',
    serverGroup: '服务器分组',
    monitoring: '监控中心',
    settings: '系统设置',
    profile: '个人资料',
    logout: '退出登录'
  },

  // 服务器类别
  serverCategory: {
    title: '服务器类别管理',
    list: '类别列表',
    category: '类别',
    name: '类别名称',
    confirmDelete: '确认删除类别 "{name}" 吗？'
  },
  
  // 服务器管理
  servers: {
    title: '服务器管理',
    category: '服务器类别',
    group: '服务器分组',
    list: '服务器列表',
    add: '添加服务器',
    edit: '编辑服务器',
    delete: '删除服务器',
    status: '状态',
    online: '在线',
    offline: '离线',
    location: '位置',
    provider: '提供商',
    specs: '配置',
    actions: '操作',
    newServer: '新建服务器',
    editServer: '编辑服务器',
    ip: 'IP地址',
    port: '端口',
    username: '用户名',
    password: '密码',
    selectGroup: '选择分组',
    ungrouped: '未分组',
    operatingSystem: '操作系统',
    cpuCores: 'CPU核心数',
    memory: '内存',
    diskSpace: '硬盘空间',
    diskType: '硬盘类型',
    networkSpeed: '网络速度',
    systemInfo: '系统信息',
    confirmDelete: '确认删除服务器 {ip} 吗？',
    loadFailed: '加载服务器失败',
    saveFailed: '保存服务器失败',
    deleteFailed: '删除服务器失败'
  },

  // 分组管理
  groups: {
    title: '分组管理',
    subtitle: '按地区和用途管理您的VPS分组',
    newGroup: '新建分组',
    editGroup: '编辑分组',
    deleteGroup: '删除分组',
    groupDetails: '分组详情',
    noDescription: '暂无描述',
    uncategorized: '未分类',
    selectCategory: '选择类别',
    groupName: '分组名称',
    groupDescription: '分组描述',
    region: '地区',
    country: '国家',
    city: '城市',
    sortOrder: '排序顺序',
    sortOrderHint: '数字越小越靠前',
    createGroup: '创建分组',
    saveGroup: '保存',
    cancelGroup: '取消',
    confirmDeleteTitle: '确认删除',
    confirmDeleteSubtitle: '此操作不可撤销，请谨慎操作',
    deleteWarning: '删除后将无法恢复，请谨慎操作！',
    groupToDelete: '即将删除的分组：',
    confirmDeleteAction: '确认删除',
    groupCreatedSuccess: '分组创建成功',
    groupUpdatedSuccess: '分组更新成功',
    groupDeletedSuccess: '分组删除成功',
    groupCreateFailed: '创建分组失败',
    groupUpdateFailed: '更新分组失败',
    groupDeleteFailed: '删除分组失败',
    getCategoriesFailed: '获取类别选项失败',
    getGroupsFailed: '获取分组选项失败',
    getServerGroupsFailed: '获取服务器分组失败',
    close: '关闭'
  },

  // 类别管理
  categories: {
    categoryName: '类别名称',
    description: '描述',
    multiLanguageSettings: '多语言设置',
    chineseName: '中文名称',
    englishName: 'English Name',
    chineseDescription: '中文描述',
    englishDescription: 'English Description',
    enableStatus: '启用状态',
    sort: '排序',
    statistics: '统计',
    groupsServers: '分组 / 服务器',
    debugInfo: '调试信息',
    currentLanguage: '当前语言',
    apiLanguageHeader: 'API语言头',
    dataLoadCount: '数据加载次数',
    hideDebugInfo: '隐藏调试信息',
    totalCategories: '共 {count} 个类别',
    multiLanguageSupport: '多语言支持',
    multiLanguageInfo: '当前语言: {language}，数据会根据您选择的语言自动显示对应的名称和描述。',
    chinese: '中文',
    english: 'English',
    confirmDeleteCategory: '确认删除类别 "{name}" 吗？',
    deleteCategoryFailed: '删除类别失败',
    loadCategoriesFailed: '加载类别失败',
    saveCategoryFailed: '保存类别失败'
  },
  
  // 仪表板
  dashboard: {
    welcome: '欢迎回来，{username}！',
    subtitle: '这里是您的个人仪表板',
    onlineStatus: '在线状态',
    userInfo: '用户信息',
    username: '用户名',
    email: '邮箱',
    quickNavigation: '快速导航',


    profileDescription: '个人资料和账户设置',
    tokenStatus: 'Token状态',
    tokenValid: '有效（永不过期）',
    systemFeatures: '系统功能',
    jwtAuth: 'JWT认证已启用',
    tokenNeverExpires: 'Token永不过期',
    h2Database: 'H2数据库存储',
    passwordEncryption: '安全的密码加密',
    enabled: '启用'
  },
  
  // 表单验证
  validation: {
    required: '此字段为必填项',
    email: '请输入有效的邮箱地址',
    minLength: '最少需要 {min} 个字符',
    maxLength: '最多允许 {max} 个字符',
    numeric: '请输入数字',
    phone: '请输入有效的手机号码',
    url: '请输入有效的URL地址',
    ip: '请输入有效的IP地址',
    invalidIp: '请输入有效的IP地址格式',
    invalidIP: '请输入有效的IP地址格式',
    port: '端口号范围应在 1-65535 之间'
  },
  
  // 系统设置
  settings: {
    title: '系统设置',
    general: '常规设置',
    theme: '主题设置',
    language: '语言设置',
    notification: '通知设置',
    security: '安全设置',
    about: '关于系统'
  },
  
  // 主题设置
  theme: {
    title: '主题设置',
    light: '浅色主题',
    dark: '深色主题',
    auto: '跟随系统',
    primaryColor: '主色调',
    secondaryColor: '辅助色',
    accentColor: '强调色',
    customTheme: '自定义主题'
  },
  
  // 认证相关
  auth: {
    // 登录
    login: {
      title: '欢迎回来',
      subtitle: '请登录您的账户',
      username: '用户名',
      password: '密码',
      loginButton: '立即登录',
      loginButtonLoading: '登录中...',
      noAccount: '还没有账号？',
      registerLink: '立即注册',
      forgotPassword: '忘记密码？'
    },
    
    // 注册
    register: {
      title: '加入我们',
      subtitle: '创建您的新账户',
      username: '用户名',
      email: '邮箱',
      password: '密码',
      confirmPassword: '确认密码',
      registerButton: '立即注册',
      registerButtonLoading: '注册中...',
      hasAccount: '已有账号？',
      loginLink: '立即登录',
      passwordStrength: '密码强度',
      passwordWeak: '弱',
      passwordMedium: '中等',
      passwordStrong: '强',
      agreeTerms: '我同意',
      termsOfService: '服务条款',
      privacyPolicy: '隐私政策'
    },
    
    // 错误信息
    errors: {
      emptyFields: '用户名和密码不能为空',
      allFieldsRequired: '所有字段都不能为空',
      pleaseFixErrors: '请修正表单中的错误后再提交',
      passwordMismatch: '两次输入的密码不一致',
      invalidCredentials: '用户名或密码错误',
      userExists: '用户已存在',
      networkError: '网络连接错误',
      serverError: '服务器错误，请稍后重试'
    }
  },
  
  // 用户资料
  profile: {
    title: '个人资料',
    username: '用户名',
    email: '邮箱',
    memberSince: '注册时间',
    changePassword: '修改密码',
    currentPassword: '当前密码',
    newPassword: '新密码',
    confirmNewPassword: '确认新密码',
    saveChanges: '保存修改',
    reset: '重置',
    updateSuccess: '资料更新成功',
    updateError: '资料更新失败',
    usernameFormat: '用户名只能包含字母、数字和下划线'
  },
  
  // 找回密码
  forgotPassword: {
    title: '找回密码',
    subtitle: '输入您的邮箱地址，我们将发送验证码',
    email: '邮箱地址',
    sendCode: '发送验证码',
    sendingCode: '发送中...',
    verificationCode: '验证码',
    newPassword: '新密码',
    confirmPassword: '确认新密码',
    resetPassword: '重置密码',
    resetting: '重置中...',
    backToEmail: '返回',
    backToLogin: '返回登录',
    rememberPassword: '想起密码了？',
    codeSent: '验证码已发送到您的邮箱',
    codeResent: '验证码已重新发送',
    codeInfo: '验证码已发送到 {email}，请查收邮件',
    codeFormat: '验证码必须是6位数字',
    resendCode: '重新发送验证码',
    resendCountdown: '重新发送 ({seconds}s)',
    resetSuccess: '密码重置成功，即将跳转到登录页面',
    sendCodeError: '发送验证码失败',
    resetError: '密码重置失败'
  },
  
  // 底部信息
  footer: {
    company: 'VPS管理系统有限公司',
    privacy: '隐私政策',
    terms: '服务条款',
    support: '技术支持',
    poweredBy: '基于 Vue.js + Spring Boot 构建'
  }
}