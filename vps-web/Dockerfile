# Node.js 版本 Dockerfile
FROM node:18-alpine

WORKDIR /app

# 复制 package 文件
COPY package*.json ./

# 安装所有依赖（包括 devDependencies，因为需要 vite）
RUN npm ci

# 复制源代码
COPY . .

# 构建应用
RUN npm run build

# 暴露端口
EXPOSE 4173

# 使用 vite preview 启动服务
CMD ["npm", "run", "preview", "--", "--host", "0.0.0.0", "--port", "4173"]