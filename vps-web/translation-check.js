// 翻译键检查脚本
const fs = require('fs');
const path = require('path');

// 读取翻译文件
const zhCN = require('./src/locales/zh-CN.ts');
const enUS = require('./src/locales/en-US.ts');

console.log('🔍 检查翻译键完整性...\n');

// 检查关键的翻译键
const keyChecks = [
  'nav.serverGroup',
  'groups.title',
  'groups.subtitle',
  'groups.newGroup',
  'groups.editGroup',
  'groups.deleteGroup',
  'groups.groupDetails',
  'groups.noDescription',
  'groups.uncategorized',
  'categories.categoryName',
  'categories.multiLanguageSettings',
  'categories.statistics'
];

let allKeysExist = true;

keyChecks.forEach(key => {
  const zhExists = checkKeyExists(zhCN.default, key);
  const enExists = checkKeyExists(enUS.default, key);
  
  if (zhExists && enExists) {
    console.log(`✅ ${key}`);
  } else {
    console.log(`❌ ${key} - 中文: ${zhExists ? '✓' : '✗'}, 英文: ${enExists ? '✓' : '✗'}`);
    allKeysExist = false;
  }
});

function checkKeyExists(obj, key) {
  const keys = key.split('.');
  let current = obj;
  
  for (const k of keys) {
    if (current && typeof current === 'object' && k in current) {
      current = current[k];
    } else {
      return false;
    }
  }
  
  return typeof current === 'string' && current.length > 0;
}

console.log('\n' + '='.repeat(50));
if (allKeysExist) {
  console.log('🎉 所有翻译键检查通过！');
} else {
  console.log('⚠️  发现缺失的翻译键，请检查上述列表');
}